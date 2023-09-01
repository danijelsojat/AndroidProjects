package com.example.danijel.sojat.parcijalni_modul3.model

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.danijel.sojat.parcijalni_modul3.db.GroceriesDao
import com.example.danijel.sojat.parcijalni_modul3.db.GroceriesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GroceriesRepository(application: Application) {

    var groceriesDao: GroceriesDao
    var allGroceries: LiveData<List<Groceries>>

    init {
        val database = GroceriesDatabase.getInstance(application)
        groceriesDao = database.groceriesDao()
        allGroceries = groceriesDao.getAllGroceries()
    }

    fun insert(groceries: Groceries) {
        GlobalScope.launch(Dispatchers.IO) {
            groceriesDao.insert(groceries)
        }
    }

    fun delete(groceries: Groceries) {
        GlobalScope.launch(Dispatchers.IO) {
            groceriesDao.delete(groceries)
        }
    }
}