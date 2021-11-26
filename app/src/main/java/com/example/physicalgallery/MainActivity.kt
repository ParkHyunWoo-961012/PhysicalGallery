package com.example.physicalgallery

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.physicalgallery.databinding.ActivityMainBinding
import com.example.physicalgallery.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    val binding by lazy{ ActivityMainBinding.inflate(layoutInflater)}
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                var detailFrag = DetailFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents, detailFrag)
                    .commit()
                return true
            }
            R.id.account -> {
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    startActivity(Intent(this, UserFrag::class.java))
                }
                return true
            }
            R.id.upload -> {
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    startActivity(Intent(this,AddPhotoActivity::class.java))
                }
                return true
            }
            R.id.search -> {
                var gridFrag = GridFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents, gridFrag)
                    .commit()
                return true
            }
            R.id.alarm -> {
                var alarmFrag = AlarmFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents, alarmFrag)
                    .commit()
                return true
            }
            else -> return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

    }


}