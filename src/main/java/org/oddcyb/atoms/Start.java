/*
 * Copyright 2016, 2017 Matt Dean
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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.oddcyb.atoms.store.HeapStore;
import org.oddcyb.atoms.store.MapDBStore;
import org.oddcyb.atoms.store.MongoDBStore;
import org.oddcyb.atoms.store.Store;
import spark.Spark;

/**
 * Start the AtoMS services.
 */
public class Start 
{
    
    public void startServices(String[] args)
    {
        OptionSet options = parseArguments(args);
        
        String path = options.valueOf("path").toString();
        int port = (int) options.valueOf("port");
        
        Store store = null;
        if ( options.has("heap") )
        {
            store = new HeapStore();
        }
        else if ( options.has("mapdb"))
        {   
            store = new MapDBStore(
                options.valueOf("mapdb-file").toString(),
                options.valueOf("mapdb-map").toString()
            );
        }
        else if ( options.has("mongodb") )
        {
            store = new MongoDBStore(
                options.valueOf("mongodb-host").toString(),
                (int) options.valueOf("mongodb-port"),
                options.valueOf("mongodb-db").toString(),
                options.valueOf("mongodb-col").toString()
            );
        }
     
        Spark.port(port);
        
        if ( options.has("secure") )
        {
            // Enable security
            Spark.secure(
                options.valueOf("keystore").toString(), 
                options.valueOf("keystore-pwd").toString(), 
                options.valueOf("cert-alias").toString(), 
                options.valueOf("truststore").toString(),
                options.valueOf("truststore-pwd").toString(), 
                true);
        }
        
        new AtomsService(path, store).start();
    }

    public static OptionSet parseArguments(String[] args)
    {
        OptionParser parser = new OptionParser();
        
        parser.recognizeAlternativeLongOptions(true);
        
        parser.accepts("path", "Path for the service")
            .withRequiredArg()
            .defaultsTo(AtomsService.SERVICE_BASE);
        
        parser.accepts("port", "Port to listen on")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(9999);
        
        parser.accepts("secure", "Enable secure mode")
            .withRequiredArg()
            .ofType(Boolean.class)
            .defaultsTo(false);
        
        parser.accepts("keystore", "Java keystore")
            .withRequiredArg()
            .defaultsTo("./etc/keystore");
        
        parser.accepts("keystore-pwd-file", "File containing keystore pwd")
            .withRequiredArg()
            .defaultsTo("./etc/keystore-pwd");
        
        parser.accepts("cert-alias", "The alias of the server cert")
            .withRequiredArg()
            .defaultsTo("atoms-service-cert");
        
        parser.accepts("truststore", "Java keystore containing keys")
            .withRequiredArg()
            .defaultsTo("./etc/truststore");
        
        parser.accepts("truststore-pwd-file", "File containing truststore pwd")
            .withRequiredArg()
            .defaultsTo("./etc/truststore-pwd");
        
        parser.accepts("heap", "Hold data on the heap");
        
        parser.accepts("mapdb", "Hold data in mapdb");
        
        parser.accepts("mapdb-file", "File to hold the mapdb")
            .requiredIf("mapdb")
            .withRequiredArg();
        
        parser.accepts("mapdb-map", "mapdb map name")
            .requiredIf("mapdb")
            .withRequiredArg();
        
        parser.accepts("mongodb", "Hold data in mongodb");
        
        parser.accepts("mongodb-host", "Hostname of the mongodb server")
            .requiredIf("mongodb")
            .withRequiredArg();
        
        parser.accepts("mongodb-port", "Port to connect to mongodb")
            .requiredIf("mongodb")
            .withRequiredArg().ofType(Integer.class);
        
        parser.accepts("mongodb-db", "mongodb database")
            .requiredIf("mongodb")
            .withRequiredArg();
        
        parser.accepts("mongodb-col", "mongodb collection")
            .requiredIf("mongodb")
            .withRequiredArg();
        
        return parser.parse(args);
    }
    
    public static void main(String[] args)
    {
        new Start().startServices(args);
    }
    
}
