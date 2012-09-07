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
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;

/**
 * A factory for creating WebSocket ingest clients.
 * 
 * @author Jörg Prante <joergprante@gmail.com>
 */
public class WebSocketIngestClientFactory  {

    private NioClientSocketChannelFactory socketChannelFactory = new NioClientSocketChannelFactory(
            Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool());
    
    /**
     * Create a new WebSocket ingest client
     *
     * @param url URL to connect to.
     * @param listener Callback interface to receive events
     * @return  WebSocket ingest client
     */
    public WebSocketIngestClient newIngestClient(final URI url, final WebSocketIngestActionListener listener) {
        String protocol = url.getScheme();
        if (!protocol.equals("ws") && !protocol.equals("wss")) {
            throw new IllegalArgumentException("unsupported protocol: " + protocol);
        }
        final ClientBootstrap bootstrap = new ClientBootstrap(socketChannelFactory);
        final WebSocketIngestClient client = new WebSocketIngestClient();
        final WebSocketIngestClientHandler clientHandler = new WebSocketIngestClientHandler(bootstrap, url, client, listener);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new HttpResponseDecoder());
                pipeline.addLast("encoder", new HttpRequestEncoder());
                pipeline.addLast("ws-handler", clientHandler);
                return pipeline;
            }
        });
        client.setHandler(clientHandler);
        return client;
    }
    
    public void shutdown() {        
        socketChannelFactory.releaseExternalResources();
    }

}
