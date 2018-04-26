package de.michael.filebinmobile.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.model.PostInfo;
import de.michael.filebinmobile.model.Server;
import de.michael.filebinmobile.model.UserProfile;

public class ServerSettingsAdapter extends SimpleDataAdapter<ServerSettingsViewholder, Server> {

    private PostInfo selectedPostInfo;

    public ServerSettingsAdapter(Activity activity) {
        super(activity);
        selectedPostInfo = getPostInfo();
    }

    @Override
    public ServerSettingsViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_server_setting, null);

        return new ServerSettingsViewholder(view);
    }

    @Override
    public void onBindViewHolder(ServerSettingsViewholder holder, int position) {
        final Server item = this.getData().get(position);

        if (this.selectedPostInfo != null &&
                this.selectedPostInfo.getServer().equals(item) &&
                this.selectedPostInfo.getUserProfile().equals(item.getUserProfiles().get(0))) {
            holder.txtIsProfileActive.setText(R.string.active);
            holder.txtIsProfileActive.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
        } else {
            holder.txtIsProfileActive.setText(R.string.notActive);
            holder.txtIsProfileActive.setTextColor(getActivity().getResources().getColor(R.color.colorTextLight));
        }

        holder.txtName.setText(item.getName());
        holder.txtAddr.setText(item.getAddr());

        holder.btnDelete.setOnClickListener(view -> {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Delete Server " + item.getName())
                    .setTitle("Are you sure you want to delete this Server?")
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {

                        SettingsManager.getInstance().deleteServer(item, getActivity());

                        if (getDataChangedListener() != null) {

                            getDataChangedListener().onAdapterDataChanged();

                        }

                        PostInfo postInfo = SettingsManager.getInstance().getPostInfo(getActivity());

                        if (postInfo != null &&
                                postInfo.equals(new PostInfo(item.getUserProfiles().get(0), item))) {
                            SettingsManager.getInstance().setPostInfo(null, getActivity());
                        }

                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        holder.btnSetForUpload.setOnClickListener(view -> {

            UserProfile userProfile = null;

            // for now, we only add one profile for each server (this will change)
            // se we don't have to display a list for selection just yet.
            ArrayList<UserProfile> userProfiles = item.getUserProfiles();
            if (userProfiles.size() > 0) {

                userProfile = userProfiles.get(0);

            }

            PostInfo postInfo = new PostInfo(userProfile, item);
            this.selectedPostInfo = postInfo;

            SettingsManager.getInstance().setPostInfo(postInfo, getActivity());

            Toast.makeText(getActivity(), "Server set and selected", Toast.LENGTH_SHORT).show();

            holder.txtIsProfileActive.setText(R.string.active);
            holder.txtIsProfileActive.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));

            notifyDataSetChanged();

        });
    }

    @Override
    public int getItemCount() {
        return this.getData().size();
    }

    public PostInfo getPostInfo() {
        return SettingsManager.getInstance().getPostInfo(getActivity());
    }

}
