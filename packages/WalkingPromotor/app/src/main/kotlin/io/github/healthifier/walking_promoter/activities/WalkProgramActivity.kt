package io.github.healthifier.walking_promoter.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_walk_program.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalkProgramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_program)

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
                Toast.makeText(this, "ネットワーク接続をしてください", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG", "ネットワークに接続していません")
            }
        }

        btn_goal.setOnClickListener { //目標値を記録
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("CHECK", "1001")
            startActivity(intent)
        }

        btn_look.setOnClickListener { //自分の歩数を見る
            val intent = Intent(this, DairyGraphActivity::class.java)
            startActivity(intent)
        }

        btn_look_all.setOnClickListener { //みんなの歩数を見る
            //val intent = Intent(this, TokaidoMapFragmentActivity::class.java)
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
                Toast.makeText(this, "ネットワーク接続をしてください", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG", "ネットワークに接続していません")
            }
        }


        btn_back.setOnClickListener { //戻る
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startClass(str: String){
        val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
        dialog.show()

        GlobalScope.launch {
            val curUser = NCMBUser.getCurrentUser()
            delay(1200)
            if(str=="write"){
                if(curUser.getString("sessionToken") != null){
                    val intent = Intent(this@WalkProgramActivity, WalkValActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                }else{
                    val intent = Intent(this@WalkProgramActivity, SignActivity::class.java)
                    intent.putExtra("CHECK", "1004")
                    startActivity(intent)
                    dialog.dismiss()
                }
            }else if(str == "lookAll"){
                if(curUser.getString("sessionToken") != null){
                    val intent = Intent(this@WalkProgramActivity, TokaidoMapFragmentActivity::class.java)
                    startActivity(intent)
                    dialog.dismiss()
                }else{
                    val intent = Intent(this@WalkProgramActivity, SignActivity::class.java)
                    intent.putExtra("CHECK", "1003")
                    startActivity(intent)
                    dialog.dismiss()
                }
            }

        }
    }
}
