package io.github.healthifier.walking_promoter.fragments;

//import android.app.Fragment;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.models.CellUtil;
import io.github.healthifier.walking_promoter.models.Database;
import io.github.healthifier.walking_promoter.models.SquareStepRecord;

public class SquareStepTestFragment extends Fragment {
    private Database _db;
    private View _view;
    private int _stepId;
    private int _cellCount;
    private int _correctCount;
    private long _startTime;
    private ImageView _imageView;
    private TextView _cellTextView;
    private TextView _resultTextView;
    private final int DELAY_FOR_SHOWING_RESULT = 500;
    private StepProcessor _processor;

    public SquareStepTestFragment() {
    }

    private void restart() {
        _correctCount = 0;
        _cellCount = 0;
        _processor = new StepProcessor(_stepId);
        CellUtil.resetButtons(_view, true);
        _imageView.setImageBitmap(null);
        _cellTextView.setText(_processor.getNextChar() + "番目のマスを押してください。");
        _resultTextView.setText("");
        _startTime = System.currentTimeMillis();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_step_test, container, false);
        _db = new Database(getActivity());

        _imageView = _view.findViewById(R.id.image_view);
        _cellTextView = _view.findViewById(R.id.cellTextView);
        _resultTextView = _view.findViewById(R.id.resultTextView);
        final Handler handler = new Handler();

        Bundle bundle = getArguments();
        _stepId = bundle.getInt("stepId", 0);

        restart();

        for (int line = 0; line < 6; line++) {
            for (int pos = 0; pos < 4; pos++) {
                int buttonId = CellUtil.getButtonId(_view, line, pos);
                final Button button = _view.findViewById(buttonId);
                final int finalLine = line;
                final int finalPos = pos;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CellUtil.resetButtons(_view, false);
                        CellUtil.updateCell(_view, finalLine, finalPos, false, _processor.getNextChar());
                        final boolean correct = finalLine == _processor.getNextLine() && finalPos == _processor.getNextPos();
                        if (correct) {
                            _imageView.setImageResource(R.drawable.ok);
                            _correctCount++;
                        } else {
                            _imageView.setImageResource(R.drawable.ng);
                        }
                        _cellTextView.setText("");
                        _cellCount++;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                _imageView.setImageBitmap(null);
                                if (correct) {
                                    if (!_processor.findNextCellLocation()) {
                                        int milliseconds = (int) (System.currentTimeMillis() - _startTime);
                                        int correctPercentage = (int) (100.0 * _correctCount / _cellCount + 0.5);
                                        boolean passed = correctPercentage == 100;
                                        String prefix = passed ? "試験に合格しました！" : "残念、もう一度がんばろう";
                                        String suffix = passed ? "次のステップに挑戦してください！" : "正解率100%を目指してください！";
                                        _resultTextView.setText(String.format("%s\n\n正解率：%d%%\nかかった時間：%.1f秒\n\n%s", prefix, correctPercentage, milliseconds / 1000.0, suffix));
                                        SquareStepRecord record = _db.getSquareStepRecord(_stepId);
                                        if (record == null) {
                                            record = new SquareStepRecord(_stepId);
                                        }
                                        if (correctPercentage > record.correctPercentage || (correctPercentage == record.correctPercentage && milliseconds < record.solvedMilliseconds)) {
                                            _db.setSquareStepRecord(new SquareStepRecord(_stepId, record.exampleChecked, correctPercentage, milliseconds));
                                        }
                                        CellUtil.resetButtons(_view, false);
                                        return;
                                    }
                                    _cellTextView.setText(_processor.getNextChar() + "番目のマスを押してください。");
                                } else {
                                    _cellTextView.setText("もう一度、正しく" + _processor.getNextChar() + "番目のマスを押してください。");
                                }
                                CellUtil.resetButtons(_view, true);
                            }
                        }, DELAY_FOR_SHOWING_RESULT);
                    }
                });
            }
        }

        Button retryButton = _view.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                restart();
            }
        });

        Button backButton = _view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                getActivity().onBackPressed();
            }
        });

        return _view;
    }
}
