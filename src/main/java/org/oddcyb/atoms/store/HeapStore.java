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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A Store that is held on the heap.
 */
public class HeapStore implements Store
{
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
    public List search(String spec)
    {
        return this.dataMap.values().stream().collect(Collectors.toList());
    }
}
