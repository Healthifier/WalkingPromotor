package io.github.healthifier.walking_promoter.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
    val cal = Calendar.getInstance()
    var RESULT_CAMERA = 1001
    val PERMISSION_REQUEST = 1002
    var path: String =""
    private var r: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        dbHandler = DatabaseHandler(this)

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
                val user = Users()
                var success = false
                val title = editText_diaryTitle.text.toString()
                val date = textCalView2.text.toString()

                /**
                 * ローカルSQLに保存.
                 */
                user.diaryTitle = title
                user.diaryDay = date
                user.photoPath = path //pathは画像のローカルパス
                success = dbHandler!!.addUser(user)

                if (success){
                    Toast.makeText(this,"日記を保存しました", Toast.LENGTH_LONG).show()
                    //Toast.makeText(this,"戻るボタンで戻れます", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, HomeProgramActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        button_back.setOnClickListener {
            val intent = Intent(this, HomeProgramActivity::class.java)
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
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)

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
            if(bitmap.width < bitmap.height){ //縦長のとき
                val mat = Matrix()
                mat.postRotate(-90F)
                val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mat, true)
                imageView.setImageBitmap(newBitmap)
            }else{
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun createSaveFileUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.JAPAN).format(Date())
        val imageFileName = "example$timeStamp"

        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/example")
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        //Log.d("Dir place", storageDir.toString())

        val file = File(storageDir, "$imageFileName.jpg")
        //Log.d("Maked file name", file.toString())
        path = file.absolutePath
        //Log.d("path", path)

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

        if (editText_diaryTitle.text.toString() != "" &&
            textCalView2.text.toString() != ""){
            if(path != ""){
                validate = true
            }else{
                validate = false
                Toast.makeText(this,"すべての項目を入力してください", Toast.LENGTH_LONG).show()
            }
        }else{
            validate = false
            Toast.makeText(this,"すべての項目を入力してください", Toast.LENGTH_LONG).show()
        }

        return validate
    }
}
