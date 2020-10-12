package io.github.healthifier.walking_promoter.activities

import android.app.Fragment
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.fragments.TokaidoMapFragment
import kotlinx.android.synthetic.main.activity_fourth.*

class FourthActivity : AppCompatActivity() {

    /*
    companion object {
        const val TITLE = "title"
        const val DAY = "day"
        const val PHOTO = "photo"
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth)

        showFragment(TokaidoMapFragment::class.java)

        backButton.setOnClickListener {
            val intent = Intent(this, StepProgramActivity::class.java)
            startActivity(intent)
        }

        /*
        var pathName = intent.getStringExtra(PHOTO)

        photo_View.setImageBitmap(BitmapFactory.decodeFile(pathName))

        title_View.text = intent.getStringExtra(TITLE)
        day_View.text = intent.getStringExtra(DAY)

        button6.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
        */

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
