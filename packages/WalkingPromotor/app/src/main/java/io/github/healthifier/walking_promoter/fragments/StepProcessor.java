package io.github.healthifier.walking_promoter.fragments;

import io.github.healthifier.walking_promoter.models.CellUtil;
import io.github.healthifier.walking_promoter.models.SquareStepStore;

public class StepProcessor {
    private final int _stepId;
    private final int _maxChar;
    private int _nextStartLine;
    private int _nextChar;
    private int _nextLine;
    private int _nextPos;
    private int _lineOffset;
    private boolean _isEven;

    public StepProcessor(int stepId) {
        int maxChar = '0';
        final String[] squareStepLines = SquareStepStore.SquareSteps[stepId];
        for (String line : squareStepLines) {
            for (int i = 0; i < CellUtil.MAX_POS; i++) {
                maxChar = Math.max(maxChar, line.charAt(i));
            }
        }
        _stepId = stepId;
        _maxChar = maxChar;
        _nextChar = '0';
        _isEven = false;
        findNextCellLocation();
    }

    public int getNextLine() {
        return _nextLine;
    }

    public int getNextPos() {
        return _nextPos;
    }

    public char getNextChar() {
        return (char) _nextChar;
    }

    public boolean findNextCellLocation() {
        String[] squareStepLines = SquareStepStore.SquareSteps[_stepId];
        if (_nextChar >= _maxChar) {
            _nextChar = '1';
            _nextStartLine += squareStepLines.length;
            if (SquareStepStore.hasMinusOneLineOffset(_stepId)) {
                _lineOffset++;
            }
            if (_nextStartLine - _lineOffset >= CellUtil.MAX_LINE) {
                return false;
            }
            _isEven = !_isEven;
        } else {
            _nextChar++;
        }
        return updateNextLineAndPos(squareStepLines);
    }

    private boolean updateNextLineAndPos(String[] squareStepLines) {
        int maxLine = Math.min(_nextStartLine - _lineOffset + squareStepLines.length, CellUtil.MAX_LINE);
        for (int iLine = _nextStartLine - _lineOffset; iLine < maxLine; iLine++) {
            String line = CellUtil.getLine(squareStepLines, iLine + _lineOffset);
            if (_isEven && SquareStepStore.isMirrored(_stepId)) {
                StringBuffer buf = new StringBuffer(line.replaceAll("0", ""));
                for (int i = 0; i < line.length() && line.charAt(i) == '0'; i++) {
                    buf.append('0');
                }
                buf.reverse();
                for (int i = line.length() - 1; i >= 0; i--) {
                    buf.append('0');
                }
                line = buf.toString();
            }
            int iPos = line.indexOf(_nextChar);
            if (iPos != -1) {
                _nextLine = iLine;
                _nextPos = iPos;
                return true;
            }
        }
        return false;
    }
}
