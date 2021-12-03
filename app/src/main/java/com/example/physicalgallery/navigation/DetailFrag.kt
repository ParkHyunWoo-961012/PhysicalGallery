package com.example.physicalgallery.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.physicalgallery.databinding.FragmentDetailBinding
import com.google.firebase.firestore.FirebaseFirestore


class DetailFrag : Fragment(){
    var firestore : FirebaseFirestore? = FirebaseFirestore.getInstance()
    override fun onCreateView(inflater : LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val binding = FragmentDetailBinding.inflate(layoutInflater)
        binding.detailRecyclerview.adapter = DetailAdapter()
        binding.detailRecyclerview.layoutManager =LinearLayoutManager(activity)
        val view = binding.root

        return view
    }

}