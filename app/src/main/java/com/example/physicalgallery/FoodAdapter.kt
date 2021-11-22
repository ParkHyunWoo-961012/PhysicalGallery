package com.example.physicalgallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.physicalgallery.databinding.FoodResultBinding

class FoodAdapter(val foods: List<Food>)
    : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {// This is class made for manage recyclerview of Lotto number

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):ViewHolder{
        val binding = FoodResultBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder,position:Int){
        val foods = foods.get(position)
        holder.bind(foods) // for reperesent to recyclerview by use bind function (binding.id.text = input data )
    }

    class ViewHolder(val binding:FoodResultBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(foods: Food){//This is made for onBindViewHolder function -> Manage One lottonumber set (6 numbers and one title)
            binding.foodName.text = foods.food_name
        }
    }

    override fun getItemCount(): Int {
        return foods.size
    }

}