package io.github.healthifier.walking_promoter.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.models.CalendarHelper;
import io.github.healthifier.walking_promoter.models.Database;
import io.github.healthifier.walking_promoter.models.DatabaseHandler;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GoalFragment extends Fragment {
    private static final SimpleDateFormat LABEL_FORMAT = new SimpleDateFormat("M/d");

    private DatabaseHandler _db;
    private int _count;
    private EditText _goalEdit;
    private TextView _goalTextView, _titleTextView;

    public GoalFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_goal, container, false);

        _db = new DatabaseHandler(getActivity());

        _goalEdit = v.findViewById(R.id.goalEditText);
        _goalEdit.setInputType(0);

        _titleTextView = v.findViewById(R.id.titleTextView);
        _goalTextView = v.findViewById(R.id.goalTextView);

        final TextView lastGoalTextView = v.findViewById(R.id.lastGoalTextView);
        final TextView lastActualTextView = v.findViewById(R.id.lastActualTextView);

        Calendar lastWeekStart = CalendarHelper.startDateOfLastWeek();
        int lastGoal = _db.getGoal(lastWeekStart);
        lastGoalTextView.setText(formatNumber(lastGoal));
        double lastActual = _db.getMyAvgStepCount(lastWeekStart, CalendarHelper.addDay(lastWeekStart, 6));
        lastActualTextView.setText(formatNumber((int) (lastActual + 0.5)));

        initButtons(v);
        updateGoalEdit();
        setGoal(false);
        return v;
    }

    private void initButtons(View v) {
        int[] buttonIds = {
            R.id.button_0,
            R.id.button_1,
            R.id.button_2,
            R.id.button_3,
            R.id.button_4,
            R.id.button_5,
            R.id.button_6,
            R.id.button_7,
            R.id.button_8,
            R.id.button_9,
        };
        for (int i = 0; i < buttonIds.length; i++) {
            final int number = i;
            Button btn = v.findViewById(buttonIds[i]);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _count = _count * 10 + number;
                    updateGoalEdit();
                }
            });
        }

        Button clear = v.findViewById(R.id.button_cancel);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _count = 0;
                updateGoalEdit();
            }
        });

        Button decide = v.findViewById(R.id.button_decision);
        decide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGoal(true);
                //Toast toast = Toast.makeText(this, "保存しました", Toast.LENGTH_SHORT);
                //toast.show();
            }
        });
    }

    private void updateGoalEdit() {
        _goalEdit.setText(formatNumber(_count));
    }

    private void setGoal(boolean save) {
        Calendar nextWeekStart = CalendarHelper.startDateOfNextWeek();

        String strStart = LABEL_FORMAT.format(nextWeekStart.getTime());
        String strEnd = LABEL_FORMAT.format(CalendarHelper.addDay(nextWeekStart, 6).getTime());
        String title = String.format("%s〜%sの目標を設定してください", strStart, strEnd);
        _titleTextView.setText(title);

        if (save) {
            _db.setGoal(nextWeekStart, _count);
            _goalTextView.setText(formatNumber(_count));
        } else {
            int count = _db.getGoal(nextWeekStart);
            _goalTextView.setText(formatNumber(count));
            _count = count;
            updateGoalEdit();
        }
    }

    private String formatNumber(int num) {
        NumberFormat format = NumberFormat.getNumberInstance();
        return format.format(num);
    }
}
