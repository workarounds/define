package in.workarounds.define.api;

import in.workarounds.define.model.urbandictionary.UrbanDictResult;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

public class UrbanDictionaryClient
{
    public UrbanDictionaryService service;

    public UrbanDictionaryClient()
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.urbandictionary.com/v0")
                .build();

        service = restAdapter.create(UrbanDictionaryService.class);
    }

    public UrbanDictionaryService get()
    {
        return service;
    }

    public interface UrbanDictionaryService
    {
        @GET("/define")
        void term(@Query("term") String term, Callback<UrbanDictResult> cb);
    }
}
