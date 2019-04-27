import React, {Component} from "react";
import {View, Platform} from "react-native";
import {WebView} from "react-native-webview";
import cettia from "cettia-client/cettia-bundler";

export default class App extends Component {
  onWebViewMessage = ({nativeEvent}) => {
    const event = JSON.parse(nativeEvent.data);
    switch (event.type) {
      case "open":
        this.socket = cettia.open(event.uri);
        this.socket.on("connecting", () => {
          this.webView.injectJavaScript('addSystemMessage("The socket starts a connection.");true;');
        });
        this.socket.on("open", () => {
          this.webView.injectJavaScript('addSystemMessage("The socket establishes a connection.");true;');
        });
        this.socket.on("close", () => {
          this.webView.injectJavaScript('addSystemMessage("All transports failed to connect or the connection was disconnected.");true;');
        });
        this.socket.on("waiting", (delay) => {
          this.webView.injectJavaScript(`addSystemMessage("The socket will reconnect after ${delay} ms");true;`);
        });
        this.socket.on("message", message => {
          this.webView.injectJavaScript(`addMessage(${JSON.stringify(message)});true;`);
        });
        break;
      case "send":
        this.socket.send("message", {text: event.text});
        break;
    }
  };

  render() {
    return (
      <View style={{flex: 1}}>
        <WebView
          originWhitelist={["*"]}
          source={{html}}
          ref={r => (this.webView = r)}
          onMessage={this.onWebViewMessage}
        />
      </View>
    );
  }
}

// This html code is basically the same with index.html in the example-web project
// Comments that describe the difference from index.html begin with @@
const html = `<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Cettia Starter Kit</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
  <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:400,700" />
  <!-- @@ Injects app.css instead of importing it -->
  <style>
  html, body {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
    position: fixed;
    overflow: hidden;
    font-family: 'Roboto', sans-serif;
  }
  
  input, textarea, select {
    font-family: inherit;
  }
  
  .inactive {
    display: none !important;
  }
  
  #index {
    margin: 3rem;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
  }
  
  #index > div {
    max-width: 100%;
  }
  
  #index > div > * {
    display: inline-block;
    font-size: 3rem;
  }
  
  #index > div > form > input {
    margin-left: 1rem;
    border-top: none;
    border-left: none;
    border-right: none;
    border-radius: 0;
    font-size: inherit;
  }
  
  #chat {
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;
  }
  
  #chat > div.messages-container {
    flex: 1 1 auto;
    position: relative;
    margin: 1rem;
    margin-bottom: 0;
  }
  
  #chat > div.messages-container > ul {
    position: absolute;
    bottom: 0;
    right: 0;
    left: 0;
    overflow-y: auto;
    max-height: 100%;
    padding: 0;
    margin: 0.5rem;
    list-style: none;
  }
  
  #chat > div.messages-container > ul > li {
    white-space: pre-wrap;
  }
  
  #chat > div.messages-container > ul > li.sender {
    margin-top: 1rem;
    margin-bottom: 0.5rem;
    font-weight: bold;
    color: #2f2f2f;
  }
  
  #chat > div.messages-container > ul > li.message {
    color: #343434;
    padding: 0.25rem;
  }
  
  #chat > div.messages-container > ul > li.message:hover {
    background-color: #f7f7f7;
  }
  
  #editor {
    margin: 1rem;
    padding: 0.5rem;
    color: #2f2f2f;
  }
  
  #editor > div[contenteditable] {
    padding: 0.25rem;
    border-bottom: 1px solid #d0d0d0;
    min-height: 1rem;
  }
  
  #editor > div[contenteditable]:focus {
    outline: 0;
    border-bottom: 1px solid #808080;
  }
  
  #editor > div[contenteditable]:empty:not(:focus)::before {
    content: attr(data-placeholder)
  }
  </style>
</head>
<body>
<div id="index">
  <div>
    <span>Hello,</span>
    <form>
      <input autofocus autocomplete="off" name="username" type="text" placeholder="username">
    </form>
  </div>
</div>
<div id="chat" class="inactive">
  <div class="messages-container">
    <ul>
      <li class="sender">system</li>
      <li class="message">This example is created to help you make a quick start with <a href="https://cettia.io/" target="_blank">Cettia</a>. For the details, see the <a href="https://cettia.io/guides/cettia-tutorial/" target="_blank">Getting started</a>.</li>
    </ul>
  </div>
  <footer>
    <div id="editor">
      <div contenteditable data-placeholder="Type a message..."></div>
    </div>
  </footer>
</div>
<!-- @@ Injects app.js instead of importing it -->
<script>
// Highlight 'socket' to see where and how a socket is used
// With https://babeljs.io/repl, you can make the following code compatible with ES5
document.querySelector("#index form").addEventListener("submit", e => {
  e.preventDefault();

  // Assumes the user is logged with the username
  const $username = e.target.elements.username;
  const username = $username.value || $username.placeholder;

  // Replaces #index with #chat
  document.querySelector("#index").classList.add("inactive");
  document.querySelector("#chat").classList.remove("inactive");

  let oldSender = "system";
  const addMessage = ({sender, text}) => {
    const $messages = document.querySelector("#chat > div.messages-container > ul");
    if (oldSender !== sender) {
      const $li = document.createElement("li");
      $li.classList.add("sender");
      $li.textContent = sender;
      $messages.appendChild($li);
      oldSender = sender;
    }

    const $li = document.createElement("li");
    $li.classList.add("message");
    $li.textContent = text;
    $messages.appendChild($li);
    $li.scrollIntoView();
  };

  // Opens and initializes a socket
  // @@ Selects a proper host per platform
  const uri = "http://${Platform.OS === "android" ? "10.0.2.2" : "localhost"}:8080/cettia?username=" + encodeURIComponent(username);
  // @@ Sends a message to React Native to open and initialize a socket
  ReactNativeWebView.postMessage(JSON.stringify({type: "open", uri}));
  const addSystemMessage = text => addMessage({sender: "system", text});

  // Configures the editor
  const $editor = document.querySelector("#editor > div[contenteditable]");
  window.addEventListener("keypress", e => {
    if (e.target !== $editor && e.key.trim() && e.key.length === 1) {
      e.preventDefault();
      $editor.innerText += e.key;

      const selection = window.getSelection();
      const range = document.createRange();
      selection.removeAllRanges();
      range.selectNodeContents($editor);
      range.collapse(false);
      selection.addRange(range);
      $editor.focus();
    }
  }, false);
  $editor.addEventListener("keypress", e => {
    if (!e.shiftKey && e.code === "Enter") {
      e.preventDefault();

      const text = $editor.innerText;
      if (text) {
        // @@ Sends a message to React Native to send a message to the server
        ReactNativeWebView.postMessage(JSON.stringify({type: "send", text}));
        $editor.innerHTML = "";
      }
    }
  }, false);
  
  // @@ Exposes the following functions to the window so that code on React Native side can call them
  window.addMessage = addMessage;
  window.addSystemMessage = addSystemMessage;
}, false);
</script>
</body>
</html>
`;