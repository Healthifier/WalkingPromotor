package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.nifcloud.mbaas.core.*
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_sign.*
import java.lang.Exception

class SignActivity : AppCompatActivity() {

    private val password = "1023"
    private var p_check = ""
    private var groupName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val curUser = NCMBUser.getCurrentUser()
        val check = intent.getStringExtra("CHECK")
        p_check = check

        Log.d("DEBUG", curUser.toString())

        //ログイン済か判定する
        when {
            curUser.getString("sessionToken") == null -> { //セッショントークンが切れていた場合
                NCMBUser.logout()
            }
            else -> { //セッショントークンが残っていた場合
                //一度ログアウトしてログインすることでセッショントークンの更新を行う
                NCMBUser.logout()
                NCMBUser.loginInBackground(curUser.userName, password){ncmbUser, e ->
                    if(e != null){
                        Log.d("[Login Error]", e.toString())
                    }else{
                        Log.d("[Login Result]", "Success")
                        Toast.makeText(this, "自動的にログインしました", Toast.LENGTH_SHORT).show()
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
            if(groupName == ""){
                Toast.makeText(this, "所属するグループを選択してください", Toast.LENGTH_SHORT).show()
            }else{
                val view: View = layoutInflater.inflate(R.layout.custom_dialog_check, null)
                val title:TextView = view.findViewById(R.id.TextView_dialog_title)
                title.text = "この名前でよろしいですか？"
                val message:TextView = view.findViewById(R.id.TextView_dialog_message)
                message.text = "あなたが入力した名前： ${userName.text}"
                val button1: Button = view.findViewById(R.id.Button_dialog_positive)
                button1.text = "やっぱりやめる"
                val button2: Button = view.findViewById(R.id.Button_dialog_negative)
                button2.text = "登録する"

                val dialog = AlertDialog.Builder(this)
                    .setView(view)
                    .create()

                // AlertDialogを表示
                dialog.show()

                // AlertDialogのサイズ調整
                val lp = dialog.window?.attributes
                lp?.width = (resources.displayMetrics.widthPixels * 0.7).toInt()
                dialog.window?.attributes = lp

                button1.setOnClickListener {
                    dialog.dismiss() // AlertDialogを閉じる
                }

                button2.setOnClickListener {
                    try {
                        signUp(groupName)
                        dialog.dismiss()
                    }catch (e:Exception){
                        Log.d("[SignUp Error]", e.toString())
                        Toast.makeText(this, "新規会員登録ができませんでした", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
        }

        //ログイン用のボタン
        btnSignIn.setOnClickListener {
            try {
                signIn(groupName)
            }catch (e:Exception){
                Log.d("[SignIn Error]", e.toString())
                Toast.makeText(this, "ログイン処理が完了しませんでした", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            when(check){
                "1000" -> {
                    val intent = Intent(this, FirstDiaryActivity::class.java)
                    startActivity(intent)
                }
                "1001" -> {
                    val intent = Intent(this, DiaryMenuActivity::class.java)
                    startActivity(intent)
                }
                "1002" -> {
                    val intent = Intent(this, HomeProgramActivity::class.java)
                    startActivity(intent)
                }
                "1003" -> {
                    val intent = Intent(this, HomeProgramActivity::class.java)
                    startActivity(intent)
                }
            }
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
    }

    /**
     * 新規登録処理用のfun
     */
    private fun signUp(group:String) {
        if(group==""){
            Toast.makeText(this, "所属するグループを選択してください", Toast.LENGTH_SHORT).show()
        }else{
            val query = NCMBUser.getQuery()
            query.whereEqualTo("groupName", group)
            val c = query.find()
            if(c.size >= 5){
                Toast.makeText(this, "選択したグループは満員となってしまいました", Toast.LENGTH_SHORT).show()
                Log.d("User Count Result", c.size.toString())
            }else{
                val acl = NCMBAcl()
                acl.publicReadAccess = true
                acl.publicWriteAccess = true
                val userName = userName.text.toString()
                val user = NCMBUser()
                NCMBUser.logout()
                user.userName = userName
                user.setPassword(password)
                user.acl = acl
                user.save()
                lblStats.text = "新規登録が完了しました！ログインをタッチしてください"
            }
        }
    }

    /**
     * ログイン処理用のfun
     */
    private fun signIn(newGroup:String) {
        if(newGroup == ""){ //グループが選択されていないとき
            Toast.makeText(this, "所属するグループを選択してください", Toast.LENGTH_SHORT).show()
        }else{
            val userName = userName.text.toString()
            NCMBUser.logout()
            NCMBUser.loginInBackground(userName, password){ ncmbUser, e ->
                if(e != null){
                    Log.d("[Login Error]", e.toString())
                    lblStats.text = "ログインできませんでした.名前を確認してもう一度試してください"
                }else{
                    Log.d("[Login Result]", "Success")
                    val queryUser = NCMBUser.getQuery()
                    queryUser.whereEqualTo("userName", userName)
                    val a = queryUser.find()
                    if(a.size > 0){
                        val findUser = a[0]
                        if(findUser.getString("groupName") != null){ //基本的にここを通るはず
                            val oldGroup = findUser.getString("groupName")
                            if(oldGroup == newGroup){ //前回のグループと同じとき削除&追加しない
                                when (p_check) { //そのままアクティビティ遷移
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
                            }else{ //今回のグループが前回のグループと異なるとき削除&追加を行う
                                //まず新しい登録先のグループメンバー数を確認
                                val queryUserGroup = NCMBUser.getQuery()
                                queryUserGroup.whereEqualTo("groupName", newGroup)
                                queryUserGroup.findInBackground{list, e ->
                                    if(e != null){
                                        Log.d("[Find Error]", e.toString())
                                    }else{
                                        if(list.size >= 5){ //5人以上だった場合には満員表示
                                            Toast.makeText(this, "選択したグループは満員となってしまいました", Toast.LENGTH_SHORT).show()
                                            NCMBUser.logout()
                                            Log.d("Logout Result", "Success")
                                            Log.d("User Count Result", list.size.toString())
                                        }else{ //空きがあるとき削除&追加を行う
                                            Log.d("User Count Result", list.size.toString())
                                            val queryOldRole = NCMBRole.getQuery()
                                            queryOldRole.whereEqualTo("roleName", oldGroup)
                                            val b = queryOldRole.find()
                                            if(b.size > 0){
                                                val findOldRole = b[0]

                                                //前回のグループ内でのユーザー情報を削除
                                                findOldRole.removeUserInBackground(listOf(findUser)){ e ->
                                                    if(e != null){
                                                        Log.d("[Remove Error]", e.toString())
                                                    }else {
                                                        Log.d("[Remove Result]", "Success")
                                                        val currentUser = NCMBUser.getCurrentUser()
                                                        currentUser.put("groupName", newGroup)

                                                        //新しい所属グループ内にユーザー情報を追加
                                                        currentUser.saveInBackground { e ->
                                                            if(e != null){
                                                                Log.d("[Save Error]", e.toString())
                                                            }else{
                                                                Log.d("[Save Result]", "Success")
                                                                val queryNewRole = NCMBRole.getQuery()
                                                                queryNewRole.whereEqualTo("roleName", newGroup)
                                                                val n = queryNewRole.find()
                                                                if(n.size > 0){
                                                                    val findNewRole = n[0]
                                                                    findNewRole.addUserInBackground(listOf(currentUser)){ e ->
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
                        }
                    }else{
                        Log.d("[User Find Error]", "user登録なし")
                    }
                }
            }
        }
    }
}
