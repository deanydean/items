/*
 * Copyright 2016 Matt Dean
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
package org.oddcyb.atoms.store;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.logging.Logger;
import org.bson.Document;

/**
 *
 */
public class MongoDBStore implements Store
{
    private static final Logger LOG = 
        Logger.getLogger(MongoDBStore.class.getName());
    
    public static final String NAME_ENTRY = "name";
    public static final String OBJECT_ENTRY = "object";
    
    private final String host;
    private final int port;
    private final String databaseName;
    private final String collectionName;
    
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;
    
    public MongoDBStore(String host, int port, String database, 
            String collection)
    {
        this.host = host;
        this.port = port;
        this.databaseName = database;
        this.collectionName = collection;
        
        this.client = new MongoClient(host, port);
        this.database = this.client.getDatabase(databaseName);
        this.collection = this.database.getCollection(host);
    }

    @Override
    public Object read(String name)
    {
        Document result = this.collection
            .find(Filters.eq(NAME_ENTRY, name))
            .first();
        
        return ( result != null ) ? result.get(OBJECT_ENTRY) : null;
    }

    @Override
    public Object add(String name, Object value) {
        // Make sure the object doesn't aleady exist
        Object existing = this.read(name);
        if ( existing != null )
        {
            return existing;
        }
        
        // Add the new entry
        Document doc = new Document();
        doc.append(NAME_ENTRY, name);
        doc.append(OBJECT_ENTRY, value);
        this.collection.insertOne(doc);
        
        return null;
    }

    @Override
    public Object replace(String name, Object newValue) {
        Object existing = this.read(name);
        if ( existing == null )
        {
            return null;
        }
        
        this.collection.updateOne(
            Filters.eq(NAME_ENTRY, name),
            new Document("$SET", new Document(OBJECT_ENTRY, newValue))
        );
        
        return existing;
    }

    @Override
    public Object delete(String name) {
        Object existing = this.read(name);
        
        this.collection.deleteOne(Filters.eq(NAME_ENTRY, name));
    
        return existing;
    }
    
}
