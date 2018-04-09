package de.michael.filebinmobile.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.model.Server;

public class ServerSettingsAdapter extends RecyclerView.Adapter<ServerSettingsViewholder> {

    private Activity activity;
    private ArrayList<Server> savedServers = new ArrayList<>();
    private OnAdapterDataChangedListener dataChangedListener;

    public ServerSettingsAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setDataChangedListener(OnAdapterDataChangedListener dataChangedListener) {
        this.dataChangedListener = dataChangedListener;
    }

    public void updateData(ArrayList<Server> serverList) {
        savedServers.clear();
        savedServers.addAll(serverList);
        notifyDataSetChanged();
    }

    public void clear() {
        savedServers.clear();
        notifyDataSetChanged();
    }

    public void add(Server server) {
        savedServers.add(server);
        notifyDataSetChanged();
    }


    @Override
    public ServerSettingsViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_server_setting, null);

        return new ServerSettingsViewholder(view);
    }

    @Override
    public void onBindViewHolder(ServerSettingsViewholder holder, int position) {
        final Server item = this.savedServers.get(position);
        holder.txtName.setText(item.getName());
        holder.txtAddr.setText(item.getAddr());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Edit server settings")
                        .setTitle("Settings for " + item.getName())
                        .setView(R.layout.edit_server_settings)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //TODO save server settings
                                //TODO we need an edit and save method in our settings manager
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Delete Server " + item.getName())
                        .setTitle("Are you sure you want to delete this Server?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                SettingsManager.getInstance().deleteServer(item, activity);

                                if (dataChangedListener != null) {

                                    dataChangedListener.onAdapterDataChanged();

                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.savedServers.size();
    }
}
