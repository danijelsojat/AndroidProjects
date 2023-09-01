package com.example.danijel.sojat.parcijalni_modul3.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class GroceriesViewModel(application: Application): AndroidViewModel(application) {

    var groceriesRepository: GroceriesRepository
    var allGroceries: LiveData<List<Groceries>>

    init {
        groceriesRepository = GroceriesRepository(application)
        allGroceries = groceriesRepository.allGroceries
    }

    fun insert(groceries: Groceries) {
        groceriesRepository.insert(groceries)
    }

    fun delete(groceries: Groceries) {
        groceriesRepository.delete(groceries)
    }
}