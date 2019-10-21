/*
 * Copyright 2016, 2019, Matt Dean
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
package org.oddcyb.items.handlers;

import com.google.gson.Gson;
import org.oddcyb.items.store.Store;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Route that adds entries to a store.
 */
public class StoreAdd implements Route
{

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
        
        Object exists = 
            this.store.add(name, this.gson.fromJson(json, Object.class));
        
        if ( exists == null )
        {
            resp.status(201);
            return "Created";
        }
        else
        {
            resp.status(409);
            return this.gson.toJson(exists);
        }
    }

}
