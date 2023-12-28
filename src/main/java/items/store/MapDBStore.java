package items.store;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * A store that is held in MapDB.
 */
public class MapDBStore implements Store
{
    
    private final DB db;
    private final String mapName;
    
    public MapDBStore(String dbFile, String mapName)
    {
        this.db = DBMaker
            .fileDB(dbFile)
            .closeOnJvmShutdown()
            .make();
        this.mapName = mapName;
    }

    @Override
    public Object read(String name)
    {
        return this.getMap().get(name);
    }

    @Override
    public Object add(String name, Object value)
    {
        return this.getMap().putIfAbsent(name, value);
    }

    @Override
    public Object replace(String name, Object newValue)
    {
        return this.getMap().replace(name, newValue);
    }

    @Override
    public Object delete(String name)
    {
        return this.getMap().remove(name);
    }
    
    @Override
    public Map<String, Object> search(String spec)
    {
        Map<String,Object> result = new HashMap<>();
        
        if ( spec.equalsIgnoreCase(Store.SEARCH_ALL) )
        {
            getMap().entrySet().forEach((entry) -> {
                result.put(entry.getKey(), entry.getValue());
            });
        }

        // TODO - Use spec to find results
        
        return result;
    }
    
    private ConcurrentMap<String,Object> getMap()
    {
        return this.db
            .hashMap(this.mapName, Serializer.STRING, Serializer.JAVA)
            .createOrOpen();
    }
}
