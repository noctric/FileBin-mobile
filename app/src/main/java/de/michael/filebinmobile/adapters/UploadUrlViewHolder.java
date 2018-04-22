package de.michael.filebinmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.michael.filebinmobile.R;

public class UploadUrlViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.txtUploadUrl)
    TextView txtUploadUrl;

    @BindView(R.id.btnClipboardUrl)
    ImageButton btnClipboardUrl;

    public UploadUrlViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }


}
