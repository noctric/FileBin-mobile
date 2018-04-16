package de.michael.filebinmobile.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.model.Server;

public class ServerSettingsAdapter extends SimpleDataAdapter<ServerSettingsViewholder, Server> {

    public ServerSettingsAdapter(Activity activity) {
        super(activity);
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
        holder.txtName.setText(item.getName());
        holder.txtAddr.setText(item.getAddr());

        final View editServerView = LayoutInflater.from(this.getActivity())
                .inflate(R.layout.edit_server_settings, null);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Delete Server " + item.getName())
                        .setTitle("Are you sure you want to delete this Server?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                SettingsManager.getInstance().deleteServer(item, getActivity());

                                if (getDataChangedListener() != null) {

                                    getDataChangedListener().onAdapterDataChanged();

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
        return this.getData().size();
    }
}
