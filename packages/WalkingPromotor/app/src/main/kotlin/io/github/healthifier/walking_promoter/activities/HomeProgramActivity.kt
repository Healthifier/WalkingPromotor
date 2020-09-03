package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_home_program.*

class HomeProgramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_program)

        write_button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        look_button.setOnClickListener {
            val intent = Intent(this, DiaryMenuActivity::class.java)
            startActivity(intent)
        }

        step_button.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }

        upload_button.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            intent.putExtra("CHECK", "1002")
            startActivity(intent)
        }

        walk_button.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            intent.putExtra("CHECK", "1003")
            startActivity(intent)
        }

        back_button.setOnClickListener {
            val intent = Intent(this, FirstDiaryActivity::class.java)
            startActivity(intent)
        }

        btn_exp_write.setOnClickListener {
            val msgView = TextView(this)
            msgView.text = "ここでは「タイトルを入力すること」、「日付を入力すること」、「写真を撮ること」、「保存すること」で日記を書くことができます。"
            msgView.textSize = 24F
            msgView.setTextColor(Color.BLACK)
            msgView.setPadding(30, 20, 30, 20)
            val dialog = AlertDialog.Builder(this)
                .setTitle("日記の書き方は？") // タイトル
                .setView(msgView) // メッセージ
                .setPositiveButton("わかりました", null)
                .create()
            // AlertDialogを表示
            dialog.show()
        }

        btn_exp_look.setOnClickListener {
            val msgView = TextView(this)
            msgView.text = "ここでは「自分が書いた日記を見ること」、「他の人が書いた日記を見ること」ができます。"
            msgView.textSize = 24F
            msgView.setTextColor(Color.BLACK)
            msgView.setPadding(30, 20, 30, 20)
            val dialog = AlertDialog.Builder(this)
                .setTitle("日記を見るとは？") // タイトル
                .setView(msgView) // メッセージ
                .setPositiveButton("わかりました", null)
                .create()
            // AlertDialogを表示
            dialog.show()
        }

        btn_exp_update.setOnClickListener {
            val msgView = TextView(this)
            msgView.text = "ここでは「自分が書いた日記を選択して、他の人が見れるようにすること」ができます。"
            msgView.textSize = 24F
            msgView.setTextColor(Color.BLACK)
            msgView.setPadding(30, 20, 30, 20)
            val dialog = AlertDialog.Builder(this)
                .setTitle("日記を投稿するとは？") // タイトル
                .setView(msgView) // メッセージ
                .setPositiveButton("わかりました", null)
                .create()
            // AlertDialogを表示
            dialog.show()
        }

        btn_exp_walk.setOnClickListener {
            val msgView = TextView(this)
            msgView.text = "ここでは「歩数の記録すること」、「歩数を見ること」、「歩数の目標を設定すること」ができます。"
            msgView.textSize = 24F
            msgView.setTextColor(Color.BLACK)
            msgView.setPadding(30, 20, 30, 20)
            val dialog = AlertDialog.Builder(this)
                .setTitle("歩数についてとは？") // タイトル
                .setView(msgView) // メッセージ
                .setPositiveButton("わかりました", null)
                .create()
            // AlertDialogを表示
            dialog.show()
        }

        btn_exp_step.setOnClickListener {
            val msgView = TextView(this)
            msgView.text = "ここでは緑色のマス目を触れていくことで、スクエアステップに似た練習ができます。"
            msgView.textSize = 24F
            msgView.setTextColor(Color.BLACK)
            msgView.setPadding(30, 20, 30, 20)
            val dialog = AlertDialog.Builder(this)
                .setTitle("ステップ練習とは？") // タイトル
                .setView(msgView) // メッセージ
                .setPositiveButton("わかりました", null)
                .create()
            // AlertDialogを表示
            dialog.show()
        }
    }
}
