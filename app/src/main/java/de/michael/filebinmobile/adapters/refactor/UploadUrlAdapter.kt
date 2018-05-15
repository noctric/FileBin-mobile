package de.michael.filebinmobile.adapters.refactor

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.michael.filebinmobile.R
import kotlinx.android.synthetic.main.list_item_upload_url.view.*

class UploadUrlAdapter(activity: Activity) : SimpleDataAdapter<UploadUrlViewHolder, String>(activity) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadUrlViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_upload_url, null)

        return UploadUrlViewHolder(view, activity)
    }
}

class UploadUrlViewHolder(itemView: View, val activity: Activity) : AbstractViewHolder<String>(itemView) {
    override fun bindItem(item: String, removeItem: (Int) -> Unit, pos: Int) {
        itemView.txtUploadUrl.text = item
        itemView.btnClipboardUrl.setOnClickListener {

            val clipboard = activity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("FileBin mobile url", item)

            clipboard.primaryClip = clip
            Toast.makeText(activity, "Url copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
}