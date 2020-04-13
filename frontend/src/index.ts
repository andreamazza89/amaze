import {Elm} from "./elm/Main"

const app = Elm.Main.init({
    flags: null,
    node: document.getElementById("elm")
});

const webSocketProtocol = (window.location.protocol === "http:") ? "ws://" : "wss://";
const webSocket = new WebSocket(`${webSocketProtocol}${window.location.host}/subscriptions`);

webSocket.onopen = () => {
    app.ports.gameUpdates.subscribe(subscription => {
        webSocket.send(JSON.stringify({query: subscription}));
    });
};

webSocket.onmessage = (message) => {
    app.ports.mazesInformationReceived.send(JSON.parse(message.data));
};
