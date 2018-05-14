package de.michael.filebinmobile.adapters.refactor

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import de.michael.filebinmobile.adapters.OnAdapterDataChangedListener

abstract class SimpleDataAdapter<T : AbstractViewHolder<K>, K>(private val activity: Activity)
    : RecyclerView.Adapter<T>() {

    val data: ArrayList<K> = ArrayList()
    var dataChangedListener: OnAdapterDataChangedListener? = null

    fun updateData(data: ArrayList<K>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        this.data.clear()
        notifyDataSetChanged()
    }

    fun removeItemAt(pos: Int) {
        this.data.removeAt(pos)
        notifyDataSetChanged()
    }

    fun add(item: K) {
        this.data.add(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: T, position: Int) =
            holder.bindItem(this.data[position])

    override fun getItemCount(): Int = this.data.size

    //TODO creating instances of generic types not yet supported in kotlin, but as soon as it is,
    // we can also move the creation of the viewholder here

}

abstract class AbstractViewHolder<K>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindItem(item: K)
}