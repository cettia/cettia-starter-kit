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
package io.cettia.starter.spring4;

import io.cettia.DefaultServer;
import io.cettia.Server;
import io.cettia.asity.bridge.spring.webmvc4.AsityController;
import io.cettia.asity.bridge.spring.webmvc4.AsityWebSocketHandler;
import io.cettia.starter.ExampleServerAction;
import io.cettia.transport.http.HttpTransportServer;
import io.cettia.transport.websocket.WebSocketTransportServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
@EnableWebMvc
@EnableWebSocket
public class CettiaServer implements WebSocketConfigurer {
  @Bean
  public Server server() {
    Server server = new DefaultServer();
    new ExampleServerAction().on(server);
    return server;
  }

  @Bean
  public HandlerMapping httpMapping(Server server) {
    HttpTransportServer httpAction = new HttpTransportServer();
    httpAction.ontransport(server);

    AsityController asityController = new AsityController().onhttp(httpAction);
    AbstractHandlerMapping mapping = new AbstractHandlerMapping() {
      @Override
      protected Object getHandlerInternal(HttpServletRequest request) {
        // Check whether a path equals '/echo'
        return "/cettia".equals(request.getRequestURI()) &&
          // Delegates WebSocket handshake requests to a webSocketHandler bean
          !"websocket".equalsIgnoreCase(request.getHeader("upgrade")) ? asityController : null;
      }
    };
    mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return mapping;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    WebSocketTransportServer wsAction = new WebSocketTransportServer();
    wsAction.ontransport(server());

    AsityWebSocketHandler asityWebSocketHandler = new AsityWebSocketHandler().onwebsocket(wsAction);
    registry.addHandler(asityWebSocketHandler, "/cettia").setAllowedOrigins("*");
  }

  public static void main(String[] args) {
    SpringApplication.run(CettiaServer.class, args);
  }
}
