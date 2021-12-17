package com.example.physicalgallery.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.physicalgallery.databinding.ActivityAddPhotoBinding
import com.example.physicalgallery.navigation.SNSDataModel.ContentDTO
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
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var currentPhotoPath: String = ""
    var firestore: FirebaseFirestore? = null
    val binding by lazy { ActivityAddPhotoBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.e("Take Picture", "${Build.VERSION.SDK_INT}")
        //Initiate
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_FROM_ALBUM)


        binding.addphotoBtnUpload.setOnClickListener {
            contentUpload()
            Log.e("test", "${photoUri}")
        }

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                Log.e("4","123123")
                val photoFile: File? =
                    try {
                        createImageFile()
                    } catch (ex: IOException) {
                        Log.d("TAG", "그림파일 만드는도중 에러생김")
                        null
                    }

                if (Build.VERSION.SDK_INT < 24) {
                    if(photoFile != null){
                        val photoURI = Uri.fromFile(photoFile)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    }
                }
                else{
                    // 그림파일을 성공적으로 만들었다면 startActivityForResult로 보내기
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this, "com.example.cameraonly.fileprovider", it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    }
                }

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT

                val chooserIntent = Intent.createChooser(intent, "-")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))
                startActivityForResult(chooserIntent, PICK_IMAGE_FROM_ALBUM)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //val storageDir1: File = "/sdcard/Android/data/com.example.physicalgallery/files/Pictures"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        Log.e("!123123","${Environment.DIRECTORY_PICTURES}")
            //getExternalFilesDir("/sdcardPICK_IMAGE_FROM_ALBUM /Android/data/com.example.physicalgallery/files/Pictures\" ")!!
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
            Log.e("123123","123123123123123123123123123")
            photoUri = data?.data
            Log.e("123123","${photoUri}}")
            binding.addphotoImage.setImageURI(photoUri)

        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
    }

    fun contentUpload(){
        //make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //promise method
        var upload_image = storageRef?.putFile(photoUri!!)

        upload_image?.continueWithTask{ task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef?.downloadUrl!!
        }?.addOnSuccessListener{
            var content = ContentDTO()

            content.imageUrl = it.toString()

            content.uid = auth?.currentUser?.uid
            content.userId = auth?.currentUser?.email
            content.explain = binding.addphotoEditExplain.text.toString()
            content.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(content)
            setResult(Activity.RESULT_OK)
            Toast.makeText(this,"업로드 성공했습니다",Toast.LENGTH_LONG).show()
            finish()
        }?.addOnFailureListener{
            Toast.makeText(this,"업로드 실했습니다",Toast.LENGTH_LONG).show()
        }
//        upload_image?.addOnSuccessListener{
//            storageRef?.downloadUrl?.addOnSuccessListener {
//                var content = ContentDTO()
//패
//                content.imageUrl = it.toString()
//
//                content.uid = auth?.currentUser?.uid
//                content.userId = auth?.currentUser?.email
//                content.explain = binding.addphotoEditExplain.text.toString()
//                content.timestamp = System.currentTimeMillis()
//
//                firestore?.collection("images")?.document()?.set(content)
//                setResult(Activity.RESULT_OK)
//                finish()
//
//            }
//        }
    }
}