package in.workarounds.define.urban;

import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.IUrbanDictionary;
import in.workarounds.define.base.Result;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class UrbanDictionary implements IUrbanDictionary {
    private static final String TAG = LogUtils.makeLogTag(UrbanDictionary.class);
    private final UrbanApi api;

    @Inject
    public UrbanDictionary(UrbanApi api) {
        this.api = api;
    }

    @Override
    public UrbanResult results(String word) throws DictionaryException {
        UrbanResult results = null;
        if (!TextUtils.isEmpty(word)) {
            Call<UrbanResult> call = api.define(word);
            try {
                Response<UrbanResult> response = call.execute();
                results = response.body();
            } catch (IOException e) {
                throw new DictionaryException(
                        DictionaryException.NETWORK_ERROR,
                        "Unable to fetch data from Urban Dictionary servers. Please check your network connection."
                );
            }
        }
        return results;
    }
}
