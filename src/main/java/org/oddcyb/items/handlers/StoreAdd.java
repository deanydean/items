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

import java.util.ArrayList;
import java.util.List;

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

    private static final String QUERY_PARAM_MODE_ADD = "add";
    private static final String QUERY_PARAM_MODE_REPLACE = "replace";
    private static final String QUERY_PARAM_MODE_APPEND = "append";

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
        Object existing = this.store.read(name);

        Object result = null;

        if ( modeParam.equalsIgnoreCase(QUERY_PARAM_MODE_REPLACE) )
        {
            this.store.replace(name, object);
            result = "Replaced";
        }
        else if ( existing != null && 
                  modeParam.equalsIgnoreCase(QUERY_PARAM_MODE_APPEND) )
        {
            List entries = null;

            if ( existing instanceof List )
            {
                entries = (List) existing;
            }
            else
            {
                entries = new ArrayList<Object>();
                entries.add(existing);
            }

            entries.add(object);
            this.store.replace(name, entries);
            result = "Appended";
        }
        else
        {
            if ( existing != null )
            {
                resp.status(409);
                return this.gson.toJson(existing);
            }
        }

        resp.status(201);
        return result;
    }

}
