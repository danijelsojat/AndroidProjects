package com.example.vjezba_15_05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

const val EXTRA1 = "EXTRA1"
const val EXTRA2 = "EXTRA2"


class MainActivity : AppCompatActivity() {

    private lateinit var et1 : EditText
    private lateinit var et2 : EditText
    private lateinit var myList: ArrayList<User>
    private lateinit var bSave: Button

    enum class SavedInfoFields {
        NAME,
        DATE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myList = arrayListOf()
        initWidgets()
        setupListeners()
    }

    private fun addToList() {
        myList.add(User(et1.text.toString(), et2.text.toString()))
    }

    private fun setupListeners() {
        bSave.setOnClickListener {
            val fieldsValidationResult =
                validateFields(et1.text.toString(), et2.text.toString())
            if(fieldsValidationResult.isNotEmpty()){
                markErrorFields(fieldsValidationResult)
            } else {
                addToList()
                val savedInfo : RecyclerView = findViewById(R.id.rvShowSavedInfo)
                savedInfo.layoutManager = LinearLayoutManager(this)
                savedInfo.adapter = TestAdapter(myList)
                Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show()
                discardMessage()
            }
        }
    }

    private fun initWidgets() {
        et1 = findViewById(R.id.et1)
        et2 = findViewById(R.id.et2)
        bSave = findViewById(R.id.bSave)
    }

    private fun markErrorFields(fieldsValidationResult: Map<SavedInfoFields, Boolean>) {
        if(fieldsValidationResult.containsKey(SavedInfoFields.NAME)){
            et1.error = "Name missing"
        }
        if(fieldsValidationResult.containsKey(SavedInfoFields.DATE)){
            et2.error = "Date missing"
        }
    }

    private fun validateFields(et1: String, et2: String): Map<SavedInfoFields, Boolean> {
        val inputFields = hashMapOf<SavedInfoFields, Boolean>()
        if(et1.isEmpty()){
            inputFields[SavedInfoFields.NAME] = false
        }
        if(et2.isEmpty()){
            inputFields[SavedInfoFields.DATE] = false
        }
        return inputFields
    }

    private fun discardMessage() {
        et1.text.clear()
        et2.text.clear()
    }
}