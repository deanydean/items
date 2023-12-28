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
package items.store;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * A store that is held in MapDB.
 */
public class MapDBStore implements Store
{
    
    private final DB db;
    private final String mapName;
    
    public MapDBStore(String dbFile, String mapName)
    {
        this.db = DBMaker
            .fileDB(dbFile)
            .closeOnJvmShutdown()
            .make();
        this.mapName = mapName;
    }

    @Override
    public Object read(String name)
    {
        return this.getMap().get(name);
    }

    @Override
    public Object add(String name, Object value)
    {
        return this.getMap().putIfAbsent(name, value);
    }

    @Override
    public Object replace(String name, Object newValue)
    {
        return this.getMap().replace(name, newValue);
    }

    @Override
    public Object delete(String name)
    {
        return this.getMap().remove(name);
    }
    
    @Override
    public Map<String, Object> search(String spec)
    {
        Map<String,Object> result = new HashMap<>();
        
        if ( spec.equalsIgnoreCase(Store.SEARCH_ALL) )
        {
            getMap().entrySet().forEach((entry) -> {
                result.put(entry.getKey(), entry.getValue());
            });
        }

        // TODO - Use spec to find results
        
        return result;
    }
    
    private ConcurrentMap<String,Object> getMap()
    {
        return this.db
            .hashMap(this.mapName, Serializer.STRING, Serializer.JAVA)
            .createOrOpen();
    }
}
