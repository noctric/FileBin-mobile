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
import android.widget.EditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.michael.filebinmobile.R;
import de.michael.filebinmobile.adapters.OnAdapterDataChangedListener;
import de.michael.filebinmobile.adapters.ServerSettingsAdapter;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.model.Server;

public class SettingsFragment extends Fragment implements OnAdapterDataChangedListener{

    @BindView(R.id.rclServerList)
    RecyclerView rclServerList;

    private ServerSettingsAdapter adapter;

    private ArrayList<Server> mockData = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new ServerSettingsAdapter(getActivity());
        this.adapter.setDataChangedListener(this);

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

        //reloadServerList();

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

    @Override
    public void onResume() {

        reloadServerList();

        super.onResume();
    }

    @OnClick(R.id.fbaAddServer)
    void addServer() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View view = LayoutInflater.from(getContext())
                .inflate(R.layout.edit_server_settings, null);
        builder.setTitle("Add Server")
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText txtName = view.findViewById(R.id.edtEditName);
                        EditText txtAddr = view.findViewById(R.id.edtEditAddress);

                        Server server = new Server(
                                txtName.getText().toString(),
                                txtAddr.getText().toString()
                        );

                        SettingsManager.getInstance().addServer(server, getActivity());

                        reloadServerList();
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

    private void reloadServerList() {

        ArrayList<Server> serverList = SettingsManager.getInstance().getServerList(getActivity());
        this.adapter.updateData(serverList);

        System.out.println("Updating server list. Current item count: " + serverList.size());

        // DEBUGGING
        System.out.println("saved server list size" + SettingsManager.getInstance().getServerList(getActivity()).size());

    }

    @Override
    public void onAdapterDataChanged() {
        reloadServerList();
    }
}
