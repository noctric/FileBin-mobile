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
import de.michael.filebinmobile.adapters.HistoryAdapter;
import de.michael.filebinmobile.model.Upload;

public class HistoryFragment extends Fragment {

    @BindView(R.id.rclUploadHistory)
    RecyclerView rclUploadHistory;

    private HistoryAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new HistoryAdapter();

        //region mock some history items
        ArrayList<Upload> fakeUploads = new ArrayList<>();

        fakeUploads.add(new Upload("Upload 1", "240 kB", 123456));
        fakeUploads.add(new Upload("Upload 2", "240 kB", 123456));
        fakeUploads.add(new Upload("Upload 3", "240 kB", 123456));
        fakeUploads.add(new Upload("Upload 4", "240 kB", 123456));
        fakeUploads.add(new Upload("Upload 5", "240 kB", 123456));
        fakeUploads.add(new Upload("Upload 6", "240 kB", 123456));
        fakeUploads.add(new Upload("Upload 7", "240 kB", 123456));
        fakeUploads.add(new Upload("Upload 8", "240 kB", 123456));
        fakeUploads.add(new Upload("Upload 9", "240 kB", 123456));
        fakeUploads.add(new Upload("Upload 10", "240 kB", 123456));

        this.adapter.updateData(fakeUploads);
        //endregion

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
}
