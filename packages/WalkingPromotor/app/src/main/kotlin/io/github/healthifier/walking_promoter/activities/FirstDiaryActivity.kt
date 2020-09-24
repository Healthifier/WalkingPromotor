package io.github.healthifier.walking_promoter.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.BuildConfig
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_first_diary.*

class FirstDiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_diary)

        //クラウドアプリの選択
        NCMB.initialize(applicationContext, BuildConfig.APPLICATION_KEY, BuildConfig.CLIENT_KEY)

        setSupportActionBar(main_toolbar) // toolbar

        // "DataStore"という名前でインスタンスを生成
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val dataBoolean = dataStore.getBoolean("Input", true)

        if(!dataBoolean){
            btn_exp_home.visibility = View.INVISIBLE
            btn_exp_online.visibility = View.INVISIBLE
        }

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

        imageView2.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_help, null)
            val button1: Button = view.findViewById(R.id.Button_dialog_explain)
            val button2: Button = view.findViewById(R.id.Button_dialog_switch)
            val button3: Button = view.findViewById(R.id.Button_dialog_logout)
            val button4: Button = view.findViewById(R.id.Button_dialog_back)
            button1.text = "このアプリについて"
            button2.text = "説明ボタン表示の切り替え"
            button3.text = "ログアウト"
            button4.text = "この画面を閉じる"

            val dialog = AlertDialog.Builder(this)
                .setView(view)
                .create()

            // AlertDialogを表示
            dialog.show()

            // AlertDialogのサイズ調整
            val lp = dialog.window?.attributes
            lp?.width = (resources.displayMetrics.widthPixels * 0.7).toInt()
            dialog.window?.attributes = lp

            button1.setOnClickListener{
                val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
                val title:TextView = view.findViewById(R.id.TextView_dialog_title)
                title.text = "このアプリで目指すこと"
                val message:TextView = view.findViewById(R.id.TextView_dialog_message)
                message.text = "このアプリでは日々のウォーキング中の日記を記録し、その日記をほかの参加者と共有、そして交流を深めていく中で運動に対する意欲を向上していきます。"
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

            button2.setOnClickListener{
                // "DataStore"という名前でインスタンスを生成
                val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
                val dataBoolean = dataStore.getBoolean("Input", true)
                val editor = dataStore.edit()
                if(dataBoolean){
                    btn_exp_home.visibility = View.INVISIBLE
                    btn_exp_online.visibility = View.INVISIBLE
                    editor.putBoolean("Input", false)
                    editor.apply()
                    Toast.makeText(this, "説明ボタンを非表示にしました", Toast.LENGTH_SHORT).show()
                }else{
                    btn_exp_home.visibility = View.VISIBLE
                    btn_exp_online.visibility = View.VISIBLE
                    editor.putBoolean("Input", true)
                    editor.apply()
                    Toast.makeText(this, "説明ボタンを常に表示にしました", Toast.LENGTH_SHORT).show()
                }
            }

            button3.setOnClickListener {
                val view: View = layoutInflater.inflate(R.layout.custom_dialog_check, null)
                val title:TextView = view.findViewById(R.id.TextView_dialog_title)
                title.text = "本当にログアウトしますか？"
                val message:TextView = view.findViewById(R.id.TextView_dialog_message)
                message.text = "あとでもう一度ログインする必要が出てきます"
                val button1: Button = view.findViewById(R.id.Button_dialog_positive)
                button1.text = "この画面を閉じる"
                val button2: Button = view.findViewById(R.id.Button_dialog_negative)
                button2.text = "ログアウトする"

                val dialog = AlertDialog.Builder(this)
                    .setView(view)
                    .create()

                // AlertDialogを表示
                dialog.show()

                // AlertDialogのサイズ調整
                val lp = dialog.window?.attributes
                lp?.width = (resources.displayMetrics.widthPixels * 0.7).toInt()
                dialog.window?.attributes = lp

                button1.setOnClickListener {
                    dialog.dismiss() // AlertDialogを閉じる
                }

                button2.setOnClickListener {
                    NCMBUser.logout()
                    Toast.makeText(this, "ログアウトしました", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            button4.setOnClickListener{
                dialog.dismiss()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //リスナー定義 複数設置できるのでitemIdごとに定義できる
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_help){
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_help, null)
            val button1: Button = view.findViewById(R.id.Button_dialog_explain)
            val button2: Button = view.findViewById(R.id.Button_dialog_switch)
            val button3: Button = view.findViewById(R.id.Button_dialog_back)
            button1.text = "このアプリについて"
            button2.text = "説明ボタン表示の切り替え"
            button3.text = "この画面を閉じる"

            val dialog = AlertDialog.Builder(this)
                .setView(view)
                .create()

            // AlertDialogを表示
            dialog.show()

            // AlertDialogのサイズ調整
            val lp = dialog.window?.attributes
            lp?.width = (resources.displayMetrics.widthPixels * 0.7).toInt()
            dialog.window?.attributes = lp

            button1.setOnClickListener{
                val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
                val title:TextView = view.findViewById(R.id.TextView_dialog_title)
                title.text = "このアプリで目指すこと"
                val message:TextView = view.findViewById(R.id.TextView_dialog_message)
                message.text = "このアプリでは日々のウォーキング中の日記を記録し、その日記をほかの参加者と共有、そして交流を深めていく中で運動に対する意欲を向上していきます。"
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

            button2.setOnClickListener{
                // "DataStore"という名前でインスタンスを生成
                val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
                val dataBoolean = dataStore.getBoolean("Input", true)
                val editor = dataStore.edit()
                if(dataBoolean){
                    btn_exp_home.visibility = View.INVISIBLE
                    btn_exp_online.visibility = View.INVISIBLE
                    editor.putBoolean("Input", false)
                    editor.apply()
                    Toast.makeText(this, "説明ボタンを非表示にしました", Toast.LENGTH_SHORT).show()
                }else{
                    btn_exp_home.visibility = View.VISIBLE
                    btn_exp_online.visibility = View.VISIBLE
                    editor.putBoolean("Input", true)
                    editor.apply()
                    Toast.makeText(this, "説明ボタンを常に表示にしました", Toast.LENGTH_SHORT).show()
                }
            }

            button3.setOnClickListener{
                dialog.dismiss()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
