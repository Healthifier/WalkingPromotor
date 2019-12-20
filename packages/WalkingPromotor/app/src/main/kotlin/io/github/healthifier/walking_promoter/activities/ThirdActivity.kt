package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import io.github.healthifier.walking_promoter.models.DiaryData
import io.github.healthifier.walking_promoter.models.DiaryListAdapter
import kotlinx.android.synthetic.main.activity_third.*

class ThirdActivity : AppCompatActivity() {

    var dbHandler = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        var diaries = dbHandler.getAllUsers()
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

            val intent = Intent(this, FourthActivity::class.java)
            intent.putExtra(FourthActivity.TITLE, title)
            intent.putExtra(FourthActivity.DAY, day)
            intent.putExtra(FourthActivity.PHOTO, photo)
            startActivity(intent)
        }
    }
}
