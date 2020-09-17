package io.github.healthifier.walking_promoter.models

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM) {

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                "($ID Integer PRIMARY KEY, $DIARY_TITLE TEXT, $DIARY_DAY TEXT, $PHOTO_PATH TEXT)"
        db?.execSQL(CREATE_TABLE)
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
        val _success = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedID", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun getAllTitles():ArrayList<String>{
        val diaryTitles = arrayListOf<String>()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
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
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
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
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
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

    companion object {
        private val DB_NAME = "UsersDB"
        private val DB_VERSIOM = 1;
        private val TABLE_NAME = "users"
        private val ID = "id"
        private val DIARY_TITLE = "DiaryTitle"
        private val DIARY_DAY = "DiaryDay"
        private val PHOTO_PATH = "PhotoPath"
    }
}
