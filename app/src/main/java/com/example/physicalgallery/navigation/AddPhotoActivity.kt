package com.example.physicalgallery.navigation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.databinding.ActivityAddPhotoBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    val binding by lazy{ActivityAddPhotoBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //Initiate storage
        storage = FirebaseStorage.getInstance()
        //Open the album

        var photoPickerIntent = Intent()
        photoPickerIntent.setAction(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES,"image/*")
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        //add image upload event

        binding.addphotoBtnUpload.setOnClickListener{
            contentUpload()
            Log.e("123123123","${photoUri}")
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_FROM_ALBUM){
            Log.e("123123","123123123123123123123123123")
                //this is path to the selected image
                photoUri = data?.data
                Log.e("123123","${photoUri}}")

                binding.addphotoImage.setImageURI(photoUri)


        }
    }
    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        var upload_image = storageRef?.putFile(photoUri!!)

        upload_image?.addOnFailureListener(){
            Toast.makeText(this,"이미지 업로드 실",Toast.LENGTH_LONG).show()
        }

        upload_image?.addOnSuccessListener(){
            Toast.makeText(this,"이미지 업로드 성공",Toast.LENGTH_LONG).show()
        }
    }
}