package com.example.parcijalni_ispit_28_06_2023

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

enum class SavedInfoFields {
    PODSJETNIK,
    DETALJI
}

class FirstFragment : Fragment() {

    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var bSave: Button
    private lateinit var myList: ArrayList<Podsjetnik>
    private lateinit var adapter: PodsjetnikAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myList = arrayListOf()
        adapter = PodsjetnikAdapter(myList)

        editText1 = view.findViewById(R.id.editText1)
        editText2 = view.findViewById(R.id.editText2)
        bSave =  view.findViewById(R.id.button1)

        setupListeners()

        val savedInfo : RecyclerView = view.findViewById(R.id.recView)
        savedInfo.layoutManager = LinearLayoutManager(view.context)
        savedInfo.adapter = adapter

    }

    private fun setupListeners() {
        bSave.setOnClickListener {
            val fieldsValidationResult =
                validateFields(editText1.text.toString(), editText2.text.toString())
            if(fieldsValidationResult.isNotEmpty()){
                markErrorFields(fieldsValidationResult)
            } else {
                addToList()
                adapter.setNewItems(myList)
                discardMessage()
            }
        }
    }

    private fun addToList() {
        myList.add(Podsjetnik(editText1.text.toString(), editText2.text.toString()))
    }

    private fun validateFields(et1: String, et2: String): Map<SavedInfoFields, Boolean> {
        val inputFields = hashMapOf<SavedInfoFields, Boolean>()
        if(et1.isEmpty()){
            inputFields[SavedInfoFields.PODSJETNIK] = false
        }
        if(et2.isEmpty()){
            inputFields[SavedInfoFields.DETALJI] = false
        }
        return inputFields
    }

    private fun markErrorFields(fieldsValidationResult: Map<SavedInfoFields, Boolean>) {
        if(fieldsValidationResult.containsKey(SavedInfoFields.PODSJETNIK)){
            editText1.error = "Unesi podsjetnik"
        }
        if(fieldsValidationResult.containsKey(SavedInfoFields.DETALJI)){
            editText2.error = "Unesi detalje"
        }
    }

    private fun discardMessage() {
        editText1.text.clear()
        editText2.text.clear()
    }

}