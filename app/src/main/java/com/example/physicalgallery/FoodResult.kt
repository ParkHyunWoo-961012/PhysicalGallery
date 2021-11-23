package com.example.physicalgallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class FoodResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result)
        val gson = Gson()
        val type: Type = object : TypeToken<List<Food>>() {}.type
        val intent = getIntent()
        var search_list = intent.getStringExtra("search result")
        search_list = gson.fromJson(search_list,type)
        Log.e("FoodResult","${search_list}")
    }
}