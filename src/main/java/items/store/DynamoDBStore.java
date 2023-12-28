package items.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.google.gson.Gson;

/**
 * A Store that is held in an AWS DynamoDB table.
 */
public class DynamoDBStore implements Store
{
    private static final Logger LOG = 
        Logger.getLogger(DynamoDBStore.class.getName());

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

        Map<String,AttributeValue> item = this.ddb.getItem(request).getItem();

        if ( item != null )
        {
            return itemToObject(item);
        }

        // Not Found
        return null;
    }

    @Override
    public Object add(String name, Object value)
    {
        LOG.info( () -> "Adding "+name+"->"+value );
        if ( value == null )
        {
            throw new NullPointerException();
        }

        Object existing = this.read(name);
        if ( existing != null )
        {
            return existing;
        }

        Map<String,AttributeValue> item = objectToItem(name, value);

        LOG.info( () -> "Adding "+item+" to "+this.tableName );
        this.ddb.putItem(this.tableName, item);

        return null;
    }

    @Override
    public Object replace(String name, Object newValue)
    {
        LOG.info( () -> "Replacing "+name+"->"+newValue );
        if ( newValue == null )
        {
            throw new NullPointerException();
        }

        Object existing = this.delete(name);
        if ( existing == null )
        {
            return null;
        }

        this.add(name, newValue);

        return existing;
    }

    @Override
    public Object delete(String name)
    {
        Map<String,AttributeValue> item = new HashMap<>();
        item.put(KEY_NAME, new AttributeValue(name));

        var deleteResult = ddb.deleteItem(this.tableName, item, "ALL_OLD");
        return itemToObject(deleteResult.getAttributes());
    }

    @Override
    public Map<String, Object> search(String spec)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public static Object itemToObject(Map<String,AttributeValue> item)
    {
        var type = item.get(KEY_TYPE).getS();

        if ( type.equalsIgnoreCase(TYPE_MAP) )
        {
            return item.entrySet()
                       .stream()
                       .filter( (e) -> !e.getKey().equalsIgnoreCase(KEY_NAME) &&
                                       !e.getKey().equalsIgnoreCase(KEY_TYPE) )
                       .collect(Collectors.toMap( (e) -> e.getKey(), 
                                                  (e) -> valueToString(e.getValue()) ) );
        }
        else if ( type.equalsIgnoreCase(TYPE_LIST) )
        {
            var value = item.get(KEY_VALUE);
            return value.getSS();
        }
        else if ( type.equalsIgnoreCase(TYPE_OBJECT) )
        {
            return stringToObject(valueToString(item.get(KEY_VALUE)));
        }
        else
        {
            return "UNKNOWN DATA";
        }
    }

    public static Map<String,AttributeValue> objectToItem(String name, Object value)
    {
        Map<String,AttributeValue> item = new HashMap<>();
        item.put(KEY_NAME, new AttributeValue(name));

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

        return item;
    }

    public static String objectToString(Object object)
    {
        return new Gson().toJson(object);
    }

    public static Object stringToObject(String str)
    {
        return new Gson().fromJson(str, Map.class);
    }

    public static String valueToString(AttributeValue value)
    {
        return value.getS().replaceAll("^\"|\"$", "");
    }
}