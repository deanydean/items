package items.store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A Store that is held on the heap.
 */
public class HeapStore implements Store
{
    private static final Logger LOG =
        Logger.getLogger(HeapStore.class.getName());

    private final Map<String,Object> dataMap = new ConcurrentHashMap<>();

    @Override
    public Object read(String name)
    {
        return this.dataMap.get(name);
    }
    
    @Override
    public Object add(String name, Object value)
    {
        return this.dataMap.putIfAbsent(name, value);
    }
    
    @Override
    public Object replace(String name, Object newValue)
    {
        return this.dataMap.replace(name, newValue);
    }

    @Override
    public Object delete(String name)
    {
        return this.dataMap.remove(name);
    }
    
    @Override
    public Map<String,Object> search(String spec)
    {
        Map<String,Object> result = new HashMap<>();

        if ( spec.equalsIgnoreCase(Store.SEARCH_ALL) )
        {
            this.dataMap.entrySet().forEach( (entry) -> {
                result.put(entry.getKey(), entry.getValue());
            });
        }
        else
        {
            // TODO match spec
            LOG.warning("Non-all search specs not implemented");
        }
        
        return result;
    }
}
