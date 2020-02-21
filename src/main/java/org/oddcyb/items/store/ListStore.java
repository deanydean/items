/*
 * Copyright 2020, Matt Dean
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
package org.oddcyb.items.store;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Store that holds typed data.
 */
public class ListStore implements Store
{

    private final Store store;

    /**
     * Create a new MapStore
     * 
     * @param store the store to hold the Items
     */
    public ListStore(Store store)
    {
        this.store = store;
    }

    /**
     * Will this store accept this object?
     * 
     * @param object the object to check
     * @return true if this store will accept this object, false if not.
     */
    public boolean accepts(Object object)
    {
        return ( object instanceof List );
    }

    @Override
    public Object read(String name)
    {
        Object item = this.store.read(name);
        return (this.accepts(item)) ? item : null;
    }

    @Override
    public Object add(String name, Object value)
    {
        Object existing = this.store.read(name);

        if ( existing != null && !this.accepts(existing) )
        {
            throw new InvalidTypeValueException(
                "Invalid existing value for "+name);   
        }

        if ( existing != null )
        {
            List entries = (List) existing;
            entries.add(value);
            this.store.replace(name, entries);
            return null;
        }
        else
        {
            if ( this.accepts(value) )
            {
                return this.store.add(name, existing);
            }
            else
            {
                List entries = new ArrayList<>();
                entries.add(value);
                return this.store.add(name, entries);
            }
        }
    }

    @Override
    public Object replace(String name, Object newValue)
    {
        Object existing = this.store.read(name);

        if ( this.accepts(existing) && this.accepts(newValue) )
        {
            return this.store.replace(name, newValue);
        }
        else
        {
            throw new InvalidTypeValueException("Invalid value for "+name);
        }
    }

    @Override
    public Object delete(String name)
    {
        // Check object exists first (will confirm filtered type)
        if ( this.read(name) != null )
        {
            return this.store.delete(name);
        }
        else
        {
            return null;
        }
    }

    @Override
    public Map<String,Object> search(String spec)
    {
        return this.store
                   .search(spec)
                   .entrySet()
                   .stream()
                   .filter( (e) -> this.accepts(e.getValue()) )
                   .collect( Collectors.toMap( (e) -> e.getKey(), 
                                               (e) -> e.getValue() ));
    }
}