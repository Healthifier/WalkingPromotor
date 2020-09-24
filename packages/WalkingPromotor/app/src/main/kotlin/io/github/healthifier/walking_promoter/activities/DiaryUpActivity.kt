package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.nifcloud.mbaas.core.NCMBAcl
import com.nifcloud.mbaas.core.NCMBFile
import com.nifcloud.mbaas.core.NCMBObject
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.CustomGridAdapter
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import io.github.healthifier.walking_promoter.models.DiaryData
import io.github.healthifier.walking_promoter.models.DiaryListAdapter
import kotlinx.android.synthetic.main.activity_diary_up.*
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.activity_start.backButton
import kotlinx.android.synthetic.main.activity_third.*
import java.io.ByteArrayOutputStream
import java.io.File

class DiaryUpActivity : AppCompatActivity() {

    private val dbHandler = DatabaseHandler(this)
    private val cloudUser = NCMBUser.getCurrentUser()
    private var cloudDiaryObj = NCMBObject("cloudDiary")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_up)

        val cloudUName = cloudUser.userName
        val cloudUId = cloudUser.objectId

        val titles = dbHandler.getAllTitles()
        val diaries = dbHandler.getAllUsers()
        val photos = dbHandler.getAllPhotos()

        val gridAdapter = CustomGridAdapter(titles, photos)
        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        //アダプターとレイアウトマネージャーをセット
        gridRecyclerView_up.layoutManager = layoutManager
        gridRecyclerView_up.setHasFixedSize(true)
        gridRecyclerView_up.adapter = gridAdapter
        //dialog.show()

        gridAdapter.setOnItemClickListener(object: CustomGridAdapter.OnItemClickListener{
            override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                //Toast.makeText(applicationContext, "${clickedText}がタップされました.位置は${position}です", Toast.LENGTH_LONG).show()
                val title = diaries[position].title
                val date = diaries[position].day
                val photo = diaries[position].photo

                setupViews(photo, title, date, cloudUName, cloudUId)
                //showDiary(title, date, photo)
            }
        })


        /*
        var diaries = dbHandler.getAllUsers()
        val adapter = DiaryListAdapter(this, diaries)
        list_view.adapter = adapter

        list_view.setOnItemClickListener { adapterView, view, position, id ->
            val diaryData = adapterView.getItemAtPosition(position) as DiaryData
            val title = diaryData.title
            val date = diaryData.day
            val photo = diaryData.photo

            setupViews(photo, title, date, cloudUName, cloudUId)
        }*/

        backButton.setOnClickListener {
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * アラートダイアログ生成
     */
    private fun setupViews(path:String, title:String, date:String, cloudUName:String, cloudUId:String) {
        // BuilderからAlertDialogを作成
        val dialog = AlertDialog.Builder(this)
            .setTitle("確認") // タイトル
            .setMessage("選択した日記を投稿してよろしいですか？") // メッセージ
            .setPositiveButton("OK") { dialog, which -> // OK
                savePicToCloud(path) //ファイルストアに画像をあげる
                saveDiaryToCloud(path.substringAfterLast("/"), title, date, cloudUName, cloudUId) //データストアに5要素をあげる
            }
            .setNegativeButton("戻る", null)
            .create()
        // AlertDialogを表示
        dialog.show()
    }

    /**
     * 画像ファイルをクラウドファイルストアにアップロード.
     */
    private fun savePicToCloud(uriName:String){
        val acl = NCMBAcl()
        acl.publicReadAccess = true
        acl.publicWriteAccess = true
        Log.d("[DEBUG296]", uriName)
        val bmp = BitmapFactory.decodeFile(uriName)
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, stream)
        //val file = NCMBFile(uriName.substringAfterLast("/"), File(uriName).readBytes(), acl)
        val file = NCMBFile(uriName.substringAfterLast("/"), stream.toByteArray(), acl)
        Toast.makeText(this, "データをアップロード中.そのままお待ちください", Toast.LENGTH_SHORT).show()
        file.saveInBackground { e ->
            if (e != null) {
                //保存失敗
                AlertDialog.Builder(this@DiaryUpActivity)
                    .setTitle("Notification from NIFCloud")
                    .setMessage("Error:" + e.message)
                    .setPositiveButton("OK", null)
                    .show()
            }else{
                Log.d("[PhotoUpload Result]", "SUCCESS")
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
