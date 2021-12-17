package com.example.physicalgallery.relatefood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.physicalgallery.databinding.ActivityDiaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class DiaryActivity : AppCompatActivity() {
    var currentuid = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = FirebaseFirestore.getInstance()
    val binding by lazy{ActivityDiaryBinding.inflate(layoutInflater)}
    val present_date = LocalDate.now().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = getIntent()
        //To manage data from food_Detail page when user click food add button to diary.
        if(intent.getStringExtra("FoodName") != null) {
            val add_food_name = intent.getStringExtra("FoodName")
            val add_food_calorie = intent.getIntExtra("Calorie",0)
            diarydatastore(add_food_name!!,add_food_calorie)
        }

        firestore?.collection("profileImages")?.document(currentuid!!)
        binding.testButton.setOnClickListener {
            val inten = Intent(this,FoodSearchActivity::class.java)
            startActivity(inten)
        }
        binding.calendar.setOnDateChangeListener { calendarView, year, month, day ->
            val selected_date = year.toString() + "-" + (month+1).toString() + "-" + day.toString()
            getFoodData(selected_date)
        }
    }

    fun diarydatastore(name:String, calorie:Int){
        var diary = firestore?.collection("diary")?.document(currentuid!!)
        firestore?.runTransaction{transaction->
            var diarydata = transaction.get(diary!!).toObject(FoodDiaryTable::class.java)
            if (diarydata == null){
                diarydata = FoodDiaryTable()
                diarydata!!.total_calories[present_date] = calorie
                diarydata!!.food_list[present_date] = mutableListOf(name)
                Log.e("!23123","${present_date}")
                transaction.set(diary,diarydata)

                return@runTransaction
            }

            if(diarydata!!.total_calories.containsKey(present_date)){
                diarydata!!.food_list[present_date]!!.add(name)
                diarydata!!.total_calories[present_date] = diarydata!!.total_calories[present_date]!! + calorie

            }else{
                diarydata!!.food_list[present_date] = mutableListOf(name)
                diarydata!!.total_calories[present_date] = calorie
            }
            transaction.set(diary,diarydata)
            return@runTransaction
        }
    }

    fun getFoodData(date : String){
        firestore?.collection("diary")?.document(currentuid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            var diarydata = documentSnapshot.toObject(FoodDiaryTable::class.java)
            //Log.e("getFoodData2","${diarydata!!.food_list[date]}")
            if( diarydata?.food_list?.get(date) != null) {
                var output_text = "먹은 음식 리스트 \n\n"
                for(i in diarydata.food_list[date]!!)
                    output_text = output_text + i.toString() + "\n"
                output_text = "\n" + output_text + "총 칼로리 " + diarydata!!.total_calories[date].toString()
                binding.diaryContent.text = output_text
            }else{
                binding.diaryContent.text = "You don't eat anything"
            }

        }
    }


}