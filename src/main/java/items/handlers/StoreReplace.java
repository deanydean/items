package items.handlers;

import com.google.gson.Gson;
import items.store.Store;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Route that replaces entries in a store.
 */
public class StoreReplace implements Route
{

    private final Store store;
    private final Gson gson = new Gson();
    
    public StoreReplace(Store store)
    {
        this.store = store;
    }

    @Override
    public Object handle(Request req, Response resp) throws Exception 
    {
        String name = req.splat()[0];
        String json = req.body();
        
        Object replaced =
            this.store.replace(name, this.gson.fromJson(json, Object.class));

        if ( replaced != null )
        {
            return this.gson.toJson(replaced);
        }
        else
        {
            resp.status(404);
            return "Not found";
        }
    }
    
}
