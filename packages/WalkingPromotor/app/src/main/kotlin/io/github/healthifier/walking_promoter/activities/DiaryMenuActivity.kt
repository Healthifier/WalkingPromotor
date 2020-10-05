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
import kotlinx.android.synthetic.main.activity_diary_menu.*

class DiaryMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_menu)

        btn_diary_mine.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        btn_diary_every.setOnClickListener {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if(capabilities != null){
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    val intent = Intent(this, SignActivity::class.java)
                    intent.putExtra("CHECK", "1001")
                    startActivity(intent)
                }
            }else{
                Toast.makeText(this, "Wi-Fi接続をしてください", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG", "ネットワークに接続していません")
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, HomeProgramActivity::class.java)
            startActivity(intent)
        }
    }
}
