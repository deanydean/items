/*
 * Copyright 2016, 2020, Matt Dean
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
package org.oddcyb.items;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.oddcyb.items.handlers.StoreAdd;
import org.oddcyb.items.handlers.StoreGet;
import org.oddcyb.items.handlers.StoreReplace;
import org.oddcyb.items.store.InvalidTypeValueException;
import org.oddcyb.items.store.Item;
import org.oddcyb.items.store.ListStore;
import org.oddcyb.items.store.MapStore;
import org.oddcyb.items.store.Store;

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