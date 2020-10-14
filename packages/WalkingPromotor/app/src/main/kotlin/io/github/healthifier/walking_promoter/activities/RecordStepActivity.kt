package io.github.healthifier.walking_promoter.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nifcloud.mbaas.core.NCMBObject
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import kotlinx.android.synthetic.main.activity_record_step.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class RecordStepActivity : AppCompatActivity() {

    private val cal = Calendar.getInstance()
    private var dbHandler: DatabaseHandler? = null
    private var _count = 0
    private val cloudUser = NCMBUser.getCurrentUser()
    private var cloudWalkObj = NCMBObject("walkValue")
    private var p_cloudUName = ""
    private var p_cloudUId = ""
    private var check = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_step)

        dbHandler = DatabaseHandler(this)

        val cloudUName = cloudUser.userName
        val cloudUId = cloudUser.objectId

        p_cloudUName = cloudUName
        p_cloudUId = cloudUId

        setGoal(false)
        updateGoalEdit()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        calButton.setOnClickListener {
            DatePickerDialog(
                this@RecordStepActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
            check = true
        }

        backButton.setOnClickListener {
            val intent = Intent(this, StepProgramActivity::class.java)
            startActivity(intent)
        }

        button_0.setOnClickListener {
            _count = _count * 10 + 0
            updateGoalEdit()
        }
        button_1.setOnClickListener {
            _count = _count * 10 + 1
            updateGoalEdit()
        }
        button_2.setOnClickListener {
            _count = _count * 10 + 2
            updateGoalEdit()
        }
        button_3.setOnClickListener {
            _count = _count * 10 + 3
            updateGoalEdit()
        }
        button_4.setOnClickListener {
            _count = _count * 10 + 4
            updateGoalEdit()
        }
        button_5.setOnClickListener {
            _count = _count * 10 + 5
            updateGoalEdit()
        }
        button_6.setOnClickListener {
            _count = _count * 10 + 6
            updateGoalEdit()
        }
        button_7.setOnClickListener {
            _count = _count * 10 + 7
            updateGoalEdit()
        }
        button_8.setOnClickListener {
            _count = _count * 10 + 8
            updateGoalEdit()
        }
        button_9.setOnClickListener {
            _count = _count * 10 + 9
            updateGoalEdit()
        }
        button_cancel.setOnClickListener {
            _count = 0
            updateGoalEdit()
        }
        button_decision.setOnClickListener {
            setGoal(true)
        }
    }

    override fun onBackPressed() {
    }

    private fun updateDateInView() {
        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        calTextView.text = sdf.format(cal.time)
    }

    private fun updateGoalEdit() {
        goalEditText.setText(formatNumber(_count))
    }

    private fun setGoal(save: Boolean) {
        //val steps = goalEditText.text.toString()
        val steps = _count
        val date = calTextView.text.toString()
        if (save) {
            setupViews(cal, date, steps, p_cloudUName, p_cloudUId)
        } else {
            updateGoalEdit()
        }
    }

    private fun formatNumber(num: Int): String{
        val format = NumberFormat.getNumberInstance()
        return format.format(num.toLong())
    }

    /**
     * アラートダイアログ生成
     */
    private fun setupViews(cal: Calendar, date:String, steps:Int, cloudUName:String, cloudUId:String) {
        // BuilderからAlertDialogを作成
        if(!check){
            Toast.makeText(this, "日付を入力してください", Toast.LENGTH_SHORT).show()
        }else{
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_check, null)
            val dialogTitle: TextView = view.findViewById(R.id.TextView_dialog_title)
            dialogTitle.text = "この記録でよろしいですか？"
            val message: TextView = view.findViewById(R.id.TextView_dialog_message)
            message.text = "日付：${date} 歩数：${steps}"
            val button1: Button = view.findViewById(R.id.Button_dialog_positive)
            button1.text = "やっぱりやめる"
            val button2: Button = view.findViewById(R.id.Button_dialog_negative)
            button2.text = "記録する"

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
                dialog.dismiss()
            }

            button2.setOnClickListener {
                saveSteps(cal, steps) //DBに保存
                saveStepsToCloud(date, steps, cloudUName, cloudUId) //クラウドデータストアに保存
                dialog.dismiss()
            }

        }
    }

    private fun saveSteps(cal: Calendar, steps: Int){
        dbHandler?.setStep(cal, steps)
    }

    /**
     * 日記データをクラウドデータストアにアップロード
     */
    private fun saveStepsToCloud(date:String, steps:Int, userName:String, userId:String){
        /*
            データストアに4つの要素をアップ
         */
        cloudWalkObj.put("date", date)
        cloudWalkObj.put("value", steps)
        cloudWalkObj.put("userName", userName)
        cloudWalkObj.put("userId", userId)

        cloudWalkObj.saveInBackground { e ->
            if(e != null){
                Log.d("[Error]", e.toString())
            }else{
                Log.d("[RESULT:objectUpload]", "SUCCESS")
                Toast.makeText(this, "記録完了", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
