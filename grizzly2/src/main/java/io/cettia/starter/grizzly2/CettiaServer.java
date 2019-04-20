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
package io.cettia.starter.grizzly2;

import io.cettia.DefaultServer;
import io.cettia.Server;
import io.cettia.asity.bridge.grizzly2.AsityHttpHandler;
import io.cettia.asity.bridge.grizzly2.AsityWebSocketApplication;
import io.cettia.starter.ExampleServerAction;
import io.cettia.transport.http.HttpTransportServer;
import io.cettia.transport.websocket.WebSocketTransportServer;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;

public class CettiaServer {
  public static void main(String[] args) throws Exception {
    Server server = new DefaultServer();
    new ExampleServerAction().on(server);

    HttpTransportServer httpAction = new HttpTransportServer().ontransport(server);
    WebSocketTransportServer wsAction = new WebSocketTransportServer().ontransport(server);

    HttpServer httpServer = HttpServer.createSimpleServer();
    ServerConfiguration config = httpServer.getServerConfiguration();
    config.addHttpHandler(new AsityHttpHandler().onhttp(httpAction), "/cettia");
    NetworkListener listener = httpServer.getListener("grizzly");
    listener.registerAddOn(new WebSocketAddOn());
    WebSocketEngine.getEngine().register("", "/cettia", new AsityWebSocketApplication().onwebsocket(wsAction));
    httpServer.start();

    System.in.read();
  }
}