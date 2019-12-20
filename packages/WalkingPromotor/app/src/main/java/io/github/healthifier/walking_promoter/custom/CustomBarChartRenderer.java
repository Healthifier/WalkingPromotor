package io.github.healthifier.walking_promoter.custom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomBarChartRenderer extends BarChartRenderer {
    public CustomBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mShadowPaint.setColor(dataSet.getBarShadowColor());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setBarSpace(dataSet.getBarSpace());
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        // if multiple colors
        if (dataSet.getColors().size() > 1) {
            for (int j = 0; j < buffer.size(); j += 4) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if (mChart.isDrawBarShadowEnabled()) {
                    c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                        buffer.buffer[j + 2],
                        mViewPortHandler.contentBottom(), mShadowPaint);
                }

                // Set the color for the currently drawn value.
                // If the id is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);
            }
        } else {
            int color = dataSet.getColor();
            mRenderPaint.setColor(color);
            if (Color.alpha(color) == 255) {
                mRenderPaint.setStyle(Paint.Style.FILL);
            } else {
                mRenderPaint.setStyle(Paint.Style.STROKE);
                mRenderPaint.setStrokeWidth(6f);
                mRenderPaint.setColor(Color.rgb(Color.red(color), Color.green(color), Color.blue(color)));
            }

            for (int j = 0; j < buffer.size(); j += 4) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;

                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;

                if (mChart.isDrawBarShadowEnabled()) {
                    c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                        buffer.buffer[j + 2],
                        mViewPortHandler.contentBottom(), mShadowPaint);
                }

                c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], mRenderPaint);
            }
        }
    }
}
