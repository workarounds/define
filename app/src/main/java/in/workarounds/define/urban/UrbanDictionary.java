package in.workarounds.define.urban;

import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.dictionary.Result;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;
import retrofit.Call;
import retrofit.Response;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class UrbanDictionary implements Dictionary {
    private static final String TAG = LogUtils.makeLogTag(UrbanDictionary.class);
    private final UrbanApi api;

    @Inject
    public UrbanDictionary(UrbanApi api) {
        this.api = api;
    }

    @Override
    public List<Result> results(String word) {
        List<Result> results = new ArrayList<>();
        if (!TextUtils.isEmpty(word)) {
            Call<UrbanResult> call = api.define(word);
            UrbanResult urbanResult = null;
            try {
                Response<UrbanResult> response = call.execute();
                urbanResult = response.body();
            } catch (IOException e) {
                LogUtils.LOGE(TAG, "Probably no internet. Or some other time out");
                e.printStackTrace();
            }
            if (urbanResult != null) {
                for (Meaning meaning : urbanResult.getMeanings()) {
                    results.add(toResult(meaning, urbanResult));
                }
            }
        }
        return results;
    }

    private Result toResult(Meaning meaning, UrbanResult urbanResult) {
        Result result = new Result();
        result.definition(meaning.getDefinition());
        List<String> usages = new ArrayList<>();
        usages.add(meaning.getExample());
        result.usages(usages);
        result.synonyms(urbanResult.getTags());
        result.type(urbanResult.getResultType());
        return result;
    }
}
