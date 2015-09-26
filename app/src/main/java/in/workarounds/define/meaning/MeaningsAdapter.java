package in.workarounds.define.meaning;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.workarounds.define.R;
import in.workarounds.define.dictionary.Result;

/**
 * Created by madki on 27/09/15.
 */
public class MeaningsAdapter extends RecyclerView.Adapter<MeaningsAdapter.ViewHolder> {
    private List<Result> results = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_meaning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(position >= 0 && position < results.size()) {
            Result result = results.get(position);
            viewHolder.type.setText(result.type());
            viewHolder.synonyms.setText(join(result.synonyms()));
            viewHolder.usages.setText(join(result.usages()));
            viewHolder.definition.setText(result.definition());
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void update(List<Result> results) {
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
