package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_first_diary.*

class FirstDiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_diary)

        btn_show_home.setOnClickListener {
            //val intent = Intent(this, SecondActivity::class.java)
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }

        btn_show_online.setOnClickListener {
            Toast.makeText(this, "読み込み中", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignActivity::class.java)
            intent.putExtra("CHECK", "1000")
            startActivity(intent)
        }

        btn_exp_home.setOnClickListener {
            val msgView = TextView(this)
            msgView.text = "自宅メニューでは「日記を書くこと、見ること」、「歩数を記録すること、確認すること」ができます。"
            msgView.textSize = 24F
            msgView.setTextColor(Color.BLACK)
            msgView.setPadding(30, 20, 30, 20)
            val dialog = AlertDialog.Builder(this)
                .setTitle("自宅メニューって何が出来るの？") // タイトル
                .setView(msgView) // メッセージ
                .setPositiveButton("この画面を閉じる", null)
                .create()
            // AlertDialogを表示
            dialog.show()
        }

        btn_exp_online.setOnClickListener {
            val msgView = TextView(this)
            msgView.text = "教室メニューでは「みんなで写真を共有すること」、「歩数の目標設定をすること」ができます。"
            msgView.textSize = 24F
            msgView.setTextColor(Color.BLACK)
            msgView.setPadding(30, 20, 30, 20)
            val dialog = AlertDialog.Builder(this)
                .setTitle("教室メニューって何が出来るの？") // タイトル
                .setView(msgView) // メッセージ
                .setPositiveButton("この画面を閉じる", null)
                .create()
            // AlertDialogを表示
            dialog.show()
        }
    }
}
