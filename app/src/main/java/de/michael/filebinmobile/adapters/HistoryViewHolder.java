package de.michael.filebinmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.michael.filebinmobile.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.txtUploadName)
    TextView txtUploadName;

    @BindView(R.id.txtUploadSize)
    TextView txtUploadSize;

    @BindView(R.id.txtUploadTimeStamp)
    TextView txtUploadTimeStamp;

    public HistoryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
