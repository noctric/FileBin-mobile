package de.michael.filebinmobile.adapters.refactor

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View

abstract class SimpleDataAdapter<T : AbstractViewHolder<K>, K>
(val activity: Activity,
 private val removeItem: (Int) -> Unit = {},
 private val onDataChanged: () -> Unit = {}) : RecyclerView.Adapter<T>() {

    val data: ArrayList<K> = ArrayList()

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

    // while binding the viewholder we set the position as a parameter so we can access it in the
    // remove function which is also passed as a function parameter
    override fun onBindViewHolder(holder: T, position: Int) =
            holder.bindItem(this.data[position], removeItem, position)

    override fun getItemCount(): Int = this.data.size

    //TODO creating instances of generic types not yet supported in kotlin, but as soon as it is,
    // we can also move the creation of the viewholder here

}

abstract class AbstractViewHolder<K>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindItem(item: K, removeItem: (Int) -> Unit = {}, pos: Int)
}