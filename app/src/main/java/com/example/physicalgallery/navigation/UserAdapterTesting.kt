package com.example.physicalgallery.navigation

import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.physicalgallery.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//xml파일 따로없이 어답터로만 이미지 리사이클러뷰 만들어
class UserAdapterTesting : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var firestore : FirebaseFirestore? = FirebaseFirestore.getInstance()
    var contents : ArrayList<ContentDTO> = arrayListOf()
    var uid = FirebaseAuth.getInstance().currentUser?.uid
    var size :Int = 0

    init{
        firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener{
            querySnapshot, firebaseFirestoreException ->
            if(querySnapshot == null) return@addSnapshotListener
            for(snapshot in querySnapshot.documents){
                contents.add(snapshot.toObject(ContentDTO::class.java)!!)
            }
            Log.e("contents","${contents.size}")
            size = contents.size

            notifyDataSetChanged()
    }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): RecyclerView.ViewHolder {
        var displayMetrics = DisplayMetrics()
        var width = displayMetrics.widthPixels/3
        var imageview = ImageView(parent.context)
        imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)

        return CustomViewHolder(imageview)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var imageview = (holder as CustomViewHolder).imageview
        Glide.with(holder.itemView.context).load(contents[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
    }

    inner class CustomViewHolder(val imageview:ImageView): RecyclerView.ViewHolder(imageview){

    }

    override fun getItemCount(): Int {
        return size
    }


}
