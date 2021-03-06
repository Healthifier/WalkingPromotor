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
import kotlinx.android.synthetic.main.activity_view_diary_menu.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewDiaryMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_diary_menu)

        btn_diary_mine.setOnClickListener {
            val intent = Intent(this, ViewMyDiaryActivity::class.java)
            startActivity(intent)
        }

        btn_diary_every.setOnClickListener {
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

        backButton.setOnClickListener {
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
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
                val intent = Intent(this@ViewDiaryMenuActivity, ViewEveryonesDiaryActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }else{
                val intent = Intent(this@ViewDiaryMenuActivity, SignActivity::class.java)
                intent.putExtra("CHECK", "1001")
                startActivity(intent)
                dialog.dismiss()
            }
        }
    }

    private fun showNetworkFailed(){
        val view: View = layoutInflater.inflate(R.layout.custom_dialog_message, null)
        val title: TextView = view.findViewById(R.id.TextView_dialog_title)
        title.text = "ネットに繋がっていません！"
        val button: Button = view.findViewById(R.id.Button_dialog_positive)
        button.text = "この画面を閉じる"
        val dialog = AlertDialog.Builder(this@ViewDiaryMenuActivity).setView(view).create()
        dialog.show()

        button.setOnClickListener {
            dialog.dismiss()
        }
    }
}
