package de.michael.filebinmobile.adapters.refactor

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.michael.filebinmobile.R
import de.michael.filebinmobile.model.refactor.Upload
import kotlinx.android.synthetic.main.list_item_upload_history.view.*
import java.text.DateFormat
import java.util.*

class HistoryAdapter(activity: Activity) : SimpleDataAdapter<HistoryViewHolder, Upload>(activity) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_upload_history, null)

        return HistoryViewHolder(view)
    }

}

class HistoryViewHolder(itemView: View) : AbstractViewHolder<Upload>(itemView) {
    private val dateFormat: DateFormat = java.text.DateFormat.getDateInstance();

    override fun bindItem(item: Upload) {
        itemView.txtUploadName.text = item.uploadTitle
        itemView.txtUploadSize.text = item.uploadSize

        val dateString = dateFormat.format(Date(item.uploadTimeStamp * 1000))
        itemView.txtUploadTimeStamp.text = dateString
    }

}