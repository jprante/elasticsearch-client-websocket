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

package org.elasticsearch.websocket.action.delete;

import java.io.IOException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.WebSocketAction;
import org.elasticsearch.client.websocket.WebSocketClient;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class WebSocketDeleteAction extends WebSocketAction<DeleteRequest,DeleteResponse> {

    public static final String NAME = "delete";
    
    @Override
    protected void doExecute(WebSocketClient client, DeleteRequest request, ActionListener<DeleteResponse> listener) {
        try {
            XContentBuilder builder = 
                builder()
                .startObject()
                .field("index", request.index())
                .field("type", request.type())
                .field("id", request.id())
                .endObject();
            responseOK(client, NAME, builder);
        } catch (IOException e) {
            responseError(client, NAME, e);            
        }
    }
    
}
