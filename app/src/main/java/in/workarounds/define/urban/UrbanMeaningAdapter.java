package in.workarounds.define.urban;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.base.Result;

/**
 * Created by madki on 13/10/15.
 */
public class UrbanMeaningAdapter extends RecyclerView.Adapter<UrbanMeaningAdapter.ViewHolder> {
    private UrbanResult results;

    @Inject
    public UrbanMeaningAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_meaning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(position >= 0 && position < getItemCount()) {
            UrbanResult result = results;
            Meaning meaning = result.getMeanings().get(position);
            viewHolder.type.setText(result.getResultType());
            viewHolder.synonyms.setText(join(result.getTags()));
            viewHolder.usages.setText(meaning.getExample());
            viewHolder.definition.setText(meaning.getDefinition());
        }
    }

    @Override
    public int getItemCount() {
        if(results == null) return 0;
        return results.getMeanings().size();
    }

    public void update(UrbanResult results) {
        this.results = results;
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