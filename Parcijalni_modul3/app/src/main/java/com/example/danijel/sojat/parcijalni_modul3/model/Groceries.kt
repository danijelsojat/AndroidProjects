package com.example.danijel.sojat.parcijalni_modul3.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.danijel.sojat.parcijalni_modul3.GROCERIES_TABLE_NAME

@Entity(tableName = GROCERIES_TABLE_NAME)
class Groceries(var groceries: String, var calorie: Int) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}