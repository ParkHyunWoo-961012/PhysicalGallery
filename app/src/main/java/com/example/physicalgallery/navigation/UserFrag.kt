package com.example.physicalgallery.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.physicalgallery.Login
import com.example.physicalgallery.MainActivity
import com.example.physicalgallery.R
import com.example.physicalgallery.databinding.FragmentUserBinding
import com.example.physicalgallery.databinding.TestsBinding
import com.example.physicalgallery.navigation.TableDataModel.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class UserFrag : Fragment(){
    val binding by lazy{FragmentUserBinding.inflate(layoutInflater)}
    var uid : String? = null
    var auth :FirebaseAuth? = null
    var firestore : FirebaseFirestore? = FirebaseFirestore.getInstance()
    companion object { //
        var profilesetupnumber = 100
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var currentuid = FirebaseAuth.getInstance().currentUser?.uid
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
            mainactivity?.alarm.visibility = View.GONE
            mainactivity?.head_title?.visibility = View.GONE

        }

        //To upload profile image to Firestore this startactivitiyforResult executed
        binding.userProfileImage.setOnClickListener{
            var profileImagesetup = Intent(Intent.ACTION_PICK)
            profileImagesetup.type = "image/"
            startActivityForResult(profileImagesetup,profilesetupnumber)
        }

        getProfileImage(currentuid!!)


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
}