package io.github.healthifier.walking_promoter.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.Fragment
import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nifcloud.mbaas.core.NCMBFile
import com.nifcloud.mbaas.core.NCMBObject
import com.nifcloud.mbaas.core.NCMBQuery
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.fragments.TokaidoMapFragment
import io.github.healthifier.walking_promoter.models.CustomGridAdapter
import io.github.healthifier.walking_promoter.models.CustomGridAdapterCloud
import kotlinx.android.synthetic.main.activity_mets.*
import java.text.SimpleDateFormat
import java.util.*

class MetsActivity : AppCompatActivity() {

    private val cal = Calendar.getInstance()
    private var copyObjList = listOf<NCMBObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mets)

        //showFragment(TokaidoMapFragment::class.java)
        /*
        tmpShowCal.setOnClickListener {
            showDatePicker()
        }*/
        showDiaryList()
        btn_back.setOnClickListener {
            val intent = Intent(this, DiaryMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showFragment(clazz: Class<out Fragment>) {
            try {
                val fragment = clazz.newInstance()
                fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
    }

    private fun showDatePicker() {
        var cloudDate = ""
        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                //updateDateInView()
                cloudDate = sdf.format(cal.time)
                //showDiaryList(cloudDate) //OK押したら日記ポップアップ画面を表示
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun showDiaryList(){
        //cloudDiaryクラスからdateをキーにオブジェクト検索
        val querydiary = NCMBQuery<NCMBObject>("cloudDiary")
        //querydiary.whereEqualTo("date", cloudDate)
        querydiary.findInBackground {objects, error ->
            if (error != null) {
                Log.d("[Error91]", error.toString())
            } else {
                if (objects.size != 0) {
                    var cloudList = listOf<NCMBObject>()
                    cloudList = objects
                    copyObjList = objects //検索結果であるオブジェクトリストをcopyObjListにコピー
                    Log.d("d95", "あるよ")
                    Log.d("size", copyObjList.size.toString())
                    textView_log.text = "みんなで${copyObjList.size}件の日記を書きました！"
                    //タイトル部分と写真のみを別の配列リストにコピー
                    val cloudTitleList = arrayListOf<String>()
                    val cloudPhotoList = arrayListOf<String>()
                    Log.d("string", copyObjList[0].getString("title"))
                    for(obj in objects){
                        cloudTitleList.add(obj.getString("title"))
                        cloudPhotoList.add(obj.getString("photo"))
                    }
                    Log.d("List", cloudTitleList[0])

                    /*
                    //ダイアログ画面の表示準備
                    val dialog = Dialog(this)
                    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
                    val customDialogView: View = layoutInflater.inflate(R.layout.custom_dialog_diary_grid, null)
                    dialog.setContentView(customDialogView)
                    //ダイアログ画面のサイズ調整
                    val display: Display = windowManager.defaultDisplay
                    val size = Point()
                    display.getSize(size)
                    val width = size.x
                    val height = size.y
                    val factor = width.toFloat() / height.toFloat()
                    dialog.window?.setLayout(
                        (width * factor * 0.4).toInt(),
                        (height* factor * 0.4).toInt()
                    )
                     */

                    val dataList = arrayListOf<ByteArray>()
                    /*
                    val gridAdapter = CustomGridAdapterCloud(cloudTitleList, dataList)
                    val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
                    //アダプターとレイアウトマネージャーをセット
                    gridRecyclerView.layoutManager = layoutManager
                    gridRecyclerView.setHasFixedSize(true)
                    gridRecyclerView.adapter = gridAdapter*/

                    var check = false

                    for(loopList in cloudPhotoList){
                        val file = NCMBFile(loopList)
                        //dataList.add(NCMBFile(loopList))
                        dataList.add(file.fetch())
                        //Glide.with(this).load(file).thumbnail(0.1f)
                        //dataList.add(file.fileData)
                        /*
                        file.fetchInBackground { bytes, ncmbException ->
                            if(ncmbException != null){

                            }else{
                                if(!check){
                                    dataList.add(bytes)
                                    val dataList = arrayListOf<ByteArray>()
                                    val gridAdapter = CustomGridAdapterCloud(cloudTitleList, dataList)
                                    val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
                                    //アダプターとレイアウトマネージャーをセット
                                    gridRecyclerView.layoutManager = layoutManager
                                    gridRecyclerView.setHasFixedSize(true)
                                    gridRecyclerView.adapter = gridAdapter
                                    check = true
                                }else{
                                    dataList.add(bytes)
                                    gridAdapter.notifyDataSetChanged()
                                }
                                /*
                                val gridAdapter = CustomGridAdapterCloud(cloudTitleList, dataList)
                                val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
                                //アダプターとレイアウトマネージャーをセット
                                gridRecyclerView.layoutManager = layoutManager
                                gridRecyclerView.setHasFixedSize(true)
                                gridRecyclerView.adapter = gridAdapter
                                //dialog.show()


                                 */
                                gridAdapter.setOnItemClickListener(object:CustomGridAdapterCloud.OnItemClickListener{
                                    override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                                        //Toast.makeText(applicationContext, "${clickedText}がタップされました.位置は${position}です", Toast.LENGTH_LONG).show()
                                        val title = cloudList[position].getString("title")
                                        val date = cloudList[position].getString("date")
                                        val photo = cloudList[position].getString("photo")
                                        showCloudDiary(title, date, photo)
                                    }
                                })
                            }
                        }*/


                        val gridAdapter = CustomGridAdapterCloud(cloudTitleList, dataList)
                        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
                        //アダプターとレイアウトマネージャーをセット
                        gridRecyclerView.layoutManager = layoutManager
                        gridRecyclerView.setHasFixedSize(true)
                        gridRecyclerView.adapter = gridAdapter
                        gridAdapter.notifyDataSetChanged()
                        //dialog.show()

                        gridAdapter.setOnItemClickListener(object:CustomGridAdapterCloud.OnItemClickListener{
                            override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                                //Toast.makeText(applicationContext, "${clickedText}がタップされました.位置は${position}です", Toast.LENGTH_LONG).show()
                                val title = cloudList[position].getString("title")
                                val date = cloudList[position].getString("date")
                                val photo = cloudList[position].getString("photo")
                                showCloudDiary(title, date, photo)
                            }
                        })
                    }

                    /*
                    val gridAdapter = CustomGridAdapterCloud(cloudTitleList, dataList)
                    val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
                    //アダプターとレイアウトマネージャーをセット
                    gridRecyclerView.layoutManager = layoutManager
                    gridRecyclerView.setHasFixedSize(true)
                    gridRecyclerView.adapter = gridAdapter
                    //dialog.show()

                    gridAdapter.setOnItemClickListener(object:CustomGridAdapterCloud.OnItemClickListener{
                        override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                            //Toast.makeText(applicationContext, "${clickedText}がタップされました.位置は${position}です", Toast.LENGTH_LONG).show()
                            val title = cloudList[position].getString("title")
                            val date = cloudList[position].getString("date")
                            val photo = cloudList[position].getString("photo")
                            showCloudDiary(title, date, photo)
                        }
                    })*/
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
            (width * factor * 0.5).toInt(),
            (height* factor * 0.5).toInt()
        )

        val query: NCMBQuery<NCMBFile> = NCMBFile.getQuery()
        Log.d("debug281", photo)
        query.whereEqualTo("fileName", photo)
        query.findInBackground { list, ncmbException ->
            if (ncmbException != null) {
                Log.d("[Error329]", ncmbException.toString())
            } else {
                Log.d("debug331", list[0].toString())
                list[0].fetchInBackground { dataFetch, er ->
                    if (er != null) {
                        //失敗処理
                        AlertDialog.Builder(this@MetsActivity)
                            .setTitle("Notification from NIFCloud")
                            .setMessage("Error:" + er.message)
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        //成功処理
                        val bMap = BitmapFactory.decodeByteArray(dataFetch, 0, dataFetch.size)
                        val imageView_dialog = customDialogView.findViewById<ImageView>(R.id.ctm_imgae_imageView)
                        imageView_dialog.setImageBitmap(bMap)
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
            }
        }
    }
}
