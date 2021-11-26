package com.example.physicalgallery.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.databinding.FragmentUserBinding
import com.example.physicalgallery.relatefood.FoodSearchActivity


class UserFrag : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("11","!1")
        binding.foodsearch.setOnClickListener{
            val intent = Intent(this,FoodSearchActivity::class.java)
            Log.e("11","!1")
            startActivity(intent)
        }
    }



}