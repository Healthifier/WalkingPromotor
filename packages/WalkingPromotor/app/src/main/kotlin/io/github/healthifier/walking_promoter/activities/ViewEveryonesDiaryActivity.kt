package io.github.healthifier.walking_promoter.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.nifcloud.mbaas.core.NCMBObject
import com.nifcloud.mbaas.core.NCMBQuery
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.CustomGridAdapterCloud
import io.github.healthifier.walking_promoter.models.GlideApp
import kotlinx.android.synthetic.main.activity_view_everyones_diary.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewEveryonesDiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_everyones_diary)

        val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
        dialog.show()
        GlobalScope.launch {
            showDiaryList()
        }
        GlobalScope.launch {
            delay(1000)
            dialog.dismiss()
        }

        btn_back.setOnClickListener {
            val intent = Intent(this, ViewDiaryMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDiaryList(){
        //cloudDiaryクラスから日記オブジェクト検索
        val queryDiary = NCMBQuery<NCMBObject>("cloudDiary")
        queryDiary.findInBackground {objects, error ->
            if (error != null) {
                Log.d("[Error91]", error.toString())
            } else {
                if (objects.size != 0) {
                    Log.d("d95", "あるよ")
                    textView_log.text = "みんなで${objects.size}件の日記を書きました！"
                    //タイトル部分と写真のみを別の配列リストにコピー
                    val cloudTitleList = arrayListOf<String>()
                    val cloudPhotoList = arrayListOf<String>()
                    for(obj in objects){
                        cloudTitleList.add(obj.getString("title"))
                        cloudPhotoList.add("https://mbaas.api.nifcloud.com/2013-09-01/applications/wvsqIkhY9ISTFdAi/publicFiles/"+obj.getString("photo"))
                    }
                    Log.d("List", cloudTitleList[0])

                    //アダプターとレイアウトマネージャーをセット
                    val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
                    gridRecyclerView.layoutManager = layoutManager
                    val gridAdapter = CustomGridAdapterCloud(cloudTitleList, cloudPhotoList)
                    gridRecyclerView.adapter = gridAdapter
                    gridRecyclerView.setHasFixedSize(true)
                    gridAdapter.notifyDataSetChanged()

                    gridAdapter.setOnItemClickListener(object:CustomGridAdapterCloud.OnItemClickListener{
                        override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                            //Toast.makeText(applicationContext, "${clickedText}がタップされました.位置は${position}です", Toast.LENGTH_LONG).show()
                            val title = objects[position].getString("title")
                            val date = objects[position].getString("date")
                            val photo = "https://mbaas.api.nifcloud.com/2013-09-01/applications/wvsqIkhY9ISTFdAi/publicFiles/"+objects[position].getString("photo")
                            showCloudDiary(title, date, photo)
                        }
                    })
                } else {
                    textView_log.text = "まだ日記は投稿されていないみたいです"
                    Log.d("d97", "ないよ")
                }
            }
        }
    }

    private fun showCloudDiary(title: String, date: String, photo: String){
        val dialog = Dialog(this)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val customDialogView: View = layoutInflater.inflate(R.layout.custom_dialog_diary, null)
        dialog.setContentView(customDialogView)

        val display: Display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        val factor = width.toFloat() / height.toFloat()
        dialog.window?.setLayout(
            (width * factor * 0.55).toInt(),
            (height* factor * 0.5).toInt()
        )

        val imageView_dialog = customDialogView.findViewById<ImageView>(R.id.ctm_imgae_imageView)
        GlideApp.with(this).load(photo).into(imageView_dialog)
        val titleTextView_dialog = customDialogView.findViewById<TextView>(R.id.ctm_title_textView)
        titleTextView_dialog.text = title
        val dayTextView_dialog = customDialogView.findViewById<TextView>(R.id.ctm_day_textView)
        dayTextView_dialog.text = date
        val baclButton_dialog = customDialogView.findViewById<TextView>(R.id.ctm_back_button)

        dialog.show()

        baclButton_dialog.setOnClickListener {
            dialog.dismiss()
        }
    }
}
