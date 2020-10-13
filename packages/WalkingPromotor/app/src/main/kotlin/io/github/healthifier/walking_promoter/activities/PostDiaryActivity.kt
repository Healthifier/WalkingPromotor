package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.nifcloud.mbaas.core.NCMBAcl
import com.nifcloud.mbaas.core.NCMBFile
import com.nifcloud.mbaas.core.NCMBObject
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.CustomGridAdapter
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import kotlinx.android.synthetic.main.activity_diary_up.*
import kotlinx.android.synthetic.main.activity_square_step.backButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class PostDiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_up)

        val cloudUser = NCMBUser.getCurrentUser()
        val cloudUName = cloudUser.userName
        val cloudUId = cloudUser.objectId

        val dbHandler = DatabaseHandler(this)
        val titles = dbHandler.getAllTitles()
        val diaries = dbHandler.getAllUsers()
        val photos = dbHandler.getAllPhotos()

        val gridAdapter = CustomGridAdapter(titles, photos)
        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        //アダプターとレイアウトマネージャーをセット
        gridRecyclerView_up.layoutManager = layoutManager
        gridRecyclerView_up.setHasFixedSize(true)
        gridRecyclerView_up.adapter = gridAdapter

        gridAdapter.setOnItemClickListener(object: CustomGridAdapter.OnItemClickListener{
            override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                val title = diaries[position].title
                val date = diaries[position].day
                val photo = diaries[position].photo

                setupViews(photo, title, date, cloudUName, cloudUId)
            }
        })

        backButton.setOnClickListener {
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * アラートダイアログ生成
     */
    private fun setupViews(path:String, title:String, date:String, cloudUName:String, cloudUId:String) {
        val view: View = layoutInflater.inflate(R.layout.custom_dialog_check, null)
        val dialogTitle: TextView = view.findViewById(R.id.TextView_dialog_title)
        dialogTitle.text = "選択した日記を投稿してよろしいですか？"
        val message: TextView = view.findViewById(R.id.TextView_dialog_message)
        message.text = "タイトル：${title} 日付：${date}"
        val button1: Button = view.findViewById(R.id.Button_dialog_positive)
        button1.text = "やっぱりやめる"
        val button2: Button = view.findViewById(R.id.Button_dialog_negative)
        button2.text = "投稿する"

        val dialog = AlertDialog.Builder(this@PostDiaryActivity).setView(view).create()
        dialog.show()

        // AlertDialogのサイズ調整
        val lp = dialog.window?.attributes
        lp?.width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog.window?.attributes = lp

        button1.setOnClickListener {
            dialog.dismiss()
        }

        button2.setOnClickListener {
            val view2: View = layoutInflater.inflate(R.layout.dialog_progress, null)
            val dialog2 = AlertDialog.Builder(this).setCancelable(false).setView(view2).create()
            dialog2.show()
            GlobalScope.launch {
                savePicToCloud(path) //ファイルストアに画像をあげる
            }
            GlobalScope.launch {
                saveDiaryToCloud(path.substringAfterLast("/"), title, date, cloudUName, cloudUId) //データストアに5要素をあげる
            }
            GlobalScope.launch {
                delay(2000)
                dialog2.dismiss()
                dialog.dismiss()
                newDialog()
            }
        }
    }

    private fun newDialog(){
        runOnUiThread{
            val view3: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val dialog3 = AlertDialog.Builder(this@PostDiaryActivity).setView(view3).create()
            val textDialogTitle: TextView = view3.findViewById(R.id.TextView_dialog_title)
            textDialogTitle.text = "日記が投稿されました！"
            val textDialogMessage: TextView = view3.findViewById(R.id.TextView_dialog_message)
            textDialogMessage.text = "日記を見るメニューで実際に見てみましょう！"
            val buttonDialog: Button = view3.findViewById(R.id.Button_dialog_positive)
            buttonDialog.text = "この画面を閉じる"
            dialog3.show()

            val lp = dialog3.window?.attributes
            lp?.width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            dialog3.window?.attributes = lp

            buttonDialog.setOnClickListener {
                dialog3.dismiss()
                val intent = Intent(this@PostDiaryActivity, HomeProgramActivity::class.java)
                startActivity(intent)
            }
        }
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
        val file = NCMBFile(uriName.substringAfterLast("/"), stream.toByteArray(), acl)
        file.saveInBackground { e ->
            if (e != null) {
                //保存失敗
                AlertDialog.Builder(this@PostDiaryActivity)
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
        val cloudDiaryObj = NCMBObject("cloudDiary")
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
            }
        }
    }
}
