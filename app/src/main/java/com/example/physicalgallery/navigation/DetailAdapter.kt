package com.example.physicalgallery.navigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.physicalgallery.R
import com.example.physicalgallery.databinding.ContentDetailBinding
import com.example.physicalgallery.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    var firestore : FirebaseFirestore? = FirebaseFirestore.getInstance()
    var contents : ArrayList<ContentDTO> = arrayListOf()
    var contentsUidList : ArrayList<String> = arrayListOf()
    var uid = FirebaseAuth.getInstance().currentUser?.uid


    init{
        firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener{
            querySnapshot, firebaseFirestoreException ->
            contents.clear()
            contentsUidList.clear()

            for(snapshot in querySnapshot!!.documents){
                var item = snapshot.toObject(ContentDTO::class.java)
                contents.add(item!!)
                contentsUidList.add(snapshot.id)
            }
            notifyDataSetChanged()
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): ViewHolder {
        val binding = ContentDetailBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailAdapter.ViewHolder, position: Int) {
        val cot = contents.get(position)
        holder.bind(cot)
    }

    inner class ViewHolder(val binding: ContentDetailBinding): RecyclerView.ViewHolder(binding.root){
        var view : View = binding.root
        fun bind(foods: ContentDTO){
            val pos = adapterPosition
            //유저 아이디 보여주기위해서 -> 나중에 닉네임컬럼 만들어서 해보
            binding.userProfileText.text = contents!![pos].userId
            //유저가 올린 사진 보여주기위해서
            Glide.with(itemView).load(contents!![pos].imageUrl).into(binding.detailContentImage)
            //유저가 쓴글 보여주기
            binding.explain.text = contents!![pos].explain
            //좋아요 개수 보여주기
            binding.favoriteCounts.text = "Likes " + contents[pos].favoriteCount
            //좋아요 버튼 클릭
            binding.favoriteButton.setOnClickListener{
                favoriteCilck(pos)
            }

            //좋아요 클릭된후 좋아요 버튼 색깔 바뀐거 보여주려고
            if(contents!![pos].favorites.containsKey(uid)){
                binding.favoriteButton.setImageResource(R.drawable.after_favor_click)
            }
            else{
                binding.favoriteButton.setImageResource(R.drawable.before_favor_click)
            }

        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    fun favoriteCilck(position:Int){
        var tsDoc = firestore?.collection("images")?.document(contentsUidList[position])
        firestore?.runTransaction{
            var content = it.get(tsDoc!!).toObject(ContentDTO::class.java)

            if (content!!.favorites.containsKey(uid)){//좋아요 눌린상태 에서 다시 누르는 액션 때문에
                content.favoriteCount = content?.favoriteCount-1
                content.favorites.remove(uid)
            }
            else{//좋아요 안눌린상태에서 좋아요 누르는 액션을 위해
                content.favoriteCount = content?.favoriteCount+1
                content.favorites[uid!!] = true
            }
            it.set(tsDoc,content)
        }
    }
}
