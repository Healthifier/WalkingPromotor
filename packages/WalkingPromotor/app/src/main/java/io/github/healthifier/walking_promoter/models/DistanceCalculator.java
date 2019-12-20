package io.github.healthifier.walking_promoter.models;

import com.google.android.gms.maps.model.LatLng;

public class DistanceCalculator {
    private static final double METER_PER_STEP = 0.65; // 65cm (http://www.hql.jp/project/funcdb2000/doutai/10m/10mkekka2.htm)
    private static final double EARTH_RADIUS_KILOMETER = 6371;
    public static final double US_CROSS_DISTANCE_KILOMETER = 4000;

    private DistanceCalculator() {
    }

    public static double calculateKilometer(long stepCount) {
        return stepCount * METER_PER_STEP / 1000;
    }

    /**
     * 二点間の距離（キロメートル）を返します。
     */
    public static double distanceKilometer(LatLng a, LatLng b) {
        double lat = Math.toRadians(a.latitude - b.latitude);
        double lng = Math.toRadians(a.longitude - b.longitude);
        double A = Math.sin(lat / 2) * Math.sin(lat / 2) + Math.cos(Math.toRadians(b.latitude)) * Math.cos(Math.toRadians(a.latitude)) * Math.sin(lng / 2) * Math.sin(lng / 2);
        double C = 2 * Math.atan2(Math.sqrt(A), Math.sqrt(1 - A));
        return EARTH_RADIUS_KILOMETER * C;
    }
}
