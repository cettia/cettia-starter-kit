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
package io.cettia.starter.vertx3;

import io.cettia.DefaultServer;
import io.cettia.Server;
import io.cettia.asity.bridge.vertx3.AsityRequestHandler;
import io.cettia.asity.bridge.vertx3.AsityWebSocketHandler;
import io.cettia.starter.ExampleServerAction;
import io.cettia.transport.http.HttpTransportServer;
import io.cettia.transport.websocket.WebSocketTransportServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

public class CettiaServerVerticle extends AbstractVerticle {
  @Override
  public void start() {
    Server server = new DefaultServer();
    new ExampleServerAction().on(server);

    HttpTransportServer httpAction = new HttpTransportServer().ontransport(server);
    WebSocketTransportServer wsAction = new WebSocketTransportServer().ontransport(server);

    HttpServer httpServer = vertx.createHttpServer();
    AsityRequestHandler asityRequestHandler = new AsityRequestHandler().onhttp(httpAction);
    httpServer.requestHandler(request -> {
      if (request.path().equals("/cettia")) {
        asityRequestHandler.handle(request);
      }
    });
    AsityWebSocketHandler asityWebsocketHandler = new AsityWebSocketHandler().onwebsocket(wsAction);
    httpServer.websocketHandler(socket -> {
      if (socket.path().equals("/cettia")) {
        asityWebsocketHandler.handle(socket);
      }
    });
    httpServer.listen(8080);
  }
}