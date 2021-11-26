package com.example.physicalgallery.relatefood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.databinding.ActivityFoodDetailPageBinding

class FoodDetailPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFoodDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = getIntent()
        val detail = intent.getSerializableExtra("detail") as Food
        binding.name.text = detail.food_name
        binding.bigClassifier.text = detail.big_classifier
        binding.smallClassifier.text = detail.small_classifier
        binding.oneTimeProvider.text = detail.provide_per_time.toString() + detail.provide_unit.toString()
        binding.calories.text = detail.calories
        detail.carbo
        detail.fiber
        binding.protein.text = detail.protein
        binding.totalFat.text = detail.totalfat
        detail.fat

    }
}