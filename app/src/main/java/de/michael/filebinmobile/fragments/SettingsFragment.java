package de.michael.filebinmobile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.michael.filebinmobile.R;
import de.michael.filebinmobile.adapters.OnAdapterDataChangedListener;
import de.michael.filebinmobile.adapters.ServerSettingsAdapter;
import de.michael.filebinmobile.controller.OnErrorOccurredCallback;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.controller.refactor.NetworkManager;
import de.michael.filebinmobile.model.PostInfo;
import de.michael.filebinmobile.model.Server;
import de.michael.filebinmobile.model.UserProfile;

public class SettingsFragment extends NavigationFragment implements OnAdapterDataChangedListener {

    @BindView(R.id.rclServerList)
    RecyclerView rclServerList;

    @BindView(R.id.txtEmptyServerList)
    TextView txtEmptyList;

    private ServerSettingsAdapter adapter;

    private CreateApikeyTask createApikeyTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new ServerSettingsAdapter(getActivity());
        this.adapter.setDataChangedListener(this);
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

    @Override
    public void onDestroy() {

        // cancel our async task in case it's running
        if (this.createApikeyTask != null) {
            this.createApikeyTask.cancel(true);
        }

        super.onDestroy();
    }

    @OnClick(R.id.fbaAddServer)
    void addServer() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View view = LayoutInflater.from(getContext())
                .inflate(R.layout.edit_server_settings, null);
        builder.setTitle("Add Server")
                .setView(view)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    EditText edtServerName = view.findViewById(R.id.edtEditName);
                    EditText edtServerAddr = view.findViewById(R.id.edtEditAddress);
                    EditText edtUserName = view.findViewById(R.id.edtUserName);
                    EditText edtUserPw = view.findViewById(R.id.edtUserPassword);

                    // create a new server model
                    Server server = new Server(
                            edtServerName.getText().toString(),
                            edtServerAddr.getText().toString()
                    );

                    // create a new user profile model
                    String userName = edtUserName.getText().toString();
                    String password = edtUserPw.getText().toString();

                    ApiKeyPostInfo apiKeyPostInfo = new ApiKeyPostInfo(userName, server, password);

                    createApikeyTask = new CreateApikeyTask(getActivity());
                    createApikeyTask.execute(apiKeyPostInfo);

                    Toast.makeText(getActivity(), "Creating Apikey", Toast.LENGTH_SHORT).show();

                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void reloadServerList() {

        // update active status of added server (even if user hasn't set the new server as
        // active, we still check here.
        PostInfo postInfo = SettingsManager.getInstance().getPostInfo(getActivity());
        this.adapter.setSelectedPostInfo(postInfo);

        ArrayList<Server> serverList = SettingsManager.getInstance().getServerList(getActivity());

        this.adapter.updateData(serverList);

        if (this.adapter.getItemCount() <= 0) {
            txtEmptyList.setVisibility(View.VISIBLE);
        } else {
            txtEmptyList.setVisibility(View.GONE);
        }


    }

    @Override
    public void onAdapterDataChanged() {
        reloadServerList();
    }

    @Override
    void cancelAllPossiblyRunningTasks() {
        if (this.createApikeyTask != null) {
            this.createApikeyTask.cancel(true);
        }
    }

    private class CreateApikeyTask extends AsyncTask<ApiKeyPostInfo, Integer, String> implements OnErrorOccurredCallback {

        private Activity activity;
        private ApiKeyPostInfo postInfo;

        public CreateApikeyTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(ApiKeyPostInfo... postInfos) {

            if (postInfos.length > 0) {

                this.postInfo = postInfos[0];

                String userName = this.postInfo.getUsername();
                String password = this.postInfo.getPassword();
                Server server = this.postInfo.getServer();


                return NetworkManager.getInstance()
                        .generateApiKey(userName, password, server.getAddr(), this);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String apikey) {

            if (apikey != null) {

                Server server = this.postInfo.getServer();
                server.addUserProfile(new UserProfile(this.postInfo.username, apikey));

                SettingsManager.getInstance().addServer(server, getActivity());

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.serverAddedToList)
                        .setMessage(R.string.setServerAsActive)
                        .setPositiveButton(R.string.yes, ((dialogInterface, i) -> {
                            UserProfile userProfile = server.getUserProfiles().get(0);
                            PostInfo postInfo = new PostInfo(userProfile, server);
                            SettingsManager.getInstance()
                                    .setPostInfo(postInfo, getActivity());
                            reloadServerList();
                        }))
                        .setNegativeButton(R.string.no, ((dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        }))
                        .create().show();

                reloadServerList();
            }

            super.onPostExecute(apikey);
        }

        @Override
        public void onError(String message) {
            // make sure we run this on the UI thread
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(this.activity, "Unable to create apikey", Toast.LENGTH_SHORT)
                            .show());
        }
    }

    /**
     * Wrapper class for information needed to create an apikey. Due to not saving the password in
     * the user model we can't use our normal PostInfo wrapper.
     */
    private class ApiKeyPostInfo {
        private Server server;
        private String username, password;

        public ApiKeyPostInfo(String username, Server server, String password) {
            this.username = username;
            this.server = server;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public Server getServer() {
            return server;
        }

        public String getPassword() {
            return password;
        }
    }
}
