// Highlight `socket` to see where and how a socket is used
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
    $li.textContent = `${text}`;
    $messages.appendChild($li);
    $li.scrollIntoView();
  };

  // Opens and initializes a socket
  const uri = `http://localhost:8080/cettia?username=${encodeURIComponent(username)}`;
  const socket = cettia.open(uri);
  const addSystemMessage = text => addMessage({sender: "system", text});

  socket.on("connecting", () => addSystemMessage("The socket starts a connection."));
  socket.on("open", () => addSystemMessage("The socket establishes a connection."));
  socket.on("close", () => addSystemMessage("All transports failed to connect or the connection was disconnected."));
  socket.on("waiting", (delay) => addSystemMessage(`The socket will reconnect after ${delay} ms`));
  socket.on("message", message => addMessage(message));

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
        socket.send("message", {text});
        $editor.innerHTML = "";
      }
    }
  }, false);
}, false);