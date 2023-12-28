package items.store;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Store that holds typed data.
 */
public class MapStore implements Store
{
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
        return (object instanceof Map);
    }

    @Override
    public Object read(String name)
    {
        Object item = this.store.read(name);
        return (this.accepts(item)) ? item : null;
    }

    @Override
    public Object add(String name, Object value)
    {
        if ( this.accepts(value) )
        {
            Object existing = this.store.read(name);

            if ( existing != null && !this.accepts(existing) )
            {
                throw new InvalidTypeValueException(
                    "Invalid existing value for "+name);   
            }

            return this.store.add(name, value);
        }
        else
        {
            throw new InvalidTypeValueException("Invalid value for "+name);
        }
    }

    @Override
    public Object replace(String name, Object newValue)
    {
        Object existing = this.store.read(name);

        if ( this.accepts(existing) && this.accepts(newValue) )
        {
            return this.store.replace(name, newValue);
        }
        else
        {
            throw new InvalidTypeValueException("Invalid value for "+name);
        }
    }

    @Override
    public Object delete(String name)
    {
        // Check object exists first (will confirm filtered type)
        if ( this.read(name) != null )
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