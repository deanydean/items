package items.authorizers;

public interface Authorizer<T> {
    
    public boolean isAuthorized(T thing);

}
