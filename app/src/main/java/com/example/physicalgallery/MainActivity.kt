package com.example.physicalgallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.physicalgallery.databinding.ActivityMainBinding
import com.example.physicalgallery.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    var PICK_IMAGE_FROM_ALBUM = 0
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
                var userFrag = UserFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents, userFrag)
                    .commit()
                return true
            }
            R.id.upload -> {
                if(checkPersmission()) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startActivity(Intent(this, AddPhotoActivity::class.java))

                    }
                }
                else{
                    requestPermission()
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
        //초기 화면 설정
        binding.bottomNavigation.selectedItemId = R.id.home


        //set default screen
        //binding.bottomNavigation.selectedItemId = R.id.action_home

    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ),
            PICK_IMAGE_FROM_ALBUM )
    }

    // 카메라 권한 체크
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    // 권한요청 결과
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Permission: " + permissions[0] + "was " + grantResults[0] + "카메라 허가 받음")
        }else{
            Log.d("TAG","카메비허가")
        }
    }

}