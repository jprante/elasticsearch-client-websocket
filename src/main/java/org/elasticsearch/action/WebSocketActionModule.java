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

package org.elasticsearch.action;

import com.google.common.collect.Maps;
import java.util.Map;
import org.elasticsearch.action.delete.DeleteAction;
import org.elasticsearch.action.flush.FlushAction;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.publish.PublishAction;
import org.elasticsearch.action.subscribe.SubscribeAction;
import org.elasticsearch.action.support.WebSocketAction;
import org.elasticsearch.websocket.action.delete.WebSocketDeleteAction;
import org.elasticsearch.websocket.action.flush.WebSocketFlushAction;
import org.elasticsearch.websocket.action.index.WebSocketIndexAction;
import org.elasticsearch.websocket.action.publish.WebSocketPublishAction;
import org.elasticsearch.websocket.action.subscribe.WebSocketSubscribeAction;

public class WebSocketActionModule {

    private final static Map<String, ActionEntry> actions = Maps.newHashMap();

    /**
     * Register our websocket actions.
     */
    static {
        registerAction(IndexAction.INSTANCE, new WebSocketIndexAction());
        registerAction(DeleteAction.INSTANCE, new WebSocketDeleteAction());
        registerAction(FlushAction.INSTANCE, new WebSocketFlushAction());
        registerAction(PublishAction.INSTANCE, new WebSocketPublishAction());
        registerAction(SubscribeAction.INSTANCE, new WebSocketSubscribeAction());
    }
        
    /**
     * Registers an action.
     *
     * @param action                  The action type.
     * @param websocketAction         The websocket action implementing the actual action.
     * @param supportWebSocketActions Any support actions that are needed by the websocket action.
     * @param <Request>               The request type.
     * @param <Response>              The response type.
     */
    private static <Request extends ActionRequest, Response extends ActionResponse> 
            void registerAction(GenericAction<Request, Response> action, WebSocketAction<Request, Response> websocketAction, Class... supportWebSocketActions) {
        actions.put(action.name(), new ActionEntry(action, websocketAction, supportWebSocketActions));
    }
        
    public <Request extends ActionRequest, Response extends ActionResponse> WebSocketAction<Request, Response> getAction(String action) {
        return actions.get(action).websocketAction;
    }

    static class ActionEntry<Request extends ActionRequest, Response extends ActionResponse> {
        public final GenericAction<Request, Response> action;
        public final WebSocketAction<Request, Response> websocketAction;
        public final Class[] supportWebSocketActions;

        ActionEntry(GenericAction<Request, Response> action, WebSocketAction<Request, Response> websocketAction, Class... supportWebSocketActions) {
            this.action = action;
            this.websocketAction = websocketAction;
            this.supportWebSocketActions = supportWebSocketActions;
        }
    }
    
}
