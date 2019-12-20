package io.github.healthifier.walking_promoter.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.models.ChartHelper;
import io.github.healthifier.walking_promoter.models.Database;

public class DailyGraphFragment extends Fragment {
    private BarChart _chart;
    private Database _db;

    public DailyGraphFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daily, container, false);

        _db = new Database(getActivity());

        _chart = v.findViewById(R.id.dailyChart);
        setupChart();

        return v;
    }

    private void setupChart() {
        _chart.setData(ChartHelper.generateDailyStepCounts(_db));

        // TODO: 基本設定 (https://github.com/PhilJay/MPAndroidChart/wiki/Getting-Started)
        _chart.setDescription("");
        _chart.setDrawGridBackground(false);

        // TODO: バーチャート専用の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/Specific-chart-settings)
        _chart.setDrawBarShadow(false);
        _chart.setDrawValueAboveBar(true);

        // TODO: タッチなど操作の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/Interaction-with-the-Chart)
        _chart.setScaleEnabled(false);

        // TODO: マーカーの調整 (https://github.com/PhilJay/MPAndroidChart/wiki/MarkerView)
//        _chart.setMarkerView(new TextMarkerView(getActivity(), R.layout.marker_view));

        // TODO: 表示範囲の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/Modifying-the-Viewport)
        _chart.setVisibleXRangeMaximum(7);
        _chart.moveViewToX(Integer.MAX_VALUE);

        // TODO: 凡例の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/Legend)
        Legend l = _chart.getLegend();
        l.setEnabled(false); // データが一種類なので凡例は不要
//        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
//        l.setYOffset(0f);
//        l.setYEntrySpace(0f);
//        l.setTextSize(8f);

        // TODO: X軸の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/XAxis-(XLabels))
        // See also: https://github.com/PhilJay/MPAndroidChart/wiki/The-Axis
        XAxis xAxis = _chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(ChartHelper.LABEL_SIZE_X);
        xAxis.setLabelsToSkip(0);

        // TODO: Y軸の調整 (https://github.com/PhilJay/MPAndroidChart/wiki/YAxis-(YLabels))
        // See also: https://github.com/PhilJay/MPAndroidChart/wiki/The-Axis
        YAxis leftAxis = _chart.getAxisLeft();
//        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
//        leftAxis.setSpaceTop(30f);
        leftAxis.setTextSize(ChartHelper.LABEL_SIZE_Y);
        _chart.getAxisRight().setEnabled(false);

        // refresh
        _chart.invalidate();
    }
}
