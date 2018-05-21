package de.michael.filebinmobile.refactor

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import de.michael.filebinmobile.R
import de.michael.filebinmobile.controller.refactor.SettingsManager
import de.michael.filebinmobile.fragments.refactor.HistoryFragment
import de.michael.filebinmobile.fragments.refactor.PasteFragment
import de.michael.filebinmobile.fragments.refactor.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val onNavigationRequest: (Int) -> Unit = {
        bnvMainNavigation.selectedItemId = it
    }

    private val onNavItemSelected = { item: MenuItem ->
        when (item.itemId) {
            R.id.navigation_paste -> {
                val pasteFragment = PasteFragment()
                pasteFragment.onTabNavigationRequested = onNavigationRequest
                switchFragment(pasteFragment)
                true

            }
            R.id.navigation_history -> {
                val historyFragment = HistoryFragment()
                historyFragment.onTabNavigationRequested = onNavigationRequest
                switchFragment(historyFragment)
                true
            }
            R.id.navigation_server_settings -> {
                val settingsFragment = SettingsFragment()
                settingsFragment.onTabNavigationRequested = onNavigationRequest
                switchFragment(settingsFragment)
                true
            }
            else -> false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        setContentView(R.layout.activity_main)

        // add our "home"/main fragment
        val pasteFragment = PasteFragment()
        // add our navigation function so we can send navigation requests from inside the fragment
        pasteFragment.onTabNavigationRequested = onNavigationRequest

        // navigation listener for our navigation bar at the bottom
        bnvMainNavigation.setOnNavigationItemSelectedListener(onNavItemSelected)

        // First launch?
        if (SettingsManager.hasLaunchedBefore(this)) {
            onFirstLaunch()
            SettingsManager.setHasLaunchedBefore(this, true)
        }
    }

    private fun onFirstLaunch() {
        AlertDialog.Builder(this)
                .setTitle(R.string.welcome)
                .setMessage(R.string.welcomeMsg)
                .setPositiveButton(R.string.navToServerSettings) { _, _ ->
                    onNavigationRequest(R.id.navigation_server_settings)
                }
                .setNegativeButton(R.string.no) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
    }

    private fun switchFragment(fragment: Fragment) {

        // do not add any transitions of bottom bar navigation views to back stack
        // as stated in android design guidelines. See link below. However we will implement the
        // behaviour for pressing back as returning to our "home" view, the first menu item (Paste)
        // https://material.io/guidelines/components/bottom-navigation.html#bottom-navigation-behavior

        supportFragmentManager.beginTransaction()
                .replace(R.id.frlMainContent, fragment)
                .commit()
    }
}