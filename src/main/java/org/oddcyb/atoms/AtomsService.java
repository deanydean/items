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
package org.oddcyb.atoms;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.oddcyb.atoms.handlers.StoreAdd;
import org.oddcyb.atoms.handlers.StoreReplace;
import org.oddcyb.atoms.store.Store;
import spark.Spark;

/**
 * Service that holds data units.
 */
public class AtomsService 
{
    private static final Logger LOG = 
        Logger.getLogger(AtomsService.class.getName());
    
    public static final String SERVICE_BASE = "/atoms";
    public static final String NAME_PARAM = ":name";
    
    private final String serviceBase;
    private final Store store;
    
    /**
     * Create a service located at the provided base path.
     * 
     * @param serviceBase the base path for the service
     * @param store the store that holds that data for this service
     */
    public AtomsService(String serviceBase, Store store)
    {
        this.serviceBase = serviceBase;
        this.store = store;
    }
    
    /**
     * Start the service.
     */
    public void start()
    {
        String path = this.serviceBase+"/"+NAME_PARAM;
        
        LOG.log(Level.INFO, "Starting data service at {0}", path);
        
        // Register the HTTP method -> store function mappings
        Spark.get(path,    (req, resp) -> 
            this.store.read(req.params(NAME_PARAM)));
        Spark.delete(path, (req, resp) -> 
            this.store.delete(req.params(NAME_PARAM)));
        
        Spark.put(path,  new StoreReplace(store));
        Spark.post(path, new StoreAdd(store));
        
        // Log exceptions
        Spark.exception(Exception.class, (ex, req, resp) -> {
            LOG.log(Level.WARNING, "Request {0} failed", req);
            LOG.log(Level.WARNING, "Request error", ex);
            
            resp.status(500);
        });
    }
    
}