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
package org.oddcyb.items.store;

import java.util.Map;

/**
 * A store of items.
 */
public interface Store
{

    public static final String SEARCH_ALL = "*";
    
    /**
     * Read an object from the store.
     * 
     * @param name the name of the object.
     * @return the object or null if the object did not exist
     */
    public Object read(String name);
    
    /**
     * Add an object to the store.
     * 
     * @param name the name of the object
     * @param value the object
     * @return the object that already exists in the store or null if the object
     * was added.
     */
    public Object add(String name, Object value);
    
    /**
     * Replace an object in the store.
     * @param name the name of the object to replace
     * @param newValue the replacement object
     * @return the object that was replaced or null if the object did not exist
     */
    public Object replace(String name, Object newValue);
    
    /**
     * Delete an object from the store.
     * 
     * @param name the name of the object
     * @return the object that was deleted or null if the object did not exist
     */
    public Object delete(String name);
    
    /**
     * Search for objects in the store.
     * 
     * @param spec the search specification
     * @return the objects that match the search spec
     */
    public Map<String,Object> search(String spec);
}
