package com.example.danijel.sojat.parcijalni_modul3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.danijel.sojat.parcijalni_modul3.R
import com.example.danijel.sojat.parcijalni_modul3.model.Groceries

interface OnItemClickListener {
    fun onItemClicked(groceries: Groceries)
}

class GroceriesAdapter : RecyclerView.Adapter<GroceriesAdapter.GroceriesHolder>() {

    var groceriesList: List<Groceries> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onItemClickListener: OnItemClickListener? = null

    inner class GroceriesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewGroceries: TextView = itemView.findViewById(R.id.textViewGroceries)
        var textViewCalorie: TextView = itemView.findViewById(R.id.textViewCalorie)
        init {
            itemView.setOnClickListener {
                if(position != RecyclerView.NO_POSITION){
                    onItemClickListener?.onItemClicked(groceriesList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceriesHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.groceries_item, parent, false)
        return GroceriesHolder(itemView)
    }

    override fun getItemCount(): Int {
        return groceriesList.size
    }

    override fun onBindViewHolder(holder: GroceriesHolder, position: Int) {
        val currentUser = groceriesList[position]
        holder.textViewGroceries.text = currentUser.groceries
        holder.textViewCalorie.text = currentUser.calorie.toString()
    }
}