package de.michael.filebinmobile.fragments;

import android.os.AsyncTask;
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
import de.michael.filebinmobile.adapters.HistoryAdapter;
import de.michael.filebinmobile.controller.NetworkManager;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.model.PostInfo;
import de.michael.filebinmobile.model.Server;
import de.michael.filebinmobile.model.Upload;
import de.michael.filebinmobile.model.UserProfile;

public class HistoryFragment extends Fragment {

    @BindView(R.id.rclUploadHistory)
    RecyclerView rclUploadHistory;

    private HistoryAdapter adapter;

    private LoadHistoryTask loadHistoryTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new HistoryAdapter(getActivity());

        PostInfo postInfo = SettingsManager.getInstance().getPostInfo(getActivity());

        if (postInfo != null) {

            // reference our task globally so we can cancel it in case the fragment get's destroyed
            this.loadHistoryTask = new LoadHistoryTask();
            this.loadHistoryTask.execute(postInfo);

        }

    }

    @Override
    public void onDestroy() {

        // cancel our task to avoid memory leaks
        if (this.loadHistoryTask != null) {
            this.loadHistoryTask.cancel(true);
        }

        super.onDestroy();

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

        return view;
    }


    private class LoadHistoryTask extends AsyncTask<PostInfo, Integer, ArrayList<Upload>> {

        @Override
        protected ArrayList<Upload> doInBackground(PostInfo... postInfos) {

            if (postInfos.length > 0) {

                UserProfile userProfile = postInfos[0].getUserProfile();
                Server server = postInfos[0].getServer();

                return NetworkManager.getInstance().loadUploadHistory(userProfile, server);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Upload> uploads) {
            super.onPostExecute(uploads);

            adapter.updateData(uploads);
        }
    }
}
