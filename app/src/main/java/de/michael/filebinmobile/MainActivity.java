package de.michael.filebinmobile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.michael.filebinmobile.fragments.HistoryFragment;
import de.michael.filebinmobile.fragments.PasteFragment;
import de.michael.filebinmobile.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bnvMainNavigation)
    BottomNavigationView bnvMainNavigation;

    // TODO remove global vars and try to retrieve fragments by id
    private Fragment pasteFragment, historyFragment, settingsFragment;

    // TODO only bring fragment to front instead of adding it multiple times to backstack
    // TODO also the highlighting of the current menu point doesn't update itself when pressing back
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_paste:
                    if (pasteFragment == null) {

                        pasteFragment = new PasteFragment();

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
        manager.beginTransaction().replace(R.id.frlMainContent, fragment)
                .addToBackStack(null).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.pasteFragment = new PasteFragment();

        // don't add the first transaction the the backstack so we don't have this weird empty view
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frlMainContent, this.pasteFragment).commit();

        bnvMainNavigation.setOnNavigationItemSelectedListener(this.onNavigationItemSelectedListener);


    }

}
