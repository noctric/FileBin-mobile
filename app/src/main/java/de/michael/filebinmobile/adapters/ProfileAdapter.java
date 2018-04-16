package de.michael.filebinmobile.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.model.UserProfile;

public class ProfileAdapter extends SimpleDataAdapter<ProfileViewHolder, UserProfile> {

    public ProfileAdapter(Activity activity) {
        super(activity);
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_profile, null);

        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {

        UserProfile item = this.getData().get(position);

        holder.txtProfileUserName.setText(item.getUsrName());

    }

    @Override
    public int getItemCount() {
        return this.getData().size();
    }
}
