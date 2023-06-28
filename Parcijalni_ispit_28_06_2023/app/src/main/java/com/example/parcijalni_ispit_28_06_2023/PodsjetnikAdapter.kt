package com.example.parcijalni_ispit_28_06_2023

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PodsjetnikAdapter(var items: ArrayList<Podsjetnik>) : RecyclerView.Adapter<PodsjetnikAdapter.InfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        return InfoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.text1.text = items[position].podsjetnik
        holder.text2.text = items[position].detalji
        holder.itemView.setOnClickListener {
            items.removeAt(position)
            notifyItemRemoved(position)
        }

    }

    fun setNewItems(items: ArrayList<Podsjetnik>){
        this.items = items
        notifyDataSetChanged()
    }
    class InfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val text1: TextView = view.findViewById(R.id.text1)
        val text2: TextView = view.findViewById(R.id.text2)

    }

}