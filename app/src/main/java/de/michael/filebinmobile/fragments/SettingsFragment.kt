package de.michael.filebinmobile.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.michael.filebinmobile.R
import de.michael.filebinmobile.adapters.ServerSettingsAdapter
import de.michael.filebinmobile.controller.NetworkManager
import de.michael.filebinmobile.controller.SettingsManager
import de.michael.filebinmobile.model.PostInfo
import de.michael.filebinmobile.model.Server
import de.michael.filebinmobile.model.UserProfile
import kotlinx.android.synthetic.main.edit_server_settings.view.*
import kotlinx.android.synthetic.main.server_settings_fragment.*
import kotlin.properties.Delegates

class SettingsFragment : NavigationFragment() {

    var adapter: ServerSettingsAdapter? = null

    private var createApiKeyTask: CreateApiKeyTask? by Delegates.observable(null) { _, oldVal: CreateApiKeyTask?, _: CreateApiKeyTask? ->
        // make sure we cancel the old task before creating a new one
        oldVal?.cancel(true)
    }

    override fun cancelAllPossiblyRunningTasks() {
        createApiKeyTask = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.server_settings_fragment, container, false)
                ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun init(view: View) {

        this.adapter = ServerSettingsAdapter(activity)

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val dividerItemDecoration = DividerItemDecoration(this.rclServerList.context,
                linearLayoutManager.orientation)

        rclServerList.layoutManager = linearLayoutManager
        rclServerList.itemAnimator = DefaultItemAnimator()
        rclServerList.adapter = this.adapter
        rclServerList.addItemDecoration(dividerItemDecoration)


        fbaAddServer.setOnClickListener {
            val dialogView = LayoutInflater.from(activity)
                    .inflate(R.layout.edit_server_settings, null)
            AlertDialog.Builder(activity)
                    .setTitle("Add Server")
                    .setPositiveButton(R.string.ok) { _: DialogInterface, _: Int ->
                        val serverName = dialogView.edtEditName.text.toString()
                        val serverAddr = dialogView.edtEditAddress.text.toString()
                        val userName = dialogView.edtUserName.text.toString()

                        val server = Server(serverName,
                                serverAddr)

                        val apiKeyPostInfo = ApiKeyPostInfo(server, userName, dialogView.edtUserPassword.text.toString())

                        createApiKeyTask = CreateApiKeyTask()
                        createApiKeyTask?.execute(apiKeyPostInfo)

                        Toast.makeText(activity, "Creating Api Key", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(R.string.cancel) { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                    .create().show()
        }

    }

    override fun onStart() {
        init(view)
        reloadServerList()
        super.onStart()
    }

    override fun onResume() {
        reloadServerList()
        super.onResume()
    }

    private fun reloadServerList() {
        // update active status of added server (even if user hasn't set the new server as
        // active, we still check here.
        val postInfo = SettingsManager.getPostInfo(activity)
        this.adapter!!.selectedPostInfo = postInfo

        val serverList = SettingsManager.getServerList(activity)

        this.adapter!!.updateData(serverList)

        if (this.adapter!!.itemCount <= 0) {
            txtEmptyServerList.visibility = View.VISIBLE
        } else {
            txtEmptyServerList.visibility = View.GONE
        }
    }

    private inner class CreateApiKeyTask : AsyncTask<ApiKeyPostInfo, Int, String?>() {

        var postInfo: ApiKeyPostInfo? = null

        override fun doInBackground(vararg apiKeyPostInfo: ApiKeyPostInfo): String? {
            if (apiKeyPostInfo.isNotEmpty()) {

                this.postInfo = apiKeyPostInfo[0]

                return NetworkManager.generateApiKey(
                        apiKeyPostInfo[0].userName,
                        apiKeyPostInfo[0].password,
                        apiKeyPostInfo[0].server.address)
            }

            throw IllegalArgumentException("Not enough information to generate an api key")
        }

        override fun onPostExecute(result: String?) {
            if (!result.isNullOrBlank()) {

                val userProfile = UserProfile(this.postInfo!!.userName, result!!)
                val server = this.postInfo!!.server.copy(userProfile = userProfile)

                SettingsManager
                        .addServer(activity, server)

                AlertDialog.Builder(activity)
                        .setTitle(R.string.serverAddedToList)
                        .setMessage(R.string.setServerAsActive)
                        .setPositiveButton(R.string.yes) { _, i ->
                            SettingsManager.setPostInfo(activity, PostInfo(server, userProfile))
                            reloadServerList()
                        }
                        .setNegativeButton(R.string.no) { dialogInterface, i -> dialogInterface.dismiss() }
                        .create().show()

            }
            super.onPostExecute(result)
        }
    }

}

private data class ApiKeyPostInfo(val server: Server, val userName: String, val password: String)