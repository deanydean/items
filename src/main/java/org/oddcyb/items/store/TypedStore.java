/*
 * Copyright 2019, Matt Dean
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Store that holds typed data.
 */
public class TypedStore implements Store<Object>
{
    public static final String META_TYPE    = "type";
    public static final String META_CREATED = "created";

    private static final DateTimeFormatter TIME_FORMAT = 
        DateTimeFormatter.ofPattern("yyyyMMdd:HHmmss.n");

    private final Store<Item> store;
    private final Type type;

    /**
     * Create a new TypedStore for Type objects.
     * 
     * @param store the store to hold the Items
     * @param type the Type of objects to allow in the store
     */
    public TypedStore(Store<Item> store, Type type)
    {
        this.store = store;
        this.type = type;
    }

    /**
     * Will this store accept this object?
     * 
     * @param object the object to check
     * @return true if this store will accept this object, false if not.
     */
    public boolean accepts(Object object)
    {
        if ( object instanceof Item )
        {
            return this.type == 
                Type.valueOf(((Item) object).getMeta(META_TYPE));
        }
        else
        {
            return this.type.isType(object);
        }
    }

    @Override
    public Object read(String name)
    {
        Item item = this.store.read(name);
        return (this.accepts(item)) ? item.getValue() : null;
    }

    @Override
    public Object add(String name, Object value)
    {
        if ( this.accepts(value) )
        {
            Item existing = this.store.add(name, newItem(value));

            if ( existing != null && !this.accepts(existing) )
            {
                throw new InvalidTypeValueException(
                    "Invalid existing value for "+name);   
            }

            return existing;
        }
        else
        {
            throw new InvalidTypeValueException("Invalid value for "+name);
        }
    }

    @Override
    public Object replace(String name, Object newValue)
    {
        Item existing = this.store.read(name);

        if ( this.accepts(existing) && this.accepts(newValue) )
        {
            return this.store.replace(name, newItem(newValue));
        }
        else
        {
            throw new InvalidTypeValueException("Invalid value for "+name);
        }
    }

    @Override
    public Item delete(String name)
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
    public Map<String, Object> search(String spec)
    {
        return this.store
                   .search(spec)
                   .entrySet()
                   .stream()
                   .filter( (e) -> this.accepts(e.getValue()) )
                   .collect( Collectors.toMap( (e) -> e.getKey(), 
                                               (e) -> e.getValue().getValue() ));
    }

    private Item newItem(Object object)
    {
        Map<String,String> meta = new HashMap<>() {{
            put(META_TYPE, type.name());
            put(META_CREATED, LocalDateTime.now().format(TIME_FORMAT));
        }};

        return new Item(object, meta);
    }

    /**
     * Types of objects for a TypedStore.
     */
    public static enum Type 
    {
        MAP(Map.class),
        LIST(List.class);

        private final Class<? extends Object> typeClass;

        private Type(Class<? extends Object> clazz)
        {
            typeClass = clazz;
        }

        public boolean isType(Object object)
        {
            return this.typeClass.isInstance(object);
        }
    }
}