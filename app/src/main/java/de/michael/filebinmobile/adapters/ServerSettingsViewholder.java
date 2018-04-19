package de.michael.filebinmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.michael.filebinmobile.R;

public class ServerSettingsViewholder extends RecyclerView.ViewHolder {

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtAddr)
    TextView txtAddr;

    @BindView(R.id.btnDelete)
    ImageButton btnDelete;

    @BindView(R.id.btnSetForUpload)
    ImageButton btnSetForUpload;

    public ServerSettingsViewholder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
