package de.michael.filebinmobile.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import de.michael.filebinmobile.R
import de.michael.filebinmobile.fragments.setUpGridList
import de.michael.filebinmobile.model.MultiPasteUpload
import de.michael.filebinmobile.model.SingleUpload
import de.michael.filebinmobile.model.Upload
import kotlinx.android.synthetic.main.any_recycler_view.view.*
import kotlinx.android.synthetic.main.list_item_upload_history.view.*
import java.text.DateFormat
import java.util.*

class HistoryAdapter(onListItemClick: (Upload) -> Boolean) : SimpleDataAdapter<HistoryViewHolder, Upload>(onListItemClick) {
    val deleteUploads = mutableListOf<Upload>()
    private val onItemSelected = { pos: Int, selected: Boolean ->
        when {
            selected && !deleteUploads.contains(data[pos]) -> deleteUploads.add(data[pos])
            !selected && deleteUploads.contains(data[pos]) -> deleteUploads.remove(data[pos])
            else -> { /* do nothing */
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_upload_history, null)

        return HistoryViewHolder(view, onItemSelected)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.itemView.ckbDeleteUpload.isChecked = this.deleteUploads.contains(this.data[position])
        super.onBindViewHolder(holder, position)
    }

}

class HistoryViewHolder(itemView: View, private val onItemSelected: (Int, Boolean) -> Any) : AbstractViewHolder<Upload>(itemView) {

    private val dateFormat: DateFormat = java.text.DateFormat.getDateInstance();

    override fun bindItem(item: Upload, removeItem: (Int) -> Unit, pos: Int, onClick: (Upload) -> Boolean) {

        when (item) {
            is SingleUpload -> {
                itemView.txtUploadName.text = item.uploadTitle
                itemView.txtUploadSize.text = item.uploadSize
                itemView.setBackgroundColor(itemView.resources.getColor(android.R.color.transparent))
                itemView.setOnClickListener { onClick(item) }
            }
            is MultiPasteUpload -> {
                itemView.txtUploadName.text = "Multipaste ${item.id}"
                itemView.setBackgroundColor(itemView.resources.getColor(R.color.colorDialogBlue))
                itemView.setOnClickListener {
                    val view = LayoutInflater.from(itemView.context)
                            .inflate(R.layout.any_recycler_view, null)
                    // this should be calculated dynamically
                    view.rclAnyRecyclerView.setUpGridList(3)

                    val adapter = GridviewAdapter(onClick)
                    adapter.updateData(item.uploads)
                    view.rclAnyRecyclerView.adapter = adapter


                    AlertDialog.Builder(itemView.context)
                            .setTitle("Multipaste items")
                            .setView(view)
                            .setPositiveButton(R.string.ok) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .create()
                            .show()
                }
            }
        }

        val dateString = dateFormat.format(Date(item.date * 1000))
        itemView.txtUploadTimeStamp.text = dateString

        itemView.ckbDeleteUpload.setOnClickListener {
            val isChecked = (it as CheckBox).isChecked
            onItemSelected(pos, isChecked)
        }
    }

}