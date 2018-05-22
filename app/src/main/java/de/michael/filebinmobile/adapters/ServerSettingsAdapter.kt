package de.michael.filebinmobile.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michael.filebinmobile.R
import de.michael.filebinmobile.controller.SettingsManager
import de.michael.filebinmobile.model.Server
import kotlinx.android.synthetic.main.list_item_server_setting.view.*

class ServerSettingsAdapter(val context: Context) : SimpleDataAdapter<ServerSettingsViewHolder, Server>() {

    var selectedPostInfo: Server? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val refreshList: (Server) -> Unit = {
        selectedPostInfo = it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerSettingsViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_server_setting, null)
        return ServerSettingsViewHolder(view, context, refreshList)
    }

    override fun onBindViewHolder(holder: ServerSettingsViewHolder, position: Int) {

        if (data[position] == selectedPostInfo) {
            holder.itemView.txtIsProfileActive.text = "active"
            holder.itemView.txtIsProfileActive.setTextColor(context.resources.getColor(R.color.colorAccent))
        } else {
            holder.itemView.txtIsProfileActive.text = "deactivated"
            holder.itemView.txtIsProfileActive.setTextColor(context.resources.getColor(R.color.colorTextLight))
        }

        super.onBindViewHolder(holder, position)
    }
}

class ServerSettingsViewHolder(itemView: View, val context: Context, private val onPostInfoSelected: (Server) -> Unit) : AbstractViewHolder<Server>(itemView) {
    override fun bindItem(item: Server, removeItem: (Int) -> Unit, pos: Int) {
        itemView.txtName.text = item.name
        itemView.txtAddr.text = item.address

        itemView.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                    .setTitle("Delete Server ${item.name}")
                    .setMessage("Are you sure you want do delete this Server?")
                    .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                        SettingsManager.deleteServer(context, item)
                        removeItem(pos)
                    }
                    .setNegativeButton(R.string.no) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .create()
                    .show()
        }

        itemView.btnSetForUpload.setOnClickListener {
            SettingsManager.setPostInfo(context, item)
            onPostInfoSelected(item)
        }
    }
}