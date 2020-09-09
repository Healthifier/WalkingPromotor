package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
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
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title:TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "自宅メニューって何が出来るの？"
            val message:TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "自宅メニューでは「日記を書くこと、見ること」、「歩数を記録すること、確認すること」ができます。"
            val button: Button = view.findViewById(R.id.Button_dialog_positive)
            button.text = "この画面を閉じる"

            val dialog = AlertDialog.Builder(this)
                .setView(view)
                .create()

            // AlertDialogを表示
            dialog.show()

            // AlertDialogのサイズ調整
            val lp = dialog.window?.attributes
            lp?.width = (resources.displayMetrics.widthPixels * 0.7).toInt()
            dialog.window?.attributes = lp

            button.setOnClickListener {
                dialog.dismiss() // AlertDialogを閉じる
            }
        }

        btn_exp_online.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title:TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "教室メニューって何が出来るの？"
            val message:TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "教室メニューでは「みんなで写真を共有すること」、「歩数の目標設定をすること」ができます。"
            val button: Button = view.findViewById(R.id.Button_dialog_positive)
            button.text = "この画面を閉じる"

            val dialog = AlertDialog.Builder(this)
                .setView(view)
                .create()

            // AlertDialogを表示
            dialog.show()

            // AlertDialogのサイズ調整
            val lp = dialog.window?.attributes
            lp?.width = (resources.displayMetrics.widthPixels * 0.7).toInt()
            dialog.window?.attributes = lp

            button.setOnClickListener {
                dialog.dismiss() // AlertDialogを閉じる
            }
        }
    }
}
