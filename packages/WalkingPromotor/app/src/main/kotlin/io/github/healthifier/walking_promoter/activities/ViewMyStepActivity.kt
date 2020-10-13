package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.fragments.WeeklyGraphFragment
import kotlinx.android.synthetic.main.activity_view_my_step.*

class ViewMyStepActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_my_step)

        val weeklyGraphFragment = WeeklyGraphFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment, weeklyGraphFragment)

        backButton.setOnClickListener {
            val intent = Intent(this, StepProgramActivity::class.java)
            startActivity(intent)
        }
    }
}
