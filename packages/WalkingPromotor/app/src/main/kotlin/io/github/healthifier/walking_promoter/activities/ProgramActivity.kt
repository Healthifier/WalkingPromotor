package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import io.github.healthifier.walking_promoter.models.DiaryData
import io.github.healthifier.walking_promoter.models.DiaryListAdapter
import kotlinx.android.synthetic.main.activity_program.*

class ProgramActivity : AppCompatActivity() {

    var dbHandler = DatabaseHandler(this)

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

        button3.setOnClickListener {
            if(count<3) {
                textView4.text = wordList[++count]
            }
            else if(count==3) {
                val intent = Intent(this, FourthActivity::class.java)
                intent.putExtra(FourthActivity.TITLE, title)
                intent.putExtra(FourthActivity.DAY, day)
                intent.putExtra(FourthActivity.PHOTO, photo)
                startActivity(intent)
            }
        }

        button4.setOnClickListener{
            if(count>0) textView4.text = wordList[--count]
        }

        button5.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
