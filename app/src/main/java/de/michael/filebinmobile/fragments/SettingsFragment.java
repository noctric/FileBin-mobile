package de.michael.filebinmobile.fragments;

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

import java.util.ArrayList;

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
}
