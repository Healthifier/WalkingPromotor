package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBRole
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_sign.*
import io.github.healthifier.walking_promoter.BuildConfig
import java.util.*

class SignActivity : AppCompatActivity() {

    private val password = "1023"
    //private val spinnerItems = arrayOf("りんご", "ぶどう", "もも", "なし")
    private var p_check = ""
    private var groupName = ""
    //private var user = NCMBUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        //クラウドアプリの選択
        NCMB.initialize(applicationContext, BuildConfig.APPLICATION_KEY, BuildConfig.CLIENT_KEY)

        val curUser = NCMBUser.getCurrentUser()
        val check = intent.getStringExtra("CHECK")
        //var groupName = ""
        p_check = check
        //user = curUser

        Log.d("DEBUG", curUser.toString())

        //ログイン済か判定する
        when {
            /*
            curUser == null -> { //端末で初めて扱うとき用
                lblStats.text = "新規登録またはログインを行ってください"
            }*/
            curUser.getString("sessionToken") == null -> { //セッショントークンが切れていた場合
                //Log.d("[name]", curUser.userName.toString())
                NCMBUser.logout()
                (findViewById<TextView>(R.id.lblStats)).text = "新規登録またはログインを行ってください"
            }
            else -> { //セッショントークンが残っていた場合
                //一度ログアウトしてログインすることでセッショントークンの更新を行う
                NCMBUser.logout()
                NCMBUser.loginInBackground(curUser.userName, password){ncmbUser, e ->
                    if(e != null){
                        Log.d("[Login Error]", e.toString())
                    }else{
                        Log.d("[Login Result]", "Success")
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
                                val intent = Intent(this, WalkProgramActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }

        // 初回会員登録処理用のボタン
        btnSignUp.setOnClickListener {
            signUp()
            //signIn(curUser)
        }

        //ログイン用のボタン
        btnSignIn.setOnClickListener {
            signIn(groupName)
        }

        image_apple.setOnClickListener {
            groupName = "group_apple"
            image_apple.setBackgroundColor(Color.parseColor("#FFFB8C00"))
            image_grape.setBackgroundColor(Color.parseColor("#FFFFFF"))
            image_peach.setBackgroundColor(Color.parseColor("#FFFFFF"))
            image_lemon.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        image_grape.setOnClickListener {
            groupName = "group_grape"
            image_apple.setBackgroundColor(Color.parseColor("#FFFFFF"))
            image_grape.setBackgroundColor(Color.parseColor("#FFFB8C00"))
            image_peach.setBackgroundColor(Color.parseColor("#FFFFFF"))
            image_lemon.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        image_peach.setOnClickListener {
            groupName = "group_peach"
            image_apple.setBackgroundColor(Color.parseColor("#FFFFFF"))
            image_grape.setBackgroundColor(Color.parseColor("#FFFFFF"))
            image_peach.setBackgroundColor(Color.parseColor("#FFFB8C00"))
            image_lemon.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        image_lemon.setOnClickListener {
            groupName = "group_lemon"
            image_apple.setBackgroundColor(Color.parseColor("#FFFFFF"))
            image_grape.setBackgroundColor(Color.parseColor("#FFFFFF"))
            image_peach.setBackgroundColor(Color.parseColor("#FFFFFF"))
            image_lemon.setBackgroundColor(Color.parseColor("#FFFB8C00"))
        }

        /*

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
        }*/
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
    private fun signIn(group:String) {
        if(group == ""){ //グループが選択されていないとき
            Toast.makeText(this, "所属するグループを選択してください", Toast.LENGTH_SHORT).show()
        }else{
            val userName = (findViewById<TextView>(R.id.userName)).text.toString()
            NCMBUser.logout()
            //NCMBUser.login(userName, password)
            NCMBUser.loginInBackground(userName, password){ ncmbUser, e ->
                if(e != null){
                    Log.d("[Login Error]", e.toString())
                }else{
                    Log.d("[Login Result]", "Success")
                    val currentUser = NCMBUser.getCurrentUser()
                    currentUser.put("groupName", group)

                    //ユーザー情報として所属グループ名のセーブ
                    currentUser.saveInBackground { e ->
                        if(e != null){
                            Log.d("[Save Error]", e.toString())
                        }else{
                            Log.d("[Save Result]", "Success")
                            val query = NCMBRole.getQuery()
                            query.whereEqualTo("roleName", group)
                            //所属グループ名と合致するロールの検索
                            query.findInBackground { list, e ->
                                if(e != null){
                                    Log.d("[Find Error]", e.toString())
                                }else{
                                    Log.d("[Find Result]", "Success")
                                    if(list.size > 0){
                                        val role =list[0]
                                        //ロールにユーザーを追加
                                        role.addUserInBackground(listOf(currentUser)){ e ->
                                            if(e != null){
                                                Log.d("[Add Error]", e.toString())
                                            }else{
                                                Log.d("[Add Result]", "Success")
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
