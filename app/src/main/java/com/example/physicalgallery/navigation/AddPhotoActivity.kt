package com.example.physicalgallery.navigation

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.physicalgallery.databinding.ActivityAddPhotoBinding
import com.example.physicalgallery.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var REQUEST_TAKE_PHOTO =0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var currentPhotoPath: String = ""
    var firestore: FirebaseFirestore? = null
    val binding by lazy { ActivityAddPhotoBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        dispatchTakePictureIntent()

        //add image upload event
        binding.addphotoBtnUpload.setOnClickListener {
            contentUpload()
            Log.e("test", "${photoUri}")
        }

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { photoPickerIntent ->
            Log.e("11","1111")
            if (photoPickerIntent.resolveActivity(this.packageManager) != null) {
                Log.e("1","$currentPhotoPath?")
                // 찍은 사진을 그림파일로 만들기
                val photoFile: File? =
                    try {
                        Log.e("2","11")
                        createImageFile()
                    } catch (ex: IOException) {
                        Log.e("11", "pictures by taken camera is errored")
                        null
                    }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.physicalgallery.fileprovider",
                        it
                    )
                    photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                }
            }
            var intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            var chooserIntent = Intent.createChooser(intent,"Pick source")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(photoPickerIntent))

            Log.e("StartActivity0","${arrayOf(photoUri)}")
            Log.e("StartActivity","${arrayOf(photoPickerIntent)}")

            startActivityForResult(chooserIntent, PICK_IMAGE_FROM_ALBUM)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //val storageDir1: File = "/sdcard/Android/data/com.example.physicalgallery/files/Pictures"
        val storageDir: File = getExternalFilesDir("/sdcard/Android/data/com.example.physicalgallery/files/Pictures\" ")!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_FROM_ALBUM){
            photoUri = data?.data
            Log.e("111","${photoUri}")
            if (photoUri != null) {
                photoUri?.let { uri ->
                    binding.addphotoImage.setImageURI(photoUri)
                }
            }
            else {
                val file = File(currentPhotoPath)
                val selectedUri = Uri.fromFile(file)
                Log.e("Take Picture", "${selectedUri}")
                try {
                    selectedUri?.let {
                        val decode = ImageDecoder.createSource(this.contentResolver, selectedUri)
                        Log.e("Take Picture123123", "${decode}")
                        val bitmap = ImageDecoder.decodeBitmap(decode)
                        Log.e("Take Picture123123", "${bitmap}")
                        binding.addphotoImage.setImageBitmap(bitmap)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
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