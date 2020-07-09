package io.github.healthifier.walking_promoter.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.nifcloud.mbaas.core.*
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import io.github.healthifier.walking_promoter.models.Users
import kotlinx.android.synthetic.main.activity_second.*
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

class SecondActivity : AppCompatActivity() {

    var dbHandler: DatabaseHandler? = null

    var button_date: Button? = null
    val cal = Calendar.getInstance()
    //var textview_date: TextView? = null
    var RESULT_CAMERA = 1001
    val PERMISSION_REQUEST = 1002
    //private lateinit var path: String
    var path: String =""
    private var objList = listOf<NCMBObject>()
    private val cloudUser = NCMBUser.getCurrentUser()
    private var cloudDiaryObj = NCMBObject("cloudDiary")
    private var r: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val cloudUName = cloudUser.userName
        val cloudUId = cloudUser.objectId
        dbHandler = DatabaseHandler(this)

        //textview_date = this.textCalView2
        //button_date = this.button_day
        //textview_date!!.text = ""

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        button_day.setOnClickListener {
            DatePickerDialog(
                this@SecondActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        button_save.setOnClickListener{
            // checking input text should not be null
            if (validation()){
                val user: Users = Users()
                var success: Boolean = false
                val title = editText_diaryTitle.text.toString()
                val date = textCalView2.text.toString()

                /**
                 * ローカルSQLに保存.
                 */
                user.diaryTitle = title
                user.diaryDay = date
                user.photoPath = path //pathは画像のローカルパス
                success = dbHandler!!.addUser(user)

                /**
                 * クラウドに保存.
                 */
                savePicToCloud(path) //ファイルストアに画像をあげる
                saveDiaryToCloud(path.substringAfterLast("/"), title, date, cloudUName, cloudUId) //データストアに5要素をあげる

                if (success){
                    Toast.makeText(this,"日記を保存しました", Toast.LENGTH_LONG).show()
                    Toast.makeText(this,"戻るボタンで戻れます", Toast.LENGTH_LONG).show()
                }
            }
        }

        button_back.setOnClickListener {
            val intent = Intent(this, ProgramActivity::class.java)
            startActivity(intent)
        }

        button_title.setOnClickListener {
            val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            editText_diaryTitle.requestFocus()
            inputMethodManager.showSoftInput(editText_diaryTitle, 0)
        }
    }

    override fun onResume() {
        super.onResume()

        imageButton.setOnClickListener {
            // カメラ機能を実装したアプリが存在するかチェック
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
                if (checkPermission()) {
                    takePicture()
                } else {
                    grantCameraPermission()
                }
            } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateDateInView() {
        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textCalView2.text = sdf.format(cal.time)
    }

    private fun takePicture() {
        val pictureUri = createSaveFileUri()
        r = pictureUri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
        }

        startActivityForResult(intent, RESULT_CAMERA)
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
        if (requestCode == RESULT_CAMERA && resultCode == Activity.RESULT_OK) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put("_data", path)
            }
            contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            val inputStream = FileInputStream(File(path))
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun createSaveFileUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.JAPAN).format(Date())
        val imageFileName = "example" + timeStamp

        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/example")
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        Log.d("Dir place", storageDir.toString())

        val file = File(storageDir, "$imageFileName.jpg")
        Log.d("Maked file name", file.toString())
        path = file.absolutePath
        Log.d("path", path.toString())

        return FileProvider.getUriForFile(this, "io.github.healthifier.walking_promoter", file)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        var isGranted = true
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty()) {
                grantResults.forEach {
                    if (it != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false
                    }
                }
            } else {
                isGranted = false
            }
        } else {
            isGranted = false
        }

        if (isGranted) {
            takePicture()
        } else {
            grantCameraPermission()
        }

    }

    private fun validation(): Boolean{
        var validate = false

        if (!editText_diaryTitle.text.toString().equals("") &&
            !textCalView2.text.toString().equals("")){
            if(!path.equals("")){
                validate = true
            }else{
                validate = false
                val toast = Toast.makeText(this,"すべての項目を入力してください", Toast.LENGTH_LONG).show()
            }
        }else{
            validate = false
            val toast = Toast.makeText(this,"すべての項目を入力してください", Toast.LENGTH_LONG).show()
        }

        return validate
    }

    /**
     * 画像ファイルをクラウドファイルストアにアップロード.
     */
    private fun savePicToCloud(uriName:String){
        val acl = NCMBAcl()
        acl.publicReadAccess = true
        acl.publicWriteAccess = true
        Log.d("[DEBUG296]", uriName)
        val file = NCMBFile(uriName.substringAfterLast("/"), File(uriName).readBytes(), acl)
        Toast.makeText(this, "データをアップロード中.そのままお待ちください", Toast.LENGTH_SHORT).show()
        file.saveInBackground { e ->
            if (e != null) {
                //保存失敗
                AlertDialog.Builder(this@SecondActivity)
                    .setTitle("Notification from NIFCloud")
                    .setMessage("Error:" + e.message)
                    .setPositiveButton("OK", null)
                    .show()
            }else{
                Log.d("[RESULT:photoUpload]", "SUCCESS")
            }
        }
    }

    /**
     * 日記データをクラウドデータストアにアップロード
     */
    private fun saveDiaryToCloud(photo:String, title:String, date:String, userName:String, userId:String){
        /*
            データストアに5つの要素をアップ
         */
        cloudDiaryObj.put("photo", photo)
        cloudDiaryObj.put("title", title)
        cloudDiaryObj.put("date", date)
        cloudDiaryObj.put("userName", userName)
        cloudDiaryObj.put("userId", userId)

        cloudDiaryObj.saveInBackground { e ->
            if(e != null){
                Log.d("[Error]", e.toString())
            }else{
                Log.d("[RESULT:objectUpload]", "SUCCESS")
                Toast.makeText(this, "アップロード完了", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
