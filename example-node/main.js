const cettia = require("cettia-client");
const username = "DH";
const uri = `http://localhost:8080/cettia?username=${encodeURIComponent(username)}`;
const socket = cettia.open(uri);

const addMessage = ({sender, text}) => console.log(`${sender} sends ${text}`);
socket.on("message", addMessage);

const addSystemMessage = text => addMessage({sender: "system", text});
socket.on("connecting", () => addSystemMessage("The socket starts a connection."));
socket.on("open", () => addSystemMessage("The socket establishes a connection."));
socket.on("close", () => addSystemMessage("All transports failed to connect or the connection was disconnected."));
socket.on("waiting", (delay) => addSystemMessage(`The socket will reconnect after ${delay} ms`));

// Once a socket has opened, sends a message every 5 seconds
socket.once("open", () => setInterval(() => socket.send("message", {text: `A message - ${Date.now()}`}), 5000));