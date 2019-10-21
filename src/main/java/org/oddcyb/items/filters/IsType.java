package org.oddcyb.items.filters;

import java.util.function.BiPredicate;

/**
 * 
 */
public class IsType implements BiPredicate<String,Object>
{

    private final Class type;

    public IsType(Class type)
    {
        this.type = type;
    }

    @Override
    public boolean test(String name, Object object)
    {
        return this.type.isInstance(object);
    }

}