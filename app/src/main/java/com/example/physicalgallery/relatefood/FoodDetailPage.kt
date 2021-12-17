package com.example.physicalgallery.relatefood

import android.content.Intent
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
        binding.calories.text = detail.calories.toString()
        binding.totalFat.text = detail.fat.toString()
        binding.transfat.text = detail.transfat.toString()
        binding.carbo.text = detail.carbo.toString()
        binding.fiber.text = detail.fiber.toString()
        binding.protein.text = detail.protein.toString()

        binding.gotodiarybutton.setOnClickListener{
            var calories = 0
            // To manage user no input number to injection amount
            if(binding.userInputAmount.text.toString() == ""){
                calories = detail.calories.toString().toFloat().toInt()
            }else {
                val amount = binding.userInputAmount.text.toString().toInt()
                val provide_amount = detail.provide_per_time.toString().toFloat().toInt()
                calories = detail.calories.toString().toFloat().toInt()
                calories = (amount * calories / provide_amount).toInt()
            }
            Intent(this@FoodDetailPage, DiaryActivity::class.java).apply {
                putExtra("FoodName", detail.food_name)
                putExtra("Calorie",calories)
            }.run { startActivity(this) }
        }

    }
}