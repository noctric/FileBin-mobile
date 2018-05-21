package de.michael.filebinmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import de.michael.filebinmobile.R
import de.michael.filebinmobile.util.FileUtil
import kotlinx.android.synthetic.main.list_item_selected_file.view.*
import java.io.File

class SelectedFilesAdapter : SimpleDataAdapter<SelectedFileViewHolder, File>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedFileViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_selected_file, null)

        return SelectedFileViewHolder(view)
    }
}

class SelectedFileViewHolder(itemView: View) : AbstractViewHolder<File>(itemView) {

    override fun bindItem(item: File, removeItem: (Int) -> Unit, pos: Int) {
        itemView.txtSelectedFileName.text = item.name

        val mimeType = FileUtil.getMimeType(item)

        when {
            mimeType != null && mimeType.contains("image") -> Picasso.get().load(item)
                    .resize(100, 100)
                    .into(itemView.imgFileThumbnail)
        }

        itemView.btnDeselectFile.setOnClickListener {
            removeItem(pos)
        }
    }

}