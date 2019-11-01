import {Elm} from "./elm/Main"

const app = Elm.Main.init({
    flags: null,
    node: document.getElementById("elm")
});

const webSocket = new WebSocket('ws://localhost:8080/subscriptions');

app.ports.tellMeSeconds.subscribe(subscription => {
    webSocket.send(JSON.stringify({query: subscription}));
});

webSocket.onmessage = (message) => {
    console.log(message);
    app.ports.mazesInformationReceived.send(message.data);
};
