package in.workarounds.define.urban;

import android.text.TextUtils;

import java.io.IOException;

import javax.inject.Inject;

import in.workarounds.define.DefineApp;
import in.workarounds.define.R;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.IUrbanDictionary;
import in.workarounds.define.portal.PerPortal;
import retrofit.Call;
import retrofit.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class UrbanDictionary implements IUrbanDictionary {
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
                        DefineApp.getContext().getString(R.string.exception_urban)
                );
            }
        }
        return results;
    }

    public Observable<UrbanResult> resultsObservable(String word) {
        return Observable.fromCallable(() -> results(word))
                .subscribeOn(Schedulers.io());
    }
}
