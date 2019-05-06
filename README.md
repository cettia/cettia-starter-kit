# Getting Started

The easiest way to get started with Cettia is to play with the [Cettia Starter Kit](https://github.com/cettia/cettia-starter-kit) that is a basic chat application made with Cettia.

![cettia-starter-kit-1555758896147](https://user-images.githubusercontent.com/1095042/56456590-5947a080-63a9-11e9-9155-36d49d33ed4c.gif)

## Running the Starter Kit

The starter kit requires Java 8+ and Maven 3+. Clone or download the repository and run `mvn install`.

```
git clone https://github.com/cettia/cettia-starter-kit.git
cd cettia-starter-kit
mvn install
```

### Server

The server example is located in the `example-server` project.

- [`/example-server/pom.xml`](https://github.com/cettia/cettia-starter-kit/blob/master/example-server/pom.xml)
- [`/example-server/src/main/java/io/cettia/starter/ExampleServerAction.java`](https://github.com/cettia/cettia-starter-kit/blob/master/example-server/src/main/java/io/cettia/starter/ExampleServerAction.java)

To run the example, you should integrate it with a web framework first. The starter kit provides example projects integrated with each web framework supporting Cettia as follows. Pick one according to your favorite framework, enter the project directory, and run the Maven command.

| Web framework  | Maven project   | Maven command                    | pom.xml                                                                           | Main class                                                                                                                                                              |
|----------------|-----------------|----------------------------------|-----------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Atmosphere     | atmosphere2     | `mvn jetty:run`                  | [pom.xml](https://github.com/cettia/cettia-starter-kit/blob/master/atmosphere2/pom.xml)     | [CettiaInitializer](https://github.com/cettia/cettia-starter-kit/blob/master/atmosphere2/src/main/java/io/cettia/starter/atmosphere2/CettiaInitializer.java)  |
| Grizzly        | grizzly2        | `mvn clean package exec:java`    | [pom.xml](https://github.com/cettia/cettia-starter-kit/blob/master/grizzly2/pom.xml)        | [CettiaServer](https://github.com/cettia/cettia-starter-kit/blob/master/grizzly2/src/main/java/io/cettia/starter/grizzly2/CettiaServer.java)                  |
| Java EE        | javaee7         | `mvn jetty:run`                  | [pom.xml](https://github.com/cettia/cettia-starter-kit/blob/master/javaee7/pom.xml)         | [CettiaInitializer](https://github.com/cettia/cettia-starter-kit/blob/master/javaee7/src/main/java/io/cettia/starter/javaee7/CettiaInitializer.java)          |
| Netty          | netty4          | `mvn clean package exec:java`    | [pom.xml](https://github.com/cettia/cettia-starter-kit/blob/master/netty4/pom.xml)          | [CettiaServer](https://github.com/cettia/cettia-starter-kit/blob/master/netty4/src/main/java/io/cettia/starter/netty4/CettiaServer.java)                      |
| Spring WebFlux | spring-webflux5 | `mvn spring-boot:run`            | [pom.xml](https://github.com/cettia/cettia-starter-kit/blob/master/spring-webflux5/pom.xml) | [CettiaServer](https://github.com/cettia/cettia-starter-kit/blob/master/spring-webflux5/src/main/java/io/cettia/starter/spring5/CettiaServer.java)            |
| Spring Web MVC | spring-webmvc4  | `mvn spring-boot:run`            | [pom.xml](https://github.com/cettia/cettia-starter-kit/blob/master/spring-webmvc4/pom.xml)  | [CettiaServer](https://github.com/cettia/cettia-starter-kit/blob/master/spring-webmvc4/src/main/java/io/cettia/starter/spring4/CettiaServer.java )            |
| Vert.x         | vertx2          | `mvn clean package vertx:runMod` | [pom.xml](https://github.com/cettia/cettia-starter-kit/blob/master/vertx2/pom.xml)          | [CettiaServerVerticle](https://github.com/cettia/cettia-starter-kit/blob/master/vertx2/src/main/java/io/cettia/starter/vertx2/CettiaServerVerticle.java)      |
|                | vertx3          | `mvn clean package exec:java`    | [pom.xml](https://github.com/cettia/cettia-starter-kit/blob/master/vertx3/pom.xml)          | [CettiaServerVerticle](https://github.com/cettia/cettia-starter-kit/blob/master/vertx3/src/main/java/io/cettia/starter/vertx3/CettiaServerVerticle.java)      |

Then, it will run a server that listens on port 8080 and exposes an endpoint `/cettia`. For how to integrate Cettia with the framework of your choice, see the above projects' source code and the reference documentation's [Plugging Into the Web Framework](https://cettia.io/guides/cettia-tutorial/#plugging-into-the-web-framework) section.

### Web

The browser-based client example is located in the `example-web` project.

- [`/example-web/src/main/webapp/index.html`](https://github.com/cettia/cettia-starter-kit/blob/master/example-web/src/main/webapp/index.html)
- [`/example-web/src/main/webapp/app.js`](https://github.com/cettia/cettia-starter-kit/blob/master/example-web/src/main/webapp/app.js)

Enter the `example-web` Maven project, start a static web server as follows, and then visit `http://localhost:8070/`.

```
cd example-web
mvn jetty:run -Djetty.port=8070
```

#### On the fly

If you prefer to run code snippets on the fly, open the developer tools on this page, click the console tab and then type `cettia` to the console.

```
> cettia;
< {open: function, transport: Object, util: Object}
```

Then, you should see that the `cettia` object is available. For your information, every page of [https://cettia.io](https://cettia.io) loads the latest version of `cettia` object so that you can play with the `cettia` object at any time.

### React Native

The React Native example is located in the `example-react-native` project.

- [`/example-react-native/App.js`](https://github.com/cettia/cettia-starter-kit/blob/master/example-react-native/App.js)

Make sure that you have set up the React Native development environment. Enter the `example-react-native` npm project, and type the following command.

```
cd example-react-native
npm install
```

Then, you can run the example with React Native CLI as follows.

#### iOS

```
react-native run-ios
```

#### Android

```
react-native run-android
```

### Node.js

The Node.js example is located in the `example-node` project.

- [`/example-node/main.js`](https://github.com/cettia/cettia-starter-kit/blob/master/example-node/main.js)

You need to have installed Node.js version 4 and above. Enter the `example-web` npm project, install the dependencies, and start main.js.

```
cd example-node
npm install
npm start
```

#### On the fly

If you prefer to run code snippets on the fly, open a Node.js console and copy the contents of the main.js and paste it into the console. You can deal with the `socket` directly.

## Understanding the Example

Here are the user stories implemented in the example.

- As a guest I want to sign in to the application by entering a username only so that I don't have to go through an annoying sign-up process.
- As a user I want to join the lounge channel automatically after sign in so that I can talk with everyone.
- As a user I want to send messages to the lounge channel so that everyone can receive my messages.
- As a user I want to receive messages when others send them to the lounge channel so that I can keep conversation in real-time.

In this guide we will skip explanation about view components and focus on how we can exchange events between the server and the client in real-time.

### Opening a Socket

Add a `socket` event handler in the server side.

```java
server.onsocket((ServerSocket socket) -> {
  System.out.println(socket + " is created");
});
```

Then, in the client, open a socket side adding a `username` parameter to the query string of the URI.

```javascript
const uri = `http://localhost:8080/cettia?username=${encodeURIComponent(username)}`;
const socket = cettia.open(uri);
```

For convenience sake, in the rest of the guide, we will assume that a `socket` is already opened. For the details, see the reference documentation's [Opening a Socket](https://cettia.io/guides/cettia-tutorial/#opening-a-socket).

### Tracking the Socket Lifecycle

Register the following built-in event handlers to track each side of socket.

The server-side:

```java
server.onsocket((ServerSocket socket) -> {
  Action<Void> logState = v -> System.out.println(socket + " transitions to " + socket.state());
  // If it performs the handshake successfully, or the connection is recovered by the client reconnection
  socket.onopen(logState);
  // If it fails to perform the handshake, or the connection is disconnected for some reason
  socket.onclose(logState);
  // After one minute has elapsed since disconnection
  socket.ondelete(logState);
});
```

The client-side:

```javascript
const addSystemMessage = text => addMessage({sender: "system", text});
socket.on("connecting", () => addSystemMessage("The socket starts a connection."));
socket.on("open", () => addSystemMessage("The socket establishes a connection."));
socket.on("close", () => addSystemMessage("All transports failed to connect or the connection was disconnected."));
socket.on("waiting", (delay) => addSystemMessage(`The socket will reconnect after ${delay} ms`));
```

`addMessage` is a function to add a message to the message list by manipulating the DOM. If you are in the console, declare the function instead, as follows.

```javascript
const addMessage = ({sender, text}) => console.log(`${sender} sends ${text}`);
```

For the details including state transition diagrams, see the reference documentation's [Socket Lifecycle](https://cettia.io/guides/cettia-tutorial/#socket-lifecycle).

### Storing Information in a Socket

A server-side socket can have custom properties in the form of a key-value pair and a set element.

```java 
server.onsocket((ServerSocket socket) -> {
  // Sets a username
  socket.set("username", findParam(socket.uri(), "username"));
  // Joins the lounge channel where everyone gets together
  socket.tag("channel:lounge");
});
```

See the reference documentation's [Attributes and Tags](https://cettia.io/guides/cettia-tutorial/#attributes-and-tags) for the details.

### Working with Sockets

To send an event to certain sockets in the server, write a socket predicate that selects which sockets to handle, and pass it to `find()`, and write a socket action that sends an event to the given socket, and pass it to `execute()`. The server will find sockets that matches the given predicate and execute the given action passing found sockets one by one.

```java
server.onsocket((ServerSocket socket) -> {
  socket.on("message", (Map<String, Object> input) -> {
    String text = (String) input.get("text");

    Map<String, Object> output = new LinkedHashMap<>();
    output.put("sender", socket.get("username"));
    output.put("text", text);

    System.out.println(socket.get("username") + "@" + socket.id() + " sends '" + text + "' to the lounge");
    server.find(s -> s.tags().contains("channel:lounge")).execute(s -> s.send("message", output));
  });
});
```

Unless you need to deal with a socket passed to a socket predicate and a socket action directly, you can rewrite the above code more concisely with predefined predicates and convenient methods as follows. 

```java
server.onsocket((ServerSocket socket) -> {
  socket.on("message", (Map<String, Object> input) -> {
    // ...
    // With 'import static io.cettia.ServerSocketPredicates.tag;'
    server.find(tag("channel:lounge")).send("message", output);
  });
});
```

In the client side, register a `message` event handler 

```javascript
socket.on("message", message => addMessage(message));
```

And send a `message` event with with a message, `text`. You will see all sockets joined the lounge channel receive the message.

```javascript
socket.send("message", {text});
```

For the details, See the reference documentation's [Working with Sockets](https://cettia.io/guides/cettia-tutorial/#working-with-sockets) and [Advanced Sockets Handling](https://cettia.io/guides/cettia-tutorial/#advanced-sockets-handling) sections.

## Conclusion

In this guide, we walked through basic features of Cettia with the [Cettia Starter Kit](https://github.com/cettia/cettia-starter-kit); opening a socket, tracking the socket lifecycle, storing information in a socket, and working with sockets. To learn more about Cettia, including 

- How to run an application on your favorite web framework
- What types are allowed for event data
- How to use POJOs as event data
- How to scale an application
- How to recover missed events, and so on

Take a look at the reference documentation – [Building Real-Time Web Applications With Cettia](https://cettia.io/guides/cettia-tutorial). If you have any questions, please let us know on the [Cettia Groups](http://groups.google.com/group/cettia).
