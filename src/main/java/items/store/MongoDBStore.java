package items.store;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.Map;
import org.bson.Document;

public class MongoDBStore implements Store
{   
    public static final String NAME_ENTRY = "name";
    public static final String OBJECT_ENTRY = "object";
    
    private final String databaseName;
    
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;
    
    public MongoDBStore(String host, int port, String database, 
            String collection)
    {
        this.databaseName = database;
        
        this.client = new MongoClient(host, port);
        this.database = this.client.getDatabase(databaseName);
        this.collection = this.database.getCollection(host);
    }

    @Override
    public Object get(String name)
    {
        Document result = this.collection
            .find(Filters.eq(NAME_ENTRY, name))
            .first();
        
        return ( result != null ) ? result.get(OBJECT_ENTRY) : null;
    }

    @Override
    public Object set(String name, Object value) {
        Object existing = this.get(name);
        if ( existing != null )
        {
            this.collection.updateOne(
                Filters.eq(NAME_ENTRY, name),
                new Document("$SET", new Document(OBJECT_ENTRY, value))
            );
        } else {
            // Add the new entry
            Document doc = new Document();
            doc.append(NAME_ENTRY, name);
            doc.append(OBJECT_ENTRY, value);
            this.collection.insertOne(doc);
        }

        return existing;
    }

    @Override
    public Object delete(String name) {
        Object existing = this.get(name);
        
        this.collection.deleteOne(Filters.eq(NAME_ENTRY, name));
    
        return existing;
    }
  
    @Override
    public Map<String,Object> search(String spec)
    {
        // TODO implement mongo search
        return null;
    }
}
