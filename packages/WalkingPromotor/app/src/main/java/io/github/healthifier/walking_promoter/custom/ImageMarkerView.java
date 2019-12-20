package io.github.healthifier.walking_promoter.custom;

import android.content.Context;
import android.widget.ImageView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import io.github.healthifier.walking_promoter.R;

import java.util.List;

public class ImageMarkerView extends MarkerView {

    private final int _dataSetIndex;
    private ImageView imageView;
    private List<Boolean> resultList;

    public ImageMarkerView(Context context) {
        super(context, R.layout.image_view_wrapper);
        imageView = findViewById(R.id.imageView);
        _dataSetIndex = 1;
    }

    public void setResultList(List<Boolean> resultList) {
        this.resultList = resultList;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int resourceId = 0;
        if (_dataSetIndex == -1 || _dataSetIndex == highlight.getDataSetIndex()) {
            Boolean achieved = resultList.get(e.getXIndex());
            if (achieved != null) {
                if (achieved.booleanValue()) {
                    resourceId = R.drawable.cat_success;
                } else {
                    resourceId = R.drawable.cat_failure;
                }
            }
        }
        imageView.setImageResource(resourceId);
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight() - 30;
    }

}
