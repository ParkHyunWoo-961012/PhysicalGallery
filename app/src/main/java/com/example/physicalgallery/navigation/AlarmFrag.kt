package com.example.physicalgallery.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.physicalgallery.R
import com.example.physicalgallery.navigation.TableDataModel.AlarmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_comment.view.*
import kotlinx.android.synthetic.main.fragment_alarm.view.*


class AlarmFrag : Fragment(){
    override fun onCreateView(inflater : LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_alarm,container,false)
        view.alarmfragment_recyclerview.adapter = AlarmRecycleviewAdapter()
        view.alarmfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }
    inner class AlarmRecycleviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var alarmDTOList : ArrayList<AlarmDTO> = arrayListOf()

        init{
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            var alarmcollection = FirebaseFirestore.getInstance().collection("alarms")
            alarmcollection.whereEqualTo("destinationUid",uid).addSnapshotListener { value, error ->
                alarmDTOList.clear()
                if(value==null) return@addSnapshotListener

                for(snapshot in value.documents){
                    alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.content_comment,parent,false)

            return CustomViewHolder(view)
        }
        override fun getItemCount(): Int {
            return alarmDTOList.size
        }
        inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView

            var profilecollection = FirebaseFirestore.getInstance().collection("profileImages")
            profilecollection.document(alarmDTOList[position].uid!!).get().addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    var url = task.result!!["image"]
                    var urlglide = Glide.with(view.context).load(url)
                    urlglide.apply(RequestOptions().circleCrop()).into(view.commentview_image_profile)
                }
            }
            when(alarmDTOList[position].kind){
                0->{
                    var str_0 = alarmDTOList[position].userId + getString(R.string.alarm_favorite)
                    view.commentview_text_profile.text = str_0
                }

                1->{
                    var str_0 = alarmDTOList[position].userId + " " + getString(R.string.alarm_comment)+" of "+ alarmDTOList[position].message
                    view.commentview_text_profile.text = str_0
                }
                2->{
                    var str_0 = alarmDTOList[position].userId + getString(R.string.alarm_follow)
                    view.commentview_text_profile.text = str_0
                }
            }
            view.commentview_text_comment.visibility = View.INVISIBLE


        }



    }
}