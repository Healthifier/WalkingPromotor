package io.github.healthifier.walking_promoter.activities;

import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonGeometry;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.geojson.GeoJsonPoint;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBObject;
import com.nifcloud.mbaas.core.NCMBQuery;
import io.github.healthifier.walking_promoter.App;
import io.github.healthifier.walking_promoter.R;
import io.github.healthifier.walking_promoter.models.DistanceCalculator;

import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TokaidoMapFragmentActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter{

    private static final int LINE_COLOR = Color.BLUE;
    private static final int LOOP_LINE_COLOR = Color.argb(64, 0, 0, 255);

    private GoogleMap _map;
    private HashMap<String, Integer> markerToImgId = new HashMap<>();
    private ArrayList<Polyline> polylinesToRemove = new ArrayList<>();
    private long walkedStep;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokaido_map_fragment);

        //DatabaseHandler db = new DatabaseHandler(this);
        //walkedStep = db.getMyStepCount();

        NCMBQuery<NCMBObject> query = new NCMBQuery<>("walkValue");
        try {
            List<NCMBObject> list = query.find();
            if(list != null){
                int steps = 0;
                for(int i = 0; i < list.size(); i++){
                    NCMBObject object = list.get(i);
                    int value = object.getInt("value");
                    steps = steps + value;
                }
                walkedStep = steps;
            }else{
                Log.d("DEBUG", "list is null");
            }
        } catch (NCMBException e) {
            e.printStackTrace();
        }

        Log.d("DEBUG", String.valueOf(walkedStep));
        textView = findViewById(R.id.textView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }else{
            Log.d("DEBUG", "mapFragment is null");
        }

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), StepProgramActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        App.logDebug("TokaidoMapFragment.onMapReady");
        _map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.setInfoWindowAdapter(this);

        try {
            GeoJsonLayer layer = new GeoJsonLayer(googleMap, R.raw.toukaidou, this.getApplicationContext());
            GeoJsonLineStringStyle lineStringStyle = layer.getDefaultLineStringStyle();
            lineStringStyle.setColor(Color.BLUE);

            LatLng tokyo = new LatLng(35.681298, 139.766247);
            _map.addMarker(new MarkerOptions().position(tokyo));
            _map.moveCamera(CameraUpdateFactory.newLatLng(tokyo));

            layer.addLayerToMap();
            putObjects(layer);
        } catch (IOException e) {
            Log.d("error", String.valueOf(e));
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d("error", String.valueOf(e));
            e.printStackTrace();
        }
    }

    private void putObjects(GeoJsonLayer layer) {
        // Tooltipのため、消して入れなおす
        ArrayList<GeoJsonFeature> list = new ArrayList<>();
        for (GeoJsonFeature feature : layer.getFeatures()) {
            GeoJsonGeometry geo = feature.getGeometry();
            if (geo instanceof GeoJsonPoint) {
                list.add(feature);
            }
        }

        markerToImgId.clear();
        ArrayList<LatLng> positions = new ArrayList<>();
        for (GeoJsonFeature feature : list) {
            String title = feature.getProperty("name") + " / " + feature.getProperty("title");
            layer.removeFeature(feature);
            GeoJsonPoint point = (GeoJsonPoint) feature.getGeometry();
            Marker marker = _map.addMarker(new MarkerOptions()
                .position(point.getCoordinates())
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("flag", 130, 130)))
            );

            String fileName = "tokaido_" + feature.getProperty("filename");
            String resName = fileName.substring(0, fileName.lastIndexOf('.'));
            int resId = getResources().getIdentifier(resName, "drawable", this.getPackageName());
            if (resId != 0) {
                markerToImgId.put(title, resId);
            } else {
                App.logError("リソースが見つかりません: " + resName);
            }
            int id = Integer.parseInt(feature.getProperty("id"));
            arraySet(positions, id, point.getCoordinates());
        }

        App.xAssert(markerToImgId.size() == 55);
        positions.removeAll(Collections.singleton(null));
        double distance = DistanceCalculator.calculateKilometer(walkedStep); /* km */

        float zoom; // 大きいほど拡大される
        if (distance > 100) {
            zoom = 9;
        } else if (distance > 50) {
            zoom = 9.5f;
        } else {
            zoom = 10;
        }

        LatLng point = drawPolyline(distance, positions, 0);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(point, zoom);

        _map.moveCamera(cu);

        _map.addMarker(new MarkerOptions()
            .position(point)
            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("samurai", 83, 145)))
        );

        _map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
    }

    private static <T> void arraySet(ArrayList<T> arrayList, int index, T value) {
        int n = index - arrayList.size() + 1;
        for (int i = 0; i < n; i++) {
            arrayList.add(null);
        }
        arrayList.set(index, value);
    }

    /* https://stackoverflow.com/a/32187898 */
    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return getMarkerView(marker);
    }

    private View getMarkerView(Marker marker) {
        String title = marker.getTitle();
        View view = this.getLayoutInflater().inflate(R.layout.map_marker, null);
        // タイトル設定
        TextView titleView = view.findViewById(R.id.textView);
        titleView.setText(title);
        // 画像設定
        Integer resId = markerToImgId.get(title);
        if (resId != null) {
            ImageView img = view.findViewById(R.id.imageView);
            img.setImageResource(resId);
        }
        return view;
    }

    private LatLng drawPolyline(final double totalDistance, ArrayList<LatLng> positions, int loop) {
        double distance = totalDistance;
        PolylineOptions options = new PolylineOptions();
        options.geodesic(true);
        options.add(positions.get(0));
        Log.d("positions.get(0)", String.valueOf(positions.get(0)));
        for (int i = 1; i < positions.size(); i++) {
            LatLng a = positions.get(i - 1);
            LatLng b = positions.get(i);
            double d = DistanceCalculator.distanceKilometer(a, b);
            if (distance > d) {
                distance -= d;
                options.add(b);
            } else {
                // 途中まで線を引く
                double rate = distance / d;
                double lat = rate * (b.latitude - a.latitude) + a.latitude;
                double lng = rate * (b.longitude - a.longitude) + a.longitude;
                options.add(new LatLng(lat, lng));
                distance = 0;
                Log.d("break", "Break");
                break;
            }
            Log.d("i", String.valueOf(i));
        }
        if (distance == 0) {
            options.color(LINE_COLOR);
            Polyline line = _map.addPolyline(options);
            polylinesToRemove.add(line);
            updateLabel(loop);
            return line.getPoints().get(line.getPoints().size() - 1);
        } else {
            options.color(LOOP_LINE_COLOR);
            Polyline line = _map.addPolyline(options);
            polylinesToRemove.add(line);

            double loopDistance = (totalDistance - distance);
            int n = (int) (totalDistance / loopDistance);
            Log.d("loopDistance", String.valueOf(loopDistance));
            Log.d("total/loop", String.valueOf(n));
            return drawPolyline(totalDistance - n * loopDistance, positions, n);
        }
    }

    private void updateLabel(int loop) {
        double distance = DistanceCalculator.calculateKilometer(walkedStep); /* km */
        NumberFormat format = NumberFormat.getNumberInstance();
        String label = String.format("%d週目 総歩数: %s歩（%skm）", loop + 1, format.format(walkedStep), format.format(distance));
        textView.setText(label);
    }
}
