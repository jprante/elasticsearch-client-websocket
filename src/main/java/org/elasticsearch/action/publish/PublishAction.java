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

package org.elasticsearch.action.publish;

import org.elasticsearch.action.Action;
import org.elasticsearch.client.websocket.WebSocketIngestClient;

/**
 * Publish action
 * 
 * @author Jörg Prante <joergprante@gmail.com>
 */
public class PublishAction extends Action<PublishRequest, PublishResponse, PublishRequestBuilder, WebSocketIngestClient> {

    public static final PublishAction INSTANCE = new PublishAction();
    public static final String NAME = "publish";

    private PublishAction() {
        super(NAME);
    }

    @Override
    public PublishResponse newResponse() {
        return new PublishResponse();
    }

    @Override
    public PublishRequestBuilder newRequestBuilder(WebSocketIngestClient client) {
        return new PublishRequestBuilder(client);
    }
}