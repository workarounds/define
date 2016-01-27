package in.workarounds.define.ui.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.workarounds.define.R;
import in.workarounds.define.webviewDicts.livio.LivioLanguages;

/**
 * Created by madki on 27/01/16.
 */
public class LivioListAdapter extends BaseAdapter {

    private Context context;
    private List<LivioLanguages.Language> languages;
    private List<Boolean> status;

    public LivioListAdapter(Context context) {
        this.context = context;
        languages = LivioLanguages.all(context);
        status = new ArrayList<>(languages.size());

        for (int i = 0; i < languages.size(); i++) {
            status.add(null);
        }
    }

    @Override
    public int getCount() {
        return languages.size();
    }

    @Override
    public LivioLanguages.Language getItem(int position) {
        return languages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_livio_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(getItem(position).name());
        holder.description.setText(getItem(position).description());
        Boolean download = status.get(position);
        if (download != null) {
            if (!download) {
                holder.button.setImageResource(R.drawable.ic_play_store);
                holder.button.setOnClickListener(v -> {installLivio(getItem(position).packageName());});
                holder.button.setColorFilter(ContextCompat.getColor(context, R.color.theme_accent));
            } else {
                holder.button.setImageResource(R.drawable.ic_tick);
                holder.button.setColorFilter(ContextCompat.getColor(context, R.color.green));
            }
        } else {
            // TODO show loading sign ?
        }
        return convertView;
    }

    public List<LivioLanguages.Language> languages() {
        return languages;
    }

    public void setStatus(int position, boolean status) {
        this.status.set(position, status);
        notifyDataSetChanged();
    }

    private void installLivio(String packageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    private static class ViewHolder {
        TextView title;
        TextView description;
        ImageView button;

        ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.tv_livio_title);
            description = (TextView) view.findViewById(R.id.tv_livio_description);
            button = (ImageView) view.findViewById(R.id.btn_install_livio);
        }
    }
}
