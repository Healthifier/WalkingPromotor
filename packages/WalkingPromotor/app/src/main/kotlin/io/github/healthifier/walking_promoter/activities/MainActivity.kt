package io.github.healthifier.walking_promoter.activities

import android.app.Fragment
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.fragments.GoalFragment
import io.github.healthifier.walking_promoter.fragments.SquareStepListFragment
import io.github.healthifier.walking_promoter.fragments.TokaidoMapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "みんなで目標設定"
        showFragment(GoalFragment::class.java)

    }

    private fun showFragment(clazz: Class<out Fragment>) {
        try {
            val fragment = clazz.newInstance()
            fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}
