package items;

import com.google.gson.Gson;

import kong.unirest.core.Callback;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;

import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemsClient 
{
    private static final Logger LOG = 
        Logger.getLogger(ItemsClient.class.getName());

    private final String url;
    
    public ItemsClient(String url)
    {
        this.url = url;
    }
    
    public String get(String name) throws CompletionException
    {   
        try
        {
            return Unirest.get(this.url+"/"+name)
                          .header("Accept", "*/*")
                          .asString()
                          .getBody();
        }
        catch ( UnirestException ue )
        {
            LOG.log(Level.WARNING, "Failed to get {0} : {1}",
                new Object[]{ name, ue });
            throw new CompletionException("Failed to get "+name, ue);
        }
    }
    
    public void get(String name, Consumer<String> onComplete,
            Consumer<CompletionException> onFail, Runnable onCancel)
    {
        Unirest.get(this.url+"/"+name)
            .asStringAsync(new AsyncCallback<>(onComplete, onFail, onCancel));
    }
    
    public String put(String name, String object) throws CompletionException
    {
        try
        {
            return Unirest.put(this.url+"/"+name).body(object)
                .asString().getBody();
        }
        catch ( UnirestException ue )
        {
            LOG.log(Level.WARNING, "Failed to put {0}->{1} : {2}",
                new Object[] { name, object, ue });
            throw new CompletionException("Failed to put "+name, ue);
        }
    }
    
    public void put(String name, String object, Consumer<String> onComplete,
            Consumer<CompletionException> onFail, Runnable onCancel)
    {
        Unirest.put(this.url+"/"+name).body(object)
            .asStringAsync(new AsyncCallback<>(onComplete, onFail, onCancel));
    }
    
    public String delete(String name)
    {
        try
        {
            return Unirest.delete(this.url+"/"+name).asString().getBody();
        } 
        catch ( UnirestException ue )
        {
            LOG.log(Level.WARNING, "Failed to delete {0} : {1}",
                new Object[] { name, ue } );
            throw new CompletionException("Failed to delete "+name, ue);
        }
    }
    
    public void delete(String name, Consumer<String> onComplete,
            Consumer<CompletionException> onFail, Runnable onCancel)
    {
        Unirest.delete(this.url+"/"+name)
            .asStringAsync(new AsyncCallback<>(onComplete, onFail, onCancel));
    }
    
    public List<String> search(String spec)
    {
        try
        {
            String result = Unirest.get(this.url+"/"+spec).asString().getBody();
            return new Gson().fromJson(result, List.class);
        }
        catch ( UnirestException ue )
        {
            LOG.log(Level.WARNING, "Failed to find {0} : {1}",
                    new Object[]{ spec, ue });
            throw new CompletionException("Failed to find "+spec, ue);
        }
    }
    
    private class AsyncCallback<T> implements Callback<T>
    {
        private final Consumer<T> onComplete;
        private final Consumer<CompletionException> onFail;
        private final Runnable onCancel;
        
        public AsyncCallback(Consumer<T> onComplete, 
            Consumer<CompletionException> onFail, Runnable onCancel)
        {
            this.onComplete = onComplete;
            this.onFail = onFail;
            this.onCancel = onCancel;
        }
        
        @Override
        public void completed(HttpResponse<T> hr)
        {
            this.onComplete.accept(hr.getBody());
        }

        @Override
        public void failed(UnirestException ue)
        {
            this.onFail.accept(new CompletionException("Request failed", ue));
        }

        @Override
        public void cancelled() {
            this.onCancel.run();
        }
        
    }

    public static void main(String[] args) {
        var op = args[0];
        var key = args[1];
        var value = (args.length > 2) ? args[2] : null;

        var items = new ItemsClient(System.getenv("ITEMS_URL"));

        switch (op) {
            case "get":
                System.out.println(items.get(key));
                break;
        
            case "put":
                items.put(key, value);
                break;

            default:
                System.err.println("Unknown op: "+op);
                break;
        }
    }
}