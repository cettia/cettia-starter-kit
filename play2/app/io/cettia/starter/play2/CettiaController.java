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
package io.cettia.starter.play2;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import io.cettia.DefaultServer;
import io.cettia.Server;
import io.cettia.asity.bridge.play2.AsityWebSocket;
import io.cettia.starter.ExampleServerAction;
import io.cettia.transport.websocket.WebSocketTransportServer;
import play.mvc.Controller;
import play.mvc.WebSocket;

import javax.inject.Inject;

public class CettiaController extends Controller {

  private Server server = new DefaultServer();
  private WebSocketTransportServer wsAction = new WebSocketTransportServer().ontransport(server);

  private final ActorSystem actorSystem;
  private final Materializer materializer;

  @Inject
  public CettiaController(ActorSystem actorSystem, Materializer materializer) {
    this.actorSystem = actorSystem;
    this.materializer = materializer;

    new ExampleServerAction().on(server);
  }

  public WebSocket websocket() {
    AsityWebSocket webSocket = new AsityWebSocket(actorSystem, materializer);
    webSocket.onwebsocket(wsAction);

    return webSocket;
  }

}
