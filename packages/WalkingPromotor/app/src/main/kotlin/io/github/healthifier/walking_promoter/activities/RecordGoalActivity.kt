package io.github.healthifier.walking_promoter.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.github.healthifier.walking_promoter.R
import io.github.healthifier.walking_promoter.models.CalendarHelper
import io.github.healthifier.walking_promoter.models.DatabaseHandler
import kotlinx.android.synthetic.main.activity_record_goal.*
import kotlinx.android.synthetic.main.activity_record_goal.backButton
import kotlinx.android.synthetic.main.activity_record_goal.button_0
import kotlinx.android.synthetic.main.activity_record_goal.button_1
import kotlinx.android.synthetic.main.activity_record_goal.button_2
import kotlinx.android.synthetic.main.activity_record_goal.button_3
import kotlinx.android.synthetic.main.activity_record_goal.button_4
import kotlinx.android.synthetic.main.activity_record_goal.button_5
import kotlinx.android.synthetic.main.activity_record_goal.button_6
import kotlinx.android.synthetic.main.activity_record_goal.button_7
import kotlinx.android.synthetic.main.activity_record_goal.button_8
import kotlinx.android.synthetic.main.activity_record_goal.button_9
import kotlinx.android.synthetic.main.activity_record_goal.button_cancel
import kotlinx.android.synthetic.main.activity_record_goal.button_decision
import kotlinx.android.synthetic.main.activity_record_step.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class RecordGoalActivity : AppCompatActivity() {

    private val LABEL_FORMAT:SimpleDateFormat = SimpleDateFormat("M/d")

    private var dbHandler: DatabaseHandler? = null
    private var _count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_goal)

        dbHandler = DatabaseHandler(this)

        val check = intent.getStringExtra("CHECK")
        val lastWeekStart = CalendarHelper.startDateOfLastWeek()
        val lastGoal = dbHandler!!.getGoal(lastWeekStart)
        lastGoalTextView.text = formatNumber(lastGoal)
        val lastActual = dbHandler!!.getMyAvgStepCount(lastWeekStart, CalendarHelper.addDay(lastWeekStart, 6))
        lastActualTextView.text = formatNumber((lastActual + 0.5).toInt())



        setGoal(false)
        updateGoalEdit()


        /*
        val goalFragment = GoalFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment, goalFragment)*/

        backButton.setOnClickListener {
            if(check == "1001"){
                val intent = Intent(this, StepProgramActivity::class.java)
                startActivity(intent)
            }else if(check == "1002"){
                val intent = Intent(this, ClassProgramActivity::class.java)
                startActivity(intent)
            }
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

    private fun updateGoalEdit() {
        goalEditText.setText(formatNumber(_count))
    }

    private fun setGoal(save: Boolean) {
        val nextWeekStart = CalendarHelper.startDateOfNextWeek()

        val strStart: String = LABEL_FORMAT.format(nextWeekStart.time)
        val strEnd = LABEL_FORMAT.format(CalendarHelper.addDay(nextWeekStart, 6).time)
        val title = String.format("%s〜%sの目標を設定してください", strStart, strEnd)
        titleTextView2.text = title
        if (save) {
            setupViews(nextWeekStart, _count)
        } else {
            updateGoalEdit()
        }
    }

    private fun formatNumber(num: Int): String{
        val format = NumberFormat.getNumberInstance()
        return format.format(num.toLong())
    }

    private fun setupViews(nextWeekStart: Calendar, steps:Int) {
        val view: View = layoutInflater.inflate(R.layout.custom_dialog_check, null)
        val dialogTitle: TextView = view.findViewById(R.id.TextView_dialog_title)
        dialogTitle.text = "この記録でよろしいですか？"
        val message: TextView = view.findViewById(R.id.TextView_dialog_message)
        message.text = "週：${LABEL_FORMAT.format(nextWeekStart.time)}   目標歩数：${steps}"
        val button1: Button = view.findViewById(R.id.Button_dialog_positive)
        button1.text = "やっぱりやめる"
        val button2: Button = view.findViewById(R.id.Button_dialog_negative)
        button2.text = "記録する"

        val dialog = AlertDialog.Builder(this).setView(view).create()
        dialog.show()

        // AlertDialogのサイズ調整
        val lp = dialog.window?.attributes
        lp?.width = (resources.displayMetrics.widthPixels * 0.7).toInt()
        dialog.window?.attributes = lp

        button1.setOnClickListener {
            dialog.dismiss()
        }

        button2.setOnClickListener {
            val view2: View = layoutInflater.inflate(R.layout.dialog_progress, null)
            val dialog2 = AlertDialog.Builder(this).setCancelable(false).setView(view2).create()
            dialog2.show()
            GlobalScope.launch {
                saveGoal(nextWeekStart, steps) //DBに保存
            }
            GlobalScope.launch {
                delay(1000)
                dialog.dismiss()
                dialog2.dismiss()
                newDialog()
            }
        }
    }

    private fun saveGoal(cal: Calendar, steps: Int){
        dbHandler?.setGoal(cal, steps)
    }

    private fun newDialog(){
        runOnUiThread{
            val view3: View = layoutInflater.inflate(R.layout.custom_dialog_explain, null)
            val dialog3 = AlertDialog.Builder(this@RecordGoalActivity).setCancelable(false).setView(view3).create()
            val textDialogTitle: TextView = view3.findViewById(R.id.TextView_dialog_title)
            textDialogTitle.text = "目標が記録されました！"
            val textDialogMessage: TextView = view3.findViewById(R.id.TextView_dialog_message)
            textDialogMessage.text = "この目標に向かって頑張っていきましょう！"
            val buttonDialog: Button = view3.findViewById(R.id.Button_dialog_positive)
            buttonDialog.text = "この画面を閉じる"
            dialog3.show()

            val lp = dialog3.window?.attributes
            lp?.width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            dialog3.window?.attributes = lp

            buttonDialog.setOnClickListener {
                dialog3.dismiss()
                val intent = Intent(this@RecordGoalActivity, StepProgramActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
