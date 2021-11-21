package com.example.physicalgallery

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.physicalgallery.databinding.ActivityFoodSearchBinding
import java.io.BufferedInputStream

class FoodSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFoodSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("FoodList", "음식데이터 조회.")
        val getFood = GetFood(applicationContext)
        getFood.start()

    }
}
class GetFood(val context: Context) : Thread(){
    override fun run() {
//        val fd = Food(100,"11","dd","11","11","11"
//        ,"11", 11F,11F,11F,11F,11F,11F,11F,11F,11F,11F,
//        11F,11F,11F,11F,1F)
//        FoodDatabase
//            .getInstance(context)!!
//            .FoodDao()
//            .addFoodDb(fd)

        val items = FoodDatabase
            .getInstance(context)!!
            .FoodDao()
            .getAll()


        for(i in items){
            Log.d("Foodlist", "${i.id}|${i.id} | ${i.food_name
            } | ${i.big_classifier} | ${i.small_classifier} | ${i.sodium}")
        }
    }
}