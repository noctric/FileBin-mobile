package de.michael.filebinmobile.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import butterknife.OnClick;
import de.michael.filebinmobile.R;
import de.michael.filebinmobile.adapters.ServerSettingsAdapter;
import de.michael.filebinmobile.controller.SettingsManager;
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
        /*
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
        */
        //endregion

        ArrayList<Server> serverList = SettingsManager.getInstance().getServerList(getActivity());

        this.adapter.updateData(serverList);

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

    @OnClick(R.id.fbaAddServer)
    void addServer() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Server")
                .setView(R.layout.edit_server_settings)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO save server to list
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

}
