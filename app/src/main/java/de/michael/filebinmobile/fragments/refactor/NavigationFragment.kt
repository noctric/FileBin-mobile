package de.michael.filebinmobile.fragments.refactor

import android.support.v4.app.Fragment


abstract class NavigationFragment : Fragment() {
    var onTabNavigationRequested: (menuItemId: Int) -> Unit = {}

    abstract fun cancelAllPossiblyRunningTasks()

    // first called in lifecycle if fragment is replaced or removed
    override fun onPause() {
        cancelAllPossiblyRunningTasks()
        super.onPause()
    }
}