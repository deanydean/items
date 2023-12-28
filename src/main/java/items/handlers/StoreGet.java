/*
 * Copyright 2018, 2020, Matt Dean
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
