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

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.flush.FlushRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.publish.PublishRequest;
import org.elasticsearch.action.subscribe.SubscribeRequest;

public class WebSocketIngestRequests {

    public static IndexRequest indexRequest() {
        return new IndexRequest();
    }

    public static DeleteRequest deleteRequest() {
        return new DeleteRequest();
    }    
    
    public static FlushRequest flushRequest() {
        return new FlushRequest();
    }

    public static PublishRequest publishRequest() {
        return new PublishRequest();
    }
    
    public static SubscribeRequest subscribeRequest() {
        return new SubscribeRequest();
    }

}
