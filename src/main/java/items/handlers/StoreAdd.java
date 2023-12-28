package items.handlers;

import com.google.gson.Gson;

import items.store.Store;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Route that adds entries to a store.
 */
public class StoreAdd implements Route
{

    private static final String QUERY_PARAM_MODE_ADD = "add";
    private static final String QUERY_PARAM_MODE_REPLACE = "replace";

    private final Store store;
    private final Gson gson = new Gson();
    
    public StoreAdd(Store store)
    {
        this.store = store;
    }
    
    @Override
    public Object handle(Request req, Response resp) throws Exception
    {
        String name = req.splat()[0];
        String json = req.body();
        String modeParam = req.queryParamOrDefault("mode", QUERY_PARAM_MODE_ADD);

        Object object = this.gson.fromJson(json, Object.class);
        Object result = null;

        if ( modeParam.equalsIgnoreCase(QUERY_PARAM_MODE_REPLACE) )
        {
            this.store.replace(name, object);
            result = "Replaced";
        }
        else
        {
            Object existing = this.store.add(name, object);

            if ( existing != null )
            {
                resp.status(409);
                return this.gson.toJson(existing);
            }

            result = "Added";
        }

        resp.status(201);
        return result;
    }

}
