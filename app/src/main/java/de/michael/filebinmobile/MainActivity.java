package de.michael.filebinmobile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.michael.filebinmobile.controller.SettingsManager;
import de.michael.filebinmobile.fragments.HistoryFragment;
import de.michael.filebinmobile.fragments.PasteFragment;
import de.michael.filebinmobile.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements OnTabNavigationRequestedListener {

    @BindView(R.id.bnvMainNavigation)
    BottomNavigationView bnvMainNavigation;

    // TODO remove global vars and try to retrieve fragments by id
    private Fragment pasteFragment, historyFragment, settingsFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_paste:
                    if (pasteFragment == null) {

                        pasteFragment = new PasteFragment();
                        //TODO create a super class for our fragments and set a listener there
                        ((PasteFragment) pasteFragment).setOnTabNavigationRequestedListener(MainActivity.this);

                    }

                    switchFragment(pasteFragment);
                    return true;
                case R.id.navigation_history:
                    if (historyFragment == null) {

                        historyFragment = new HistoryFragment();

                    }
                    switchFragment(historyFragment);
                    return true;
                case R.id.navigation_server_settings:
                    if (settingsFragment == null) {

                        settingsFragment = new SettingsFragment();

                    }
                    switchFragment(settingsFragment);
                    return true;
            }
            return false;
        }
    };

    private void switchFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();

        // do not add any transitions of bottom bar navigation views to back stack
        // as stated in android design guidelines. See link below. However we will implement the
        // behaviour for pressing back as returning to our "home" view, the first menu item (Paste)
        // https://material.io/guidelines/components/bottom-navigation.html#bottom-navigation-behavior
        manager.beginTransaction().replace(R.id.frlMainContent, fragment).commit();
    }

    @Override
    public void onBackPressed() {

        int selectedItemId = bnvMainNavigation.getSelectedItemId();

        if (selectedItemId != R.id.navigation_paste) {
            bnvMainNavigation.setSelectedItemId(R.id.navigation_paste);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.pasteFragment = new PasteFragment();
        ((PasteFragment) pasteFragment).setOnTabNavigationRequestedListener(MainActivity.this);

        // don't add the first transaction the the backstack so we don't have this weird empty view
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frlMainContent, this.pasteFragment).commit();

        bnvMainNavigation.setOnNavigationItemSelectedListener(this.onNavigationItemSelectedListener);

        // Do some stuff on first launch
        boolean hasLaunchedBefore = SettingsManager.getInstance().hasLaunchedBefore(this);
        if (!hasLaunchedBefore) {

            onFirstLaunch();

        }

    }

    /**
     * We wan't to show a welcome screen and give a short introduction on adding server profiles
     */
    public void onFirstLaunch() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.welcome)
                .setMessage(R.string.welcomeMsg)
                .setPositiveButton(R.string.navToServerSettings, (dialogInterface, i) ->
                        onNavigationRequest(R.id.navigation_server_settings))
                .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss())
                .create().show();


    }

    @Override
    public void onNavigationRequest(int menuItemId) {
        bnvMainNavigation.setSelectedItemId(menuItemId);
    }
}
