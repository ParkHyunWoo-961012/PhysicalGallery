package com.example.physicalgallery.navigation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.R
import com.example.physicalgallery.databinding.FragmentUserBinding
import com.example.physicalgallery.relatefood.FoodSearchActivity


class UserFrag : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user)
        val binding = FragmentUserBinding.inflate(layoutInflater)
        binding.foodsearch.setOnClickListener{
            val intent = Intent(this, FoodSearchActivity::class.java)
            startActivity(intent)
        }
    }



}