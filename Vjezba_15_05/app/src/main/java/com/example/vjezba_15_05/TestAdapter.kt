package com.example.vjezba_15_05

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView.Adapter


class TestAdapter(var items: ArrayList<User>) : Adapter<TestAdapter.InfoViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        return InfoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.saved_info, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.tv1.text = items[position].name
        holder.tv2.text = items[position].date

    }

    fun setNewItems(items: ArrayList<User>){
        this.items = items
        notifyDataSetChanged()
    }

    class InfoViewHolder(view: View) : ViewHolder(view) {

        val tv1: TextView = view.findViewById(R.id.tv1)
        val tv2: TextView = view.findViewById(R.id.tv2)

        init {
            itemView.setOnClickListener {
                val startNextActivity = Intent(view.context, ItemActivity::class.java).apply {
                    putExtra(EXTRA1, tv1.text.toString())
                    putExtra(EXTRA2, tv2.text.toString())
                }
                view.context.startActivity(startNextActivity)
            }
        }
    }
}
