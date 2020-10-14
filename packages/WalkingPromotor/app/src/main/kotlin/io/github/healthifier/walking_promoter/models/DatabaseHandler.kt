package io.github.healthifier.walking_promoter.models

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM) {

    class StepCounts(val date: String, val count: Int){

        constructor(cal: Calendar, count: Int) : this(DATE_FORMAT.format(cal.time), count)

        override fun toString(): String {
            return String.format("date=%s, count=%d", date, count)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_DIARY = "CREATE TABLE $TABLE_NAME1 " +
                "($DIARY_DAY TEXT NOT NULL PRIMARY KEY, $DIARY_TITLE TEXT NOT NULL, $PHOTO_PATH TEXT NOT NULL)"
        val CREATE_TABLE_GOAL = "CREATE TABLE $TABLE_NAME2 " +
            "($DATE TEXT NOT NULL PRIMARY KEY, $COUNT INTEGER NOT NULL)"
        val CREATE_TABLE_STEPCOUNT = "CREATE TABLE $TABLE_NAME3 " +
            "($DATE TEXT NOT NULL PRIMARY KEY, $COUNT INTEGER NOT NULL)"
        db.execSQL(CREATE_TABLE_DIARY)
        db.execSQL(CREATE_TABLE_GOAL)
        db.execSQL(CREATE_TABLE_STEPCOUNT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Called when the database needs to be upgraded
    }

    //Inserting (Creating) data
    fun addUser(user: Users): Boolean {
        //Create and/or open a database that will be used for reading and writing.
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DIARY_TITLE, user.diaryTitle)
        values.put(DIARY_DAY, user.diaryDay)
        values.put(PHOTO_PATH, user.photoPath)
        val _success = db.insert(TABLE_NAME1, null, values)
        db.close()
        Log.v("InsertedID", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun getAllTitles():ArrayList<String>{
        val diaryTitles = arrayListOf<String>()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME1"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    diaryTitles.add(cursor.getString(cursor.getColumnIndex(DIARY_TITLE)))
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()

        return diaryTitles
    }

    fun getAllPhotos():ArrayList<String>{
        val diaryPhotos = arrayListOf<String>()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME1"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    diaryPhotos.add(cursor.getString(cursor.getColumnIndex(PHOTO_PATH)))
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()

        return diaryPhotos
    }

    //get all users
    fun getAllUsers(): List<DiaryData> {
        //var allUser: String = ""
        //var data = arrayListOf<Triple<String, String, String>>()
        //var contents: String = ""
        val diaryTitles = arrayListOf<String>()
        val diaryDays = arrayListOf<String>()
        val photoPaths = arrayListOf<String>()

        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME1"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    //var id = cursor.getString(cursor.getColumnIndex(ID))
                    //var diaryTitle = cursor.getString(cursor.getColumnIndex(DIARY_TITLE))
                    //var diaryDay = cursor.getString(cursor.getColumnIndex(DIARY_DAY))
                    //var photoPath = cursor.getString(cursor.getColumnIndex(PHOTO_PATH))
                    //var info = Triple(diaryTitle,diaryDay,photoPath)
                    //allUser = "$allUser\n$id $diaryTitle $diaryDay $photoPath"
                    //contents = "$diaryTitle  ($diaryDay) $photoPath"
                    //data.add(contents)
                    //data.add(info)
                    diaryTitles.add(cursor.getString(cursor.getColumnIndex(DIARY_TITLE)))
                    diaryDays.add(cursor.getString(cursor.getColumnIndex(DIARY_DAY)))
                    photoPaths.add(cursor.getString(cursor.getColumnIndex(PHOTO_PATH)))

                    //allUser = "$allUser\n$id $diaryTitle"
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        //return allUser
        val allDiaries = List(diaryTitles.size) { i -> DiaryData(diaryTitles[i], diaryDays[i], photoPaths[i]) }

        return allDiaries
    }

    fun setGoal(cal: Calendar, count: Int){
        val cv = ContentValues()
        cv.put(DATE, DATE_FORMAT.format(cal.time))
        cv.put(COUNT, count)
        replace(TABLE_NAME2, cv)
    }

    fun getGoal(cal: Calendar):Int{
        val query = "SELECT $COUNT FROM $TABLE_NAME2 WHERE $DATE = ?"
        val params = arrayOf<String>(DATE_FORMAT.format(cal.time))
        return selectInt(query, params, 0)
    }

    fun setStep(cal: Calendar, count: Int){
        val cv = ContentValues()
        cv.put(DATE, DATE_FORMAT.format(cal.time))
        cv.put(COUNT, count)
        replace(TABLE_NAME3, cv)
    }

    fun getStep(cal: Calendar):Int{
        val query = "SELECT $COUNT FROM $TABLE_NAME3 WHERE $DATE = ?"
        val params = arrayOf<String>(DATE_FORMAT.format(cal.time))
        return selectInt(query, params, 0)
    }

    fun updateStepCountRecords(records: List<StepCounts>){
        val stepQuery = "SELECT $COUNT FROM $TABLE_NAME3 WHERE $DATE = ?"
        val db = writableDatabase
        try {
            db.beginTransaction()
            for(record in records){
                try {
                    val dataStr = DATE_FORMAT.format(DATE_FORMAT.parse(record.date)!!)
                    var stepCount:Int = -1
                    val cursor = db.rawQuery(stepQuery, arrayOf(dataStr))
                    try {
                        if(cursor.moveToNext()){
                            if (cursor.isNull(0) == false){
                                stepCount = cursor.getInt(0)
                            }
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    val cv = ContentValues()
                    cv.put(DATE, dataStr)
                    cv.put(COUNT, record.count)
                    if (stepCount == -1){
                        db.insert(TABLE_NAME3, null, cv)
                    }else if(stepCount < record.count){
                        db.replace(TABLE_NAME3, null, cv)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }

            db.setTransactionSuccessful()
            db.endTransaction()

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun getMyStepCount():Int{
        val query = "SELECT SUM($COUNT) FROM $TABLE_NAME3"
        //val params = arrayOf()
        val db = writableDatabase
        val cursor = db.rawQuery(query, null)
        try {
            if(cursor.moveToNext()){
                if(cursor.isNull(0) == false){
                    return cursor.getInt(0)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return 0
    }

    fun getMyStepCount(date: Calendar):Int{
        val query ="SELECT AVG($COUNT) FROM $TABLE_NAME3 WHERE $DATE = ?"
        val params = arrayOf(DATE_FORMAT.format(date.time))
        return selectInt(query, params, 0)
    }

    fun getMyAvgStepCount(start: Calendar, inclusiveEnd: Calendar):Double{
        val query = "SELECT AVG($COUNT) FROM $TABLE_NAME3 WHERE ? <= $DATE AND $DATE <= ? AND $COUNT > 0"
        val params = arrayOf(DATE_FORMAT.format(start.time), DATE_FORMAT.format(inclusiveEnd.time))
        return selectDouble(query, params, 0.0)
    }

    /*
    fun getProgress(today: Calendar): Fraction? {
        val query = "SELECT COUNT(DISTINCT(user_id)) FROM step_count"
        val max = selectInt(query, null, 0)
        if (max == 0) {
            return null
        }
        val query2 = "$query WHERE date = ?"
        val params = arrayOf(DATE_FORMAT.format(today.time))
        val current = selectInt(query2, params, 0)
        return Fraction(current, max)
    }*/

    private fun replace(tableName: String, cv: ContentValues){
        val db = writableDatabase
        try {
            db.replace(tableName, null, cv)
        }catch (e: Exception){
            Log.d("DB replace", e.toString())
        }
    }

    private fun selectInt(query: String, params: Array<String>, notFound: Int):Int{
        val db = readableDatabase
        val cursor = db.rawQuery(query, params)
        try {
            if(cursor.moveToNext()){
                if(cursor.isNull(0) == false){
                    return cursor.getInt(0)
                }
            }
        }catch (e: Exception){
            Log.d("selectInt", e.toString())
        }
        return notFound
    }

    private fun selectDouble(query: String, params: Array<String>, notFound: Double):Double{
        val db = readableDatabase
        val cursor = db.rawQuery(query, params)
        try {
            if(cursor.moveToNext()){
                if(cursor.isNull(0) == false){
                    return cursor.getDouble(0)
                }
            }
        }catch (e: Exception){
            Log.d("selectInt", e.toString())
        }
        return notFound
    }

    companion object {
        private val DB_NAME = "DiaryStep.sqlite"
        private val DB_VERSIOM = 2
        private val TABLE_NAME1 = "users"
        private val TABLE_NAME2 = "goal"
        private val TABLE_NAME3 = "step_count"
        private val ID = "id"
        private val DIARY_TITLE = "DiaryTitle"
        private val DIARY_DAY = "DiaryDay"
        private val PHOTO_PATH = "PhotoPath"
        private val DATE = "date"
        private val COUNT = "count"
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
    }
}
