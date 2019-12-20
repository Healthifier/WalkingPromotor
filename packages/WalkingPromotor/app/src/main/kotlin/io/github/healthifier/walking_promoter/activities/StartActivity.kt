package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        startButton_walking.setOnClickListener {
            val intent = Intent(this, ItemListActivity::class.java)
            startActivity(intent)
        }

        startButton_diary.setOnClickListener {
        }
    }
}
