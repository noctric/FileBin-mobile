package de.michael.filebinmobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.michael.filebinmobile.R;
import de.michael.filebinmobile.adapters.ServerSettingsAdapter;
import de.michael.filebinmobile.model.Server;

public class SettingsFragment extends Fragment {

    @BindView(R.id.rclServerList)
    RecyclerView rclServerList;

    private ServerSettingsAdapter adapter;

    private ArrayList<Server> mockData = new ArrayList<>();

    private static final String KEY_SERVER_LIST = "de.michael.filebin.SERVER_NAMES";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new ServerSettingsAdapter(getContext());

        //region let's just add some mock samples | 16 items
        mockData.add(new Server("Soapsurfer", "https://p.soapsurfer.de"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));
        mockData.add(new Server("Xinu", "https://paste.xinu.at"));

        adapter.updateData(mockData);
        //endregion

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.server_settings_fragment, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.rclServerList.getContext(),
                linearLayoutManager.getOrientation());

        this.rclServerList.setLayoutManager(linearLayoutManager);
        this.rclServerList.setItemAnimator(new DefaultItemAnimator());
        this.rclServerList.setAdapter(this.adapter);
        this.rclServerList.addItemDecoration(dividerItemDecoration);


        return view;
    }



    //TODO probably should move this functionality to a more sophisticated manager class
    /**
     * Using a database management system to save servers and associated profiles would be a bit
     * of an overkill. This is because in probably 99.9% of the cases a user will not have more than
     * one account on 1-2 different servers. So for now we use shared preferences and serialization
     * as json strings to store our information privately on the device.
     *
     * @param server the server to be saved
     * @return true on success
     */
    private boolean addServer(Server server) {
        Gson gson = new Gson();
        String serializedServerInfo = gson.toJson(server);

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> serializedServerSet = preferences.getStringSet(KEY_SERVER_LIST, null);


        if (serializedServerSet == null) {
            // create a new set and add it
            serializedServerSet = new HashSet<>();
        }

        serializedServerSet.add(serializedServerInfo);

        editor.putStringSet(KEY_SERVER_LIST, serializedServerSet);
        editor.apply();
        
        return true;
    }

    private List<Server> getServerList() {
        Gson gson = new Gson();

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        Set<String> serverListSet = preferences.getStringSet(KEY_SERVER_LIST, null);

        ArrayList<Server> serverList = new ArrayList<>();

        if (serverListSet != null) {
            for (String serializedServerInfo : serverListSet) {
                Server server = gson.fromJson(serializedServerInfo, Server.class);
                serverList.add(server);
            }
        }

        return serverList;
    }


}
