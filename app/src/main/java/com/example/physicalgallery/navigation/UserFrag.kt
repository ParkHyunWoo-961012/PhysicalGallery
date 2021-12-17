package com.example.physicalgallery.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.physicalgallery.Login
import com.example.physicalgallery.MainActivity
import com.example.physicalgallery.R
import com.example.physicalgallery.databinding.FragmentUserBinding
import com.example.physicalgallery.databinding.UserpageRecyclerviewBinding
import com.example.physicalgallery.navigation.SNSDataModel.AlarmDTO
import com.example.physicalgallery.navigation.SNSDataModel.ContentDTO
import com.example.physicalgallery.navigation.SNSDataModel.FollowTable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class UserFrag : Fragment(){
    val binding by lazy{FragmentUserBinding.inflate(layoutInflater)}
    var uid : String? = null
    var auth :FirebaseAuth? = null
    var currentuid = FirebaseAuth.getInstance().currentUser?.uid
    var firestore : FirebaseFirestore? = FirebaseFirestore.getInstance()
    companion object { //
        var profilesetupnumber = 100
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        binding.btnItemAlertDialog.setOnClickListener{
//            val items = arrayOf("Profile Image Change", "NickName Setting")
//            val toast : Toast
//            val builder = AlertDialog.Builder(this)
//                .setTitle("Select Item")
//                .setItems(items) { dialog, which ->
//                    toast("${items[which]} is Selected")
//                }
//                .show()
//        }

        uid = arguments?.getString("destination").toString()

        if (uid == currentuid) { //when Go to My page not other user's page
            binding.followButton.text = "SignOut"
            binding.followButton.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity, Login::class.java))
                auth?.signOut()
            }
        }

        else{//when go to other user's page not to my page
            binding.followButton.text = "Follow"
            var mainactivity = (activity as MainActivity)
            var username = arguments?.getString("userid")?.split("@")
            mainactivity?.head_user_name?.text = username?.get(0).toString()
            mainactivity?.back_button?.setOnClickListener{
                mainactivity.bottom_navigation.selectedItemId = R.id.home
            }
            mainactivity?.back_button?.visibility = View.VISIBLE
            mainactivity?.head_user_name?.visibility = View.VISIBLE
            mainactivity?.head_title?.visibility = View.GONE

            //follow statue button click event managed in this line
            binding.followButton.setOnClickListener {
                follow()
                getfollownumber()
            }
        }

        //To upload profile image to Firestore this startactivitiyforResult executed
        binding.userProfileImage.setOnClickListener{
            var profileImagesetup = Intent(Intent.ACTION_PICK)
            profileImagesetup.type = "image/"
            startActivityForResult(profileImagesetup,profilesetupnumber)
        }

        getProfileImage(currentuid!!)
        getfollownumber()


        var adapter = UserAdapter()
        binding.userRecyclerview.adapter = adapter

        binding.userRecyclerview.layoutManager = GridLayoutManager(activity,3)
        val view = binding.root
        return view
    }

    inner class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
        var contents : ArrayList<ContentDTO> = arrayListOf()
        init{
            firestore?.collection("alarms")?.whereEqualTo("uid",uid)?.addSnapshotListener{
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
            val binding = UserpageRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
            holder.bind(contents[position])
        }

        inner class ViewHolder(val binding: UserpageRecyclerviewBinding): RecyclerView.ViewHolder(binding.root){
            fun bind(data : ContentDTO){
                var wid = resources.displayMetrics.widthPixels/3
                itemView.layoutParams = LinearLayoutCompat.LayoutParams(wid,wid)
                Glide.with(itemView.context).load(data.imageUrl).apply(
                    RequestOptions().centerCrop()).into(binding.userPageImage)
            }
        }

        override fun getItemCount(): Int {
            return contents.size
        }
    }
    //팔로우 알람 함수
    fun followerAlarm(destinationUid : String){
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        Log.e("!23123123","${alarmDTO.userId}")
        alarmDTO.uid = currentuid
        alarmDTO.kind = "follow"
        alarmDTO.timestamp = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
    }


    fun getProfileImage(id : String){
        firestore?.collection("profileImages")?.document(id!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener

            if(documentSnapshot.data != null){
                Log.e("value.data","${documentSnapshot.data}")
                var url = documentSnapshot?.data!!["profile_image"]
                activity?.let { Glide.with(it).load(url).apply(RequestOptions().circleCrop()).into(binding.userProfileImage!!) }
            }else{
                binding.userProfileImage.setImageResource(R.mipmap.ic_launcher)
            }

        }
    }

    fun follow(){
        //save follow data to my account table data
        var following = firestore?.collection("FollowData")?.document(currentuid!!)
        firestore?.runTransaction{transaction->
            var followingdata = transaction.get(following!!).toObject(FollowTable::class.java)
            if (followingdata == null) {
                followingdata = FollowTable(numfollowing = 1)
                followingdata.followings[uid!!] = true
                transaction.set(following, followingdata)
                return@runTransaction
            }
            if (followingdata.followings.containsKey(uid)){
                followingdata.numfollowing = followingdata.numfollowing - 1
                followingdata.followings.remove(uid)
                transaction.set(following,followingdata)
            }else{
                followingdata.numfollowing = followingdata.numfollowing + 1
                followingdata.followings.remove(uid)
                transaction.set(following,followingdata)
            }
            return@runTransaction
        }

        var follower = firestore?.collection("FollowData")?.document(uid!!)
        firestore?.runTransaction{transaction->
            var followerdata = transaction.get(follower!!).toObject(FollowTable::class.java)
            if (followerdata == null){
                followerdata = FollowTable(numfollower = 1)
                followerdata.followers[currentuid!!] = true
                binding.followButton.text = "Follow Cancel"
                transaction.set(follower,followerdata)
                followerAlarm(uid!!)
                return@runTransaction
            }
            if(followerdata.followers.containsKey(currentuid)){
                followerdata.numfollower = followerdata.numfollower - 1
                followerdata.followers.remove(currentuid)
                binding.followButton.text = "Follow"
                transaction.set(follower,followerdata)
            }else{
                followerdata.numfollower = followerdata.numfollower + 1
                followerdata.followers[currentuid!!] = true
                binding.followButton.text = "Follow Cancel"
                followerAlarm(uid!!)
                transaction.set(follower,followerdata)
            }
            return@runTransaction
        }
        //save follow data to event occured other user table data
    }

    fun getfollownumber(){
        firestore?.collection("FollowData")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            var followdata = documentSnapshot.toObject(FollowTable::class.java)
            if(followdata?.numfollowing == null){
                binding.followingNumber.text = 0.toString()
                binding.followerNumber.text = 0.toString()//followdata?.numfollwing.toString()
            }
            if(followdata?.numfollower != null){
                binding.followingNumber.text = followdata?.numfollowing.toString()
                binding.followerNumber.text = followdata?.numfollower.toString()

                if(followdata?.followers?.containsKey(currentuid)!!){
                    binding.followButton.text = "Follow Cancel"
                }else{
                    if(uid == currentuid){
                        binding.followButton.text = "Sign Out"
                    }else{
                        binding.followButton.text = "Follow"
                    }
                }
            }
        }
    }
}