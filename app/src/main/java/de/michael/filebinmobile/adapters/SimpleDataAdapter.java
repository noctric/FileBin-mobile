package de.michael.filebinmobile.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public abstract class SimpleDataAdapter<T extends RecyclerView.ViewHolder, K> extends RecyclerView.Adapter<T> {

    private Activity activity;
    private ArrayList<K> data = new ArrayList<>();
    private OnAdapterDataChangedListener dataChangedListener;

    public SimpleDataAdapter(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public ArrayList<K> getData() {
        return data;
    }

    public OnAdapterDataChangedListener getDataChangedListener() {
        return dataChangedListener;
    }

    public void setDataChangedListener(OnAdapterDataChangedListener dataChangedListener) {
        this.dataChangedListener = dataChangedListener;
    }

    public void updateData(ArrayList<K> serverList) {
        this.data.clear();
        this.data.addAll(serverList);
        notifyDataSetChanged();
    }

    public void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public void add(K item) {
        this.data.add(item);
        notifyDataSetChanged();
    }
}
