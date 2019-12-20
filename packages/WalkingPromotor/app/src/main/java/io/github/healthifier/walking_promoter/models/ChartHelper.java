package io.github.healthifier.walking_promoter.models;

import android.graphics.Color;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import io.github.healthifier.walking_promoter.custom.ImageMarkerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartHelper {

    public static final float LABEL_SIZE_X = 26f;
    public static final float LABEL_SIZE_Y = 26f;
    public static final float LABEL_SIZE_LEGEND = 26f;
    public static final float LABEL_SIZE_VALUE = 24f;

    private static final SimpleDateFormat LABEL_FORMAT = new SimpleDateFormat("M/d");
    private static final int GOAL_COLOR = Color.argb(254, 196, 0, 0);
    private static final int ACTUAL_COLOR = Color.rgb(25, 25, 112);
    private static final int LINE_COLOR = ACTUAL_COLOR;

    private ChartHelper() {
    }

    public static void generateWeeklyStepCountsAndGoals(Database db, BarChart chart, @Nullable ImageMarkerView marker) {
        List<Calendar> weeklyCalendars = CalendarHelper.createWeeklyList(CalendarHelper.startDateOfExperiment(), CalendarHelper.getToday());

        List<String> labels = new ArrayList<>(weeklyCalendars.size());
        ArrayList<BarEntry> goalValues = new ArrayList<>(weeklyCalendars.size());
        ArrayList<BarEntry> actualValues = new ArrayList<>(weeklyCalendars.size());
        ArrayList<Boolean> isPassed = new ArrayList<>(weeklyCalendars.size());

        for (int index = 0; index < weeklyCalendars.size(); index++) {
            Calendar start = weeklyCalendars.get(index);
            Calendar inclusiveEnd = CalendarHelper.addDay(start, 6);
            // ラベル
            String from = LABEL_FORMAT.format(start.getTime());
            String to = LABEL_FORMAT.format(inclusiveEnd.getTime());
            labels.add(from + "～" + to);
            // 目標
            int goalCount = db.getGoal(weeklyCalendars.get(index));
            if (goalCount > 0) {
                goalValues.add(new BarEntry(goalCount, index));
            }
            // 実際
            double actualCount = db.getMyAvgStepCount(start, inclusiveEnd);
            if (actualCount > 0) {
                actualValues.add(new BarEntry((float) actualCount, index));
            }
            // marker
            if (actualCount > 0 && goalCount > 0) {
                isPassed.add(actualCount >= goalCount);
            } else {
                isPassed.add(null);
            }
        }

        BarDataSet goalDataSet = new BarDataSet(goalValues, "目標値");
        goalDataSet.setColor(GOAL_COLOR);
        goalDataSet.setValueTextSize(LABEL_SIZE_VALUE);
        BarDataSet actualDataSet = new BarDataSet(actualValues, "実績値");
        actualDataSet.setColor(ACTUAL_COLOR);
        actualDataSet.setValueTextSize(LABEL_SIZE_VALUE);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(goalDataSet);
        dataSets.add(actualDataSet);

        BarData barData = new BarData(labels, dataSets);

        // add space between the dataset groups in percent of bar-width
        barData.setGroupSpace(80f);
        chart.setData(barData);
        if (marker != null) {
            marker.setResultList(isPassed);
        }
    }

    public static BarData generateDailyStepCounts(Database db) {
        List<Calendar> calendars = CalendarHelper.createDailyList(CalendarHelper.startDateOfExperiment(), CalendarHelper.getToday());

        List<String> labels = new ArrayList<>(calendars.size());
        ArrayList<BarEntry> actualValues = new ArrayList<>();

        for (int index = 0; index < calendars.size(); index++) {
            Calendar start = calendars.get(index);

            int value = db.getMyStepCount(start);
            actualValues.add(new BarEntry(value, index));

            labels.add(LABEL_FORMAT.format(start.getTime()));
        }

        BarDataSet actualDataSet = new BarDataSet(actualValues, "歩数");
        actualDataSet.setColor(ACTUAL_COLOR);
        actualDataSet.setValueTextSize(LABEL_SIZE_VALUE);

        BarData barData = new BarData(labels, actualDataSet);
        // add space between the dataset groups in percent of bar-width
        barData.setGroupSpace(80f);
        return barData;
    }

    public static LineData generateEveryoneStepCounts(Database db) {
        Calendar startDateOfExperiment = CalendarHelper.startDateOfExperiment();
        List<Calendar> calendars = CalendarHelper.createWeeklyList(startDateOfExperiment, CalendarHelper.getToday());

        List<String> labels = new ArrayList<>(calendars.size());
        ArrayList<Entry> totalValues = new ArrayList<>();

        for (int index = 0; index < calendars.size(); index++) {
            Calendar date = calendars.get(index);

            String dateStr = LABEL_FORMAT.format(date.getTime());
            labels.add(dateStr);

            int stepCount = db.getEveryoneStepCount(startDateOfExperiment, CalendarHelper.addDay(date, -1));
            totalValues.add(new Entry(stepCount, index));
        }

        // TODO: 線グラフの線の見た目を調整 (https://github.com/PhilJay/MPAndroidChart/wiki/DataSet-classes-in-detail)
        // See also: https://github.com/PhilJay/MPAndroidChart/wiki/The-DataSet-class
        LineDataSet set = new LineDataSet(totalValues, "みんなの総歩数");
        set.setColor(LINE_COLOR);
        set.setLineWidth(4f);
        set.setCircleColor(LINE_COLOR);
        set.setFillColor(LINE_COLOR);
        set.setDrawCubic(false);
        set.setDrawValues(false); // 値を表示しない
//        set.setValueTextSize(LABEL_SIZE_VALUE);
//        set.setValueTextColor(Color.BLACK);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData lineData = new LineData(labels);
        lineData.addDataSet(set);
        return lineData;
    }
}
