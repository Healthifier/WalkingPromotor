package io.github.healthifier.walking_promoter.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.CustomGridAdapter
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import io.github.healthifier.walking_promoter.models.DiaryData
import io.github.healthifier.walking_promoter.models.DiaryListAdapter
import kotlinx.android.synthetic.main.activity_mets.*
import kotlinx.android.synthetic.main.activity_third.*

class ThirdActivity : AppCompatActivity() {

    private val dbHandler = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val titles = dbHandler.getAllTitles()
        val diaries = dbHandler.getAllUsers()
        val photos = dbHandler.getAllPhotos()

        val gridAdapter = CustomGridAdapter(titles, photos)
        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        //アダプターとレイアウトマネージャーをセット
        gridRecyclerView_third.layoutManager = layoutManager
        gridRecyclerView_third.setHasFixedSize(true)
        gridRecyclerView_third.adapter = gridAdapter
        //dialog.show()

        gridAdapter.setOnItemClickListener(object: CustomGridAdapter.OnItemClickListener{
            override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                //Toast.makeText(applicationContext, "${clickedText}がタップされました.位置は${position}です", Toast.LENGTH_LONG).show()
                val title = diaries[position].title
                val date = diaries[position].day
                val photo = diaries[position].photo
                showDiary(title, date, photo)
            }
        })

        /*
        val adapter = DiaryListAdapter(this, diaries)
        list_view.adapter = adapter

        list_view.setOnItemClickListener { adapterView, view, position, id ->
            //val name = view.findViewById<TextView>(android.R.id.text1).text
            val diarydata = adapterView.getItemAtPosition(position) as DiaryData
            val title = diarydata.title
            val day = diarydata.day
            val photo = diarydata.photo
            //Log.d("test", name1)
            Toast.makeText(this, "$title の日記を表示します", Toast.LENGTH_LONG).show()

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
            //val bMap = BitmapFactory.decodeByteArray(dataFetch, 0, dataFetch.size)
            val imageView_dialog = customDialogView.findViewById<ImageView>(R.id.ctm_imgae_imageView)
            imageView_dialog.setImageBitmap(BitmapFactory.decodeFile(photo))
            val titleTextView_dialog = customDialogView.findViewById<TextView>(R.id.ctm_title_textView)
            titleTextView_dialog.text = title
            val dayTextView_dialog = customDialogView.findViewById<TextView>(R.id.ctm_day_textView)
            dayTextView_dialog.text = day
            val baclButton_dialog = customDialogView.findViewById<TextView>(R.id.ctm_back_button)

            dialog.show()

            baclButton_dialog.setOnClickListener {
                dialog.dismiss()
            }

            /*
            val intent = Intent(this, FourthActivity::class.java)
            intent.putExtra(FourthActivity.TITLE, title)
            intent.putExtra(FourthActivity.DAY, day)
            intent.putExtra(FourthActivity.PHOTO, photo)
            startActivity(intent)

             */
        }*/

        button_back.setOnClickListener {
            val intent = Intent(this, DiaryMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDiary(title: String, date: String, photo: String){
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
        //val bMap = BitmapFactory.decodeByteArray(dataFetch, 0, dataFetch.size)
        val imageView_dialog = customDialogView.findViewById<ImageView>(R.id.ctm_imgae_imageView)
        imageView_dialog.setImageBitmap(BitmapFactory.decodeFile(photo))
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
