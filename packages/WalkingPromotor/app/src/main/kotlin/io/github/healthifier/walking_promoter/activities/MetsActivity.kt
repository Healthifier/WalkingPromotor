package io.github.healthifier.walking_promoter.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.fragments.TokaidoMapFragment
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import io.github.healthifier.walking_promoter.models.DiaryData
import io.github.healthifier.walking_promoter.models.DiaryListAdapter
import kotlinx.android.synthetic.main.activity_mets.*
import java.util.*

class MetsActivity : AppCompatActivity() {

    private val cal = Calendar.getInstance()
    private val dbHandler = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mets)

        showFragment(TokaidoMapFragment::class.java)
        tmpShowCal.setOnClickListener {
            showDatePicker()
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
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                //updateDateInView()
                showDiaryList(this) //OK押したら日記ポップアップ画面を表示
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    private fun showDiaryList(context: Context){
        val dialog = Dialog(this)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val customDialogView: View = layoutInflater.inflate(R.layout.custom_dialog_diary_list, null)
        dialog.setContentView(customDialogView)

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
        val diaries = dbHandler.getAllUsers()
        val adapter = DiaryListAdapter(this, diaries)
        val list_view = customDialogView.findViewById<ListView>(R.id.list_view)
        list_view.adapter = adapter
        dialog.show()

        list_view.setOnItemClickListener { adapterView, view, position, id ->
            val diarydata = adapterView.getItemAtPosition(position) as DiaryData
            val title = diarydata.title
            val day = diarydata.day
            val photo = diarydata.photo
            showDiary(title, day ,photo)
        }
    }

    private fun showDiary(title: String, day: String, photo: String){
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
            (width * factor * 0.4).toInt(),
            (height* factor).toInt()
        )

        val imageView_dialog = customDialogView.findViewById<ImageView>(R.id.ctm_imgae_imageView)
        imageView_dialog.setImageBitmap(BitmapFactory.decodeFile(photo))
        val titleTextView_dialog = customDialogView.findViewById<TextView>(R.id.ctm_title_textView)
        titleTextView_dialog.text = title
        val dayTextView_dialog = customDialogView.findViewById<TextView>(R.id.ctm_day_textView)
        dayTextView_dialog.text = day

        dialog.show()
    }
}
