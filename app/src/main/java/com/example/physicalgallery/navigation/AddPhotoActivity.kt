package com.example.physicalgallery.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.R
import com.example.physicalgallery.databinding.ActivityAddPhotoBinding
import com.example.physicalgallery.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    val binding by lazy{ActivityAddPhotoBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //Open the album
        var photoPickerIntent = Intent()
        photoPickerIntent.setAction(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES,"image/*")
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)

        //add image upload event
        binding.addphotoBtnUpload.setOnClickListener {
            contentUpload()
            Log.e("test","${photoUri}")
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_FROM_ALBUM){
            Log.e("test","test1")
//            if(requestCode== Activity.RESULT_OK){
            //this is path to the selected image

            photoUri = data?.data
            Log.e("test","RESULT_OK!")
            binding.addphotoImage.setImageURI(photoUri)
            Log.e("test","${photoUri}")

//            }else{
//                //exit the addphotoactivity if you leave the album sithout selecting it
//                Log.e("test","RESULT_NOT OK!")
//                finish()
//            }
        }
    }
    fun contentUpload(){
        //make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //promise method
        var upload_image = storageRef?.putFile(photoUri!!)
        //upload_image?.continueWithTask { task:Task<UploadTask.TaskSnapshot> ->
        //  return@continueWithTask storageRef?.downloadUrl

        upload_image?.continueWithTask { task:Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef?.downloadUrl
        }?.addOnSuccessListener(){ uri ->
            var contentDTO = ContentDTO()

            // Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()

            // Insert uid of user
            contentDTO.uid = auth?.currentUser?.uid

            // Insert userId
            contentDTO.userId = auth?.currentUser?.email

            // Insert explain of content
            contentDTO.explain = binding.addphotoEditExplain.text.toString()

            // Insert timestamp
            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            Toast.makeText(this,"image upload success",Toast.LENGTH_LONG).show()

            setResult(Activity.RESULT_OK)

            finish()
            Log.e("test", "data upload success")

        }
        storageRef?.putFile(photoUri!!)?.addOnFailureListener(){
            Toast.makeText(this,"failure image upload",Toast.LENGTH_LONG).show()
            Log.e("test", "data upload fail")
        }

//        //callback method
//        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
//            //(toast erase)Toast.makeText(this,getString(R.string.upload_success),Toast.LENGTH_LONG).show()
//         storageRef.downloadUrl.addOnSuccessListener {
//             uri ->
//             var contentDTO = contentDTO()
//
//             // Insert downloadUrl of image
//             contentDTO.imageUrl = uri.toString()
//
//             // Insert uid of user
//             contentDTO.uid = auth?.currentUser?.uid
//
//             // Insert userId
//             contentDTO.userID = auth?.currentUser?.email
//
//             // Insert explain of content
//             contentDTO.explain = addphoto_edit_explain.text.toString()
//
//             // Insert timestamp
//             contentDTO.timestamp = System.currentTimeMillis()
//
//             firestore?.collection("images")?.document()?.set(contentDTO)
//
//             setResult(Activity.RESULT_OK)
//
//             finish()
//            }
//        }
    }
}