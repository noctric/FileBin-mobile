package de.michael.filebinmobile.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.michael.filebinmobile.R
import de.michael.filebinmobile.adapters.ServerSettingsAdapter
import de.michael.filebinmobile.controller.NetworkManager
import de.michael.filebinmobile.controller.NetworkManager.updateServerInfo
import de.michael.filebinmobile.controller.SettingsManager
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

    private var loadServerInfoTask: LoadServerInfoTask? by Delegates.observable(null) { _, oldVal: LoadServerInfoTask?, _: LoadServerInfoTask? ->
        // make sure we cancel the old task before creating a new one
        oldVal?.cancel(true)
    }

    override fun cancelAllPossiblyRunningTasks() {
        createApiKeyTask = null
        loadServerInfoTask = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.server_settings_fragment, container, false)
                ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun init() {

        this.adapter = ServerSettingsAdapter(activity!!)

        rclServerList.setup()
        rclServerList.adapter = this.adapter

        fbaAddServer.setOnClickListener {
            val dialogView = LayoutInflater.from(activity)
                    .inflate(R.layout.edit_server_settings, null)

            var isValidUrl = false

            dialogView.edtEditAddress.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    isValidUrl = Patterns.WEB_URL.matcher(p0?.toString()).matches()
                    if (isValidUrl) {
                        dialogView.edtEditAddress.background
                                .setColorFilter(resources.getColor(R.color.colorError), PorterDuff.Mode.SRC_IN)
                    } else {
                        dialogView.edtEditAddress.background
                                .setColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    /* empty */
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    /* empty */
                }

            })
            AlertDialog.Builder(activity)
                    .setTitle("Add Server")
                    .setView(dialogView)
                    .setPositiveButton(R.string.ok) { _: DialogInterface, _: Int ->
                        if (isValidUrl) {
                            val serverName = dialogView.edtEditName.text.toString()
                            val serverAddr = dialogView.edtEditAddress.text.toString()
                            val userName = dialogView.edtUserName.text.toString()

                            val server = Server(serverAddr, serverName)

                            val apiKeyPostInfo = ApiKeyPostInfo(server, userName, dialogView.edtUserPassword.text.toString())

                            createApiKeyTask = CreateApiKeyTask()
                            createApiKeyTask?.execute(apiKeyPostInfo)

                            Toast.makeText(activity, "Creating Api Key", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "URL seems to be invalid", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(R.string.cancel) { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                    .create().show()
        }

    }

    override fun onStart() {
        init()
        reloadServerList()
        super.onStart()
    }

    override fun onResume() {
        reloadServerList()
        super.onResume()
    }

    private fun reloadServerList() {
        // update active status of added server (even if user hasn't set the new server as
        // active, we still check here)
        val postInfo = SettingsManager.getPostInfo(activity!!)
        this.adapter!!.selectedPostInfo = postInfo

        val serverList = SettingsManager.getServerList(activity!!)

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
                        .addServer(activity!!, server)

                AlertDialog.Builder(activity)
                        .setTitle(R.string.serverAddedToList)
                        .setMessage(R.string.setServerAsActive)
                        .setPositiveButton(R.string.yes) { _, i ->
                            SettingsManager.setPostInfo(activity!!, server)
                            reloadServerList()
                        }
                        .setNegativeButton(R.string.no) { dialogInterface, _ -> dialogInterface.dismiss() }
                        .create().show()

                // Load the server info and replace result from this task if success
                loadServerInfoTask = LoadServerInfoTask()
                loadServerInfoTask?.execute(server)

            }
            super.onPostExecute(result)
        }
    }

    /**
     * Due to the fact that this requires an extra api call we wrap it in it's own AsyncTask to
     * maintain some structure.
     * This results in extra work done by the settings manager, as we write to shared prefs after
     * adding the server and creating an api key and then overwrite the saved server after this task
     * finished successfully.
     */
    private inner class LoadServerInfoTask : AsyncTask<Server, Int, Pair<Server, Server>?>() {
        override fun doInBackground(vararg params: Server): Pair<Server, Server>? {
            if (params.isNotEmpty()) {
                val oldVal = params[0].copy()
                params[0].updateServerInfo()
                // we return a pair consisting of old value and new value so we can effectively
                // replace the current saved server
                return Pair(oldVal, params[0])
            }
            return null
        }

        override fun onPostExecute(result: Pair<Server, Server>?) {
            if (result != null) {
                SettingsManager.deleteServer(activity!!, result.first)
                SettingsManager.addServer(activity!!, result.second)
                val isCurrentUploadServer = result.first == SettingsManager.getPostInfo(activity!!)
                if (isCurrentUploadServer) {
                    // update our postInfo too
                    SettingsManager.setPostInfo(activity!!, result.second)
                }
            }
            super.onPostExecute(result)
        }
    }

}

private data class ApiKeyPostInfo(val server: Server, val userName: String, val password: String)