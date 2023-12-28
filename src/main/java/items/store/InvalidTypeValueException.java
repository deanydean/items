package items.store;

/**
 * Exception that's thrown when a value's type is invalid.
 */
public class InvalidTypeValueException extends RuntimeException
{

    private static final long serialVersionUID = -125479539255144653L;

    public InvalidTypeValueException(String msg)
    {
        super(msg);
    }
    
}