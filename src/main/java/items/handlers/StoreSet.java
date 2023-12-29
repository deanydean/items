package items.handlers;

import com.google.gson.Gson;

import items.store.Store;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Route that adds entries to a store.
 */
public class StoreSet<T> implements Route
{

    private final Store store;
    private final Gson gson = new Gson();
    private final Class<T> type;
    
    public StoreSet(Store store, Class<T> type)
    {
        this.store = store;
        this.type = type;
    }
    
    @Override
    public Object handle(Request req, Response resp) throws Exception
    {
        String name = req.splat()[0];
        String json = req.body();

        var object = this.gson.fromJson(json, type);
        var existing = this.store.set(name, object);

        if ( existing == null) {
            resp.status(201);
            return "";
        } else {
            return existing;
        }
    }

}
