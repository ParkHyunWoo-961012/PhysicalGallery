package com.example.physicalgallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.physicalgallery.databinding.ActivityMainBinding
import com.example.physicalgallery.navigation.AlarmFrag
import com.example.physicalgallery.navigation.DetailFrag
import com.example.physicalgallery.navigation.GridFrag
import com.example.physicalgallery.navigation.UserFrag
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home ->{
                var detailFrag = DetailFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents,detailFrag).commit()
                return true
            }
            R.id.account ->{
                var userFrag = UserFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents,userFrag).commit()
                return true
            }
            R.id.upload ->{
                var detailFrag = DetailFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents,detailFrag).commit()
                return true
            }
            R.id.search->{
                var gridFrag = GridFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents,gridFrag).commit()
                return true
            }
            R.id.alarm ->{
                var alarmFrag = AlarmFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents,alarmFrag).commit()
                return true
            }
        }
    }
}