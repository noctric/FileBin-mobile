package de.michael.filebinmobile.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.Date;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.model.Upload;

public class HistoryAdapter extends SimpleDataAdapter<HistoryViewHolder, Upload> {

    private final DateFormat DATE_FORMAT = DateFormat.getDateInstance();

    public HistoryAdapter(Activity activity) {
        super(activity);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_upload_history, null);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {

        Upload item = this.getData().get(position);
        String formattedDateString = DATE_FORMAT.format(new Date(item.getUploadTimeStamp()));

        holder.txtUploadName.setText(item.getUploadTitle());
        holder.txtUploadSize.setText(item.getUploadSize());
        holder.txtUploadTimeStamp.setText(formattedDateString);

    }

    @Override
    public int getItemCount() {
        return this.getData().size();
    }
}
