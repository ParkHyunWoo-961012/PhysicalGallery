package com.example.physicalgallery.relatefood

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.physicalgallery.databinding.FoodResultBinding

class FoodResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FoodResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        var search_list = intent.getSerializableExtra("search result") as MutableList<Food>
        val foodadapter = FoodAdapter(search_list)
        binding.recyclerview.adapter = foodadapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        foodadapter.setOnItemClickListener(object : FoodAdapter.OnItemClickListener{
            override fun onItemClick(view : View, data: Food, pos : Int) {
                Intent(this@FoodResult, FoodDetailPage::class.java).apply {
                    putExtra("detail", data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { startActivity(this) }
            }

        })
        //검색할때 김밥용 햄 검색하면 하나만 나옴 -> 다관찰
    }
}

