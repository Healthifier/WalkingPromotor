package io.github.healthifier.walking_promoter.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_walk_program.*

class WalkProgramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_program)

        btn_write.setOnClickListener { //歩数を記録
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if(capabilities != null){
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    val intent = Intent(this, SignActivity::class.java)
                    intent.putExtra("CHECK", "1004")
                    startActivity(intent)
                }
            }else{
                Toast.makeText(this, "Wi-Fi接続をしてください", Toast.LENGTH_SHORT).show()
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
                    val intent = Intent(this, SignActivity::class.java)
                    intent.putExtra("CHECK", "1003")
                    startActivity(intent)
                }
            }else{
                Toast.makeText(this, "Wi-Fi接続をしてください", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG", "ネットワークに接続していません")
            }
        }


        btn_back.setOnClickListener { //戻る
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }
    }
}
