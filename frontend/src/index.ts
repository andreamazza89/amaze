import ApolloClient from "apollo-client";
import { WebSocketLink } from "apollo-link-ws";
import gql from "graphql-tag";
import { InMemoryCache } from "apollo-cache-inmemory";
import { Elm } from "./elm/Main.elm";

const GRAPHQL_URI = "localhost:8080/subscriptions";

const wsLink = new WebSocketLink({
  uri: `ws://${GRAPHQL_URI}`
});

const client = new ApolloClient({
  link: wsLink,
  cache: new InMemoryCache({
    addTypename: true
  })
});

const app = Elm.Main.init({
  node: document.getElementById("elm")
});

app.ports.tellMeSeconds.subscribe(subscription => {
  client.subscribe({ query: gql(subscription) }).subscribe({
    next(response) {
      app.ports.secondsReceived.send(JSON.stringify(response));
    }
  });
});
