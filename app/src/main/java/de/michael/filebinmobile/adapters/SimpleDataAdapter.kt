package de.michael.filebinmobile.adapters

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class SimpleDataAdapter<T : AbstractViewHolder<K>, K>(
        val onClick: (K) -> Boolean = { false },
        val onItemRemoved: (K) -> Boolean = { false }) : RecyclerView.Adapter<T>() {

    val data: MutableList<K> = mutableListOf()
    private val removeItemAt = { pos: Int ->
        onItemRemoved(this.data[pos])
        this.data.removeAt(pos)
        notifyDataSetChanged()
    }

    fun updateData(data: List<K>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        this.data.clear()
        notifyDataSetChanged()
    }

    fun add(item: K) {
        this.data.add(item)
        notifyDataSetChanged()
    }

    // while binding the viewholder we set the position as a parameter so we can access it in the
    // remove function which is also passed as a function parameter
    override fun onBindViewHolder(holder: T, position: Int) =
            holder.bindItem(this.data[position], removeItemAt, position, onClick)

    override fun getItemCount(): Int = this.data.size

    //TODO creating instances of generic types not yet supported in kotlin, but as soon as it is,
    // we can also move the creation of the viewholder here

}

abstract class AbstractViewHolder<K>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindItem(item: K, removeItem: (Int) -> Unit = {}, pos: Int, onClick: (K) -> Boolean)
}