package items.store;

import java.util.Map;

/**
 * A store of items.
 */
public interface Store
{

    public static final String SEARCH_ALL = "*";
    
    /**
     * Read an object from the store.
     * 
     * @param name the name of the object.
     * @return the object or null if the object did not exist
     */
    public Object read(String name);
    
    /**
     * Add an object to the store.
     * 
     * @param name the name of the object
     * @param value the object
     * @return the object that already exists in the store or null if the object
     * was added.
     */
    public Object add(String name, Object value);
    
    /**
     * Replace an object in the store.
     * @param name the name of the object to replace
     * @param newValue the replacement object
     * @return the object that was replaced or null if the object did not exist
     */
    public Object replace(String name, Object newValue);
    
    /**
     * Delete an object from the store.
     * 
     * @param name the name of the object
     * @return the object that was deleted or null if the object did not exist
     */
    public Object delete(String name);
    
    /**
     * Search for objects in the store.
     * 
     * @param spec the search specification
     * @return the objects that match the search spec
     */
    public Map<String,Object> search(String spec);
}
