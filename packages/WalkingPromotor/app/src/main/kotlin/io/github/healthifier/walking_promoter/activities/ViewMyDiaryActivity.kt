package io.github.healthifier.walking_promoter.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.*
import kotlinx.android.synthetic.main.activity_view_my_diary.*

class ViewMyDiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_my_diary)

        val dbHandler = DatabaseHandler(this)
        val titles = dbHandler.getAllTitles()
        val diaries = dbHandler.getAllUsers()
        val photos = dbHandler.getAllPhotos()

        val gridAdapter = CustomGridAdapter(titles, photos)
        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        //アダプターとレイアウトマネージャーをセット
        gridRecyclerView_third.layoutManager = layoutManager
        gridRecyclerView_third.setHasFixedSize(true)
        gridRecyclerView_third.adapter = gridAdapter

        gridAdapter.setOnItemClickListener(object: CustomGridAdapter.OnItemClickListener{
            override fun onItemClickListener(view: View, position: Int, clickedText: String) {
                val title = diaries[position].title
                val date = diaries[position].day
                val photo = diaries[position].photo
                showDiary(title, date, photo)
            }
        })

        button_back.setOnClickListener {
            val intent = Intent(this, ViewDiaryMenuActivity::class.java)
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
            (width * factor * 0.9).toInt(),
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
