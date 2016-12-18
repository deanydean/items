/*
 * Copyright 2016 Matt Dean
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
package org.oddcyb.atoms.handlers;

import org.oddcyb.atoms.DataService;
import org.oddcyb.atoms.store.Store;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 *
 */
public class StoreAdd implements Route
{
    public static final String SUCCESS_RESPONSE = 
        "<html><body>201 Created</body></html>";

    private final Store store;
    
    public StoreAdd(Store store)
    {
        this.store = store;
    }
    
    @Override
    public Object handle(Request req, Response resp) throws Exception
    {
        String name = req.params(DataService.NAME_PARAM);
        String object = req.body();
        
        Object exists = this.store.add(name, object);
        
        if ( exists == null )
        {
            resp.status(201);
            return SUCCESS_RESPONSE;
        }
        else
        {
            resp.status(409);
            return exists;
        }
    }

}
