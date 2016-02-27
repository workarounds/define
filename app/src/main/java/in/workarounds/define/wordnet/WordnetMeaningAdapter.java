package in.workarounds.define.wordnet;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import in.workarounds.define.R;
import in.workarounds.define.base.DictConstants;
import in.workarounds.define.portal.PerPortal;

/**
 * Created by madki on 27/09/15.
 */
@PerPortal
public class WordnetMeaningAdapter extends RecyclerView.Adapter<WordnetMeaningAdapter.ViewHolder> {
    private List<Synset> results = new ArrayList<>();

    @Inject
    public WordnetMeaningAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_meaning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(position >= 0 && position < results.size()) {
            Synset synset = results.get(position);
            viewHolder.type.setText(convertType(synset.getType()));
            viewHolder.synonyms.setText(join(Arrays.asList(synset.getWordForms())));
            viewHolder.usages.setText(join(Arrays.asList(synset.getUsageExamples())));
            viewHolder.definition.setText(synset.getDefinition());
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    private String convertType(SynsetType type) {
        String typeStr = "";
        if (type == SynsetType.NOUN) {
            typeStr = DictConstants.TYPE_NOUN;
        } else if (type == SynsetType.VERB) {
            typeStr = DictConstants.TYPE_VERB;
        } else if (type == SynsetType.ADJECTIVE) {
            typeStr = DictConstants.TYPE_ADJ;
        } else if (type == SynsetType.ADVERB) {
            typeStr = DictConstants.TYPE_ADV;
        } else if (type == SynsetType.ADJECTIVE_SATELLITE) {
            typeStr = DictConstants.TYPE_ADJS;
        } else {
            typeStr = DictConstants.TYPE_NONE;
        }
        return typeStr;
    }

    public void update(List<Synset> results) {
        this.results = new ArrayList<>();
        this.results.addAll(results);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView definition;
        TextView synonyms;
        TextView usages;

        public ViewHolder(View itemView) {
            super(itemView);

            type = (TextView) itemView.findViewById(R.id.tv_meaning_type);
            definition = (TextView) itemView.findViewById(R.id.tv_definition);
            synonyms = (TextView) itemView.findViewById(R.id.tv_synonyms);
            usages = (TextView) itemView.findViewById(R.id.tv_usages);
        }
    }

    private String join(List<String> list) {
        return TextUtils.join(", ", list);
    }
}
