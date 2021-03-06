package com.example.physicalgallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.physicalgallery.databinding.ActivityMainBinding
import com.example.physicalgallery.navigation.*
import com.example.physicalgallery.relatefood.DiaryActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    var PICK_IMAGE_FROM_ALBUM = 0
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        headdefault()
        when (item.itemId) {
            R.id.home -> {
                var detailFrag = DetailFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents, detailFrag)
                    .commit()
                return true
            }
            R.id.Food -> {
                if (checkPersmission()) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startActivity(Intent(this, DiaryActivity::class.java))
                    }
                } else {
                    requestPermission()
                }
                return true
            }
            R.id.upload -> {
                if (checkPersmission()) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startActivity(Intent(this, AddPhotoActivity::class.java))

                    }
                } else {
                    requestPermission()
                }
                return true
            }
            R.id.alarm -> {
                var alarmFrag = AlarmFrag()
                supportFragmentManager.beginTransaction().replace(R.id.main_contents, alarmFrag)
                    .commit()
                return true
            }
            R.id.account -> {
                var userFrag = UserFrag()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                bundle.putString("destination", uid)
                userFrag.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_contents, userFrag)
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

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        //?????? ?????? ??????
        binding.bottomNavigation.selectedItemId = R.id.home
        //set default screen
        //binding.bottomNavigation.selectedItemId = R.id.action_home

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            PICK_IMAGE_FROM_ALBUM
        )
    }

    // ????????? ?????? ??????
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    // ???????????? ??????
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Permission: " + permissions[0] + "was " + grantResults[0] + "????????? ?????? ??????")
        } else {
            Log.d("TAG", "???????????????")
        }
    }

    fun headdefault() {
        head_title.visibility = View.VISIBLE
        head_user_name.visibility = View.GONE
        back_button.visibility = View.GONE

//        binding.alarm.visibility = View.VISIBLE
//        binding.backButton.visibility = View.GONE
//        binding.headUserName.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var profileUrl = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef =
                FirebaseStorage.getInstance().reference.child("profileImages").child(uid!!)
            storageRef.putFile(profileUrl!!)
                .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storageRef.downloadUrl
                }.addOnSuccessListener { uri ->
                var mapping = HashMap<String, Any>()
                mapping["profile_image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid)
                    .set(mapping)
            }
        }
    }
}