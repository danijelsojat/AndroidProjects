package com.example.danijel.sojat.parcijalni_modul3.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.danijel.sojat.parcijalni_modul3.GROCERIES_DATABASE_NAME
import com.example.danijel.sojat.parcijalni_modul3.model.Groceries

@Database(entities = [Groceries::class], version = 1, exportSchema = true)
abstract class GroceriesDatabase: RoomDatabase() {

    abstract fun groceriesDao(): GroceriesDao

    companion object {
        var instance: GroceriesDatabase? = null

        @Synchronized
        fun getInstance(context: Context): GroceriesDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, GroceriesDatabase::class.java, GROCERIES_DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as GroceriesDatabase
        }
    }
}