package de.michael.filebinmobile.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.model.Server;

public class ServerSettingsAdapter extends RecyclerView.Adapter<ServerSettingsViewholder>{

    private Context context;
    private ArrayList<Server> savedServers = new ArrayList<>();

    public ServerSettingsAdapter(Context context) {
        this.context = context;
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
                .inflate(R.layout.list_item_server_setting_relative_layout, null);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Edit server settings")
                        .setTitle("Settings for " + item.getName());
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
