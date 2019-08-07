package com.hagergroup.sample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Ludovic Roland
 * @since 2019.08.07
 */
class MyAdapter(private val myDataset: List<String>)
  : RecyclerView.Adapter<MyAdapter.ViewHolder>()
{

  class ViewHolder(val item: View)
    : RecyclerView.ViewHolder(item)


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
  {
    val itemView = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)

    return ViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int)
  {
    holder.item.findViewById<TextView>(android.R.id.text1).text = myDataset[position]
  }

  override fun getItemCount() =
      myDataset.size

}