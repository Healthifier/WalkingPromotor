package io.github.healthifier.walking_promoter.fragments;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.models.CalendarHelper;
import io.github.healthifier.walking_promoter.models.ChartHelper;
import io.github.healthifier.walking_promoter.models.Database;
import io.github.healthifier.walking_promoter.models.DistanceCalculator;
import io.github.healthifier.walking_promoter.models.TextHelper;

import java.io.IOException;
import java.io.InputStream;

public class TotalGraphFragment extends Fragment {
    private LineChart _chart;
    private Database _db;

    private static int MARKER_WIDTH = 48;
    private static int MARKER_HEIGHT = 32;

    public TotalGraphFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_total, container, false);

        _db = new Database(getActivity());

        _chart = v.findViewById(R.id.everyoneDailyChart);
        updateChartData();
        setChartSettings();

        TextView textView1 = v.findViewById(R.id.textView1);
        TextView textView2 = v.findViewById(R.id.textView2);
        int totalStepCount = _db.getEveryoneStepCount(CalendarHelper.startDateOfExperiment(), CalendarHelper.getToday());
        double distance = DistanceCalculator.calculateKilometer(totalStepCount);
        double ratio = distance / DistanceCalculator.US_CROSS_DISTANCE_KILOMETER;
        textView1.setText(String.format("みんなで %s歩、%.0f kmを踏破！", TextHelper.floorJpUnit(totalStepCount), distance));
        textView2.setText(String.format("アメリカ大陸横断 %.2f 回分", ratio));

        LinearLayout layout = v.findViewById(R.id.globeLayout);

        try (InputStream input = getResources().openRawResource(R.raw.us_flag48)) {
            int nGlobes = (int) (ratio * MARKER_WIDTH);
            while (nGlobes > 0) {
                BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(input, true);
                Rect rect = new Rect(0, 0, Math.min(nGlobes, MARKER_WIDTH), MARKER_HEIGHT);
                Bitmap bitmap = decoder.decodeRegion(rect, null);
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageBitmap(bitmap);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMarginStart(2);
                layout.addView(imageView, lp);
                nGlobes -= MARKER_WIDTH;
            }
        } catch (IOException e) {
        }
        return v;
    }

    private void setChartSettings() {
        // TODO: 基本設定 (https://github.com/PhilJay/MPAndroidChart/wiki/Getting-Started)
        _chart.setDescription("");
//        _chart.setBackgroundColor(Color.WHITE);
        _chart.setDrawGridBackground(false);

        // TODO: タッチなど操作の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/Interaction-with-the-Chart)
        _chart.setScaleEnabled(false);

        // TODO: マーカーの調整 (https://github.com/PhilJay/MPAndroidChart/wiki/MarkerView)
//        _chart.setMarkerView(new TextMarkerView(getActivity(), R.layout.marker_view));

        // TODO: 表示範囲の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/Modifying-the-Viewport)
        _chart.setVisibleXRangeMaximum(100);
        _chart.moveViewToX(Integer.MAX_VALUE);

        // TODO: 凡例の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/Legend)
        Legend l = _chart.getLegend();
        l.setEnabled(false); // 凡例は一つなので不要
//        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
//        l.setYOffset(0f);
//        l.setYEntrySpace(0f);
//        l.setTextSize(8f);

        // TODO: X軸の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/XAxis-(XLabels))
        // See also: https://github.com/PhilJay/MPAndroidChart/wiki/The-Axis
        XAxis xAxis = _chart.getXAxis();
        xAxis.setTextSize(ChartHelper.LABEL_SIZE_X);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // TODO: Y軸の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/YAxis-(YLabels))
        // See also: https://github.com/PhilJay/MPAndroidChart/wiki/The-Axis
        _chart.getAxisLeft().setEnabled(false);

        YAxis rightAxis = _chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTextSize(ChartHelper.LABEL_SIZE_Y);
    }

    private void updateChartData() {
        _chart.setData(ChartHelper.generateEveryoneStepCounts(_db));
        _chart.invalidate();
    }
}
