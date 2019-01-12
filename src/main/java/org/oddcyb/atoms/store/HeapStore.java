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
package org.oddcyb.atoms.store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A Store that is held on the heap.
 */
public class HeapStore implements Store
{
    private static final Logger LOG =
        Logger.getLogger(HeapStore.class.getName());

    private final Map<String,Object> dataMap = new ConcurrentHashMap<>();

    @Override
    public Object read(String name)
    {
        return this.dataMap.get(name);
    }
    
    @Override
    public Object add(String name, Object value)
    {
        return this.dataMap.putIfAbsent(name, value);
    }
    
    @Override
    public Object replace(String name, Object newValue)
    {
        return this.dataMap.replace(name, newValue);
    }

    @Override
    public Object delete(String name)
    {
        return this.dataMap.remove(name);
    }
    
    @Override
    public Map<String,Object> search(String spec)
    {
        Map<String,Object> result = new HashMap<>();

        if ( spec.equalsIgnoreCase(Store.SEARCH_ALL) )
        {
            this.dataMap.entrySet().forEach( (entry) -> {
                result.put(entry.getKey(), entry.getValue());
            });
        }
        else
        {
            // TODO match spec
            LOG.warning("Non-all search specs not implemented");
        }
        
        return result;
    }
}
