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
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionResponse;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * Listener for WebSocket actions.
 *
 * @author Jörg Prante <joergprante@gmail.com>
 */
public interface WebSocketIngestActionListener<Response extends ActionResponse> 
    extends ActionListener<Response> {

    /**
     * Called when the client is connected to the server
     *
     * @param client Current client used to connect
     */
    void onConnect(WebSocketIngestClient client) throws IOException;

    /**
     * Called when the client got disconnected from the server
     *
     * @param client Current client that was disconnected
     */
    void onDisconnect(WebSocketIngestClient client) throws IOException;

    /**
     * Called when a message arrives from the server
     *
     * @param client the connected client
     * @param frame the data received from server
     */
    void onMessage(WebSocketIngestClient client, WebSocketFrame frame) throws IOException;
    
    /**
     * An adapter class for the websocket action listener
     * 
     * @param <Response> 
     */
    static class Adapter<Response extends ActionResponse> implements WebSocketIngestActionListener<Response> {

        @Override
        public void onConnect(WebSocketIngestClient client) throws IOException {
        }

        @Override
        public void onDisconnect(WebSocketIngestClient client) throws IOException {
        }

        @Override
        public void onMessage(WebSocketIngestClient client, WebSocketFrame frame) throws IOException {
        }

        @Override
        public void onResponse(Response response) {
        }

        @Override
        public void onFailure(Throwable e) {
        }
        
    }
}
