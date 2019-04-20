/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cettia.starter.javaee7;

import io.cettia.DefaultServer;
import io.cettia.Server;
import io.cettia.asity.bridge.jwa1.AsityServerEndpoint;
import io.cettia.asity.bridge.servlet3.AsityServlet;
import io.cettia.starter.ExampleServerAction;
import io.cettia.transport.http.HttpTransportServer;
import io.cettia.transport.websocket.WebSocketTransportServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import javax.websocket.DeploymentException;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

@WebListener
public class CettiaInitializer implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent event) {
    Server server = new DefaultServer();
    new ExampleServerAction().on(server);

    HttpTransportServer httpAction = new HttpTransportServer().ontransport(server);
    WebSocketTransportServer wsAction = new WebSocketTransportServer().ontransport(server);

    ServletContext context = event.getServletContext();
    registerCettiaServlet(httpAction, context);
    registerCettiaWebSocketEndpoint(wsAction, context);
  }

  private void registerCettiaServlet(HttpTransportServer httpAction, ServletContext context) {
    ServletRegistration.Dynamic reg = context.addServlet("cettia", new AsityServlet().onhttp(httpAction));

    reg.setAsyncSupported(true);
    reg.addMapping("/cettia");
  }

  private void registerCettiaWebSocketEndpoint(WebSocketTransportServer wsAction, ServletContext context) {
    ServerContainer container = (ServerContainer) context.getAttribute(ServerContainer.class.getName());
    ServerEndpointConfig config = ServerEndpointConfig.Builder
      .create(AsityServerEndpoint.class, "/cettia")
      .configurator(new ServerEndpointConfig.Configurator() {
        @Override
        public <T> T getEndpointInstance(Class<T> endpointClass) {
          return endpointClass.cast(new AsityServerEndpoint().onwebsocket(wsAction));
        }

        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
          config.getUserProperties().put(HandshakeRequest.class.getName(), request);
        }
      })
      .build();

    try {
      container.addEndpoint(config);
    } catch (DeploymentException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}

}
