package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
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
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title:TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "日記の書き方は？"
            val message:TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "ここではウォーキング中に見つけたものを写真に撮って日記を書きます。"
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

        btn_exp_look.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title:TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "日記を見るとは？"
            val message:TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "ここでは「自分が書いた日記を見ること」、「他の人が書いた日記を見ること」ができます。"
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

        btn_exp_update.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title:TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "日記を投稿するとは？"
            val message:TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "ここでは「自分が書いた日記を選択して、他の人が見れるようにすること」ができます。"
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

        btn_exp_walk.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title:TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "歩数についてとは？"
            val message:TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "ここでは「歩数の記録すること」、「歩数を見ること」、「歩数の目標を設定すること」ができます。"
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

        btn_exp_step.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title:TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "ステップ練習とは？"
            val message:TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "ここでは緑色のマス目を触れていくことで、スクエアステップに似た練習ができます。"
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
