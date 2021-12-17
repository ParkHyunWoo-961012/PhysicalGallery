package com.example.physicalgallery.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.physicalgallery.R
import com.example.physicalgallery.databinding.ContentDetailBinding
import com.example.physicalgallery.databinding.FragmentDetailBinding
import com.example.physicalgallery.navigation.TableDataModel.AlarmDTO
import com.example.physicalgallery.navigation.TableDataModel.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.content_detail.view.*


class DetailFrag : Fragment(){
    var firestore : FirebaseFirestore? = FirebaseFirestore.getInstance()
    override fun onCreateView(inflater : LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val binding = FragmentDetailBinding.inflate(layoutInflater)
        binding.detailRecyclerview.adapter = DetailAdapter()
        binding.detailRecyclerview.layoutManager =LinearLayoutManager(activity)
        val view = binding.root

        return view
    }
    inner class DetailAdapter : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
        var firestore : FirebaseFirestore? = FirebaseFirestore.getInstance()
        var contents : ArrayList<ContentDTO> = arrayListOf()
        var contentsUidList : ArrayList<String> = arrayListOf()
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        init{
            firestore?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)?.addSnapshotListener{
                //Want to show new working list by upper side so i add descending direction.
                    querySnapshot, firebaseFirestoreException ->
                contents.clear()
                contentsUidList.clear()
                if(querySnapshot == null) return@addSnapshotListener

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
            //val cot = contents.get(position)
            holder.bind()
        }

        inner class ViewHolder(val binding: ContentDetailBinding): RecyclerView.ViewHolder(binding.root){
            var view : View = binding.root
            fun bind(){
                val pos = adapterPosition
                //유저 아이디 보여주기위해서 -> 나중에 닉네임컬럼 만들어서 해보
                val arrayId = contents!![pos].userId.toString().split("@")
                binding.userProfileText.text = arrayId[0]
                //유저가 올린 사진 보여주기위해서
                Log.e("nqkwelnrqklwenr","${contents!![pos].imageUrl}")
                Glide.with(itemView).load(contents!![pos].imageUrl).into(binding.detailContentImage)
                //유저가 쓴글 보여주기
                binding.explain.text = contents!![pos].explain
                //좋아요 개수 보여주기
                binding.favoriteCounts.text = "Likes " + contents[pos].favoriteCount
                //favorite button click and favorite count increase -> this clicklistner action is executed
                binding.favoriteButton.setOnClickListener{
                    favoriteCilck(pos)
                }
                firestore?.collection("profileImages")?.document(contents!![pos].uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if(documentSnapshot == null) return@addSnapshotListener
                    Log.e("getFoodData2","${documentSnapshot.getString("profile_image")}")
                    Glide.with(itemView).load(documentSnapshot.getString("profile_image")).apply(
                        RequestOptions().circleCrop()).into(binding.userProfileImage)
                }
                //after clicked faivrote button faovorite button color changed or not changed if not cilcked
                if(contents!![pos].favorites.containsKey(uid)){
                    binding.favoriteButton.setImageResource(R.drawable.after_favor_click)
                }
                else{
                    binding.favoriteButton.setImageResource(R.drawable.before_favor_click)
                }
                binding.userProfileImage.setOnClickListener{
                    var userFrag = UserFrag()
                    var bundle = Bundle()
                    bundle.putString("destination",contents[pos].uid)
                    bundle.putString("userid",contents[pos].userId)
                    userFrag.arguments = bundle
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_contents,userFrag)?.commit()
                }
                view.comment_detail.setOnClickListener { v ->
                    var intent = Intent(v.context,CommentActivity::class.java)
                    intent.putExtra("contentUid",contentsUidList[pos])
                    intent.putExtra("destinationUid",contents[pos].uid)


                    ContextCompat.startActivity(v.context, intent, Bundle())
                }

            }

        }

        override fun getItemCount(): Int {
            return contents.size
        }

        fun favoriteCilck(position:Int){
            var favor = firestore?.collection("images")?.document(contentsUidList[position])
            firestore?.runTransaction{
                var content = it.get(favor!!).toObject(ContentDTO::class.java)

                if (content!!.favorites.containsKey(uid)){//좋아요 눌린상태 에서 다시 누르는 액션 때문에
                    content.favoriteCount = content?.favoriteCount-1
                    content.favorites.remove(uid)
                }
                else{//좋아요 안눌린상태에서 좋아요 누르는 액션을 위해
                    content.favoriteCount = content?.favoriteCount+1
                    content.favorites[uid!!] = true
                    // 좋아요 알림
                    favoriteAlarm(contents[position].uid!!)

                }
                it.set(favor,content)
            }

        }
        //좋아요 알람 함수
        fun favoriteAlarm(destinationUid : String){
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.kind= 0
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        }
    }

}
