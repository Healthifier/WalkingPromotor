package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_sign.*

class SignActivity : AppCompatActivity() {

    private var state = false
    private val password = "1023"
    private val spinnerItems = arrayOf("りんご", "ぶどう", "もも", "なし")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        NCMB.initialize(applicationContext, "8d3584c70aedac126b19635825096cbe82ac4f4b863a2b18d43e6fada9505ba2", "c3750b96bb4c722dc73228c16eea6ddecae52d10af3d626ef9da8643488a4abf")
        // 会員登録処理用のボタン
        btnSignUp.setOnClickListener {
            this.signIn()
        }

        // ログイン用のボタン
        /*btnSignIn.setOnClickListener {
            this.signIn()
        }*/

        //メイン画面遷移用のボタン
        btnMainAct.setOnClickListener {
            if(!state){
                Toast.makeText(this, "ログインしていないためオフラインでの使用になります", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else {
                val intent = Intent(this, DataSelectActivity::class.java)
                startActivity(intent)
            }
        }

        val adapter = ArrayAdapter(applicationContext,
            android.R.layout.simple_spinner_item, spinnerItems)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?, position: Int, id: Long
            ) {
                val spinnerParent = parent as Spinner
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }
    }

    fun signUp() {
        var userName = (findViewById<TextView>(R.id.userName)).text.toString()
        //var password = (findViewById(R.id.password) as TextView).text.toString()
        val user = NCMBUser()
        NCMBUser.logout()
        user.userName = userName
        //user.setPassword(password)
        user.saveInBackground {e ->
            if (e != null) {
                Log.d("[Error1]", e.toString())
            } else {
                (findViewById<TextView>(R.id.lblStats)).text = "新規登録が完了しました"
            }
        }
    }

    fun signIn() {
        var userName = (findViewById<TextView>(R.id.userName)).text.toString()
        //var password = (findViewById(R.id.password) as TextView).text.toString()
        //NCMBUser.logout()
        NCMBUser.loginInBackground(userName, password) { ncmbUser, e ->
            if (e != null) {
                //Log.d("[Error2]", e.toString())
                val user = NCMBUser()
                NCMBUser.logout()
                user.userName = userName
                user.setPassword(password)
                user.saveInBackground {e ->
                    if (e != null) {
                        Log.d("[Error1]", e.toString())
                    } else {
                        (findViewById<TextView>(R.id.lblStats)).text = "新規登録が完了しました"
                    }
                }
            } else {
                (findViewById<TextView>(R.id.lblStats)).text = "${ncmbUser.userName} さんでログインしました"
                state = true
            }

        }
    }
}
