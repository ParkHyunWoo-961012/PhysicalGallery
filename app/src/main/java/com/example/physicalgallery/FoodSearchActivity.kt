package com.example.physicalgallery

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.physicalgallery.databinding.ActivityFoodSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.BufferedInputStream

class FoodSearchActivity : AppCompatActivity() {
    val FoodDB : FoodDatabase by lazy {FoodDatabase.getInstance(this)!!}
    var name = "돼지고기"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFoodSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("FoodList", "음식데이터 조회.")
        val getFood = GetFood(applicationContext)
        getFood.start()

    }
    fun findfood(view: View){
        GlobalScope.launch(Dispatchers.IO){
            val foodList : List<Food> = FoodDB.FoodDao().getItem(name)
            Log.d("112323","${foodList}")
        }
    }
}
class GetFood(val context: Context) : Thread(){
    override fun run() {
        val items = FoodDatabase
            .getInstance(context)!!
            .FoodDao()
            .getItem(name)
       Log.d("123123123","${name.toString()}")
    }
}