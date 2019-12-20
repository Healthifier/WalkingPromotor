package io.github.healthifier.walking_promoter;

import com.google.android.gms.maps.model.LatLng;

import io.github.healthifier.walking_promoter.models.DistanceCalculator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

@RunWith(RobolectricTestRunner.class)
public class MapHelperTest {
    @Test
    public void testDistance() throws Exception {
        LatLng tokyo = new LatLng(35.681382, 139.766084);
        LatLng osaka = new LatLng(34.693738, 135.502165);
        double distance = DistanceCalculator.distanceKilometer(tokyo, osaka);
        assertThat(distance, is(allOf(greaterThan(350.0), lessThan(450.0))));
    }
}
