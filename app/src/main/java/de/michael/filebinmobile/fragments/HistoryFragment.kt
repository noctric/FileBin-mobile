package de.michael.filebinmobile.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import de.michael.filebinmobile.R
import de.michael.filebinmobile.adapters.HistoryAdapter
import de.michael.filebinmobile.controller.NetworkManager
import de.michael.filebinmobile.controller.SettingsManager
import de.michael.filebinmobile.model.MultiPasteUpload
import de.michael.filebinmobile.model.Server
import de.michael.filebinmobile.model.Upload
import kotlinx.android.synthetic.main.history_fragment.*
import kotlinx.android.synthetic.main.history_fragment.view.*
import kotlin.properties.Delegates

class HistoryFragment : NavigationFragment() {

    var adapter: HistoryAdapter? = null

    private val onListItemClick: (Upload) -> Boolean = {
        openInBrowser(it)
        true
    }

    private var deleteUploadsTask: DeleteUploadsTask? by Delegates.observable(null) { _, oldVal: DeleteUploadsTask?, _: DeleteUploadsTask? ->
        // make sure we cancel the old task before creating a new one
        oldVal?.cancel(true)
    }

    private var loadHistoryTask: LoadHistoryTask? by Delegates.observable(null) { _, oldVal: LoadHistoryTask?, _: LoadHistoryTask? ->
        // make sure we cancel the old task before creating a new one
        oldVal?.cancel(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.history_fragment, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {

        this.adapter = HistoryAdapter(onListItemClick)

        view.rclUploadHistory.setup()
        view.rclUploadHistory.adapter = this.adapter

        view.fbaDeleteCheckedItems.setOnClickListener {
            val postInfo = SettingsManager.getPostInfo(activity!!)

            if (postInfo != null) {
                this.deleteUploadsTask = DeleteUploadsTask()
                this.deleteUploadsTask?.execute(postInfo)
            }
        }

        view.srlRefreshHistory.setOnRefreshListener {
            reloadHistory()
        }

        reloadHistory(view)
    }

    override fun cancelAllPossiblyRunningTasks() {
        // using the object delegate observable we cancel a task when it's reference is changed
        deleteUploadsTask = null
        loadHistoryTask = null
    }

    private fun reloadHistory(view: View = this.view!!) {
        val postInfo = SettingsManager.getPostInfo(activity!!)
        if (postInfo != null) {
            this.loadHistoryTask = LoadHistoryTask()
            this.loadHistoryTask?.execute(postInfo)

            view.pgbLoadHistory.visibility = View.VISIBLE
        } else {
            showNoServerSelectedDialog()
        }
    }

    private inner class LoadHistoryTask : AsyncTask<Server, Int, List<Upload>>() {
        override fun doInBackground(vararg postInfos: Server): List<Upload> {
            if (postInfos.isNotEmpty()) {

                return NetworkManager.loadUploadHistory(
                        postInfos[0].userProfile!!,
                        postInfos[0],
                        createAndShowToastOnUIThread) ?: emptyList()

            }

            return emptyList()
        }

        override fun onPostExecute(result: List<Upload>) {

            for (upload in result) {
                if (upload is MultiPasteUpload) {
                    upload.loadSingleUploadsRefs(result)
                }
            }
            adapter?.updateData(result)
            pgbLoadHistory.visibility = View.GONE

            if (result.isEmpty()) {
                txtEmptyHistory.visibility = View.VISIBLE
            } else {
                txtEmptyHistory.visibility = View.GONE
            }
            srlRefreshHistory.isRefreshing = false

            super.onPostExecute(result)
        }
    }

    private inner class DeleteUploadsTask : AsyncTask<Server, Int, Boolean>() {
        override fun doInBackground(vararg postInfos: Server): Boolean {
            if (postInfos.isNotEmpty()) {

                return NetworkManager.deleteUploads(
                        postInfos[0],
                        adapter!!.deleteUploads,
                        createAndShowToastOnUIThread)

            }

            return false
        }

        override fun onPostExecute(success: Boolean) {

            if (success) {
                reloadHistory()
            } else {
                Toast.makeText(activity, "Could not delete items", LENGTH_SHORT).show()
            }

            super.onPostExecute(success)
        }

    }
}