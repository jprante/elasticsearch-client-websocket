Elasticsearch WebSocket Client
==============================

A Netty-based websocket client for Elasticsearch, based upon the 
`Elasticsearch Client Project <http://jprante.github.com/elasticsearch-client>`_ framework.

In order to connect to an Elasticsearch node with this client, be sure it has the `Websocket transport plugin <http://jprante.github.com/elasticsearch-transport-websocket>`_ installed.

This is a prerelease and not feature-complete.

Currently the client can handle five commands:

index, delete, flush (three commands for demonstration of automatic bulk), 
and publish and subscribe (for asynchronous distributed messaging in an Elasticsearch cluster).

Example code:

::

            final String subscriberId = "mysubscriber";
            final String topic = "test";
            final URI uri = new URI("ws://localhost:9400/websocket");
            final WebSocketIngestClientFactory clientFactory = new WebSocketIngestClientFactory();

            final SubscribeRequest subscribe = subscribeRequest()
                    .topic(topic)
                    .subscriberId(subscriberId);

            final PublishRequest publish = publishRequest()
                    .topic(topic)
                    .source("Hello World");

            // open two clients

            WebSocketIngestClient subscribingClient = clientFactory.newIngestClient(uri,
                    new WebSocketIngestActionListener.Adapter() {
                        @Override
                        public void onConnect(WebSocketIngestClient client) throws IOException {
                            logger.info("sending subscribe command, channel = {}", client.channel());
                            client.subscribe(subscribe);
                        }

                        @Override
                        public void onMessage(WebSocketIngestClient client, WebSocketFrame frame) {
                            logger.info("frame received: {}", frame);
                        }
                    });
            
            WebSocketIngestClient publishingClient = clientFactory.newIngestClient(uri,
                    new WebSocketIngestActionListener.Adapter() {
                        @Override
                        public void onConnect(WebSocketIngestClient client) throws IOException {
                            logger.info("sending publish command, channel = {}", client.channel());
                            client.publish(publish);
                        }

                        @Override
                        public void onMessage(WebSocketIngestClient client, WebSocketFrame frame) {
                            logger.info("frame received: {}", frame);
                        }
                    });

            // connect both clients to node
            subscribingClient.connect().await(1000, TimeUnit.MILLISECONDS);
            publishingClient.connect().await(1000, TimeUnit.MILLISECONDS);

            // wait for publish/subscribe actions
            Thread.sleep(1000);

            // close first client
            publishingClient.close();
            publishingClient.disconnect();

            // close second client
            subscribingClient.close();
            subscribingClient.disconnect();


Logfile excerpt of two clients communicating with each other by a topic with publish/subscribe:

::

 [20:32:15,257][INFO ][test                     ] sending subscribe command, channel = [id: 0x46213544, /127.0.0.1:55036 => localhost/127.0.0.1:9400]
 [20:32:15,258][INFO ][test                     ] sending publish command (to ourselves), channel = [id: 0x46213544, /127.0.0.1:55036 => localhost/127.0.0.1:9400]  
 [20:32:15,274][INFO ][test                     ] frame received: TextWebSocketFrame(text: {"ok":true,"type":"subscribe", "data" : {"ok":true,"id":"oneclienttest"}})
 [20:32:15,275][INFO ][test                     ] frame received: TextWebSocketFrame(text: {"ok":true,"type":"publish", "data" : {"id":"hl4jHzAzTp-mPwPghSXnWA","subscribers":1}})
 [20:32:15,282][INFO ][test                     ] frame received: TextWebSocketFrame(text: {"ok":true,"type":"message","data":{"timestamp":1347042735261,"data":{"topic":"oneclienttest","message":"SGVsbG8gV29ybGQ="}}})
 [20:32:16,258][INFO ][test                     ] frame received: CloseWebSocketFrame
 [20:32:17,274][INFO ][test                     ] sending subscribe command, channel = [id: 0x5833ea04, /127.0.0.1:55037 => localhost/127.0.0.1:9400]
 [20:32:17,277][INFO ][test                     ] sending publish command, channel = [id: 0x13c2a62a, /127.0.0.1:55038 => localhost/127.0.0.1:9400]
 [20:32:17,287][INFO ][test                     ] frame received: TextWebSocketFrame(text: {"ok":true,"type":"subscribe", "data" : {"ok":true,"id":"twoclienttest"}})
 [20:32:17,288][INFO ][test                     ] frame received: TextWebSocketFrame(text: {"ok":true,"type":"publish", "data" : {"id":"0TIWOTBxSHWKMpL3_dIPDQ","subscribers":1}})
 [20:32:17,292][INFO ][test                     ] frame received: TextWebSocketFrame(text: {"ok":true,"type":"message","data":{"timestamp":1347042737278,"data":{"topic":"twoclienttest","message":"SGVsbG8gV29ybGQ="}}})
 Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 8.77 sec