package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_program.*

class ProgramActivity : AppCompatActivity() {

    private val curUser = NCMBUser.getCurrentUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program)
        //Toast.makeText(this, "${curUser.userName}さんでログインしています", Toast.LENGTH_LONG).show()
        textView_log.text="${curUser.userName}さんでログインしています。"

        button4.setOnClickListener {
            val intent = Intent(this, DataSelectActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("CHECK", "1002")
            startActivity(intent)
        }

        button5.setOnClickListener {
            val intent = Intent(this, FirstDiaryActivity::class.java)
            startActivity(intent)
        }

        /**
         * ここから説明ボタンの内容
         */

        btn_exp_share.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title: TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "みんなで写真を共有とは？"
            val message: TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "ここでは今までに撮ってきた写真の中でお気に入りの3枚をみんなで共有しあい、交流を深めていきます。"
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

        btn_exp_goal.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title:TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "みんなで目標設定とは？"
            val message:TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "ここではみんなで話し合いながら、自分の次週の歩数目標を決めて設定していきましょう。"
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
