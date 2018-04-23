package de.michael.filebinmobile.fragments;

import android.support.v4.app.Fragment;

import de.michael.filebinmobile.OnTabNavigationRequestedListener;

public class NavigationFragment extends Fragment {

    private OnTabNavigationRequestedListener onTabNavigationRequestedListener;

    public void setOnTabNavigationRequestedListener(OnTabNavigationRequestedListener onTabNavigationRequestedListener) {
        this.onTabNavigationRequestedListener = onTabNavigationRequestedListener;
    }

    OnTabNavigationRequestedListener getOnTabNavigationRequestedListener() {
        return onTabNavigationRequestedListener;
    }
}
