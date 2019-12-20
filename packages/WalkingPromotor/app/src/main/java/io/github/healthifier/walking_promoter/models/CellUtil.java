package io.github.healthifier.walking_promoter.models;

import android.graphics.Color;
import androidx.core.content.res.ResourcesCompat;
import android.view.View;
import android.widget.Button;

import io.github.healthifier.walking_promoter.R;

public class CellUtil {
    public static final int MAX_LINE = 6;
    public static final int MAX_POS = 4;

    public static int getButtonId(View v, int iLine, int iPos) {
        return v.getResources().getIdentifier("button_" + (iLine * 4 + iPos + 1), "id", v.getContext().getPackageName());
    }

    public static String getLine(String[] squareStepLines, int iLine) {
        return squareStepLines[squareStepLines.length - (iLine % squareStepLines.length) - 1];
    }

    public static void resetButtons(View v, boolean enabled) {
        for (int iLine = 0; iLine < MAX_LINE; iLine++) {
            for (int iPos = 0; iPos < MAX_POS; iPos++) {
                CellUtil.updateCell(v, iLine, iPos, enabled, '0');
            }
        }
    }

    public static void updateCell(View v, int iLine, int iPos, boolean enabled, char ch) {
        int buttonId = CellUtil.getButtonId(v, iLine, iPos);
        if (ch == '0') {
            CellUtil.changeToEmptyCell(v, buttonId, enabled);
        } else {
            CellUtil.changeToNumberCell(v, buttonId, enabled, ch);
        }
    }

    public static void changeToEmptyCell(View v, int buttonId, boolean enabled) {
        Button button = v.findViewById(buttonId);
        button.setText("");
        button.setEnabled(enabled);
        button.setBackground(ResourcesCompat.getDrawable(v.getResources(), R.drawable.empty_cell, null));
        button.setTextColor(Color.WHITE);
    }

    public static void changeToNumberCell(View v, int buttonId, boolean enabled, char ch) {
        Button button = v.findViewById(buttonId);
        button.setText(String.valueOf(ch));
        button.setEnabled(enabled);
        button.setBackground(ResourcesCompat.getDrawable(v.getResources(), R.drawable.number_cell, null));
        button.setTextColor(Color.WHITE);
    }
}
