package de.michael.filebinmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.michael.filebinmobile.R;
import de.michael.filebinmobile.model.UserProfile;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileViewHolder> {

    private ArrayList<UserProfile> profileList = new ArrayList<>();

    //TODO put these data methods in a superclass and let ALL our adapters extend it
    public void updateData(ArrayList<UserProfile> profileList) {
        this.profileList.clear();
        this.profileList.addAll(profileList);
        notifyDataSetChanged();
    }

    public void clear() {
        this.profileList.clear();
        notifyDataSetChanged();
    }

    public void add(UserProfile userProfile) {
        this.profileList.add(userProfile);
        notifyDataSetChanged();
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_profile, null);

        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {

        UserProfile item = this.profileList.get(position);

        holder.txtProfileUserName.setText(item.getUsrName());

    }

    @Override
    public int getItemCount() {
        return this.profileList.size();
    }
}
