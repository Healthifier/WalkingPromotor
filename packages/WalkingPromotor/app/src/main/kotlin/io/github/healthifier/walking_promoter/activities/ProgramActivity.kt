package io.github.healthifier.walking_promoter.activities

import android.app.DatePickerDialog
import android.app.PendingIntent.getActivity
import android.content.Intent
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

        button4.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this, MetsActivity::class.java)
            startActivity(intent)
        }

    }
}
