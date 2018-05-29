package de.michael.filebinmobile.fragments

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.view.ViewGroup
import android.widget.Toast
import de.michael.filebinmobile.R
import de.michael.filebinmobile.controller.SettingsManager
import de.michael.filebinmobile.model.Upload
import de.michael.filebinmobile.view.GridViewItemDecorator


abstract class NavigationFragment : Fragment() {
    var onTabNavigationRequested: (menuItemId: Int) -> Unit = {}

    val createAndShowToastOnUIThread: (String) -> Unit = { message ->
        // make sure we run this on the UI thread
        Handler(Looper.getMainLooper()).post({
            Toast.makeText(this.activity!!, message, Toast.LENGTH_SHORT).show()
        })
    }

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

    fun openInBrowser(upload: Upload) {
        val address = SettingsManager.getPostInfo(activity!!)!!.address
        val uploadUrl = "$address/${upload.id}/"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uploadUrl)))
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

fun RecyclerView.setUpGridList(columnNum: Int) {
    val gridLayoutManager = GridLayoutManager(this.context!!, columnNum)
    gridLayoutManager.orientation = GridLayoutManager.VERTICAL

    val decorator = GridViewItemDecorator(resources.getDimensionPixelSize(R.dimen.grid_spacing), columnNum)
    this.addItemDecoration(decorator)

    this.layoutManager = gridLayoutManager
    this.itemAnimator = DefaultItemAnimator()
    this.setHasFixedSize(true)
}