package io.github.healthifier.walking_promoter.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;

public class CustomBarChart extends BarChart {
    public CustomBarChart(Context context) {
        super(context);
    }

    public CustomBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new CustomBarChartRenderer(this, mAnimator, mViewPortHandler);
    }
}
