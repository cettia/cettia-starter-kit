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
package io.cettia.starter;

import io.cettia.Server;
import io.cettia.ServerSocket;
import io.cettia.asity.action.Action;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.cettia.ServerSocketPredicates.tag;

/**
 * An example action to handle {@link Server}.
 */
public class ExampleServerAction implements Action<Server> {

  @Override
  public void on(Server server) {
    server.onsocket((ServerSocket socket) -> {
      // On the receipt of a transport, the server creates a socket with a NULL state
      System.out.println(socket + " is created");

      Action<Void> logState = v -> System.out.println(socket + " transitions to " + socket.state());
      // If it performs the handshake successfully, or the connection is recovered by the client reconnection
      socket.onopen(logState);
      // If it fails to perform the handshake, or the connection is disconnected for some reason
      socket.onclose(logState);
      // After one minute has elapsed since disconnection
      socket.ondelete(logState);

      // Sets a username
      socket.set("username", findParam(socket.uri(), "username"));
      // Joins the lounge channel where everyone gets together
      socket.tag("channel:lounge");

      // If a message is given, broadcasts it to everyone in the lounge channel
      socket.on("message", (Map<String, Object> input) -> {
        String text = (String) input.get("text");

        Map<String, Object> output = new LinkedHashMap<>();
        output.put("sender", socket.get("username"));
        output.put("text", text);

        System.out.println(socket.get("username") + "@" + socket.id() + " sends '" + text + "' to the lounge");
        server.find(tag("channel:lounge")).send("message", output);

        // If you prefer to deal with each socket directly,
        // server.find(s -> s.tags().contains("channel:lounge")).execute(s -> s.send("message", output));
      });

      // If you want to rewrite the above event handler with POJOs
      // socket.on("message", raw -> {
      //   ChatInputMessage in = new ObjectMapper().convertValue(raw, ChatInputMessage.class);
      //   ChatOutputMessage out = new ChatOutputMessage(socket.get("username"), in.getText());
      //   server.find(tag("channel:lounge")).send("message", out);
      // });
    });
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
}
