import {Elm} from "./elm/Main"

const app = Elm.Main.init({
    flags: null,
    node: document.getElementById("elm")
});

const webSocket = new WebSocket(`ws://${window.location.host}/subscriptions`);

app.ports.gameUpdates.subscribe(subscription => {
    webSocket.send(JSON.stringify({query: subscription}));
});

webSocket.onmessage = (message) => {
    app.ports.mazesInformationReceived.send(JSON.parse(message.data));
};
