package de.michael.filebinmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.model.Upload;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private ArrayList<Upload> uploadHistory = new ArrayList<>();
    private final DateFormat DATE_FORMAT = DateFormat.getDateInstance();

    public void updateData(ArrayList<Upload> uploadHistory) {
        this.uploadHistory.clear();
        this.uploadHistory.addAll(uploadHistory);
        notifyDataSetChanged();
    }

    public void clear() {
        this.uploadHistory.clear();
        notifyDataSetChanged();
    }

    public void add(Upload upload) {
        this.uploadHistory.add(upload);
        notifyDataSetChanged();
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_upload_history, null);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {

        Upload item = this.uploadHistory.get(position);
        String formattedDateString = DATE_FORMAT.format(new Date(item.getUploadTimeStamp()));

        holder.txtUploadName.setText(item.getUploadTitle());
        holder.txtUploadSize.setText(item.getUploadSize());
        holder.txtUploadTimeStamp.setText(formattedDateString);

    }

    @Override
    public int getItemCount() {
        return uploadHistory.size();
    }
}
