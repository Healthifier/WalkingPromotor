package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_program.*

class ProgramActivity : AppCompatActivity() {

    private val curUser = NCMBUser.getCurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program)
        Toast.makeText(this, "${curUser.userName}さんでログインしています", Toast.LENGTH_LONG).show()

        //setSupportActionBar(main_toolbar)
        //main_toolbar.title = "オンラインメニュー"
        //main_toolbar.setTitleTextColor(Color.WHITE)

        button4.setOnClickListener {
            val intent = Intent(this, DataSelectActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        button5.setOnClickListener {
            val intent = Intent(this, FirstDiaryActivity::class.java)
            startActivity(intent)
        }

    }
    /*
    // オプションメニューを作成する
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//
        getMenuInflater().inflate(R.menu.main, menu)
// オプションメニュー表示する場合はtrue
        return true
    }
    // ここでメニュー項目を表示前に調整できる
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_settings0).isVisible = false
        //menu.findItem(R.id.action_settings3).setTitle(R.string.menu_msg3X)
        menu.findItem(R.id.action_add).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings1 -> {
                //tmpLowDisp(item.title.toString())
                true
            }
            R.id.action_settings2 -> {
                //tmpLowDisp(item.title.toString())
                true
            }

            R.id.action_search -> {
                //tmpLowDisp(item.title.toString())
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }*/
}
