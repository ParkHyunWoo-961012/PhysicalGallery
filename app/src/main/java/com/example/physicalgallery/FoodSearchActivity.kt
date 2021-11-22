package com.example.physicalgallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.physicalgallery.databinding.ActivityFoodSearchBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import org.w3c.dom.Text
import java.io.BufferedInputStream

class FoodSearchActivity : AppCompatActivity() {
    val FoodDB: FoodDatabase by lazy { FoodDatabase.getInstance(this)!! }
    val binding by lazy{ActivityFoodSearchBinding.inflate(layoutInflater)}
    lateinit var food: FoodDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.foodSearch.setOnClickListener{
            var name = (binding.foodName.text).toString()
            if (name == ""){
                Toast.makeText(applicationContext,"you don't any input",Toast.LENGTH_LONG).show()
            }
            else {
                CoroutineScope(Dispatchers.IO).launch {
                    var A = FoodDB.FoodDao().getItem(name)
                    if (A[0] != null){

                        for(i in 0 .. (A.size-1)) {
                            Log.e("Food Result", "${A[i].food_name}")
                        }
                    }
                }
            }
        }
    }

//    fun find_food(view: View) {
//        var name:String? = binding.foodName.text as String?
//        if (name == null){
//            Toast.makeText(applicationContext,"you don't any input",Toast.LENGTH_LONG).show()
//        }
//        else {
//            CoroutineScope(Dispatchers.IO).launch {
//                var A = FoodDB?.FoodDao().getItem(name)
//                Log.e("11","${A.toString()}")
//            }
//        }
//    }
}
