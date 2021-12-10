package com.example.physicalgallery.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.physicalgallery.databinding.FragmentUserBinding
import com.example.physicalgallery.databinding.TestsBinding
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
    inner class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
        var firestore : FirebaseFirestore? = FirebaseFirestore.getInstance()
        var contents : ArrayList<ContentDTO> = arrayListOf()
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        init{
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener{
                    querySnapshot, firebaseFirestoreException ->
                if(querySnapshot == null) return@addSnapshotListener
                for(snapshot in querySnapshot.documents){
                    contents.add(snapshot.toObject(ContentDTO::class.java)!!)
                    binding.postingNumber.text = contents.size.toString()
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): UserAdapter.ViewHolder {
            val binding = TestsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
            holder.bind(contents[position])
        }

        inner class ViewHolder(val binding: TestsBinding): RecyclerView.ViewHolder(binding.root){
            fun bind(data : ContentDTO){
                Glide.with(itemView.context).load(data.imageUrl).apply(
                    RequestOptions().centerCrop()).into(binding.userPageImage)
            }
        }

        override fun getItemCount(): Int {
            return contents.size
        }
    }
}