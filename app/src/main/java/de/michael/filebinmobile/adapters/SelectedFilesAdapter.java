package de.michael.filebinmobile.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import de.michael.filebinmobile.R;

public class SelectedFilesAdapter extends SimpleDataAdapter<SelectedFileViewHolder, File> {

    private OnDataRemovedListener onDataRemovedListener;

    public SelectedFilesAdapter(Activity activity) {
        super(activity);
    }

    public void setOnDataRemovedListener(OnDataRemovedListener onDataRemovedListener) {
        this.onDataRemovedListener = onDataRemovedListener;
    }

    @Override
    public SelectedFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_selected_file, null);

        return new SelectedFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectedFileViewHolder holder, int position) {

        File item = getData().get(position);

        holder.txtSelectedFileName.setText(item.getName());

        holder.btnDeselectFile.setOnClickListener(view -> {

            // update any data linked to the adapter
            if (onDataRemovedListener != null) {
                onDataRemovedListener.onDataRemoved(position);
            }

        });

    }

    @Override
    public int getItemCount() {
        return getData().size();
    }
}
