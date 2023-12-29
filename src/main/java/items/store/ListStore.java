package items.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Store that holds typed data.
 */
public class ListStore implements Store
{

    private static final Logger LOG = Logger.getLogger(ListStore.class.getName());
    private final Store store;

    /**
     * Create a new MapStore
     * 
     * @param store the store to hold the Items
     */
    public ListStore(Store store)
    {
        this.store = store;
    }

    /**
     * Will this store accept this object?
     * 
     * @param object the object to check
     * @return true if this store will accept this object, false if not.
     */
    public boolean accepts(Object object)
    {
        return ( object instanceof List );
    }

    @Override
    public Object get(String name)
    {
        Object item = this.store.get(name);
        return (item != null && this.accepts(item)) ? item : null;
    }

    @Override
    public Object set(String name, Object value)
    {
        Object existing = this.store.get(name);

        if ( existing != null && !this.accepts(existing) )
        {
            LOG.warning(() -> "Existing "+name+" is not valid List type: "+existing.getClass());  
        }

        if ( this.accepts(value) )
        {
            return this.store.set(name, value);
        }

        var list = (existing != null) ? (List<Object>) existing : new ArrayList<Object>();
        list.add(value);
        return this.store.set(name, list);
    }

    @Override
    public Object delete(String name)
    {
        // Check object exists first (will confirm filtered type)
        if ( this.get(name) != null )
        {
            return this.store.delete(name);
        }
        else
        {
            return null;
        }
    }

    @Override
    public Map<String,Object> search(String spec)
    {
        return this.store
                   .search(spec)
                   .entrySet()
                   .stream()
                   .filter( (e) -> this.accepts(e.getValue()) )
                   .collect( Collectors.toMap( (e) -> e.getKey(), 
                                               (e) -> e.getValue() ));
    }
}