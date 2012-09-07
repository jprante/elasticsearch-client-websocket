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

package org.elasticsearch.action.support;

import java.io.IOException;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.ActionResponse;
import static org.elasticsearch.action.support.PlainActionFuture.newFuture;
import org.elasticsearch.client.websocket.WebSocketClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public abstract class WebSocketAction<Request extends ActionRequest, Response extends ActionResponse> {

    protected abstract void doExecute(WebSocketClient client, Request request, ActionListener<Response> listener);

    public ActionFuture<Response> execute(WebSocketClient client, Request request) throws ElasticSearchException {
        PlainActionFuture<Response> future = newFuture();
        request.listenerThreaded(false);
        execute(client, request, future);
        return future;
    }

    public void execute(WebSocketClient client, Request request, ActionListener<Response> listener) {
        ActionRequestValidationException validationException = request.validate();
        if (validationException != null) {
            listener.onFailure(validationException);
            return;
        }
        try {
            doExecute(client, request, listener);
        } catch (Exception e) {
            listener.onFailure(e);
        }
    }

    protected XContentBuilder builder() throws IOException {
        return jsonBuilder();
    }

    /**
     * The format for an OK response is
     * <pre>
     *    {
     *        "ok" : true,
     *        "type" : [type],
     *        "data" : {
     *              [data]
     *        }
     *    }
     * </pre>
     * 
     * @param client
     * @param type
     * @param builder 
     */
    protected void responseOK(WebSocketClient client, String type, XContentBuilder builder) {
        try {
            XContentBuilder responseBuilder = builder().startObject().field("ok", true).field("type", type);
            if (builder != null) {
                responseBuilder.rawField("data", builder.bytes());
            }
            responseBuilder.endObject();
            client.send(new TextWebSocketFrame(responseBuilder.string()));
        } catch (Exception e) {
            responseError(client, type, e);
        }
    }
    
    /**
     * The format for an error response is
     * <pre>
     *    {
     *        "ok" : false,
     *        "type" : [type],
     *        "error" : [errormessage]
     *    }
     * </pre>
     *
     * @param client
     * @param type
     * @param t 
     */
    protected void responseError(WebSocketClient client, String type, Throwable t) {
        client.send(new TextWebSocketFrame("{\"ok\":false,\"type\":\"" + type + "\",\"error\":\"" + t.getMessage() + "\""));        
    }
}
