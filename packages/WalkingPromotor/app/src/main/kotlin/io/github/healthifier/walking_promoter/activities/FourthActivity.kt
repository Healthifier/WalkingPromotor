package io.github.healthifier.walking_promoter.activities

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_fourth.*

class FourthActivity : AppCompatActivity() {

    companion object {
        const val TITLE = "title"
        const val DAY = "day"
        const val PHOTO = "photo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth)

        var pathName = intent.getStringExtra(PHOTO)

        photo_View.setImageBitmap(BitmapFactory.decodeFile(pathName))

        title_View.text = intent.getStringExtra(TITLE)
        day_View.text = intent.getStringExtra(DAY)

    }
}
