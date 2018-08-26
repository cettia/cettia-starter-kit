# Cettia starter kit

This starter kit is created to help you make a quick start with [Cettia](http://cettia.io). It is based on Servlet 3 and Java WebSocket API 1, which are the most-used I/O frameworks in writing Cettia applications. For the details, see [Building real-time web applications with Cettia](http://cettia.io/guides/cettia-tutorial/).

To get started, make sure you have Java 8+ and Maven 3+ installed.

- Clone the repository.
   ```
   git clone https://github.com/cettia/cettia-starter-kit && cd cettia-starter-kit
   ```
- Start up the server on port 8080.
   ```
   mvn jetty:run
   ```

Now the application is running now, open your browser and navigate to [http://127.0.0.1:8080/](http://127.0.0.1:8080/). If you want to form a cluster, start up one more server on port 8090 by running `mvn jetty:run -Djetty.port=8090`.