package io.github.healthifier.walking_promoter.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBUser
//import io.github.healthifier.walking_promoter.BuildConfig
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_first_diary.*
import kotlinx.coroutines.*

class FirstDiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_diary)

        //クラウドアプリの選択
        //NCMB.initialize(applicationContext, BuildConfig.APPLICATION_KEY, BuildConfig.CLIENT_KEY)
        NCMB.initialize(applicationContext, "8d3584c70aedac126b19635825096cbe82ac4f4b863a2b18d43e6fada9505ba2", "c3750b96bb4c722dc73228c16eea6ddecae52d10af3d626ef9da8643488a4abf")

        setSupportActionBar(main_toolbar) // toolbar

        // "DataStore"という名前でインスタンスを生成
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val dataBoolean = dataStore.getBoolean("Input", true)

        if(!dataBoolean){
            btn_exp_home.visibility = View.INVISIBLE
            btn_exp_online.visibility = View.INVISIBLE
        }

        btn_show_home.setOnClickListener {//自宅メニュー
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }

        btn_show_online.setOnClickListener {//教室メニュー
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
                Toast.makeText(this, "ネットワーク接続をしてください", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG", "ネットワークに接続していません")
            }
        }

        btn_exp_home.setOnClickListener {
            introApp("自宅メニューって何が出来るの？",
                "自宅メニューでは「日記を書くこと、見ること」、「歩数を記録すること、確認すること」ができます。",
                "この画面を閉じる")
        }

        btn_exp_online.setOnClickListener {
            introApp("教室メニューって何が出来るの？",
                "教室メニューでは「みんなで写真を共有すること」、「歩数の目標設定をすること」ができます。",
                "この画面を閉じる")
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

            val dialog = AlertDialog.Builder(this).setView(view).create()
            dialog.show()

            // AlertDialogのサイズ調整
            val lp = dialog.window?.attributes
            lp?.width = (resources.displayMetrics.widthPixels * 0.7).toInt()
            dialog.window?.attributes = lp

            button1.setOnClickListener{
                introApp("このアプリで目指すこと",
                    "このアプリでは日々のウォーキング中の日記を記録し、その日記をほかの参加者と共有、そして交流を深めていく中で運動に対する意欲を向上していきます。",
                    "この画面を閉じる")
            }

            button2.setOnClickListener{
                changeVisible()
            }

            button3.setOnClickListener {
                val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                if(capabilities != null){
                    if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                        logOut()
                    }
                    if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                        logOut()
                    }
                }else{
                    Toast.makeText(this, "ネットワーク接続をしてください", Toast.LENGTH_SHORT).show()
                    Log.d("DEBUG", "ネットワークに接続していません")
                }
            }

            button4.setOnClickListener{
                dialog.dismiss()
            }
        }
    }

    override fun onBackPressed() {
    }

    private fun startClass(){
        val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
        dialog.show()

        val curUser = NCMBUser.getCurrentUser()

        GlobalScope.launch {
            delay(1200)
            if(curUser.getString("sessionToken") != null){
                val intent = Intent(this@FirstDiaryActivity, ClassProgramActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }else{
                val intent = Intent(this@FirstDiaryActivity, SignActivity::class.java)
                intent.putExtra("CHECK", "1000")
                startActivity(intent)
                dialog.dismiss()
            }
        }
    }

    private fun introApp(titleText:String, messageText: String, buttonText: String){
        val view: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
        val title:TextView = view.findViewById(R.id.TextView_dialog_title)
        title.text = titleText
        val message:TextView = view.findViewById(R.id.TextView_dialog_message)
        message.text = messageText
        val button: Button = view.findViewById(R.id.Button_dialog_positive)
        button.text = buttonText

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

    private fun changeVisible(){
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

    private fun logOut(){
        val view: View = layoutInflater.inflate(R.layout.custom_dialog_check, null)
        val title:TextView = view.findViewById(R.id.TextView_dialog_title)
        title.text = "本当にログアウトしますか？"
        val message:TextView = view.findViewById(R.id.TextView_dialog_message)
        message.text = "あとでもう一度ログインする必要が出てきます"
        val button1: Button = view.findViewById(R.id.Button_dialog_positive)
        button1.text = "この画面を閉じる"
        val button2: Button = view.findViewById(R.id.Button_dialog_negative)
        button2.text = "ログアウトする"

        val dialog = AlertDialog.Builder(this).setView(view).create()
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
}
