package io.github.healthifier.walking_promoter.activities

import android.app.Fragment
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.fragments.GoalFragment
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import kotlinx.android.synthetic.main.activity_record_goal.*

class RecordGoalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_goal)

        val check = intent.getStringExtra("CHECK")

        val goalFragment = GoalFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment, goalFragment)

        backButton.setOnClickListener {
            if(check == "1001"){
                val intent = Intent(this, StepProgramActivity::class.java)
                startActivity(intent)
            }else if(check == "1002"){
                val intent = Intent(this, ClassProgramActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
    }
}
