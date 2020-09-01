package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_home_program.*

class HomeProgramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_program)

        write_button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        look_button.setOnClickListener {
            val intent = Intent(this, DiaryMenuActivity::class.java)
            startActivity(intent)
        }

        step_button.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }

        upload_button.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            intent.putExtra("CHECK", "1002")
            startActivity(intent)
        }

        walk_button.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            intent.putExtra("CHECK", "1003")
            startActivity(intent)
        }

        back_button.setOnClickListener {
            val intent = Intent(this, FirstDiaryActivity::class.java)
            startActivity(intent)
        }
    }
}
