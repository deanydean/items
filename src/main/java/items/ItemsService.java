package items;

import java.util.logging.Level;
import java.util.logging.Logger;

import items.handlers.StoreAdd;
import items.handlers.StoreGet;
import items.handlers.StoreReplace;
import items.store.InvalidTypeValueException;
import items.store.ListStore;
import items.store.MapStore;
import items.store.Store;

import static spark.Spark.*;

/**
 * Service that holds data units.
 */
public class ItemsService 
{
    private static final Logger LOG = 
        Logger.getLogger(ItemsService.class.getName());
    
    public static final String SERVICE_BASE = "/items";
    
    private final String serviceBase;
    private final Store store;
    
    /**
     * Create a service located at the provided base path.
     * 
     * @param serviceBase the base path for the service
     * @param store the store that holds that data for this service
     */
    public ItemsService(String serviceBase, Store store)
    {
        this.serviceBase = serviceBase;
        this.store = store;
    }
    
    /**
     * Start the service.
     */
    public void start()
    {
        String base = this.serviceBase;
        
        LOG.log(Level.INFO, "Starting data service at {0}", base);

        // Link for map types
        // /items/maps/*     <- all Map types
        link(base+"/maps", new MapStore(this.store));

        // Link for list types
        // /items/lists/*    <- all List types
        link(base+"/lists", new ListStore(this.store));

        // TODO - /items/queues/*   <- all Queue types
        // TODO - /items/deques/*   <- all Deque types
        // TODO - /items/drops/*    <- all Drop types (timeoutable, single-retrieve)

        // /items/<anything> <- anything (read-only)
        path(base, () ->
        {
            get("/*", new StoreGet(this.store));
        });

        // Handle exceptions and log errors
        exception(InvalidTypeValueException.class, (ex, req, resp) -> {
            LOG.log(Level.INFO, "Invalid type provided for {0}", 
                    req.pathInfo());
            resp.status(400); // Bad request
            resp.body("Bad Request - Invalid type");
        });
        exception(Exception.class, (ex, req, resp) -> {
            LOG.log(Level.WARNING, "Request {0} failed", req);
            LOG.log(Level.WARNING, "Request error", ex);
            resp.status(500); // Internal server error
            resp.body("Invalid server error");
        });
    }

    private static void link(String path, Store store)
    {
        path(path, () ->
        {
            get("/*", new StoreGet(store));
            put("/*", new StoreReplace(store));
            post("/*", new StoreAdd(store));
            delete("/*", (req, resp) -> store.delete(req.splat()[0]));
        });
    }

}