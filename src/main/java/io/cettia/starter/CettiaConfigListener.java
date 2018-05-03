package io.cettia.starter;

//import com.hazelcast.config.Config;
//import com.hazelcast.core.HazelcastInstance;
//import com.hazelcast.core.ITopic;
//import com.hazelcast.instance.HazelcastInstanceFactory;
//import io.cettia.ClusteredServer;
import io.cettia.DefaultServer;
import io.cettia.Server;
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
//import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebListener
public class CettiaConfigListener implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent event) {
    // Cettia part
//    ClusteredServer server = new ClusteredServer();
     Server server = new DefaultServer();
    HttpTransportServer httpTransportServer = new HttpTransportServer().ontransport(server);
    WebSocketTransportServer wsTransportServer = new WebSocketTransportServer().ontransport(server);

    // The socket handler
    server.onsocket((ServerSocket socket) -> {
      System.out.println(socket);

      Action<Void> logState = v -> System.out.println(socket + " " + socket.state());
      socket.onopen(logState).onclose(logState).ondelete(logState);

      socket.on("echo", data -> socket.send("echo", data));
      socket.on("chat", data -> server.all().send("chat", data));

      String username = findUsername(socket.uri());
      if (username != null) {
        socket.tag(username).on("myself", data -> server.byTag(username).send("myself", data));
      }

      Queue<Object[]> queue = new ConcurrentLinkedQueue<>();
      socket.oncache(args -> queue.offer(args));
      socket.onopen(v -> {
        while (socket.state() == ServerSocket.State.OPENED && !queue.isEmpty()) {
          Object[] args = queue.poll();
          socket.send((String) args[0], args[1], (Action<?>) args[2], (Action<?>) args[3]);
        }
      });
      socket.ondelete(v -> queue.forEach(args -> System.out.println(socket + " missed event - name: " + args[0] + ", data: " + args[1])));
    });

    // Servlet part
    ServletContext context = event.getServletContext();
    AsityServlet asityServlet = new AsityServlet().onhttp(httpTransportServer);
    ServletRegistration.Dynamic reg = context.addServlet(AsityServlet.class.getName(), asityServlet);
    reg.setAsyncSupported(true);
    reg.addMapping("/cettia");

    // Java WebSocket API part
    ServerContainer container = (ServerContainer) context.getAttribute(ServerContainer.class.getName());
    ServerEndpointConfig.Configurator configurator = new ServerEndpointConfig.Configurator() {
      @Override
      public <T> T getEndpointInstance(Class<T> endpointClass) {
        AsityServerEndpoint asityServerEndpoint = new AsityServerEndpoint().onwebsocket(wsTransportServer);
        return endpointClass.cast(asityServerEndpoint);
      }
    };
    try {
      container.addEndpoint(ServerEndpointConfig.Builder.create(AsityServerEndpoint.class, "/cettia").configurator(configurator).build());
    } catch (DeploymentException e) {
      throw new RuntimeException(e);
    }

    // Hazelcast part
//    HazelcastInstance hazelcast = HazelcastInstanceFactory.newHazelcastInstance(new Config());
//    ITopic<Map<String, Object>> topic = hazelcast.getTopic("cettia");
//    server.onpublish(message -> topic.publish(message));
//    topic.addMessageListener(message -> server.messageAction().on(message.getMessageObject()));
  }

  private String findUsername(String uri) {
    String username = null;
    String regex = "(?:^|.*&)username=([^&]+).*";
    String query = java.net.URI.create(uri).getQuery();
    if (query.matches(regex)) {
      username = query.replaceAll(regex, "$1");
    }

    return username;
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}
}
