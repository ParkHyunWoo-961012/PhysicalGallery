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
import com.example.physicalgallery.navigation.TableDataModel.AlarmDTO
import com.example.physicalgallery.navigation.TableDataModel.ContentDTO
import com.example.physicalgallery.navigation.TableDataModel.FollowTable
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
            Log.e("testing", "${arguments?.getString("userid")}")
            mainactivity?.back_button?.setOnClickListener{
                mainactivity.bottom_navigation.selectedItemId = R.id.home
            }
            mainactivity?.back_button?.visibility = View.VISIBLE
            mainactivity?.head_user_name?.visibility = View.VISIBLE
            mainactivity?.head_title?.visibility = View.GONE

            //follow statue button click event managed in this line
            binding.followButton.setOnClickListener {
                follow()
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
        alarmDTO.userId = auth?.currentUser?.email
        alarmDTO.uid = auth?.currentUser?.uid
        alarmDTO.kind = 2
        alarmDTO.timestamp = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

    }


    fun getProfileImage(id : String){
        firestore?.collection("profileImages")?.document(id!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            Log.e("123123","${21231231231233123}")
            if(documentSnapshot.data != null){
                Log.e("value.data","${documentSnapshot.data}")
                var url = documentSnapshot?.data!!["profile_image"]
                activity?.let { Glide.with(it).load(url).apply(RequestOptions().circleCrop()).into(binding.userProfileImage!!) }
            }
        }
    }

    fun follow(){
        //save follow data to my account table data
        var following = firestore?.collection("user")?.document(currentuid!!)
        firestore?.runTransaction{transaction->
            var followingdata = transaction.get(following!!).toObject(FollowTable::class.java)
            if (followingdata == null){// Initialize the follow data of current user if don't have any follower
                followingdata = FollowTable()

                followingdata!!.numfollwing = 1
                followingdata!!.followings[uid!!] = true

                transaction.set(following,followingdata)

                return@runTransaction
            }
            if(followingdata.followings.containsKey(uid)){
                followingdata?.numfollwing = followingdata?.numfollwing!! -1
                followingdata?.followings?.remove(uid)
                binding.followButton.text = "Follow"
            }else{
                followingdata?.numfollwing = followingdata?.numfollwing!! +1
                followingdata?.followings!![uid!!] = true
                binding.followButton.text = "Follow Cancel"
                return@runTransaction
            }

            transaction.set(following,followingdata)
            return@runTransaction
        }

        var follower = firestore?.collection("user")?.document(uid!!)
        firestore?.runTransaction{transaction->
            var followerdata = transaction.get(follower!!).toObject(FollowTable::class.java)
            if (followerdata == null){// Initialize the follow data of current user if don't have any follower
                followerdata = FollowTable()

                followerdata!!.numfollwing = 1
                followerdata!!.followings[currentuid!!] = true
                followerAlarm(uid!!)
                transaction.set(follower,followerdata)

                return@runTransaction
            }
            if(followerdata.followers.containsKey(currentuid)){
                //when click follow cancel other users
                followerdata?.numfollwing = followerdata?.numfollower!! -1
                followerdata?.followers?.remove(currentuid)
                binding.followButton.text = "Follow"
            }else{
                followerdata?.numfollower = followerdata?.numfollower!! +1
                followerdata?.followers!![currentuid!!] = true
                binding.followButton.text = "Follow Cancel"
                followerAlarm(uid!!)
            }

            transaction.set(follower,followerdata)
            return@runTransaction
        }
        //save follow data to event occured other user table data
    }
    fun getfollownumber(){
        firestore?.collection("user")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            var followdata = documentSnapshot.toObject(FollowTable::class.java)
            if(followdata?.numfollwing == null){
                binding.followingNumber.text = 0.toString() //followdata?.numfollwing.toString()
            }
            if(followdata?.numfollower != null){
                binding.followerNumber.text = followdata?.numfollwing.toString()
                if(followdata?.followers?.containsKey(currentuid)!!){
                    binding.followButton.text = "Follow Cancel"
                }else{
                    if(uid != currentuid){
                        binding.followButton.text = "Follow"
                    }
                }
            }
        }
    }
}