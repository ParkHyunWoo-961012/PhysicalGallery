package com.example.physicalgallery.relatefood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.databinding.ActivityFoodSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodSearchActivity : AppCompatActivity() {
    val FoodDB: FoodDatabase by lazy { FoodDatabase.getInstance(this)!! }
    val binding by lazy{ActivityFoodSearchBinding.inflate(layoutInflater)}
    lateinit var food: FoodDatabase
    var search_result : ArrayList<Food> = arrayListOf()
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
                    if (A!= emptyList<Food>()){
                        for(i in 0..(A.size-1)){
                            search_result.add(A[i])
                        }
                        var intent = Intent(this@FoodSearchActivity, FoodResult::class.java)
                        intent.putExtra("search result",search_result)
                        startActivity(intent)
                    }
                    //예외처리 필요 이상한거 검색했을때
                }

            }
        }
    }
}
