package io.github.healthifier.walking_promoter.fragments;

//import android.app.Fragment;
import androidx.fragment.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.models.CellUtil;
import io.github.healthifier.walking_promoter.models.Database;
import io.github.healthifier.walking_promoter.models.SquareStepRecord;

public class SquareStepMainFragment extends Fragment {
    private Database _db;
    private StepProcessor _processor;

    public SquareStepMainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_step_main, container, false);
        _db = new Database(getActivity());

        Bundle bundle = getArguments();
        final int stepId = bundle.getInt("stepId", 0);

        CellUtil.resetButtons(v, false);
        _processor = new StepProcessor(stepId);
        do {
            CellUtil.updateCell(v, _processor.getNextLine(), _processor.getNextPos(), false, _processor.getNextChar());
        } while (_processor.findNextCellLocation());

        TextView exampleTextView = v.findViewById(R.id.exampleTextView);
        SquareStepRecord record = _db.getSquareStepRecord(stepId);
        if (record != null && record.exampleChecked) {
            exampleTextView.setText("確認が完了済みです！");
            exampleTextView.setTextColor(Color.BLUE);
        } else {
            exampleTextView.setText("まだ未確認です。");
            exampleTextView.setTextColor(Color.RED);
        }

        TextView testMessageTextView = v.findViewById(R.id.testMessageTextView);
        TextView testResultTextView = v.findViewById(R.id.testResultTextView);
        if (record != null && record.correctPercentage != 0) {
            if (record.correctPercentage == 100) {
                testMessageTextView.setText("試験に合格しました！");
                testMessageTextView.setTextColor(Color.BLUE);
            } else {
                testMessageTextView.setText("まだ合格していません");
                testMessageTextView.setTextColor(Color.RED);
            }
            testResultTextView.setText(String.format("正解率：%d%%\nかかった時間：%.1f秒", record.correctPercentage, record.solvedMilliseconds / 1000.0));
        } else {
            testMessageTextView.setText("まだ試験に挑戦したことがありません。");
            testMessageTextView.setTextColor(Color.RED);
            testResultTextView.setText("");
        }

        Button backButton = v.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Button exampleButton = v.findViewById(R.id.exampleButton);
        exampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("stepId", stepId);

                Fragment fragment = new SquareStepExampleFragment();
                fragment.setArguments(bundle);

                /**
                 * 都竹用に変更
                 */
                /*
                getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .addToBackStack(null)
                    .commit();*/
                getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });

        Button testButton = v.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("stepId", stepId);

                Fragment fragment = new SquareStepTestFragment();
                fragment.setArguments(bundle);

                /**
                 * 都竹用に変更
                 */
                /*
                getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .addToBackStack(null)
                    .commit();*/
                getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });

        return v;
    }
}
