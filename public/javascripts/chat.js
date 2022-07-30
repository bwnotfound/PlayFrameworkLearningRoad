


const inputField = document.getElementById("chat-input");
const outputArea = document.getElementById("chat-area");

const socketRoute = document.getElementById("ws-route").value;
const socket = new WebSocket(socketRoute.replace("http","ws"));

inputField.onkeydown = (event) => {
    if(event.key === 'Enter'){
        socket.send(inputField.value);
        inputField.value = '';
        // inputField.focus = true
    }
};

socket.onopen = (event) => socket.send("New window!");
socket.onmessage = (event) => {
    outputArea.value += '\n' + event.data;
};