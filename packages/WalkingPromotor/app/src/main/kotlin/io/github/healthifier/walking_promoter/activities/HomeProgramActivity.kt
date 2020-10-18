package io.github.healthifier.walking_promoter.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_home_program.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeProgramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_program)

        // "DataStore"という名前でインスタンスを生成
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val dataBoolean = dataStore.getBoolean("Input", true)

        if(!dataBoolean){
            btn_exp_write.visibility = View.INVISIBLE
            btn_exp_look.visibility = View.INVISIBLE
            btn_exp_update.visibility = View.INVISIBLE
            btn_exp_walk.visibility = View.INVISIBLE
            btn_exp_step.visibility = View.INVISIBLE
        }

        write_button.setOnClickListener {//日記を書く
            val intent = Intent(this, WriteDiaryActivity::class.java)
            startActivity(intent)
        }

        look_button.setOnClickListener {//日記を見る
            val intent = Intent(this, ViewDiaryMenuActivity::class.java)
            startActivity(intent)
        }

        upload_button.setOnClickListener {//日記を投稿する
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if(capabilities != null){
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    startClass()
                }
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    startClass()
                }
            }else{
                showNetworkFailed()
            }
        }

        walk_button.setOnClickListener {//歩数に関して
            val intent = Intent(this, StepProgramActivity::class.java)
            startActivity(intent)
        }

        step_button.setOnClickListener {//ステップ練習
            val intent = Intent(this, SquareStepActivity::class.java)
            startActivity(intent)
        }

        back_button.setOnClickListener {
            val intent = Intent(this, FirstDiaryActivity::class.java)
            startActivity(intent)
        }

        /**
         * ここから説明ボタンの内容
         */

        btn_exp_write.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val title:TextView = view.findViewById(R.id.TextView_dialog_title)
            title.text = "日記の書き方は？"
            val message:TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "ここではウォーキング中に見つけたものを写真に撮って日記を書きます。"
            val button: Button = view.findViewById(R.id.Button_dialog_positive)
            button.text = "この画面を閉じる"

            val dialog = AlertDialog.Builder(this).setView(view).create()
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

            val dialog = AlertDialog.Builder(this).setView(view).create()
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

            val dialog = AlertDialog.Builder(this).setView(view).create()
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

            val dialog = AlertDialog.Builder(this).setView(view).create()
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

            val dialog = AlertDialog.Builder(this).setView(view).create()
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

    override fun onBackPressed() {
    }

    private fun startClass(){
        val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
        dialog.show()

        GlobalScope.launch {
            val curUser = NCMBUser.getCurrentUser()
            delay(1200)
            if(curUser.getString("sessionToken") != null){
                val intent = Intent(this@HomeProgramActivity, PostDiaryActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }else{
                val intent = Intent(this@HomeProgramActivity, SignActivity::class.java)
                intent.putExtra("CHECK", "1002")
                startActivity(intent)
                dialog.dismiss()
            }
        }
    }

    private fun showNetworkFailed(){
        val view: View = layoutInflater.inflate(R.layout.custom_dialog_message, null)
        val title:TextView = view.findViewById(R.id.TextView_dialog_title)
        title.text = "ネットに繋がっていません！"
        val button:Button = view.findViewById(R.id.Button_dialog_positive)
        button.text = "この画面を閉じる"
        val dialog = AlertDialog.Builder(this@HomeProgramActivity).setView(view).create()
        dialog.show()

        button.setOnClickListener {
            dialog.dismiss()
        }
    }
}
