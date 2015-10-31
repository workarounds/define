package in.workarounds.define.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import in.workarounds.define.R;
import in.workarounds.define.ui.activity.PermissionsActivity.Permission;

/**
 * Created by manidesto on 31/10/15.
 */
public class PermissionsAdapter extends RecyclerView.Adapter<PermissionsAdapter.ViewHolder>{
    private List<Permission> permissions;
    private GrantClickListener grantClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_permission, parent, false));
    }

    @Override
    public int getItemCount() {
        return permissions == null ? 0 : permissions.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Permission permission = permissions.get(position);
        holder.title.setText(permission.title);
        holder.rationale.setText(permission.rationale);
        if(permission.granted && !permission.given){
            holder.grant.setEnabled(false);
            holder.grant.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.medium_gray));
            holder.grant.setText(R.string.button_granted);
        } else {
            holder.grant.setEnabled(true);
            holder.grant.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.theme_accent));
            holder.grant.setText(R.string.button_grant);
            if(grantClickListener != null){
                holder.grant.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        grantClickListener.onGrantClicked(permission);
                    }
                });
            }
        }

        holder.required.setText(
                permission.required ? R.string.permission_required : R.string.permission_optional
        );
    }

    public void setPermissions(List<Permission> permissions){
        this.permissions = permissions;
        notifyDataSetChanged();
    }

    public void setGrantClickListener(GrantClickListener listener){
        grantClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView rationale;
        TextView required;
        Button grant;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_permission_title);
            rationale = (TextView) itemView.findViewById(R.id.tv_permission_rationale);
            grant = (Button) itemView.findViewById(R.id.button_grant);
            required = (TextView) itemView.findViewById(R.id.tv_required);
        }
    }

    public interface GrantClickListener {
        void onGrantClicked(Permission permission);
    }
}
