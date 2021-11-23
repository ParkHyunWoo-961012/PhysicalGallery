package com.example.physicalgallery.relatefood

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.R

class FoodResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result)

        val intent = getIntent()
        var search_list = intent.getSerializableExtra("search result")
        //검색할때 김밥용 햄 검색하면 하나만 나옴 -> 다관찰
        Log.e("FoodResult","${search_list}")
    }
}