package io.github.healthifier.walking_promoter.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Display
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.nifcloud.mbaas.core.*
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.GlideApp
import kotlinx.android.synthetic.main.activity_share_photo.*
import kotlinx.android.synthetic.main.activity_share_photo.button_back
import kotlinx.android.synthetic.main.activity_write_diary.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import kotlin.collections.ArrayList

class SharePhotoActivity : AppCompatActivity() {

    private var objList = listOf<NCMBObject>()
    private var currentPathList = arrayListOf<String>()
    private val user = NCMBUser.getCurrentUser()
    private var abc = NCMBObject("photoPath")
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
        setContentView(R.layout.activity_share_photo)

        Log.d("name", user.userName.toString())
        Log.d("objectId", user.objectId.toString())

        val buttonsList = arrayListOf<Button>(button_user1, button_user2, button_user3, button_user4, button_user5)
        val imageList = arrayListOf<ImageView>(imageView3, imageView4, imageView5)
        val name = user.userName
        val group = user.getString("groupName")
        textView_user.text = name + "さんが選んだ写真の画面です"

        val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
        dialog.show()

        GlobalScope.launch {
            delay(2500)
            dialog.dismiss()
        }

        val querySelect = NCMBQuery<NCMBObject>("photoPath")
        querySelect.whereEqualTo("groupName", group)
        querySelect.findInBackground { objects, error ->
            if (error != null) {
                Log.d("[Find Error]", error.toString())
            } else {
                Log.d("[DEBUG71]", objects.toString())
                Log.d("[DEBUG72]", objects.size.toString())
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
                Log.d("[myNumber]", myNumber.toString())
                Log.d("[objList.size]", objList.size.toString())
            }
        }
        val queryPhoto = NCMBQuery<NCMBObject>("photoPath")
        queryPhoto.whereEqualTo("groupName", group)
        queryPhoto.whereEqualTo("userID", user.objectId.toString())
        queryPhoto.findInBackground {objects, error ->
            if (error != null) {
                Log.d("[Error91]", error.toString())
            } else {
                if (objects.size == 1) {
                    abc = objects[0]
                    Log.d("d95", "写真データあり")
                } else {
                    Log.d("d97", "ないよ")
                }
            }
        }

