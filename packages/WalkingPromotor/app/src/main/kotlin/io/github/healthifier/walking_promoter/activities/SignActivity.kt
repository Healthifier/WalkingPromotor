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

    private val password = "1023"
    private val spinnerItems = arrayOf("りんご", "ぶどう", "もも", "なし")
    private var p_check = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        //クラウドアプリの選択
        NCMB.initialize(applicationContext, "8d3584c70aedac126b19635825096cbe82ac4f4b863a2b18d43e6fada9505ba2", "c3750b96bb4c722dc73228c16eea6ddecae52d10af3d626ef9da8643488a4abf")

        val curUser = NCMBUser.getCurrentUser()
        val check = intent.getStringExtra("CHECK")
        p_check = check

        Log.d("DEBUG", curUser.toString())

        //ログイン済か判定する
        if(curUser.getString("sessionToken") == null){ //セッショントークンが切れていた場合
            NCMBUser.logout()
            (findViewById<TextView>(R.id.lblStats)).text = "新規登録または再度ログインを行ってください"
        }else{ //セッショントークンが残っていた場合
            NCMBUser.logout()
            NCMBUser.login(curUser.userName, password)
            when (check) {
                "1000" -> {
                    val intent = Intent(this, ProgramActivity::class.java)
                    startActivity(intent)
                }
                "1001" -> {
                    val intent = Intent(this, MetsActivity::class.java)
                    startActivity(intent)
                }
                "1002" -> {
                    val intent = Intent(this, DiaryUpActivity::class.java)
                    startActivity(intent)
                }
                "1003" -> {
                    val intent = Intent(this, WalkValActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        // 初回会員登録処理用のボタン
        btnSignUp.setOnClickListener {
            signUp()
            signIn()
        }

        //ログイン用のボタン
        btnSignIn.setOnClickListener {
            signIn()
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

    /**
     * 新規登録処理用のfun
     */
    private fun signUp() {
        val userName = (findViewById<TextView>(R.id.userName)).text.toString()
        val user = NCMBUser()
        NCMBUser.logout()
        user.userName = userName
        user.setPassword(password)
        user.save()
    }

    /**
     * ログイン処理用のfun
     */
    private fun signIn() {
        val userName = (findViewById<TextView>(R.id.userName)).text.toString()
        NCMBUser.logout()
        NCMBUser.login(userName, password)
        val intent = Intent(this, ProgramActivity::class.java)
        startActivity(intent)
        when (p_check) {
            "1000" -> {
                val intent = Intent(this, ProgramActivity::class.java)
                startActivity(intent)
            }
            "1001" -> {
                val intent = Intent(this, MetsActivity::class.java)
                startActivity(intent)
            }
            "1002" -> {
                val intent = Intent(this, DiaryUpActivity::class.java)
                startActivity(intent)
            }
            "1003" -> {
                val intent = Intent(this, WalkValActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
