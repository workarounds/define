package in.workarounds.define.urban;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by madki on 26/09/15.
 */
public interface UrbanApi {

    @GET("define")
    Call<UrbanResult> define(@Query("term") String word);
}
