package de.michael.filebinmobile.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.michael.filebinmobile.R
import de.michael.filebinmobile.adapters.SelectedFilesAdapter
import de.michael.filebinmobile.adapters.UploadUrlAdapter
import de.michael.filebinmobile.controller.NetworkManager
import de.michael.filebinmobile.controller.SettingsManager
import de.michael.filebinmobile.model.Server
import de.michael.filebinmobile.util.FileUtil
import kotlinx.android.synthetic.main.any_recycler_view.view.*
import kotlinx.android.synthetic.main.paste_fragment.*
import java.io.File
import java.io.OutputStreamWriter
import kotlin.properties.Delegates

private const val READ_REQUEST_CODE: Int = 1
private const val FILE_NAME_DEFAULT = "stdin"

class PasteFragment : NavigationFragment() {

    private var uploadFilesTask: UploadFileTask? by Delegates.observable(null) { _, oldVal: UploadFileTask?, _: UploadFileTask? ->
        // make sure we cancel the old task before creating a new one
        oldVal?.cancel(true)
    }

    private var selectedFilesAdapter: SelectedFilesAdapter? = null

    private val filesToUpload: MutableList<File> = mutableListOf()

    override fun cancelAllPossiblyRunningTasks() {
        uploadFilesTask = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.paste_fragment, container, false)
                ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        init()
        super.onStart()
    }

    override fun onResume() {

        val postInfo = SettingsManager.getPostInfo(activity!!)
        txtSelectedServer.text = "Uploads to ${postInfo!!.name} by ${postInfo!!.userProfile!!.usrName}"

        super.onResume()
    }

    private fun init() {
        selectedFilesAdapter = SelectedFilesAdapter()
        selectedFilesAdapter?.updateData(filesToUpload)

        btnSelectFiles.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"

            startActivityForResult(intent, READ_REQUEST_CODE)

        }

        btnPasteUpload.setOnClickListener {
            val content = edtPasteText.text.toString()
            if (content.isNotBlank()) {
                writeToFile(content)
                val filePath = "${activity!!.filesDir}${File.separator}$FILE_NAME_DEFAULT"
                filesToUpload.add(File(filePath))
            }

            if (this.filesToUpload.isNotEmpty()) {
                // ui adjustments
                pgbUploadProgress.visibility = View.VISIBLE
                btnPasteUpload.isEnabled = false

                // start upload
                val postInfo = SettingsManager.getPostInfo(activity!!)
                if (postInfo != null) {
                    startUpload(postInfo)
                } else {

                    val builder = AlertDialog.Builder(activity!!)
                    builder.setTitle("Woops")
                            .setMessage("No Server for uploading selected. Please go to Server Settings and select one.")
                            .setPositiveButton(R.string.ok) { dialogInterface, _ ->

                                dialogInterface.dismiss()

                                onTabNavigationRequested(R.id.navigation_server_settings)

                            }.setNegativeButton(R.string.cancel
                            ) { dialogInterface, i ->

                                dialogInterface.dismiss()

                            }
                    builder.create().show()
                }

            } else {
                Toast.makeText(activity, "Nothing to upload!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startUpload(postInfo: Server) {
        this.uploadFilesTask = UploadFileTask()
        uploadFilesTask?.execute(postInfo)
    }

    private fun writeToFile(content: String) {
        val fileOutputStream = activity!!.openFileOutput(FILE_NAME_DEFAULT, Context.MODE_PRIVATE)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        outputStreamWriter.write(content)
        outputStreamWriter.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data

            val fileCopy = FileUtil.createFileCopyFromUri(uri, activity!!)
            filesToUpload.add(fileCopy)
            selectedFilesAdapter?.updateData(filesToUpload)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private inner class UploadFileTask : AsyncTask<Server, Int, List<String>>() {
        override fun doInBackground(vararg postInfos: Server): List<String> {
            if (postInfos.isNotEmpty()) {

                return NetworkManager.pasteUploadFiles(postInfos[0].userProfile!!,
                        postInfos[0], this@PasteFragment.filesToUpload)

            }

            return emptyList()
        }

        override fun onPostExecute(result: List<String>) {

            pgbUploadProgress.visibility = View.INVISIBLE
            btnPasteUpload.isEnabled = true

            val uploadUrlAdapter = UploadUrlAdapter(activity!!)
            uploadUrlAdapter.updateData(result)

            val linearLayoutManager = LinearLayoutManager(activity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

            val dividerItemDecoration = DividerItemDecoration(activity,
                    linearLayoutManager.orientation)

            val urlListView = LayoutInflater.from(context!!).inflate(R.layout.any_recycler_view, null)

            urlListView.rclAnyRecyclerView.layoutManager = linearLayoutManager
            urlListView.rclAnyRecyclerView.itemAnimator = DefaultItemAnimator()
            urlListView.rclAnyRecyclerView.adapter = uploadUrlAdapter
            urlListView.rclAnyRecyclerView.addItemDecoration(dividerItemDecoration)

            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle("Upload completed")
                    .setView(urlListView)
                    .setPositiveButton(R.string.ok) { dialogInterface, i -> dialogInterface.dismiss() }

            builder.create().show()

            // clear selected files
            // clear our selected files
            edtPasteText.text.clear()
            filesToUpload.clear()
            selectedFilesAdapter?.clear()

            super.onPostExecute(result)
        }

    }

}

