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
                            logger.info("subscriber received a frame: {}", frame);
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
                            logger.info("publisher received a frame: {}", frame);
                        }
                    });

            // connect both clients to node
			
            subscribingClient.connect().await(1000, TimeUnit.MILLISECONDS);
            // wait for subscribe
            Thread.sleep(1000);

            publishingClient.connect().await(1000, TimeUnit.MILLISECONDS);
            // wait for publish
            Thread.sleep(1000);

            // close first client
            publishingClient.close();
            publishingClient.disconnect();

            // close second client
            subscribingClient.close();
            subscribingClient.disconnect();


Logfile example of two independent clients communicating with each other by a topic with publish/subscribe:

::

 [21:31:40,801][INFO ][test                     ] sending subscribe command, channel = [id: 0x7588262d, /127.0.0.1:55384 => localhost/127.0.0.1:9400]
 [21:31:40,813][INFO ][test                     ] subscriber received a frame: TextWebSocketFrame(text: {"ok":true,"type":"subscribe", "data" : {"ok":true,"id":"test"}})
 [21:31:41,803][INFO ][test                     ] sending publish command, channel = [id: 0x7a783e0d, /127.0.0.1:55385 => localhost/127.0.0.1:9400]
 [21:31:41,813][INFO ][test                     ] publisher received a frame: TextWebSocketFrame(text: {"ok":true,"type":"publish", "data" : {"id":"EpDeB7MsQmu5d8s_gvgvSg","subscribers":1}})
 [21:31:41,816][INFO ][test                     ] subscriber received a frame: TextWebSocketFrame(text: {"ok":true,"type":"message","data":{"timestamp":1347046301804,"data":{"topic":"test","message":"Hello World"}}})