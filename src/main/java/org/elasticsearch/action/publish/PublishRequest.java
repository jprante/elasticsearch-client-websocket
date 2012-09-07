/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this 
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

import com.google.common.base.Charsets;
import java.io.IOException;
import java.util.Map;
import org.elasticsearch.ElasticSearchGenerationException;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.single.custom.SingleCustomOperationRequest;
import org.elasticsearch.client.IngestRequests;
import org.elasticsearch.common.Required;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * Publish request
 * 
 * @author Jörg Prante <joergprante@gmail.com>
 */
public class PublishRequest extends SingleCustomOperationRequest {

    private String topic;
    
    private BytesReference source;
    private boolean sourceUnsafe;

    private XContentType contentType = IngestRequests.INDEX_CONTENT_TYPE;
    
    
    public PublishRequest() {
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = super.validate();
        return validationException;
    }

    /**
     * Should the listener be called on a separate thread if needed.
     */
    @Override
    public PublishRequest listenerThreaded(boolean threadedListener) {
        super.listenerThreaded(threadedListener);
        return this;
    }

    /**
     * Controls if the operation will be executed on a separate thread when executed locally. Defaults
     * to <tt>true</tt> when running in embedded mode.
     */
    @Override
    public PublishRequest operationThreaded(boolean threadedOperation) {
        super.operationThreaded(threadedOperation);
        return this;
    }

    public String topic() {
        return topic;
    }
    
    public PublishRequest topic(String topic) {
        this.topic = topic;
        return this;
    }
    
    public BytesReference source() {
        return source;
    }

    public BytesReference safeSource() {
        if (sourceUnsafe) {
            source = source.copyBytesArray();
        }
        return source;
    }

    public Map<String, Object> sourceAsMap() {
        return XContentHelper.convertToMap(source, false).v2();
    }

    /**
     * Index the Map as a {@link org.elasticsearch.client.IngestRequests#INDEX_CONTENT_TYPE}.
     *
     * @param source The map to index
     */
    @Required
    public PublishRequest source(Map source) throws ElasticSearchGenerationException {
        return source(source, contentType);
    }

    /**
     * Index the Map as the provided content type.
     *
     * @param source The map to index
     */
    @Required
    public PublishRequest source(Map source, XContentType contentType) throws ElasticSearchGenerationException {
        try {
            XContentBuilder builder = XContentFactory.contentBuilder(contentType);
            builder.map(source);
            return source(builder);
        } catch (IOException e) {
            throw new ElasticSearchGenerationException("Failed to generate [" + source + "]", e);
        }
    }

    /**
     * Sets the document source to index.
     * <p/>
     * <p>Note, its preferable to either set it using {@link #source(org.elasticsearch.common.xcontent.XContentBuilder)}
     * or using the {@link #source(byte[])}.
     */
    @Required
    public PublishRequest source(String source) {
        this.source = new BytesArray(source.getBytes(Charsets.UTF_8));
        this.sourceUnsafe = false;
        return this;
    }

    /**
     * Sets the content source to index.
     */
    @Required
    public PublishRequest source(XContentBuilder sourceBuilder) {
        source = sourceBuilder.bytes();
        sourceUnsafe = false;
        return this;
    }

    @Required
    public PublishRequest source(String field1, Object value1) {
        try {
            XContentBuilder builder = XContentFactory.contentBuilder(contentType);
            builder.startObject().field(field1, value1).endObject();
            return source(builder);
        } catch (IOException e) {
            throw new ElasticSearchGenerationException("Failed to generate", e);
        }
    }

    @Required
    public PublishRequest source(String field1, Object value1, String field2, Object value2) {
        try {
            XContentBuilder builder = XContentFactory.contentBuilder(contentType);
            builder.startObject().field(field1, value1).field(field2, value2).endObject();
            return source(builder);
        } catch (IOException e) {
            throw new ElasticSearchGenerationException("Failed to generate", e);
        }
    }

    @Required
    public PublishRequest source(String field1, Object value1, String field2, Object value2, String field3, Object value3) {
        try {
            XContentBuilder builder = XContentFactory.contentBuilder(contentType);
            builder.startObject().field(field1, value1).field(field2, value2).field(field3, value3).endObject();
            return source(builder);
        } catch (IOException e) {
            throw new ElasticSearchGenerationException("Failed to generate", e);
        }
    }

    @Required
    public PublishRequest source(String field1, Object value1, String field2, Object value2, String field3, Object value3, String field4, Object value4) {
        try {
            XContentBuilder builder = XContentFactory.contentBuilder(contentType);
            builder.startObject().field(field1, value1).field(field2, value2).field(field3, value3).field(field4, value4).endObject();
            return source(builder);
        } catch (IOException e) {
            throw new ElasticSearchGenerationException("Failed to generate", e);
        }
    }

    /**
     * Sets the message in bytes form.
     */
    public PublishRequest source(BytesReference source, boolean unsafe) {
        this.source = source;
        this.sourceUnsafe = unsafe;
        return this;
    }

    /**
     * Sets the message in bytes form.
     */
    public PublishRequest source(byte[] source) {
        return source(source, 0, source.length);
    }

    /**
     * Sets the document to index in bytes form (assumed to be safe to be used from different
     * threads).
     *
     * @param source The source to index
     * @param offset The offset in the byte array
     * @param length The length of the data
     */
    @Required
    public PublishRequest source(byte[] source, int offset, int length) {
        return source(source, offset, length, false);
    }

    /**
     * Sets the document to index in bytes form.
     *
     * @param source The source to index
     * @param offset The offset in the byte array
     * @param length The length of the data
     * @param unsafe Is the byte array safe to be used form a different thread
     */
    @Required
    public PublishRequest source(byte[] source, int offset, int length, boolean unsafe) {
        this.source = new BytesArray(source, offset, length);
        this.sourceUnsafe = unsafe;
        return this;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        topic = in.readString();
        source = in.readBytesReference();
        sourceUnsafe = false;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeString(topic);
        out.writeBytesReference(source);
    }

    @Override
    public String toString() {
        String sSource = "_na_";
        try {
            sSource = XContentHelper.convertToJson(source, false);
        } catch (Exception e) {
            // ignore
        }
        return "publish {["+topic+"] " + sSource + "}";
    }
}