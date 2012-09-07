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

import org.elasticsearch.action.Action;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.WebSocketActionModule;
import org.elasticsearch.action.flush.FlushAction;
import org.elasticsearch.action.flush.FlushRequest;
import org.elasticsearch.action.flush.FlushRequestBuilder;
import org.elasticsearch.action.flush.FlushResponse;
import org.elasticsearch.action.publish.PublishAction;
import org.elasticsearch.action.publish.PublishRequest;
import org.elasticsearch.action.publish.PublishRequestBuilder;
import org.elasticsearch.action.publish.PublishResponse;
import org.elasticsearch.action.subscribe.SubscribeAction;
import org.elasticsearch.action.subscribe.SubscribeRequest;
import org.elasticsearch.action.subscribe.SubscribeRequestBuilder;
import org.elasticsearch.action.subscribe.SubscribeResponse;
import org.elasticsearch.action.support.WebSocketAction;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.support.AbstractIngestClient;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketIngestClient extends AbstractIngestClient
        implements WebSocketClient {

    private WebSocketActionModule actions = new WebSocketActionModule();
    private WebSocketIngestClientHandler handler;

    public WebSocketIngestClient setHandler(WebSocketIngestClientHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public Channel channel() {
        return handler.channel();
    }

    @Override
    public ChannelFuture connect() {
        return handler.connect();
    }

    @Override
    public ChannelFuture disconnect() {
        return handler.disconnect();
    }

    @Override
    public ChannelFuture send(WebSocketFrame frame) {
        return handler.send(frame);
    }

    @Override
    public void close() {
        handler.send(new CloseWebSocketFrame());
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response>, C extends Client> ActionFuture<Response> execute(Action<Request, Response, RequestBuilder, C> action, Request request) {
        WebSocketAction<Request, Response> websocketAction = actions.getAction(action.name());
        return websocketAction.execute(this, request);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response>, C extends Client> void execute(Action<Request, Response, RequestBuilder, C> action, Request request, ActionListener<Response> listener) {
        WebSocketAction<Request, Response> websocketAction = actions.getAction(action.name());
        websocketAction.execute(this, request, listener);
    }

    /**
     * Send a flush request to the server.
     */
    public ActionFuture<FlushResponse> flush(FlushRequest request) {
        return execute(FlushAction.INSTANCE, request);
    }

    /**
     * Send a flush request to the server.
     */
    public void flush(FlushRequest request, ActionListener<FlushResponse> listener) {
        execute(FlushAction.INSTANCE, request, listener);
    }

    /**
     * Send a flush request to the server.
     */
    public FlushRequestBuilder prepareFlush() {
        return new FlushRequestBuilder(this);
    }

    /**
     * Send a publish request to the server.
     */
    public ActionFuture<PublishResponse> publish(PublishRequest request) {
        return execute(PublishAction.INSTANCE, request);
    }

    /**
     * Send a publish request to the server.
     */
    public void publish(PublishRequest request, ActionListener<PublishResponse> listener) {
        execute(PublishAction.INSTANCE, request, listener);
    }

    /**
     * Send a publish request to the server.
     */
    public PublishRequestBuilder preparePublish() {
        return new PublishRequestBuilder(this);
    }

    /**
     * Send a subscribe request to the server.
     */
    public ActionFuture<SubscribeResponse> subscribe(SubscribeRequest request) {
        return execute(SubscribeAction.INSTANCE, request);
    }

    /**
     * Send a subscribe request to the server.
     */
    public void subscribe(SubscribeRequest request, ActionListener<SubscribeResponse> listener) {
        execute(SubscribeAction.INSTANCE, request, listener);
    }

    /**
     * Send a subscribe request to the server.
     */
    public SubscribeRequestBuilder prepareSubscribe() {
        return new SubscribeRequestBuilder(this);
    }
}
