/*
 * Copyright 2020, Matt Dean
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oddcyb.items.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.google.gson.Gson;

/**
 * A Store that is helf in an AWS DynamoDB
 */
public class DynamoDBStore implements Store
{

    private static final String KEY_NAME = "item-name";
    private static final String KEY_VALUE = "item-value";
    private static final String KEY_TYPE = "item-type";

    private static final String TYPE_MAP = "map";
    private static final String TYPE_LIST = "list";
    private static final String TYPE_OBJECT = "obj";

    private final String tableName;
    private final AmazonDynamoDB ddb;

    public DynamoDBStore(String tableName)
    {
        this.tableName = tableName;
        this.ddb = AmazonDynamoDBClientBuilder.defaultClient();
    }

    @Override
    public Object read(String name)
    {
        Map<String,AttributeValue> key = new HashMap<>();
        key.put(KEY_NAME, new AttributeValue(name));

        GetItemRequest request = 
            new GetItemRequest().withKey(key)
                                .withTableName(this.tableName);

        try
        {
            Map<String,AttributeValue> item = this.ddb.getItem(request).getItem();
            return item.get(KEY_VALUE);
        }
        catch ( ResourceNotFoundException rnfe )
        {
            // Object doesn't exist
            return null;
        }
    }

    @Override
    public Object add(String name, Object value)
    {
        Map<String,AttributeValue> item = new HashMap<>();
        item.put(KEY_NAME, new AttributeValue(name));

        Object existing = this.read(name);
        if ( existing != null )
        {
            return existing;
        }

        if ( value instanceof Map )
        {
            item.put(KEY_TYPE, new AttributeValue(TYPE_MAP));
            ((Map<String,Object>) value).forEach( (k,v) -> {
                item.put(k, new AttributeValue(objectToString(v)));
            });
        }
        else if ( value instanceof List )
        {
            item.put(KEY_TYPE, new AttributeValue(TYPE_LIST));
            List<String> values = 
                ((List<Object>) value).stream()
                              .map( (v) -> objectToString(v) )
                              .collect(Collectors.toList());
            item.put(KEY_VALUE, new AttributeValue(values));
        }
        else
        {
            item.put(KEY_TYPE, new AttributeValue(TYPE_OBJECT));
            item.put(KEY_VALUE, new AttributeValue(objectToString(value)));
        }

        return null;
    }

    @Override
    public Object replace(String name, Object newValue)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object delete(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> search(String spec)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public static String objectToString(Object object)
    {
        return new Gson().toJson(object);
    }

    public static Object stringToObject(String str)
    {
        return new Gson().fromJson(str, Map.class);
    }
}