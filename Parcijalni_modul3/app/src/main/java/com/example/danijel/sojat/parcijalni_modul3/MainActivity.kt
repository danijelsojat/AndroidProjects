package com.example.danijel.sojat.parcijalni_modul3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.danijel.sojat.parcijalni_modul3.adapter.GroceriesAdapter
import com.example.danijel.sojat.parcijalni_modul3.adapter.OnItemClickListener
import com.example.danijel.sojat.parcijalni_modul3.databinding.ActivityMainBinding
import com.example.danijel.sojat.parcijalni_modul3.model.Groceries
import com.example.danijel.sojat.parcijalni_modul3.model.GroceriesViewModel

class MainActivity : AppCompatActivity(), OnItemClickListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var groceriesViewModel: GroceriesViewModel
    private lateinit var allGroceries: LiveData<List<Groceries>>
    private lateinit var adapter: GroceriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
        initButtonListener()
        initViewModel()
    }

    private fun initViewModel() {
        groceriesViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[GroceriesViewModel::class.java]
        allGroceries = groceriesViewModel.allGroceries
        allGroceries.observe(this, object: Observer<List<Groceries>>{
            override fun onChanged(groceries: List<Groceries>?) {
                groceries?.let {
                    adapter.groceriesList = it
                }
            }
        })
    }

    private fun initButtonListener() {
        binding.bAdd.setOnClickListener {
            if (binding.etGroceries.text.isNullOrEmpty()) {
                binding.etGroceries.error = "Food item or calorie missing"
            } else if (binding.etCalories.text.isNullOrEmpty()) {
                binding.etCalories.error = "Food item or calorie missing"
            } else {
                val foodItem = Groceries(binding.etGroceries.text.toString(), Integer.valueOf(binding.etCalories.text.toString()))
                groceriesViewModel.insert(foodItem)
                binding.etGroceries.text.clear()
                binding.etGroceries.requestFocus()
                binding.etCalories.text.clear()
            }
        }
    }

    private fun initUi() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GroceriesAdapter()
        binding.recyclerView.adapter = adapter
        adapter.onItemClickListener = this
    }

    override fun onItemClicked(groceries: Groceries) {
        val deleteDialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("Delete item")
            .setMessage("Are you sure you want to delete this item from list?")
            .setPositiveButton("Delete") { _, _ ->
                groceriesViewModel.delete(groceries)
            }
            .setNegativeButton("Cancel", null)
            .create()
        deleteDialog.show()
    }
}