package com.example.physicalgallery

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "Food")
data class Food(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "food_name") val food_name:String,
    @ColumnInfo(name = "big_classifier") val big_classifier:String,
    @ColumnInfo(name = "small_classifier") val small_classifier:String,
    @ColumnInfo(name = "provide_per_time") val provide_per_time:String,
    @ColumnInfo(name = "provide_unit") val provide_unit:String,
    @ColumnInfo(name = "provide_total") val provide_total:String,
    @ColumnInfo(name = "calories") val calories:String,
    @ColumnInfo(name = "protein") val protein:String,
    @ColumnInfo(name = "fat") val fat:String,
    @ColumnInfo(name = "carbo") val carbo:String,
    @ColumnInfo(name = "sugar") val sugar:String,
    @ColumnInfo(name = "fiber") val fiber:String,
    @ColumnInfo(name = "calcium") val calcium:String,
    @ColumnInfo(name = "potassium") val potassium:String,
    @ColumnInfo(name = "sodium") val sodium:String,
    @ColumnInfo(name = "amino") val amino:String,
    @ColumnInfo(name = "cholestero") val cholestero:String,
    @ColumnInfo(name = "totalfat") val totalfat:String,
    @ColumnInfo(name = "transfat") val transfat:String,
    @ColumnInfo(name = "caffein") val caffein:String
)

@Dao
interface FoodInterface{
    @Query("Select * From Food")
    fun getAll(): List<Food>

    @Insert
    fun addFoodDb(food : Food)
}

@Database(entities = [Food::class],version = 1,exportSchema = true)
abstract class FoodDatabase: RoomDatabase() {
    abstract fun FoodDao(): FoodInterface
    companion object {
        private var INSTANCE: FoodDatabase? = null

        fun getInstance(context: Context): FoodDatabase? {
            if (INSTANCE == null) {
                synchronized(FoodDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        FoodDatabase::class.java,
                        "Food.db"
                    ).createFromAsset("food.db").build()
                }
            }
            return INSTANCE
        }

    }
}

