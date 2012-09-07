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

import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IngestRequests;
import org.elasticsearch.client.websocket.WebSocketIngestActionListener;
import org.elasticsearch.client.websocket.WebSocketIngestClient;
import org.elasticsearch.client.websocket.WebSocketIngestClientFactory;
import org.elasticsearch.client.websocket.WebSocketIngestRequests;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.testng.annotations.Test;

public class IndexTest {

    private final static ESLogger logger = ESLoggerFactory.getLogger("test");

    
    /**
     * Test a single indexing.
     */
    @Test
    public void testIndex() {
        try {
            final WebSocketIngestClientFactory factory = new WebSocketIngestClientFactory();

            WebSocketIngestClient client = factory.newIngestClient(new URI("ws://localhost:9400/websocket"),
                    new WebSocketIngestActionListener.Adapter<IndexResponse>() {
                        @Override
                        public void onConnect(WebSocketIngestClient client) {
                            try {
                                IndexRequest request = 
                                        IngestRequests.indexRequest("test2")
                                        .type("test").id("1")
                                        .source("field1", "value1", "field2", "value2");
                                logger.info("sending index request {}", request.toString());
                                client.index(request, this);
                                // optional flush
                                client.flush(WebSocketIngestRequests.flushRequest());
                            } catch (Exception e) {
                                onFailure(e);
                            }
                        }

                        @Override
                        public void onDisconnect(WebSocketIngestClient client) {
                            logger.info("disconnected");
                        }


                        @Override
                        public void onFailure(Throwable t) {
                            logger.error(t.getMessage(), t);
                        }

                        @Override
                        public void onResponse(IndexResponse response) {
                            logger.info("got response: {}", response.toString());
                        }
                    });
            client.connect().await(1000, TimeUnit.MILLISECONDS);
            Thread.sleep(1000);
            logger.info("closing client");
            client.close();
            Thread.sleep(1000);
            client.disconnect();
            factory.shutdown();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * Test the "index" operation on WebSocket that is operating
     * automatically in bulk mode.
     */
    
    @Test
    public void testBulk() {
        try {
            final WebSocketIngestClientFactory factory = new WebSocketIngestClientFactory();

            WebSocketIngestClient client = factory.newIngestClient(new URI("ws://localhost:9400/websocket"),
                    new WebSocketIngestActionListener.Adapter<IndexResponse>() {
                        @Override
                        public void onConnect(WebSocketIngestClient client) {
                            try {
                                logger.info("sending requests in bulk mode...");
                                for (int i = 0; i < 250; i++) {
                                     IndexRequest request = 
                                        IngestRequests.indexRequest("test")
                                        .type("test").id(Integer.toString(i))
                                        .source("field1", "value" + i, "field2", "value" + i);
                                     client.index(request, this);
                                }
                                client.flush(WebSocketIngestRequests.flushRequest()); // because the action listener is missing here, we do not get a message back for the last 50 docs
                            } catch (Exception e) {
                                onFailure(e);
                            }
                        }

                        @Override
                        public void onMessage(WebSocketIngestClient client, WebSocketFrame frame) {
                            logger.info("frame received: {}", frame);
                        }

                        @Override
                        public void onResponse(IndexResponse response) {
                            logger.info("got response: {}", response.toString());
                        }
                    });
            client.connect().await(1000, TimeUnit.MILLISECONDS);
             Thread.sleep(1000); // time for bulk indexing
            logger.info("closing client");
            client.close();
            Thread.sleep(1000);
            client.disconnect();
            factory.shutdown();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

}
