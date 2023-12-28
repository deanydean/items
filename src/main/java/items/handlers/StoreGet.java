package items.handlers;

import com.google.gson.Gson;
import items.store.Store;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Route that gets entries from a store.
 */
public class StoreGet implements Route
{
    private final Store store;
    private final Gson gson = new Gson();
    
    public StoreGet(Store store)
    {
        this.store = store;
    }

    @Override
    public String handle(Request req, Response resp) throws Exception
    {
        String[] splatParams = req.splat();
        
        if ( splatParams.length == 0 )
        {
            // Get all
            return gson.toJson(this.store.search(Store.SEARCH_ALL));
        }
        else
        {
            // Get the named object
            Object obj = this.store.read(splatParams[0]);

            if ( obj != null )
            {
                return gson.toJson(obj);
            }
            else
            {
                resp.status(404);
                return "Not Found";
            }
        }
    }

}
