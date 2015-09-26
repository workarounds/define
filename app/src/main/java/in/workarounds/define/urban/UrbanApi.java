package in.workarounds.define.urban;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by madki on 26/09/15.
 */
public interface UrbanApi {

    @GET("/define")
    UrbanResult term(@Query("term") String term);
}
