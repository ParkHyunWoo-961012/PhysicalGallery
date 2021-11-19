package com.example.physicalgallery.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.physicalgallery.R


class UserFrag : Fragment(){
    override fun onCreateView(inflater : LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        return super.onCreateView(inflater,container,savedInstanceState)
    }

}