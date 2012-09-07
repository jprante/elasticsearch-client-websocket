/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.client.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.publish.PublishRequest;
import org.elasticsearch.action.subscribe.SubscribeRequest;
import static org.elasticsearch.client.websocket.WebSocketIngestRequests.*;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.testng.annotations.Test;

public class PublishSubscribeTest {

    private final static ESLogger logger = ESLoggerFactory.getLogger("test");

    /**
     * Tests if publish/subscribes works with a single client connected.
     */
    @Test
    public void testOneClient() {
        try {
            final String subscriberId = "oneclient";
            final String topic = "oneclienttest";
            final URI uri = new URI("ws://localhost:9400/websocket");
            final WebSocketIngestClientFactory clientFactory = new WebSocketIngestClientFactory();

            final SubscribeRequest subscribe = subscribeRequest()
                    .topic(topic)
                    .subscriberId(subscriberId);

            final PublishRequest publish = publishRequest()
                    .topic(topic)
                    .source("Hello World");

            WebSocketIngestClient client = clientFactory.newIngestClient(uri,
                    new WebSocketIngestActionListener.Adapter() {
                        @Override
                        public void onConnect(WebSocketIngestClient client) throws IOException {
                            logger.info("sending subscribe command, channel = {}", client.channel());
                            client.subscribe(subscribe);
                            logger.info("sending publish command (to ourselves), channel = {}", client.channel());
                            client.publish(publish);
                        }

                        @Override
                        public void onMessage(WebSocketIngestClient client, WebSocketFrame frame) {
                            logger.info("frame received: {}", frame);                            
                        }
                    });
            client.connect().await(1000, TimeUnit.MILLISECONDS);
            Thread.sleep(1000);
            client.close();
            Thread.sleep(1000);
            client.disconnect();

            clientFactory.shutdown();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Tests if publish/subscribe work with two clients connected.
     * The two clients communicate by each other via the topic.
     */
    @Test
    public void testTwoClients() {
        try {
            final String subscriberId = "twoclients";
            final String topic = "twoclienttest";
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

            // just for safety to avoid interruption messages
            Thread.sleep(1000);

            clientFactory.shutdown();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
