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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class SignActivity : AppCompatActivity() {

    private val password = "1023"
    private var p_check = ""
    private var groupName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val check = intent.getStringExtra("CHECK")
        p_check = check!!

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
                    try {
                        signUp()
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
                val view: View = layoutInflater.inflate(R.layout.dialog_progress, null)
                val dialog = AlertDialog.Builder(this).setCancelable(false).setView(view).create()
                dialog.show()
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
                    val intent = Intent(this, ViewDiaryMenuActivity::class.java)
                    startActivity(intent)
                }
                "1002" -> {
                    val intent = Intent(this, HomeProgramActivity::class.java)
                    startActivity(intent)
                }
                "1003" -> {
                    val intent = Intent(this, StepProgramActivity::class.java)
                    startActivity(intent)
                }
                "1004" -> {
                    val intent = Intent(this, StepProgramActivity::class.java)
                    startActivity(intent)
                }
                "1005" -> {
                    val intent = Intent(this, StepProgramActivity::class.java)
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
    private fun signUp(){
        val acl = NCMBAcl()
        acl.publicReadAccess = true
        acl.publicWriteAccess = true
        val userName = userName.text.toString()
        val user = NCMBUser()
        NCMBUser.logout()
        user.userName = userName
        user.setPassword(password)
        user.acl = acl
        //user.put("groupName", group) グループはまだ格納しない
        GlobalScope.launch{ user.save()}
        lblStats.text = "新規登録が完了しました！ログインをタッチしてください"
    }

    /**
     * ログイン処理用のfun
     */
    private fun signIn(newGroup:String) {
        if(newGroup == ""){ //グループが選択されていないとき
            Toast.makeText(this, "所属するグループを選択してください", Toast.LENGTH_SHORT).show()
        }else{ //グループが満員かどうか確認
            val query = NCMBUser.getQuery()
            query.whereEqualTo("groupName", newGroup)
            val c = query.find()
            if(c.size >= 5){
                Toast.makeText(this@SignActivity, "選択したグループは満員となってしまいました", Toast.LENGTH_SHORT).show()
                Log.d("User Count Result", c.size.toString())
            }else{
                val userName = userName.text.toString()
                NCMBUser.logout()
                NCMBUser.loginInBackground(userName, password){ _, e ->
                    if(e != null){
                        Log.d("[Login Error]", e.toString())
                        lblStats.text = "ログインできませんでした.名前を確認してもう一度試してください"
                    }else{
                        Log.d("[Login Result]", "Success")
                        val queryUser = NCMBUser.getQuery()
                        queryUser.whereEqualTo("userName", userName)
                        //val a = queryUser.find()
                        queryUser.findInBackground { userList, error ->
                            if(error != null){
                                Log.d("[Find Error]", e.toString())
                            }else{
                                Log.d("user", userList[0].toString())
                                if(userList[0].getString("groupName") != null){ // 2回目以降のログインのとき
                                    val oldGroup = userList[0].getString("groupName")
                                    if(oldGroup == newGroup){ //前回のグループと同じとき削除&追加しない
                                        when (p_check) { //そのままアクティビティ遷移
                                            "1000" -> {
                                                val intent = Intent(this, ClassProgramActivity::class.java)
                                                startActivity(intent)
                                            }
                                            "1001" -> {
                                                val intent = Intent(this, ViewEveryonesDiaryActivity::class.java)
                                                startActivity(intent)
                                            }
                                            "1002" -> {
                                                val intent = Intent(this, PostDiaryActivity::class.java)
                                                startActivity(intent)
                                            }
                                            "1003" -> {
                                                val intent = Intent(this, TokaidoMapFragmentActivity::class.java)
                                                startActivity(intent)
                                            }
                                            "1004" -> {
                                                val intent = Intent(this, RecordStepActivity::class.java)
                                                startActivity(intent)
                                            }
                                            "1005" -> {
                                                val intent = Intent(this, MyMapFragmentActivity::class.java)
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
                                                    if(b != null){
                                                        val findOldRole = b[0]

                                                        //前回のグループ内でのユーザー情報を削除
                                                        findOldRole.removeUser(listOf(userList[0]))
                                                        //新しい所属グループ内にユーザー情報を追加
                                                        val currentUser = NCMBUser.getCurrentUser()
                                                        currentUser.put("groupName", newGroup)
                                                        currentUser.saveInBackground { saveError ->
                                                            if(saveError != null){
                                                                Log.d("[Save Error]", saveError.toString())
                                                            }else{
                                                                Log.d("[Save Result]", "Success")
                                                                val queryNewRole = NCMBRole.getQuery()
                                                                queryNewRole.whereEqualTo("roleName", newGroup)
                                                                val n = queryNewRole.find()
                                                                if(n.size > 0){
                                                                    val findNewRole = n[0]
                                                                    findNewRole.addUserInBackground(listOf(currentUser)){ addUserError ->
                                                                        if(addUserError != null){
                                                                            Log.d("[Add Error]", addUserError.toString())
                                                                        }else{
                                                                            Log.d("[Add Result]", "Success")
                                                                            val queryObject = NCMBQuery<NCMBObject>("photoPath")
                                                                            //var abc = NCMBObject("photoPath")
                                                                            queryObject.whereEqualTo("name", currentUser.userName)
                                                                            queryObject.findInBackground { mutableList, ncmbException ->
                                                                                if(ncmbException != null){
                                                                                    ncmbException.printStackTrace()
                                                                                }else{
                                                                                    if(mutableList.isNotEmpty()){
                                                                                        //var abc = NCMBObject("photoPath")
                                                                                        val obj = mutableList[0]
                                                                                        obj.put("groupName", newGroup)
                                                                                        obj.save()
                                                                                    }else{
                                                                                        Log.d("error", "mutableList is empty")
                                                                                    }
                                                                                }
                                                                            }
                                                                            when (p_check) {
                                                                                "1000" -> {
                                                                                    val intent = Intent(this, ClassProgramActivity::class.java)
                                                                                    startActivity(intent)
                                                                                }
                                                                                "1001" -> {
                                                                                    val intent = Intent(this, ViewEveryonesDiaryActivity::class.java)
                                                                                    startActivity(intent)
                                                                                }
                                                                                "1002" -> {
                                                                                    val intent = Intent(this, PostDiaryActivity::class.java)
                                                                                    startActivity(intent)
                                                                                }
                                                                                "1003" -> {
                                                                                    val intent = Intent(this, TokaidoMapFragmentActivity::class.java)
                                                                                    startActivity(intent)
                                                                                }
                                                                                "1004" -> {
                                                                                    val intent = Intent(this, RecordStepActivity::class.java)
                                                                                    startActivity(intent)
                                                                                }
                                                                                "1005" -> {
                                                                                    val intent = Intent(this, MyMapFragmentActivity::class.java)
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
                                }else{//初回ログイン時
                                    //新しい所属グループ内にユーザー情報を追加
                                    val currentUser = NCMBUser.getCurrentUser()
                                    currentUser.put("groupName", newGroup)
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
                                                findNewRole.addUserInBackground(listOf(currentUser)){ addUserError ->
                                                    if(addUserError != null){
                                                        Log.d("[Add Error]", addUserError.toString())
                                                    }else{
                                                        Log.d("[Add Result]", "Success")
                                                        when (p_check) {
                                                            "1000" -> {
                                                                val intent = Intent(this, ClassProgramActivity::class.java)
                                                                startActivity(intent)
                                                            }
                                                            "1001" -> {
                                                                val intent = Intent(this, ViewEveryonesDiaryActivity::class.java)
                                                                startActivity(intent)
                                                            }
                                                            "1002" -> {
                                                                val intent = Intent(this, PostDiaryActivity::class.java)
                                                                startActivity(intent)
                                                            }
                                                            "1003" -> {
                                                                val intent = Intent(this, TokaidoMapFragmentActivity::class.java)
                                                                startActivity(intent)
                                                            }
                                                            "1004" -> {
                                                                val intent = Intent(this, RecordStepActivity::class.java)
                                                                startActivity(intent)
                                                            }
                                                            "1005" -> {
                                                                val intent = Intent(this, MyMapFragmentActivity::class.java)
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
}
