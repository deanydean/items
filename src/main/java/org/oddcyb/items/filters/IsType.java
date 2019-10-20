package org.oddcyb.items.filters;

import java.util.function.Predicate;

/**
 * 
 */
public class IsType implements Predicate<Object>
{

    private final Class<Object> type;

    public IsType(Class<Object> type)
    {
        this.type = type;
    }

    @Override
    public boolean test(Object object)
    {
        return this.type.isInstance(object);
    }

}