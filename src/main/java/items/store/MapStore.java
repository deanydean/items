package items.store;

import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Store that holds typed data.
 */
public class MapStore implements Store
{
    private static final Logger LOG = Logger.getLogger(MapStore.class.getName());

    private final Store store;

    /**
     * Create a new MapStore
     * 
     * @param store the store to hold the Items
     */
    public MapStore(Store store)
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
        return Map.class.isAssignableFrom(object.getClass());
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
        if ( value != null && this.accepts(value) )
        {
            Object existing = this.store.get(name);

            if ( existing != null && !this.accepts(existing) )
            {
                LOG.warning(() -> "Existing "+name+" is not valid Map type: "+existing.getClass());  
            }

            return this.store.set(name, value);
        }
        else
        {
            LOG.warning(() -> "Unable to add "+name+" is not a Map: "+value);
            throw new InvalidTypeValueException("Invalid value for "+name);
        }
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
    public Map<String, Object> search(String spec)
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