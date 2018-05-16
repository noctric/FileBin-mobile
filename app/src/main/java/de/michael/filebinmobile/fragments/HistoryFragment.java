package de.michael.filebinmobile.fragments;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.michael.filebinmobile.R;
import de.michael.filebinmobile.adapters.HistoryAdapter;
import de.michael.filebinmobile.controller.NetworkManager;
import de.michael.filebinmobile.controller.OnErrorOccurredCallback;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.model.PostInfo;
import de.michael.filebinmobile.model.Server;
import de.michael.filebinmobile.model.Upload;
import de.michael.filebinmobile.model.UserProfile;

public class HistoryFragment extends NavigationFragment {

    @BindView(R.id.rclUploadHistory)
    RecyclerView rclUploadHistory;

    @BindView(R.id.pgbLoadHistory)
    ProgressBar pgbLoadHistory;

    private HistoryAdapter adapter;

    private LoadHistoryTask loadHistoryTask;
    private DeleteUploadsTask deleteUploadsTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new HistoryAdapter(getActivity());

    }

    private void reloadHistory() {

        PostInfo postInfo = SettingsManager.getInstance().getPostInfo(getActivity());

        if (postInfo != null) {
            // reference our task globally so we can cancel it in case the fragment get's destroyed
            this.loadHistoryTask = new LoadHistoryTask(getActivity());
            this.loadHistoryTask.execute(postInfo);

        }

        pgbLoadHistory.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDetach() {

        // cancel any possibly running tasks to avoid memory leaks
        cancelAllPossiblyRunningTasks();

        super.onDetach();
    }

    @Override
    public void onDestroy() {

        // cancel any possibly running tasks to avoid memory leaks
        cancelAllPossiblyRunningTasks();

        super.onDestroy();

    }

    @Override
    public void onDestroyView() {

        // cancel any possibly running tasks to avoid memory leaks
        cancelAllPossiblyRunningTasks();

        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.rclUploadHistory.getContext(),
                linearLayoutManager.getOrientation());

        this.rclUploadHistory.setLayoutManager(linearLayoutManager);
        this.rclUploadHistory.setItemAnimator(new DefaultItemAnimator());
        this.rclUploadHistory.setAdapter(this.adapter);
        this.rclUploadHistory.addItemDecoration(dividerItemDecoration);

        // seeing as we update our UI both in reloadHistory() and in the asynctask itself we should
        // only call reloadHistory AFTER the view has been bound
        reloadHistory();

        return view;
    }

    @OnClick(R.id.fbaDeleteCheckedItems)
    void deleteUploads() {

        if (this.adapter != null) {

            PostInfo postInfo = SettingsManager.getInstance().getPostInfo(getActivity());

            this.deleteUploadsTask = new DeleteUploadsTask(getActivity());
            this.deleteUploadsTask.execute(postInfo);

        }

    }

    @Override
    void cancelAllPossiblyRunningTasks() {
        // cancel any possibly running tasks to avoid memory leaks
        if (this.loadHistoryTask != null) {
            this.loadHistoryTask.cancel(true);
        }

        if (this.deleteUploadsTask != null) {
            this.deleteUploadsTask.cancel(true);
        }
    }

    private class LoadHistoryTask extends AsyncTask<PostInfo, Integer, ArrayList<Upload>> implements OnErrorOccurredCallback {

        // keep a reference to our activity/context in case we want to do some changes to the UI
        // and prevent the application from crashing
        Activity activity;

        public LoadHistoryTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected ArrayList<Upload> doInBackground(PostInfo... postInfos) {

            if (postInfos.length > 0) {

                UserProfile userProfile = postInfos[0].getUserProfile();
                Server server = postInfos[0].getServer();

                return NetworkManager.getInstance().loadUploadHistory(userProfile, server, this);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Upload> uploads) {

            adapter.updateData(uploads);

            pgbLoadHistory.setVisibility(View.GONE);

            super.onPostExecute(uploads);
        }

        @Override
        public void onError(String message) {

            // make sure we run this on the UI thread
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show()
            );
        }

    }

    private class DeleteUploadsTask extends AsyncTask<PostInfo, Integer, Boolean> implements OnErrorOccurredCallback {

        // keep a reference to our activity/context in case we want to do some changes to the UI
        // and prevent the application from crashing
        Activity activity;

        public DeleteUploadsTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(PostInfo... postInfos) {

            if (postInfos.length > 0) {
                PostInfo postInfo = postInfos[0];
                ArrayList<Upload> uploadList = adapter.getDeleteUploads();

                return NetworkManager.getInstance().deleteUploads(postInfo, uploadList, this);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (aBoolean) {

                reloadHistory();

            } else {

                Toast.makeText(getActivity(), "Could not delete items", Toast.LENGTH_SHORT).show();

            }

            super.onPostExecute(aBoolean);
        }

        @Override
        public void onError(String message) {

            // make sure we run this on the UI thread
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show()
            );

        }
    }
}
