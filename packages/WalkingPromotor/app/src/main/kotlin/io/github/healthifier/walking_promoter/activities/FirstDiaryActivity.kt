package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_first_diary.*

class FirstDiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_diary)

        btn_make_diary.setOnClickListener {
            //val intent = Intent(this, SecondActivity::class.java)
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }

        btn_show_online.setOnClickListener {
            Toast.makeText(this, "読み込み中", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignActivity::class.java)
            intent.putExtra("CHECK", "1000")
            startActivity(intent)
        }
    }
}
