package de.michael.filebinmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.michael.filebinmobile.R;

class SelectedFileViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.imgFileThumbnail)
    ImageView imgFileThumbnail;

    @BindView(R.id.txtSelectedFileName)
    TextView txtSelectedFileName;

    @BindView(R.id.btnDeselectFile)
    ImageButton btnDeselectFile;

    public SelectedFileViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
