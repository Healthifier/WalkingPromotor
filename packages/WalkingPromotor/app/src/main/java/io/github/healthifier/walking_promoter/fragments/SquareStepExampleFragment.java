package io.github.healthifier.walking_promoter.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.models.CellUtil;
import io.github.healthifier.walking_promoter.models.Database;
import io.github.healthifier.walking_promoter.models.SquareStepRecord;

public class SquareStepExampleFragment extends Fragment {
    private Database _db;
    private View _view;
    private int _stepId;
    private StepProcessor _processor;
    private int _lastLine;
    private int _lastPos;

    public SquareStepExampleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_step_example, container, false);
        _db = new Database(getActivity());

        final TextView resultTextView = _view.findViewById(R.id.resultTextView);
        resultTextView.setVisibility(View.INVISIBLE);

        Bundle bundle = getArguments();
        _stepId = bundle.getInt("stepId", 0);

        CellUtil.resetButtons(_view, false);
        _processor = new StepProcessor(_stepId);

        final int DELAY = 1000;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CellUtil.updateCell(_view, _lastLine, _lastPos, false, '0');
                _lastLine = _processor.getNextLine();
                _lastPos = _processor.getNextPos();
                CellUtil.updateCell(_view, _lastLine, _lastPos, false, _processor.getNextChar());
                if (_processor.findNextCellLocation()) {
                    handler.postDelayed(this, DELAY);
                } else {
                    resultTextView.setVisibility(View.VISIBLE);
                    _db.setSquareStepRecord(new SquareStepRecord(_stepId, true, 0, 0));
                }
            }
        }, DELAY);

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
