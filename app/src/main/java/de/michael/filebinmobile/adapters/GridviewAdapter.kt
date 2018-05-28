package de.michael.filebinmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import de.michael.filebinmobile.R
import de.michael.filebinmobile.model.SingleUpload
import kotlinx.android.synthetic.main.list_item_single_upload_in_multipaste.view.*

class GridviewAdapter(onClick: (SingleUpload) -> Boolean = { false }) : SimpleDataAdapter<GridItemViewHolder, SingleUpload>(onClick) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridItemViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_single_upload_in_multipaste, null)

        return GridItemViewHolder(view, onClick)
    }

}

class GridItemViewHolder(itemView: View, private val onItemSelected: (SingleUpload) -> Boolean) : AbstractViewHolder<SingleUpload>(itemView) {
    override fun bindItem(item: SingleUpload, removeItem: (Int) -> Unit, pos: Int, onClick: (SingleUpload) -> Boolean) {
        itemView.txtMultiPasteSingleItemName.text = item.uploadTitle
        when {
            item.mimeType.contains("image") -> {
                // for some reason we are not allowed to pass an EMPTY string (although null is ok)
                if (item.thumbnail.isNotEmpty()) {
                    Picasso.get().load(item.thumbnail)
                            .resize(100, 100)
                            .into(itemView.imgMultiPasteSingleItemThumb)
                }
            }
        }
        itemView.setOnClickListener {
            onItemSelected(item)
        }
    }

}
