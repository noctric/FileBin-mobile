package de.michael.filebinmobile.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michael.filebinmobile.R
import de.michael.filebinmobile.controller.SettingsManager
import de.michael.filebinmobile.model.PostInfo
import de.michael.filebinmobile.model.Server
import kotlinx.android.synthetic.main.list_item_server_setting.view.*

class ServerSettingsAdapter(activity: Activity) : SimpleDataAdapter<ServerSettingsViewHolder, Server>(activity) {

    var selectedPostInfo: PostInfo? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerSettingsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_server_setting, null)
        return ServerSettingsViewHolder(view, activity)
    }

}

class ServerSettingsViewHolder(itemView: View, val activity: Activity) : AbstractViewHolder<Server>(itemView) {
    override fun bindItem(item: Server, removeItem: (Int) -> Unit, pos: Int) {
        itemView.txtName.text = item.name
        itemView.txtAddr.text = item.address

        itemView.btnDelete.setOnClickListener {
            AlertDialog.Builder(activity)
                    .setTitle("Delete Server ${item.name}")
                    .setMessage("Are you sure you want do delete this Server?")
                    .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                        SettingsManager.deleteServer(activity, item)
                        removeItem(pos)
                    }
        }

        itemView.btnSetForUpload.setOnClickListener {
            val userProfile = item.userProfile
            PostInfo(item, userProfile)
            SettingsManager.setPostInfo(activity, PostInfo(item, userProfile))
            itemView.txtIsProfileActive.text = activity.getString(R.string.active)
            itemView.txtIsProfileActive.setTextColor(activity.resources.getColor(R.color.colorAccent))
            // TODO refresh list
        }
    }
}