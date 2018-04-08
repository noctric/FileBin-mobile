package de.michael.filebinmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.michael.filebinmobile.R;

class ProfileViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.txtProfileUserName)
    TextView txtProfileUserName;

    public ProfileViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
