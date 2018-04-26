package de.michael.filebinmobile.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.michael.filebinmobile.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class UploadUrlAdapter extends SimpleDataAdapter<UploadUrlViewHolder, String> {

    public UploadUrlAdapter(Activity activity) {
        super(activity);
    }

    @Override
    public UploadUrlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_upload_url, null);

        return new UploadUrlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UploadUrlViewHolder holder, int position) {

        String url = getData().get(position);

        holder.txtUploadUrl.setText(url);

        holder.btnClipboardUrl.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("FileBin mobile url", url);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Url copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return getData().size();
    }
}
