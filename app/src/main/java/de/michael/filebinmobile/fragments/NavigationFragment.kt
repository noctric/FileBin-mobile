package de.michael.filebinmobile.fragments

import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
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

// for now we just leave it here as it is the only view related extension function and doesn't
// really belong anywhere :(
fun RecyclerView.setup() {
    val linearLayoutManager = object : LinearLayoutManager(this.context) {
        // override this since it seems to ignore match_parent when setting it in the item layout's
        // xml when displaying it in a dialog (at least)
        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
            return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

    val dividerItemDecoration = DividerItemDecoration(this.context,
            linearLayoutManager.orientation)

    this.layoutManager = linearLayoutManager
    this.itemAnimator = DefaultItemAnimator()
    this.addItemDecoration(dividerItemDecoration)
    this.setHasFixedSize(true)
}