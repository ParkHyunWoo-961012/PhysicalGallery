package com.example.physicalgallery.relatefood

data class FoodDiaryTable (
        //MutableMap< Date information , Food Information >
        var total_calories : MutableMap<String,Int> = HashMap(),
        var food_list : MutableMap<String,MutableList<String>> = HashMap()
)
