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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.oddcyb.atoms.store.HeapStore;
import org.oddcyb.atoms.store.Store;

/**
 * Start the AtoMS services.
 */
public class Start 
{
    
    public void startServices(String[] args)
    {
        OptionSet options = parseArguments(args);
        
        String path = options.valueOf("path").toString();
        
        Store store = null;
        if ( options.has("heap") )
        {
            store = new HeapStore();
        }
        else if ( options.has("mongodb") )
        {
            throw new UnsupportedOperationException("Not implemented yet");
        }
            
        new DataService(path, store).start();
    }

    public static OptionSet parseArguments(String[] args)
    {
        OptionParser parser = new OptionParser();
        
        parser.recognizeAlternativeLongOptions(true);
        
        parser.accepts("path", "The base path for the service")
            .withOptionalArg().defaultsTo(DataService.SERVICE_BASE);
        
        parser.accepts("heap", "The service holds data on the heap");
        
        parser.accepts("mongodb", "The services holds data in mongodb");
        
        parser.accepts("mongodb-url", "The URL for mongodb")
            .requiredIf("mongodb");
        
        return parser.parse(args);
    }
    
    public static void main(String[] args)
    {
        new Start().startServices(args);
    }
    
}
