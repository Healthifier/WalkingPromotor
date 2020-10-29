package io.github.healthifier.walking_promoter.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_step_program.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StepProgramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_program)

        btn_write.setOnClickListener { //歩数を記録
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if(capabilities != null){
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    startClass("write")
                }
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    startClass("write")
                }
            }else{
                showNetworkFailed()
            }
        }

        btn_goal.setOnClickListener { //目標値を記録
            val intent = Intent(this, RecordGoalActivity::class.java)
            intent.putExtra("CHECK", "1001")
            startActivity(intent)
        }

        btn_look_own.setOnClickListener {//自分の歩数を見る
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if(capabilities != null){
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    startClass("lookOwn")
                }
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    startClass("lookOwn")
                }
            }else{
                showNetworkFailed()
            }
        }

        btn_look.setOnClickListener { //歩数グラフを見る
            val intent = Intent(this, ViewMyStepActivity::class.java)
            startActivity(intent)
        }

        btn_look_all.setOnClickListener { //みんなの歩数を見る
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if(capabilities != null){
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    startClass("lookAll")
                }
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    startClass("lookAll")
                }
            }else{
                showNetworkFailed()
            }
        }


        btn_back.setOnClickListener { //戻る
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
    }

    private fun startClass(str: String){
        val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
        dialog.show()

        GlobalScope.launch {
            val curUser = NCMBUser.getCurrentUser()
            delay(1200)
            if(str=="write") {
                if (curUser.getString("sessionToken") != null) {
                    val intent = Intent(this@StepProgramActivity, RecordStepActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                } else {
                    val intent = Intent(this@StepProgramActivity, SignActivity::class.java)
                    intent.putExtra("CHECK", "1004")
                    startActivity(intent)
                    dialog.dismiss()
                }
            }else if(str == "lookOwn"){
                if(curUser.getString("sessionToken") != null){
                    val intent = Intent(this@StepProgramActivity, MyMapFragmentActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                }else{
                    val intent = Intent(this@StepProgramActivity, SignActivity::class.java)
                    intent.putExtra("CHECK", "1005")
                    startActivity(intent)
                    dialog.dismiss()
                }
            }else if(str == "lookAll"){
                if(curUser.getString("sessionToken") != null){
                    val intent = Intent(this@StepProgramActivity, TokaidoMapFragmentActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                }else{
                    val intent = Intent(this@StepProgramActivity, SignActivity::class.java)
                    intent.putExtra("CHECK", "1003")
                    startActivity(intent)
                    dialog.dismiss()
                }
            }
        }
    }

    private fun showNetworkFailed(){
        val view: View = layoutInflater.inflate(R.layout.custom_dialog_message, null)
        val title: TextView = view.findViewById(R.id.TextView_dialog_title)
        title.text = "ネットに繋がっていません！"
        val button: Button = view.findViewById(R.id.Button_dialog_positive)
        button.text = "この画面を閉じる"
        val dialog = AlertDialog.Builder(this@StepProgramActivity).setView(view).create()
        dialog.show()

        button.setOnClickListener {
            dialog.dismiss()
        }
    }
}
