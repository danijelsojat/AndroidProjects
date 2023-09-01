package com.example.danijel.sojat.parcijalni_modul3.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.danijel.sojat.parcijalni_modul3.GROCERIES_TABLE_NAME
import com.example.danijel.sojat.parcijalni_modul3.model.Groceries

@Dao
interface GroceriesDao {

    @Insert
    fun insert(groceries: Groceries)

    @Delete
    fun delete(groceries: Groceries)

    @Query("SELECT * FROM $GROCERIES_TABLE_NAME")
    fun getAllGroceries(): LiveData<List<Groceries>>
}