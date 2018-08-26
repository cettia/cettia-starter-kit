package io.cettia.starter;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.instance.HazelcastInstanceFactory;
import io.cettia.ClusteredServer;
import io.cettia.ServerSocket;
import io.cettia.asity.action.Action;
import io.cettia.asity.bridge.jwa1.AsityServerEndpoint;
import io.cettia.asity.bridge.servlet3.AsityServlet;
import io.cettia.transport.http.HttpTransportServer;
import io.cettia.transport.websocket.WebSocketTransportServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

@WebListener
public class CettiaConfigListener implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent event) {
    // Cettia part
    // If you don't want to form a cluster,
    // replace the following line with `Server server = new DefaultServer();`
    ClusteredServer server = new ClusteredServer();
    HttpTransportServer httpAction = new HttpTransportServer().ontransport(server);
    WebSocketTransportServer wsAction = new WebSocketTransportServer().ontransport(server);

    // If a client opens a socket, the server creates and passes a ServerSocket to socket handlers
    server.onsocket((ServerSocket socket) -> {
      // ## Socket Lifecycle
      Action<Void> logState = v -> System.out.println(socket + " " + socket.state());
      socket.onopen(logState).onclose(logState).ondelete(logState);

      // ## Sending and Receiving Events
      // An `echo` event handler where any received echo event is sent back
      socket.on("echo", data -> socket.send("echo", data));

      // ## Attributes and Tags
      // Attributes and tags are contexts to store the socket state in the form of Map and Set
      String username = findParam(socket.uri(), "username");
      if (username == null) {
        // Attaches a tag to the socket.
        socket.tag("nonmember");
      } else {
        // Associates an attribute with the the socket
        socket.set("username", username);
      }

      // ## Working with Sockets
      // A `chat` event handler to send a given chat event to every socket in every server in the cluster
      socket.on("chat", data -> server.all().send("chat", data));

      // ## Finder Methods and Sentence
      if (username != null) {
        // A myself event handler to send a given myself event to sockets whose username is the same
        socket.on("myself", data -> {
          // Find sockets by the attribute and deal with them directly
          server.find(s -> username.equals(s.get("username"))).execute(s -> s.send("myself", data));
        });

        // How to allow only one socket per username
        boolean onlyOneSocket = Boolean.parseBoolean(findParam(socket.uri(), "onlyOneSocket"));
        if (onlyOneSocket) {
          // Finds sockets whose username is the same except this socket
          String me = socket.id();
          server.find(s -> username.equals(s.get("username")) && !me.equals(s.id()))
          // Sends a `signout` event to prevent reconnection and closes a connection
          .send("signout").close();
          // As of 1.2, it can be written more concisely
          // `server.byAttr("username", username).exclude(socket).send("signout").close();`
        }
      }

      // ## Recovering Missed Events
      Queue<Object[]> queue = new ConcurrentLinkedQueue<>();
      // Caches events that fail to send due to disconnection
      socket.oncache(args -> queue.offer(args));
      // Sends cached events on the next connection
      socket.onopen(v -> {
        while (socket.state() == ServerSocket.State.OPENED && !queue.isEmpty()) {
          Object[] args = queue.poll();
          socket.send((String) args[0], args[1], (Action<?>) args[2], (Action<?>) args[3]);
        }
      });
      // If the client fails to connect within 1 minute after disconnection,
      // You may want to consider notifying the user of finally missed events, like push notifications
      socket.ondelete(v -> queue.forEach(args -> {
        System.out.println(socket + " missed event - name: " + args[0] + ", data: " + args[1]);
      }));
    });

    // ## Working with Sockets
    // To deal with sockets, inject the server wherever you want
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    // Sends a welcome event to sockets representing user not signed in every 5 seconds
    executor.scheduleAtFixedRate(() -> server.byTag("nonmember").send("welcome"), 0, 5, SECONDS);

    // ## Plugging Into the Web Framework
    // Cettia is designed to run on any web framework seamlessly on the JVM
    // Note how `httpAction` and `wsAction` are plugged into Servlet and Java API for Websocket
    ServletContext context = event.getServletContext();
    AsityServlet asityServlet = new AsityServlet().onhttp(/* ㅇㅅㅇ */ httpAction);
    ServletRegistration.Dynamic reg = context.addServlet(AsityServlet.class.getName(), asityServlet);
    reg.setAsyncSupported(true);
    reg.addMapping("/cettia");

    ServerContainer container = (ServerContainer) context.getAttribute(ServerContainer.class.getName());
    ServerEndpointConfig.Configurator configurator = new ServerEndpointConfig.Configurator() {
      @Override
      public <T> T getEndpointInstance(Class<T> endpointClass) {
        AsityServerEndpoint asityServerEndpoint = new AsityServerEndpoint();
        asityServerEndpoint.onwebsocket(/* ㅇㅅㅇ */ wsAction);
        return endpointClass.cast(asityServerEndpoint);
      }
    };
    try {
      container.addEndpoint(ServerEndpointConfig.Builder.create(AsityServerEndpoint.class, "/cettia").configurator(configurator).build());
    } catch (DeploymentException e) {
      throw new RuntimeException(e);
    }

    // ## Scaling a Cettia Application
    // Any publish-subscribe messaging system can be used to scale a Cettia application horizontally,
    // and it doesn’t require any modification in the existing application.
    HazelcastInstance hazelcast = HazelcastInstanceFactory.newHazelcastInstance(new Config());
    ITopic<Map<String, Object>> topic = hazelcast.getTopic("cettia");
    // It publishes messages given by the server
    server.onpublish(message -> topic.publish(message));
    // It relays published messages to the server
    topic.addMessageListener(message -> server.messageAction().on(message.getMessageObject()));
  }

  private String findParam(String uri, String key) {
    String value = null;
    String regex = "(?:^|.*&)" + key + "=([^&]+).*";
    String query = java.net.URI.create(uri).getQuery();
    if (query.matches(regex)) {
      value = query.replaceAll(regex, "$1");
    }

    return value;
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}

}
