/*
 * Copyright 2018, 2019, Matt Dean
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
package org.oddcyb.items;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
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
            return Unirest.get(this.url+"/"+name).asString().getBody();
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
            return Unirest.put(this.url+"/"+name).body(name)
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
        Unirest.put(this.url+"/"+name).body(name)
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
}