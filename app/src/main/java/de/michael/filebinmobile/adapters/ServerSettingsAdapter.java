package de.michael.filebinmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.model.Server;

public class ServerSettingsAdapter extends RecyclerView.Adapter<ServerSettingsViewholder>{

    private ArrayList<Server> savedServers = new ArrayList<>();

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

        if (parent != null) {
            parent.removeAllViews();
        }

        return new ServerSettingsViewholder(view);
    }

    @Override
    public void onBindViewHolder(ServerSettingsViewholder holder, int position) {
        Server item = this.savedServers.get(position);
        holder.txtName.setText(item.getName());
        holder.txtAddr.setText(item.getAddr());
    }

    @Override
    public int getItemCount() {
        return this.savedServers.size();
    }
}
