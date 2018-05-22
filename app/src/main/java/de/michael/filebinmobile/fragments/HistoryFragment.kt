package de.michael.filebinmobile.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import de.michael.filebinmobile.R
import de.michael.filebinmobile.adapters.HistoryAdapter
import de.michael.filebinmobile.controller.NetworkManager
import de.michael.filebinmobile.controller.SettingsManager
import de.michael.filebinmobile.model.Server
import de.michael.filebinmobile.model.Upload
import kotlinx.android.synthetic.main.history_fragment.*
import kotlin.properties.Delegates

class HistoryFragment : NavigationFragment() {

    var adapter: HistoryAdapter? = null

    private var deleteUploadsTask: DeleteUploadsTask? by Delegates.observable(null) { _, oldVal: DeleteUploadsTask?, _: DeleteUploadsTask? ->
        // make sure we cancel the old task before creating a new one
        oldVal?.cancel(true)
    }

    private var loadHistoryTask: LoadHistoryTask? by Delegates.observable(null) { _, oldVal: LoadHistoryTask?, _: LoadHistoryTask? ->
        // make sure we cancel the old task before creating a new one
        oldVal?.cancel(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
                ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        init()

        super.onStart()
    }

    private fun init() {

        this.adapter = HistoryAdapter()

        val linearLayoutManager = LinearLayoutManager(this.activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val dividerItemDecoration = DividerItemDecoration(this.activity,
                linearLayoutManager.orientation)

        rclUploadHistory.layoutManager = linearLayoutManager
        rclUploadHistory.itemAnimator = DefaultItemAnimator()
        rclUploadHistory.adapter = this.adapter
        rclUploadHistory.addItemDecoration(dividerItemDecoration)

        fbaDeleteCheckedItems.setOnClickListener {
            val postInfo = SettingsManager.getPostInfo(activity!!)

            if (postInfo != null) {
                this.deleteUploadsTask = DeleteUploadsTask()
                this.deleteUploadsTask?.execute(postInfo)
            }
        }

        reloadHistory()
    }

    override fun cancelAllPossiblyRunningTasks() {
        // using the object delegate observable we cancel a task when it's reference is changed
        deleteUploadsTask = null
        loadHistoryTask = null
    }

    private fun reloadHistory() {
        val postInfo = SettingsManager.getPostInfo(activity!!)
        if (postInfo != null) {
            this.loadHistoryTask = LoadHistoryTask()
            this.loadHistoryTask?.execute(postInfo)

            pgbLoadHistory.visibility = View.VISIBLE
        }
    }

    private inner class LoadHistoryTask : AsyncTask<Server, Int, List<Upload>>() {
        override fun doInBackground(vararg postInfos: Server): List<Upload> {
            if (postInfos.isNotEmpty()) {

                return NetworkManager.loadUploadHistory(postInfos[0].userProfile!!, postInfos[0])
                        ?: emptyList()

            }

            return emptyList()
        }

        override fun onPostExecute(result: List<Upload>) {
            adapter?.updateData(result)
            pgbLoadHistory.visibility = View.GONE
            super.onPostExecute(result)
        }
    }

    private inner class DeleteUploadsTask() : AsyncTask<Server, Int, Boolean>() {
        override fun doInBackground(vararg postInfos: Server): Boolean {
            if (postInfos.isNotEmpty()) {

                return NetworkManager.deleteUploads(postInfos[0], adapter!!.deleteUploads)

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