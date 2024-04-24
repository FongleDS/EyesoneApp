package kr.ac.duksung.eyesone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

public class NeviActivity2 extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private EditText set_destination;
    private Button nevi_start;
    private Geocoder geocoder;
    private String currentAddress;
    private String destinationAddress;
    private double currentLat;
    private double currentLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevi2); // XML 파일 이름에 맞게 수정
        nevi_start = findViewById(R.id.nevi_start);

        //set_destination = findViewById(R.id.set_destination);
        geocoder = new Geocoder(this);
        Intent intent = getIntent();
        currentLat = intent.getDoubleExtra("CurrentLocationLat", 0);
        currentLng = intent.getDoubleExtra("CurrentLocationLng", 0);
        destinationAddress = intent.getStringExtra("Destination");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nevi_fragment);
        mapFragment.getMapAsync(this);

        nevi_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NeviActivity2.this, NeviStartActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng currentLocation = new LatLng(currentLat, currentLng);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("현재 위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));

        try {
            List<Address> addresses = geocoder.getFromLocationName(destinationAddress, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng destinationLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("목적지"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15));
                drawRoute(currentLocation, destinationLatLng); // 경로 그리기 메서드 호출
            } else {
                Toast.makeText(this, "주소를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "주소 검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        // Directions API 요청
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCRmidlm35d9b3axL3gnJpCgIu38XYSHug") // API 키 설정
                .build();

        DirectionsApiRequest req = DirectionsApi.newRequest(context)
                .mode(TravelMode.WALKING)    // 이동 수단: DRIVING, WALKING, BICYCLING 등
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude));

        req.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.routes != null && result.routes.length > 0) {
                            DirectionsRoute route = result.routes[0];
                            // 경로 추출
                            PolylineOptions lineOptions = new PolylineOptions();
                            List<com.google.maps.model.LatLng> path = route.overviewPolyline.decodePath();
                            for (com.google.maps.model.LatLng point : path) {
                                lineOptions.add(new LatLng(point.lat, point.lng));
                            }
                            lineOptions.width(10);
                            lineOptions.color(Color.RED);

                            mMap.addPolyline(lineOptions);  // 경로 그리기
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();  // 이 부분에 로그 추가
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NeviActivity2.this, "경로를 불러오는 데 실패했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

