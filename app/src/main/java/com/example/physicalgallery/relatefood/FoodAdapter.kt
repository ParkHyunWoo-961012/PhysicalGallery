package com.example.physicalgallery.relatefood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.physicalgallery.databinding.FoodRecyclerviewBinding

class FoodAdapter(val foods : MutableList<Food>)
    : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(view:View,data:Food ,pos :Int)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener : OnItemClickListener){
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): ViewHolder {
        val binding = FoodRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position:Int){
        val foods = foods.get(position)
        holder.bind(foods)
    }

    inner class ViewHolder(val binding: FoodRecyclerviewBinding): RecyclerView.ViewHolder(binding.root){
        var view : View = binding.root
        fun bind(foods: Food){
            val pos = adapterPosition
            binding.foodName.text = foods.food_name

            if(pos!= RecyclerView.NO_POSITION)
            {
                binding.rootclick.setOnClickListener {
                    mListener?.onItemClick(itemView,foods, pos)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return foods.size
    }


}
