package de.michael.filebinmobile.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.michael.filebinmobile.R
import kotlinx.android.synthetic.main.list_item_upload_url.view.*

class UploadUrlAdapter(val context: Context) : SimpleDataAdapter<UploadUrlViewHolder, String>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadUrlViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_upload_url, null)

        return UploadUrlViewHolder(view, context)
    }
}

class UploadUrlViewHolder(itemView: View, val context: Context) : AbstractViewHolder<String>(itemView) {
    override fun bindItem(item: String, removeItem: (Int) -> Unit, pos: Int, onClick: (String) -> Boolean) {
        itemView.txtUploadUrl.text = item
        itemView.btnClipboardUrl.setOnClickListener {

            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("FileBin mobile url", item)

            clipboard.primaryClip = clip
            Toast.makeText(context, "Url copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
}