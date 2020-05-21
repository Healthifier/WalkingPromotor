package io.github.healthifier.walking_promoter.activities

import android.app.DatePickerDialog
import android.app.PendingIntent.getActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.Database
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import io.github.healthifier.walking_promoter.models.DiaryData
import io.github.healthifier.walking_promoter.models.DiaryListAdapter
import kotlinx.android.synthetic.main.activity_program.*
import java.util.*

class ProgramActivity : AppCompatActivity() {

    var dbHandler = DatabaseHandler(this)
    private var _db: Database? = null
    val cal = Calendar.getInstance()

    val wordList : List<String> = listOf("それでは実際に日記を紹介していきましょう",
                                          "あなたがつけた日記の中からひとつ選んでください",
                                          "選択してボタンを押す",
                                          "写真の情報や自分の感想などを踏まえながら話してみましょう")
    var count = 0
    var title = ""
    var day = ""
    var photo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program)
        _db = Database(this)

        textView4.text = wordList[count]

        var diaries = dbHandler.getAllUsers()
        val adapter = DiaryListAdapter(this, diaries)
        list_view2.adapter = adapter

        list_view2.setOnItemClickListener { adapterView, view, position, id ->
            //val name = view.findViewById<TextView>(android.R.id.text1).text
            val diarydata = adapterView.getItemAtPosition(position) as DiaryData
            title = diarydata.title
            day = diarydata.day
            photo = diarydata.photo
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            //updateDateInView()
        }

        button4.setOnClickListener {
            DatePickerDialog(this@ProgramActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

            val ex = _db?.getMyStepCount(cal)
            textView4.text = ex.toString()
        }

    }
}
