package io.github.healthifier.walking_promoter.activities

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.fragments.WeeklyGraphFragment
import kotlinx.android.synthetic.main.activity_dairy_graph.*

class DairyGraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dairy_graph)

        //showFragment(WeeklyGraphFragment::class.java)

        val WeeklyGraphFragment = WeeklyGraphFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment, WeeklyGraphFragment)

        backButton.setOnClickListener {
            val intent = Intent(this, WalkProgramActivity::class.java)
            startActivity(intent)
        }

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
