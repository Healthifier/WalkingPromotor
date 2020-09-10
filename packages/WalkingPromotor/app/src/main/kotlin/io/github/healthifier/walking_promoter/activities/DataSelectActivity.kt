package io.github.healthifier.walking_promoter.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Display
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.nifcloud.mbaas.core.*
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import kotlinx.android.synthetic.main.activity_data_select.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DataSelectActivity : AppCompatActivity() {

    private var objList = listOf<NCMBObject>()
    private val user = NCMBUser.getCurrentUser()
    private var abc = NCMBObject("photoPath")
    private var dbHandler = DatabaseHandler(this)
    private val REQUEST_CAMERA = 1000
    private val REQUEST_GARALLY1 = 1001
    private val REQUEST_GARALLY2 = 1002
    private val REQUEST_GARALLY3 = 1003
    private val PERMISSION_REQUEST = 1010
    private var path: String =""
    private var filename: String = ""
    private var myNumber = 0
    private var displayNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_select)

        Log.d("name", user.userName.toString())
        Log.d("objectId", user.objectId.toString())

        val buttonsList = arrayListOf<Button>(button_user1, button_user2, button_user3, button_user4, button_user5)
        val imageList = arrayListOf<ImageView>(imageView3, imageView4, imageView5)
        var name = user.userName
        textView_user.text = name + "さんが選んだ写真の画面です"
        val querySelect = NCMBQuery<NCMBObject>("photoPath")
        querySelect.findInBackground { objects, error ->
            if (error != null) {
                Log.d("[Error69]", error.toString())
            } else {
                Log.d("[DEBUG71]", objects.toString())
                Log.d("[DEBUG72]", objects.size.toString())
                objList = objects
                for(i in 1..objList.size){
                    buttonsList[i-1].text = objList[i-1].getString("name")
                    if(user.userName == buttonsList[i-1].text){
                        if(objList[i-1].getList("array") != null) {
                            //setImages(objList[i - 1].getList("array"), imageList)
                        }
                        myNumber = i-1
                    }
                }
                Log.d("[DEBUG83]", myNumber.toString())
                Log.d("[DEBUG83]", objList.size.toString())
            }
        }
        val queryphoto = NCMBQuery<NCMBObject>("photoPath")
        queryphoto.whereEqualTo("userID", user.objectId.toString())
        queryphoto.findInBackground {objects, error ->
            if (error != null) {
                Log.d("[Error91]", error.toString())
            } else {
                if (objects.size == 1) {
                    abc = objects[0]
                    Log.d("d95", "あるよ")
                } else {
                    Log.d("d97", "ないよ")
                }
            }
        }

        //更新用のボタン
        button_update.setOnClickListener {
            querySelect.findInBackground { objects, error ->
                if (error != null) {
                    Log.d("[Error106]", error.toString())
                } else {
                    Log.d("[DEBUG108]", objects.toString())
                    Log.d("[DEBUG109]", objects.size.toString())
                    objList = objects
                    for(i in 1..objList.size){
                        buttonsList[i-1].text = objList[i-1].getString("name")
                        if(user.userName == buttonsList[i-1].text){
                            if(objList[i-1].getList("array") != null) {
                                setImages(objList[i - 1].getList("array"), imageList)
                            }
                            myNumber = i-1
                            displayNumber = i-1
                        }
                    }
                    Log.d("[DEBUG120]", objList.size.toString())
                    Log.d("[DEBUG122]", myNumber.toString())
                    Log.d("[DEBUG123]", displayNumber.toString())
                }
                Toast.makeText(this, "更新完了", Toast.LENGTH_SHORT).show()
            }
            textView_user.text = user.userName + "さんが選んだ写真の画面です"
        }

        button_back.setOnClickListener {
            val intent = Intent(this, ProgramActivity::class.java)
            startActivity(intent)
        }

        //user1の画像データ表示用のボタン
        button_user1.setOnClickListener {
            name = button_user1.text.toString()
            displayNumber = 0
            deleteImages()
            textView_user.text = name + "さんが選んだ写真の画面です"
            if(objList.isEmpty()){
                Toast.makeText(this, "データがありません", Toast.LENGTH_SHORT).show()
            }else {
                setImages(objList[0].getList("array"), imageList)
                Toast.makeText(this, "画像の表示完了", Toast.LENGTH_SHORT).show()
            }
        }

        //user2の画像データ表示用のボタン
        button_user2.setOnClickListener {
            name = button_user2.text.toString()
            displayNumber = 1
            deleteImages()
            textView_user.text = name + "さんが選んだ写真の画面です"
            if(objList.size < 2){
                Toast.makeText(this, "データがありません", Toast.LENGTH_SHORT).show()
            }else{
                setImages(objList[1].getList("array"), imageList)
                Toast.makeText(this, "画像の表示完了", Toast.LENGTH_SHORT).show()
            }
        }

        //user3の画像データ表示用のボタン
        button_user3.setOnClickListener {
            name = button_user3.text.toString()
            displayNumber = 2
            deleteImages()
            textView_user.text = name + "さんが選んだ写真の画面です"
            if(objList.size < 3){
                Toast.makeText(this, "データがありません", Toast.LENGTH_SHORT).show()
            }else{
                setImages(objList[2].getList("array"), imageList)
                Toast.makeText(this, "画像の表示完了", Toast.LENGTH_SHORT).show()
            }
        }

        //user4の画像データ表示用のボタン
        button_user4.setOnClickListener {
            name = button_user4.text.toString()
            displayNumber = 3
            deleteImages()
            textView_user.text = name + "さんが選んだ写真の画面です"
            if(objList.size < 4){
                Toast.makeText(this, "データがありません", Toast.LENGTH_SHORT).show()
            }else{
                setImages(objList[3].getList("array"), imageList)
                Toast.makeText(this, "画像の表示完了", Toast.LENGTH_SHORT).show()
            }
        }

        //user5の画像データ表示用のボタン
        button_user5.setOnClickListener {
            name = button_user5.text.toString()
            displayNumber = 4
            deleteImages()
            textView_user.text = name + "さんが選んだ写真の画面です"
            if(objList.size < 5){
                Toast.makeText(this, "データがありません", Toast.LENGTH_SHORT).show()
            }else{
                setImages(objList[4].getList("array"), imageList)
                Toast.makeText(this, "画像の表示完了", Toast.LENGTH_SHORT).show()
            }
        }

        imageView3.setOnClickListener{
            if(null == imageView3.drawable){
                Toast.makeText(this, "データがありません", Toast.LENGTH_SHORT).show()
                selectPhoto1()
            }else {
                val dialog = Dialog(this)
                dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                val bitmap = (imageView3.drawable as BitmapDrawable).bitmap
                val customdialogView: View = layoutInflater.inflate(R.layout.custom_dialog_layout, null)
                dialog.setContentView(customdialogView)
                Log.d("bitmap", bitmap.width.toString())
                val imageView_dialog = customdialogView.findViewById<ImageView>(R.id.imageView_dialog)
                imageView_dialog.setImageBitmap(bitmap)
                val btn_change = customdialogView.findViewById<Button>(R.id.btn_change)
                btn_change.setOnClickListener {
                    if(displayNumber == myNumber) {
                        selectPhoto1()
                        //Toast.makeText(this, "写真の差し替えに成功しました", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "他の人の写真は差し替えられません", Toast.LENGTH_SHORT).show()
                    }
                }

                val btn_close = customdialogView.findViewById<Button>(R.id.btn_close)
                btn_close.setOnClickListener {
                    dialog.dismiss()
                }

                val btn_speaker = customdialogView.findViewById<Button>(R.id.btn_speaker)
                btn_speaker.setOnClickListener {
                    val dialog = Dialog(this)
                    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                    val customdialogView: View = layoutInflater.inflate(R.layout.custom_dialog_speak, null)
                    dialog.setContentView(customdialogView)
                    dialog.show()
                }

                val btn_listener = customdialogView.findViewById<Button>(R.id.btn_listener)
                btn_listener.setOnClickListener {
                    val dialog = Dialog(this)
                    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                    val customdialogView: View = layoutInflater.inflate(R.layout.custom_dialog_listen, null)
                    dialog.setContentView(customdialogView)
                    dialog.window?.setLayout(1450, 500)
                    dialog.show()
                }

                val display: Display = windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                val width = size.x
                val factor = width.toFloat() / bitmap.width.toFloat()
                dialog.window?.setLayout(
                    (bitmap.width * factor).toInt(),
                    (bitmap.height * factor *0.7).toInt()
                )

                dialog.show()
            }
        }

        imageView4.setOnClickListener{
            if(null == imageView4.drawable){
                Toast.makeText(this, "データがありません", Toast.LENGTH_SHORT).show()
                selectPhoto2()
            }else {
                val dialog = Dialog(this)
                dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                val bitmap = (imageView4.drawable as BitmapDrawable).bitmap
                val customdialogView: View = layoutInflater.inflate(R.layout.custom_dialog_layout, null)
                dialog.setContentView(customdialogView)
                Log.d("bitmap", bitmap.width.toString())
                val imageView_dialog = customdialogView.findViewById<ImageView>(R.id.imageView_dialog)
                imageView_dialog.setImageBitmap(bitmap)
                val btn_change = customdialogView.findViewById<Button>(R.id.btn_change)
                btn_change.setOnClickListener {
                    if(displayNumber == myNumber) {
                        selectPhoto2()
                        //Toast.makeText(this, "写真の差し替えに成功しました", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "他の人の写真は差し替えられません", Toast.LENGTH_SHORT).show()
                    }
                }

                val btn_close = customdialogView.findViewById<Button>(R.id.btn_close)
                btn_close.setOnClickListener {
                    dialog.dismiss()
                }

                val btn_speaker = customdialogView.findViewById<Button>(R.id.btn_speaker)
                btn_speaker.setOnClickListener {
                    val dialog = Dialog(this)
                    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                    val customdialogView: View = layoutInflater.inflate(R.layout.custom_dialog_speak, null)
                    dialog.setContentView(customdialogView)
                    dialog.show()
                }

                val btn_listener = customdialogView.findViewById<Button>(R.id.btn_listener)
                btn_listener.setOnClickListener {
                    val dialog = Dialog(this)
                    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                    val customdialogView: View = layoutInflater.inflate(R.layout.custom_dialog_listen, null)
                    dialog.setContentView(customdialogView)
                    dialog.window?.setLayout(1450, 500)
                    dialog.show()
                }

                val display: Display = windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                val width = size.x
                val factor = width.toFloat() / bitmap.width.toFloat()
                dialog.window?.setLayout(
                    (bitmap.width * factor).toInt(),
                    (bitmap.height * factor *0.7).toInt()
                )

                dialog.show()
            }
        }

        imageView5.setOnClickListener {
            if (null == imageView5.drawable) {
                Toast.makeText(this, "データがありません", Toast.LENGTH_SHORT).show()
                selectPhoto3()
            } else {
                val dialog = Dialog(this)
                dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                val bitmap = (imageView5.drawable as BitmapDrawable).bitmap
                val customdialogView: View = layoutInflater.inflate(R.layout.custom_dialog_layout, null)
                dialog.setContentView(customdialogView)
                Log.d("bitmap", bitmap.width.toString())
                val imageView_dialog = customdialogView.findViewById<ImageView>(R.id.imageView_dialog)
                imageView_dialog.setImageBitmap(bitmap)
                val btn_change = customdialogView.findViewById<Button>(R.id.btn_change)
                btn_change.setOnClickListener {
                    if(displayNumber == myNumber) {
                        selectPhoto3()
                        //Toast.makeText(this, "写真の差し替えに成功しました", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "他の人の写真は差し替えられません", Toast.LENGTH_SHORT).show()
                    }
                }

                val btn_close = customdialogView.findViewById<Button>(R.id.btn_close)
                btn_close.setOnClickListener {
                    dialog.dismiss()
                }

                val btn_speaker = customdialogView.findViewById<Button>(R.id.btn_speaker)
                btn_speaker.setOnClickListener {
                    val dialog = Dialog(this)
                    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                    val customdialogView: View = layoutInflater.inflate(R.layout.custom_dialog_speak, null)
                    dialog.setContentView(customdialogView)
                    dialog.show()
                }

                val btn_listener = customdialogView.findViewById<Button>(R.id.btn_listener)
                btn_listener.setOnClickListener {
                    val dialog = Dialog(this)
                    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                    val customdialogView: View = layoutInflater.inflate(R.layout.custom_dialog_listen, null)
                    dialog.setContentView(customdialogView)
                    dialog.window?.setLayout(1450, 500)
                    dialog.show()
                }

                val display: Display = windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                val width = size.x
                val factor = width.toFloat() / bitmap.width.toFloat()
                dialog.window?.setLayout(
                    (bitmap.width * factor).toInt(),
                    (bitmap.height * factor *0.7).toInt()
                )

                dialog.show()
            }
        }
    }

    /**
     * 表示されている画像を取り除く.
     */
    private fun deleteImages(){
        imageView3.setImageDrawable(null)
        imageView4.setImageDrawable(null)
        imageView5.setImageDrawable(null)
    }

    private fun setImages(selectName: List<*>, imageName: ArrayList<ImageView>){
        Toast.makeText(this, "画像を準備中", Toast.LENGTH_SHORT).show()
        val query: NCMBQuery<NCMBFile> = NCMBFile.getQuery()
        val listSize = selectName.size-1
        for (i in 0..listSize) {
            query.whereEqualTo("fileName", selectName[i])
            query.findInBackground { list, ncmbException ->
                if (ncmbException != null) {
                    Log.d("[Error329]", ncmbException.toString())
                } else {
                    Log.d("debug331", list[0].toString())
                    list[0].fetchInBackground { dataFetch, er ->
                        if (er != null) {
                            //失敗処理
                            AlertDialog.Builder(this@DataSelectActivity)
                                .setTitle("Notification from NIFCloud")
                                .setMessage("Error:" + er.message)
                                .setPositiveButton("OK", null)
                                .show()
                        } else {
                            //成功処理
                            val bMap = BitmapFactory.decodeByteArray(dataFetch, 0, dataFetch.size)
                            imageName[i].setImageBitmap(bMap)
                        }
                    }
                }
            }
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra(MediaStore.EXTRA_OUTPUT, createSaveFileUri())
        }

        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun selectPhoto1(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GARALLY1 )
    }

    private fun selectPhoto2(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GARALLY2 )
    }

    private fun selectPhoto3(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GARALLY3 )
    }

    private fun checkPermission(): Boolean {
        val cameraPermission = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)

        val extraStoragePermission = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)

        return cameraPermission && extraStoragePermission
    }

    private fun grantCameraPermission() =
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put("_data", path)
            }

            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        }else if(requestCode == REQUEST_GARALLY1 && resultCode == Activity.RESULT_OK) {
            Log.d("deb", "REQUEST_GARALLY1")
            val uri: Uri?
            if(data != null){
                uri = data.data
                try{
                    Log.d("[DEBUG503]", uri.toString())
                    Log.d("[DEBUG504]", uri?.path.toString())
                    val strDocId = DocumentsContract.getDocumentId(uri)
                    val strSplittedDocId = strDocId.split(":")
                    val strId = strSplittedDocId[strSplittedDocId.size-1]
                    val projection = arrayOf(MediaStore.MediaColumns.DATA)
                    val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, "_id=?", arrayOf(strId), null)
                    var name = ""
                    if(cursor != null){
                        if(cursor.moveToFirst()){
                            name = cursor.getString(0)
                        }
                        cursor.close()
                        Log.d("[DEBUG516]", name)
                    }
                    val query: NCMBQuery<NCMBFile> = NCMBFile.getQuery()
                    query.whereEqualTo("fileName", name.substringAfterLast("/"))
                    query.findInBackground { list, ncmbException ->
                        if (ncmbException != null) {
                            Log.d("[Error522]", ncmbException.toString())
                        } else {
                            if(list.size != 0){
                                Log.d("[DEBUG525]", "クラウド上にあるよ")
                                Log.d("Size461", File(name).readBytes().toString())
                            }else{
                                savePicToCloud(name)
                            }
                            val updateList = abc.getList("array")
                            val photoNameListCloud = arrayListOf<String>()
                            if(updateList == null){
                                photoNameListCloud.add(name.substringAfterLast("/"))
                                abc.put("array", photoNameListCloud)
                                saveUsersList(user.objectId.toString(), user.userName.toString(), photoNameListCloud)
                            }else{
                                updateList[0] = name.substringAfterLast("/")
                                saveUsersList(user.objectId.toString(), user.userName.toString(), updateList)
                            }
                        }
                    }
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    imageView3.setImageBitmap(bitmap)
                }catch (e: IOException){
                }
            }
        }else if(requestCode == REQUEST_GARALLY2 && resultCode == Activity.RESULT_OK) {
            Log.d("deb", "REQUEST_GARALLY2")
            val uri: Uri?
            if(data != null){
                uri = data.data
                try{
                    Log.d("[DEBUG]", uri.toString())
                    Log.d("[DEBUG]", uri?.path.toString())
                    val strDocId = DocumentsContract.getDocumentId(uri)
                    val strSplittedDocId = strDocId.split(":")
                    val strId = strSplittedDocId[strSplittedDocId.size-1]
                    val projection = arrayOf(MediaStore.MediaColumns.DATA)
                    val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, "_id=?", arrayOf(strId), null)
                    var name = ""
                    if(cursor != null){
                        if(cursor.moveToFirst()){
                            name = cursor.getString(0)
                        }
                        cursor.close()
                        Log.d("[DEBUG]", name)
                    }
                    val query: NCMBQuery<NCMBFile> = NCMBFile.getQuery()
                    query.whereEqualTo("fileName", name.substringAfterLast("/"))
                    query.findInBackground { list, ncmbException ->
                        if (ncmbException != null) {
                            Log.d("[Error]", ncmbException.toString())
                        } else {
                            if(list.size != 0){
                                Log.d("[DEBUG]", "クラウド上にあるよ")
                                Log.d("Size", File(name).readBytes().toString())
                            }else{
                                savePicToCloud(name)
                            }
                            val updateList = abc.getList("array")
                            Log.d("deb", updateList.toString())
                            when (updateList.size) {
                                0 -> {
                                    updateList.add(0, name.substringAfterLast("/"))
                                }
                                1 -> {
                                    updateList.add(1, name.substringAfterLast("/"))
                                }
                                else -> {
                                    updateList[1] = name.substringAfterLast("/")
                                }
                            }
                            saveUsersList(user.objectId.toString(), user.userName.toString(), updateList)
                        }
                    }
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    imageView4.setImageBitmap(bitmap)
                }catch (e: IOException){
                }
            }
        }else if(requestCode == REQUEST_GARALLY3 && resultCode == Activity.RESULT_OK) {
            Log.d("deb", "REQUEST_GARALLY3")
            val uri: Uri?
            if(data != null){
                uri = data.data
                try{
                    Log.d("[DEBUG607]", uri.toString())
                    Log.d("[DEBUG608]", uri?.path.toString())
                    val strDocId = DocumentsContract.getDocumentId(uri)
                    val strSplittedDocId = strDocId.split(":")
                    val strId = strSplittedDocId[strSplittedDocId.size-1]
                    val projection = arrayOf(MediaStore.MediaColumns.DATA)
                    val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, "_id=?", arrayOf(strId), null)
                    var name = ""
                    if(cursor != null){
                        if(cursor.moveToFirst()){
                            name = cursor.getString(0)
                        }
                        cursor.close()
                        Log.d("[DEBUG620]", name)
                    }
                    val query: NCMBQuery<NCMBFile> = NCMBFile.getQuery()
                    query.whereEqualTo("fileName", name.substringAfterLast("/"))
                    query.findInBackground { list, ncmbException ->
                        if (ncmbException != null) {
                            Log.d("[Error]", ncmbException.toString())
                        } else {
                            if(list.size != 0){
                                Log.d("[DEBUG]", "クラウド上にあるよ")
                                Log.d("Size", File(name).readBytes().toString())
                            }else{
                                savePicToCloud(name)
                            }
                            val updateList = abc.getList("array")
                            Log.d("deb", updateList.toString())
                            when (updateList.size) {
                                0 -> {
                                    updateList.add(0, name.substringAfterLast("/"))
                                }
                                1 -> {
                                    updateList.add(1, name.substringAfterLast("/"))
                                }
                                2 -> {
                                    updateList.add(2, name.substringAfterLast("/"))
                                }
                                else -> {
                                    updateList[2] = name.substringAfterLast("/")
                                }
                            }
                            saveUsersList(user.objectId.toString(), user.userName.toString(), updateList)
                        }
                    }
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    imageView5.setImageBitmap(bitmap)
                }catch (e: IOException){
                }
            }
        }
    }

    /**
     * 画像ファイルをクラウドデータベースにアップロード.
     */
    private fun savePicToCloud(uriName:String){
        val acl = NCMBAcl()
        acl.publicReadAccess = true
        acl.publicWriteAccess = true
        val file = NCMBFile(uriName.substringAfterLast("/"), File(uriName).readBytes(), acl)
        Toast.makeText(this, "データをアップロード中.そのままお待ちください", Toast.LENGTH_SHORT).show()
        file.saveInBackground { e ->
            if (e != null) {
                //保存に失敗したとき
                AlertDialog.Builder(this@DataSelectActivity)
                    .setTitle("Notification from NIFCloud")
                    .setMessage("Error:" + e.message)
                    .setPositiveButton("OK", null)
                    .show()
            }else{
                //保存に成功したとき
                Log.d("[RESULT:photoUpload]", "SUCCESS")
            }
        }
    }

    /**
     * 各ユーザのアップロードした画像のリストを更新.
     */
    private fun saveUsersList(id:String, name:String, list:List<Any?>){
        abc.put("userID", id)
        abc.put("name", name)
        abc.put("array", list)

        abc.saveInBackground { e ->
            if(e != null){
                Log.d("[Error]", e.toString())
            }else{
                Log.d("[RESULT:objectUpload]", "SUCCESS")
                Toast.makeText(this, "アップロード完了", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * FileのUriを作成.
     */
    private fun createSaveFileUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.JAPAN).format(Date())
        val imageFileName = "example$timeStamp"

        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/example")
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        Log.d("Dir place", storageDir.toString())

        val file = File(storageDir, "$imageFileName.jpg")
        Log.d("Maked file name", file.toString())
        filename = "$imageFileName.jpg"
        path = file.absolutePath
        Log.d("path", path)

        return FileProvider.getUriForFile(this, "io.github.healthifier.walking_promoter", file)
    }
}
