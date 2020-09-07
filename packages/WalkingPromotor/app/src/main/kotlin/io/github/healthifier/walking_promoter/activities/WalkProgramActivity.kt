package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_walk_program.*

class WalkProgramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_program)

        btn_write.setOnClickListener {
            val intent = Intent(this, WalkValActivity::class.java)
            startActivity(intent)
        }

        btn_look.setOnClickListener {
            val intent = Intent(this, DairyGraphActivity::class.java)
            startActivity(intent)
        }

        btn_look_all.setOnClickListener {
            val intent = Intent(this, MetsActivity::class.java)
            startActivity(intent)
        }

        btn_goal.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("CHECK", "1001")
            startActivity(intent)
        }

        btn_back.setOnClickListener {
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }
    }
}
