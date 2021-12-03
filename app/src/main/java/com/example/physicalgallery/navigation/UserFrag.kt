package com.example.physicalgallery.navigation

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.physicalgallery.databinding.FragmentUserBinding
import com.example.physicalgallery.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserFrag : Fragment(){
    val binding by lazy{FragmentUserBinding.inflate(layoutInflater)}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var adapter = UserAdapter()
        binding.userRecyclerview.adapter = adapter
        binding.userRecyclerview.layoutManager = GridLayoutManager(activity,3)
        val view = binding.root
        return view
    }
    inner class UserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var firestore : FirebaseFirestore? = FirebaseFirestore.getInstance()
        var contents : ArrayList<ContentDTO> = arrayListOf()
        var uid = FirebaseAuth.getInstance().currentUser?.uid

        init{
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener{
                    querySnapshot, firebaseFirestoreException ->
                if(querySnapshot == null) return@addSnapshotListener
                for(snapshot in querySnapshot.documents){
                    contents.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                binding.postingNumber.text = contents.size.toString()

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

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contents[position].imageUrl).apply(
                RequestOptions().centerCrop()).into(imageview)
        }


        override fun getItemCount(): Int {
            return contents.size
        }


    }

//        binding.foodsearch.setOnClickListener{
//            val intent = Intent(this,FoodSearchActivity::class.java)
//            startActivity(intent)
}