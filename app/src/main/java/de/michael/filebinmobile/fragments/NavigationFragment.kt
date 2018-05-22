package de.michael.filebinmobile.fragments

import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import de.michael.filebinmobile.R


abstract class NavigationFragment : Fragment() {
    var onTabNavigationRequested: (menuItemId: Int) -> Unit = {}

    abstract fun cancelAllPossiblyRunningTasks()

    // first called in lifecycle if fragment is replaced or removed
    override fun onPause() {
        cancelAllPossiblyRunningTasks()
        super.onPause()
    }

    fun showNoServerSelectedDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Woops")
                .setMessage("No Server selected. Please go to Server Settings and select one.")
                .setPositiveButton(R.string.ok) { dialogInterface, _ ->

                    dialogInterface.dismiss()

                    onTabNavigationRequested(R.id.navigation_server_settings)

                }.setNegativeButton(R.string.cancel
                ) { dialogInterface, i ->

                    dialogInterface.dismiss()

                }
        builder.create().show()
    }
}