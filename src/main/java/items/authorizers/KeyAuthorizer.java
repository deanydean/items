package items.authorizers;

import java.util.List;

public class KeyAuthorizer implements Authorizer<String>{
    
    private final List<String> allowedKeys;

    public KeyAuthorizer(List<String> validKeys){
        this.allowedKeys = validKeys;
    }

    public boolean isAuthorized(String token){
        return this.allowedKeys.contains(token);
    }

}
