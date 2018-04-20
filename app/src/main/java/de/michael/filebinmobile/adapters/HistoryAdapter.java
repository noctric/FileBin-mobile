package de.michael.filebinmobile.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.model.Upload;

public class HistoryAdapter extends SimpleDataAdapter<HistoryViewHolder, Upload> {

    private final DateFormat DATE_FORMAT = DateFormat.getDateInstance();

    private ArrayList<Upload> deleteUploads = new ArrayList<>();

    public HistoryAdapter(Activity activity) {
        super(activity);
    }

    public ArrayList<Upload> getDeleteUploads() {
        return deleteUploads;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_upload_history, null);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {

        final Upload item = this.getData().get(position);

        // don't forget that we are handling a unix time stamp
        String formattedDateString = DATE_FORMAT.format(new Date(item.getUploadTimeStamp() * 1000));

        holder.txtUploadName.setText(item.getUploadTitle());
        holder.txtUploadSize.setText(item.getUploadSize());
        holder.txtUploadTimeStamp.setText(formattedDateString);

        holder.ckbDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean checked = ((CheckBox) view).isChecked();

                if (checked) {

                    if (!deleteUploads.contains(item)) {

                        deleteUploads.add(item);

                    }

                } else {

                    if (deleteUploads.contains(item)) {

                        deleteUploads.remove(item);

                    }

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.getData().size();
    }
}
