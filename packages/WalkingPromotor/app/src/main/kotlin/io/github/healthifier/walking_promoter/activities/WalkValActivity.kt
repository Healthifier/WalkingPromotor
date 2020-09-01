package io.github.healthifier.walking_promoter.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nifcloud.mbaas.core.NCMBObject
import com.nifcloud.mbaas.core.NCMBUser
import io.github.healthifier.walking_promoter.R
import kotlinx.android.synthetic.main.activity_walk_val.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class WalkValActivity : AppCompatActivity() {

    private val cal = Calendar.getInstance()
    private var _count = 0
    private val cloudUser = NCMBUser.getCurrentUser()
    private var cloudWalkObj = NCMBObject("walkValue")
    //private val _goalEdit: EditText? = null
    private var p_cloudUName = ""
    private var p_cloudUId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_val)

        val cloudUName = cloudUser.userName
        val cloudUId = cloudUser.objectId

        p_cloudUName = cloudUName
        p_cloudUId = cloudUId

        setGoal(false)
        updateGoalEdit()
        //_goalEdit!!.inputType = 0

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        calButton.setOnClickListener {
            DatePickerDialog(
                this@WalkValActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, HomeProgramActivity::class.java)
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
        button_decision.setOnClickListener { setGoal(true) }

    }

    private fun updateDateInView() {
        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        calTextView.text = sdf.format(cal.time)
    }

    private fun initButtons() {
        /*
        val buttonIds = intArrayOf(
            R.id.button_0,
            R.id.button_1,
            R.id.button_2,
            R.id.button_3,
            R.id.button_4,
            R.id.button_5,
            R.id.button_6,
            R.id.button_7,
            R.id.button_8,
            R.id.button_9)
        for (i in buttonIds.indices) {
            val btn = v.findViewById<Button>(buttonIds[i])
            btn.setOnClickListener {
                _count = _count * 10 + i
                updateGoalEdit()
            }
        }*/
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
        //val clear = v.findViewById<Button>(R.id.button_cancel)
        button_cancel.setOnClickListener {
            _count = 0
            updateGoalEdit()
        }
        //val decide = v.findViewById<Button>(R.id.button_decision)
        button_decision.setOnClickListener { setGoal(true) }
    }

    private fun updateGoalEdit() {
        goalEditText.setText(formatNumber(_count))
    }

    private fun setGoal(save: Boolean) {
        //val nextWeekStart = CalendarHelper.startDateOfNextWeek()
        //val strStart = GoalFragment.LABEL_FORMAT.format(nextWeekStart.time)
        //val strEnd = GoalFragment.LABEL_FORMAT.format(CalendarHelper.addDay(nextWeekStart, 6).time)
        //val title = String.format("%s〜%sの目標を設定してください", strStart, strEnd)
        //_titleTextView.setText(title)
        val steps = goalEditText.text.toString()
        val date = calTextView.text.toString()
        if (save) {
            //_db.setGoal(nextWeekStart, _count)
            //_goalTextView.setText(formatNumber(_count))
            setupViews(date, steps, p_cloudUName, p_cloudUId)
        } else {
            //val count: Int = _db.getGoal(nextWeekStart)
            //_goalTextView.setText(formatNumber(count))
            //_count = count
            updateGoalEdit()
        }
    }

    private fun formatNumber(num: Int): String? {
        val format = NumberFormat.getNumberInstance()
        return format.format(num.toLong())
    }

    /**
     * アラートダイアログ生成
     */
    private fun setupViews(date:String, steps:String, cloudUName:String, cloudUId:String) {
        // BuilderからAlertDialogを作成
        val dialog = AlertDialog.Builder(this)
            .setTitle("確認") // タイトル
            .setMessage("本当に記録してよろしいですか？") // メッセージ
            .setPositiveButton("OK") { dialog, which -> // OK
                saveStepsToCloud(date, steps, cloudUName, cloudUId) //データストアに4要素をあげる
            }
            .setNegativeButton("戻る", null)
            .create()
        // AlertDialogを表示
        dialog.show()
    }

    /**
     * 日記データをクラウドデータストアにアップロード
     */
    private fun saveStepsToCloud(date:String, steps:String, userName:String, userId:String){
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