        //更新用のボタン
        button_update.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
            val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
            dialog.show()
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
                //Toast.makeText(this, "更新完了", Toast.LENGTH_SHORT).show()
            }
            textView_user.text = user.userName + "さんが選んだ写真の画面です"
            GlobalScope.launch {
                delay(2000)
                dialog.dismiss()
            }
        }

        button_back.setOnClickListener {
            val intent = Intent(this, ClassProgramActivity::class.java)
            startActivity(intent)
        }

        //user1の画像データ表示用のボタン
        button_user1.setOnClickListener {
            showImages(0, imageList)
        }

        //user2の画像データ表示用のボタン
        button_user2.setOnClickListener {
            showImages(1, imageList)
        }

        //user3の画像データ表示用のボタン
        button_user3.setOnClickListener {
            showImages(2, imageList)
        }

        //user4の画像データ表示用のボタン
        button_user4.setOnClickListener {
            showImages(3, imageList)
        }

        //user5の画像データ表示用のボタン
        button_user5.setOnClickListener {
            showImages(4, imageList)
        }

        imageView3.setOnClickListener{
            if(null == imageView3.drawable){
                selectPhoto("1")
            }else {
                showDialog(imageView3, currentPathList[0])
            }
        }

        imageView4.setOnClickListener{
            if(null == imageView4.drawable){
                selectPhoto("2")
            }else {
                showDialog(imageView4, currentPathList[1])
            }
        }

        imageView5.setOnClickListener {
            if (null == imageView5.drawable) {
                selectPhoto("3")
            } else {
                showDialog(imageView5, currentPathList[2])
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
        //Toast.makeText(this, "画像を準備中", Toast.LENGTH_SHORT).show()
        val query: NCMBQuery<NCMBFile> = NCMBFile.getQuery()
        val listSize = selectName.size-1
        val pathStr = "https://mbaas.api.nifcloud.com/2013-09-01/applications/sqfCZvIEdoFSWOQX/publicFiles/"
        currentPathList.clear()
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
                            AlertDialog.Builder(this@SharePhotoActivity)
                                .setTitle("Notification from NIFCloud")
                                .setMessage("Error:" + er.message)
                                .setPositiveButton("OK", null)
                                .show()
                        } else {
                            //成功処理
                            val preBitmap = BitmapFactory.decodeByteArray(dataFetch, 0, dataFetch.size)
                            if (preBitmap.width < preBitmap.height) { //縦長のとき
                                val mat = Matrix()
                                mat.postRotate(-90F)
                                val newBitmap = Bitmap.createBitmap(preBitmap, 0, 0, preBitmap.width, preBitmap.height, mat, true)
                                GlideApp.with(this).load(newBitmap).thumbnail(1f).into(imageName[i])
                            } else {
                                GlideApp.with(this).load(dataFetch).thumbnail(1f).into(imageName[i])
                            }
                        }
                    }
                }
            }

            currentPathList.add(i, pathStr + selectName[i])
        }
    }

    private fun showImages(number:Int, imageList:ArrayList<ImageView>){

        val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
        dialog.show()

        var name = String()
        when (number) {
            0 -> {
                name = button_user1.text.toString()
            }
            1 -> {
                name = button_user2.text.toString()
            }
            2 -> {
                name = button_user3.text.toString()
            }
            3 -> {
                name = button_user4.text.toString()
            }
            4 -> {
                name = button_user5.text.toString()
            }
        }

        textView_user.text = name + "さんが選んだ写真の画面です"
        displayNumber = number
        deleteImages()

        GlobalScope.launch {
            if(objList.size < number+1){
                //Toast.makeText(this@SharePhotoActivity, "データがありません", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }else{
                setImages(objList[number].getList("array"), imageList)
                //Toast.makeText(this@DataSelectActivity, "画像の表示完了", Toast.LENGTH_SHORT).show()
            }
        }

        GlobalScope.launch {
            delay(2500)
            dialog.dismiss()
        }

    }
    private fun showDialog(imageView: ImageView, path: String){
        val dialog = Dialog(this)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val customLayoutView: View = layoutInflater.inflate(R.layout.custom_dialog_layout, null)
        dialog.setContentView(customLayoutView)

        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        val factor = width.toFloat() / height.toFloat()
        dialog.window?.setLayout(
            (width * factor * 0.5).toInt(),
            (height* factor * 0.5).toInt()
        )

        val imageView_dialog = customLayoutView.findViewById<ImageView>(R.id.imageView_dialog)
        GlideApp.with(this).load(path).into(imageView_dialog)
        val btn_change = customLayoutView.findViewById<Button>(R.id.btn_change)
        btn_change.setOnClickListener {
            if(displayNumber == myNumber) {
                //selectPhoto3()
                when (imageView) {
                    imageView3 -> {
                        selectPhoto("1")
                    }
                    imageView4 -> {
                        selectPhoto("2")
                    }
                    imageView5 -> {
                        selectPhoto("3")
                    }
                }
            }else{
                Toast.makeText(this, "他の人の写真は差し替えられません", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        val btn_close = customLayoutView.findViewById<Button>(R.id.btn_close)
        btn_close.setOnClickListener {
            dialog.dismiss()
        }

        val btn_speaker = customLayoutView.findViewById<Button>(R.id.btn_speaker)
        btn_speaker.setOnClickListener {
            val dialog2 = Dialog(this)
            dialog2.window?.requestFeature(Window.FEATURE_NO_TITLE)
            val customDialogView: View = layoutInflater.inflate(R.layout.custom_dialog_speak, null)
            dialog2.setContentView(customDialogView)
            dialog2.show()
        }

        val btn_listener = customLayoutView.findViewById<Button>(R.id.btn_listener)
        btn_listener.setOnClickListener {
            val dialog2 = Dialog(this)
            dialog2.window?.requestFeature(Window.FEATURE_NO_TITLE)
            val customDialogView: View = layoutInflater.inflate(R.layout.custom_dialog_listen, null)
            dialog2.setContentView(customDialogView)
            dialog2.window?.setLayout(1450, 500)
            dialog2.show()
        }

        dialog.show()
    }

    private fun selectPhoto(number:String){
        if(checkPermission()){
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            when (number) {
                "1" -> {
                    startActivityForResult(intent, REQUEST_GARALLY1 )
                }
                "2" -> {
                    startActivityForResult(intent, REQUEST_GARALLY2 )
                }
                "3" -> {
                    startActivityForResult(intent, REQUEST_GARALLY3 )
                }
            }
        }else{
            grantStoragePermission()
        }
    }

    private fun uploadPic(data: Intent?, imageView: ImageView){
        val uri: Uri?
        val pathStr = "https://mbaas.api.nifcloud.com/2013-09-01/applications/sqfCZvIEdoFSWOQX/publicFiles/"
        if(data != null) {
            uri = data.data
            try {
                Log.d("uri", uri.toString())
                Log.d("uri?.path", uri?.path.toString())
                val cursor = contentResolver.query(uri!!, null, null, null, null)
                val nameColumnIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                var name = ""
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        //name = cursor.getString(0)
                        name = cursor.getString(nameColumnIndex!!)
                    }
                    cursor.close()
                    Log.d("name", name)
                }
                val group = user.getString("groupName")
                val query: NCMBQuery<NCMBFile> = NCMBFile.getQuery()
                //val jpgName = name.substringAfterLast("/")
                val jpgName = name
                query.whereEqualTo("fileName", jpgName)
                query.findInBackground { list, ncmbException ->
                    if (ncmbException != null) {
                        Log.d("[Error522]", ncmbException.toString())
                    } else {
                        if (list.size != 0) {
                            Log.d("[DEBUG525]", "クラウド上にあるよ")
                            //Log.d("Size461", File(name).readBytes().toString())
                        } else {
                            savePicToCloud(uri, name)
                        }
                        val updateList = abc.getList("array")
                        when (imageView) {
                            imageView3 -> {
                                val photoNameListCloud = arrayListOf<String>()
                                if (updateList == null) {
                                    photoNameListCloud.add(jpgName)
                                    abc.put("array", photoNameListCloud)
                                    currentPathList.add(0, pathStr + jpgName)
                                    saveUsersList(user.objectId.toString(), user.userName.toString(), group, photoNameListCloud)
                                } else {
                                    updateList[0] = jpgName
                                    currentPathList[0] = pathStr + jpgName
                                    saveUsersList(user.objectId.toString(), user.userName.toString(), group, updateList)
                                }
                            }
                            imageView4 -> {
                                when (updateList.size) {
                                    0 -> {
                                        updateList.add(0, jpgName)
                                        currentPathList.add(0, pathStr + jpgName)
                                    }
                                    1 -> {
                                        updateList.add(1, jpgName)
                                        currentPathList.add(1, pathStr + jpgName)
                                    }
                                    else -> { //3枚すでにup済みのとき
                                        updateList[1] = jpgName
                                        currentPathList[1] = pathStr + jpgName
                                    }
                                }
                                saveUsersList(user.objectId.toString(), user.userName.toString(), group, updateList)
                            }
                            imageView5 -> {
                                when (updateList.size) {
                                    0 -> {
                                        updateList.add(0, jpgName)
                                        currentPathList.add(0, pathStr + jpgName)
                                    }
                                    1 -> {
                                        updateList.add(1, jpgName)
                                        currentPathList.add(1, pathStr + jpgName)
                                    }
                                    2 -> {
                                        updateList.add(2, jpgName)
                                        currentPathList.add(2, pathStr + jpgName)
                                    }
                                    else -> { //3枚すでにup済みのとき
                                        updateList[2] = jpgName
                                        currentPathList[2] = pathStr + jpgName
                                    }
                                }
                                saveUsersList(user.objectId.toString(), user.userName.toString(), group, updateList)
                            }
                        }
                    }
                }
                if(Build.VERSION.SDK_INT <= 27){ // APIレベル27以下の時
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    if(bitmap.width < bitmap.height){ //縦長のとき
                        val mat = Matrix()
                        mat.postRotate(-90F)
                        val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mat, true)
                        imageView.setImageBitmap(newBitmap)
                    }else{
                        runOnUiThread {
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                }
                if(Build.VERSION.SDK_INT >= 28){ // APIレベル28以上の時
                    val bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                    if(bitmap.width < bitmap.height){ //縦長のとき
                        val mat = Matrix()
                        mat.postRotate(-90F)
                        val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mat, true)
                        imageView.setImageBitmap(newBitmap)
                    }else{
                        runOnUiThread {
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                }
            } catch (e: IOException) {
            }
        }
    }

    private fun checkPermission(): Boolean {

        return PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun grantStoragePermission() = ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST)

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
            val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
            val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
            val text: TextView = view.findViewById(R.id.textView4)
            text.text = "送信中"
            dialog.show()
            GlobalScope.launch {
                uploadPic(data, imageView3)
            }
            GlobalScope.launch {
                delay(2000)
                dialog.dismiss()
            }
        }else if(requestCode == REQUEST_GARALLY2 && resultCode == Activity.RESULT_OK) {
            Log.d("deb", "REQUEST_GARALLY2")
            val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
            val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
            val text: TextView = view.findViewById(R.id.textView4)
            text.text = "送信中"
            dialog.show()
            GlobalScope.launch {
                uploadPic(data, imageView4)
            }
            GlobalScope.launch {
                delay(2000)
                dialog.dismiss()
            }
        }else if(requestCode == REQUEST_GARALLY3 && resultCode == Activity.RESULT_OK) {
            Log.d("deb", "REQUEST_GARALLY3")
            val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
            val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
            val text: TextView = view.findViewById(R.id.textView4)
            text.text = "送信中"
            dialog.show()
            GlobalScope.launch {
                uploadPic(data, imageView5)
            }
            GlobalScope.launch {
                delay(2000)
                dialog.dismiss()
            }
        }
    }

    /**
     * 画像ファイルをクラウドデータベースにアップロード.
     */
    private fun savePicToCloud(uri: Uri, fileName:String){
        val acl = NCMBAcl()
        acl.publicReadAccess = true
        acl.publicWriteAccess = true
        //val bmp = BitmapFactory.decodeFile(uriName)
        if(Build.VERSION.SDK_INT <= 27) { // APIレベル27以下の時
            val bmp = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 20, stream)
            val file = NCMBFile(fileName, stream.toByteArray(), acl)
            //Toast.makeText(this, "データをアップロード中.そのままお待ちください", Toast.LENGTH_SHORT).show()
            file.saveInBackground { e ->
                if (e != null) {
                    //保存に失敗したとき
                    AlertDialog.Builder(this@SharePhotoActivity)
                        .setTitle("Notification from NIFCloud")
                        .setMessage("Error:" + e.message)
                        .setPositiveButton("OK", null)
                        .show()
                }else{
                    //保存に成功したとき
                    Log.d("[PhotoUpload Result]", "SUCCESS")
                }
            }
        }
        if(Build.VERSION.SDK_INT >= 28){
            val bmp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 20, stream)
            val file = NCMBFile(fileName, stream.toByteArray(), acl)
            //Toast.makeText(this, "データをアップロード中.そのままお待ちください", Toast.LENGTH_SHORT).show()
            file.saveInBackground { e ->
                if (e != null) {
                    //保存に失敗したとき
                    AlertDialog.Builder(this@SharePhotoActivity)
                        .setTitle("Notification from NIFCloud")
                        .setMessage("Error:" + e.message)
                        .setPositiveButton("OK", null)
                        .show()
                }else{
                    //保存に成功したとき
                    Log.d("[PhotoUpload Result]", "SUCCESS")
                }
            }
        }
    }

    /**
     * 各ユーザのアップロードした画像のリストを更新.
     */
    private fun saveUsersList(id:String, name:String, group:String, list:List<Any?>){
        abc.put("userID", id)
        abc.put("name", name)
        abc.put("groupName", group)
        abc.put("array", list)

        abc.saveInBackground { e ->
            if(e != null){
                Log.d("[Error]", e.toString())
            }else{
                Log.d("[ObjectUpload Result]", "SUCCESS")
                //Toast.makeText(this, "アップロード完了", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
