package items;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.List;

import items.authorizers.Authorizer;
import items.handlers.StoreSet;
import items.handlers.StoreGet;
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
    private final Authorizer<String> authorizer;
    
    /**
     * Create a service located at the provided base path.
     * 
     * @param serviceBase the base path for the service
     * @param store the store that holds that data for this service
     */
    public ItemsService(String serviceBase, Store store, Authorizer<String> authorizer){
        this.serviceBase = serviceBase;
        this.store = store;
        this.authorizer = authorizer;
    }
    
    /**
     * Start the service.
     */
    public void start()
    {
        String base = this.serviceBase;
        
        LOG.log(Level.INFO, "Starting data service at {0}", base);

        if ( authorizer != null ) {
            before((req, res) -> {
                var key = req.headers("x-api-key");
                if ( key == null || ! this.authorizer.isAuthorized(key) ){
                    halt(401, "Forbidden");
                }
            });
        }

        // Link for map types
        // /items/maps/*     <- all Map types
        link(base+"/maps", new MapStore(this.store), Map.class);

        // Link for list types
        // /items/lists/*    <- all List types
        link(base+"/lists", new ListStore(this.store), List.class);

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
            LOG.log(Level.INFO, "Invalid type provided for {0} : {1}", 
                    new Object[] { req.pathInfo(), ex });
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

    private static <T> void link(String path, Store store, Class<T> type)
    {
        path(path, () ->
        {
            get("/*", new StoreGet(store));
            put("/*", new StoreSet<T>(store, type));
            post("/*", new StoreSet<T>(store, type));
            delete("/*", (req, resp) -> store.delete(req.splat()[0]));
        });
    }

}