package kr.ac.duksung.eyesone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;


public class NeviActivity2 extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private EditText set_destination;
    private Button nevi_start;
    private Geocoder geocoder;
    private String currentAddress;
    private String destinationAddress;
    private double currentLat;
    private double currentLng;
    private LocationManager locationManager;
    TextView walking;
    TextView driving;
    TextView bicycling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevi2);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        nevi_start = findViewById(R.id.nevi_start);
        walking = findViewById(R.id.walking);

        //set_destination = findViewById(R.id.set_destination);
        geocoder = new Geocoder(this);
        Intent intent = getIntent();
        currentLat = intent.getDoubleExtra("CurrentLocationLat", 0);
        currentLng = intent.getDoubleExtra("CurrentLocationLng", 0);
        destinationAddress = intent.getStringExtra("Destination");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nevi_fragment);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this, Looper.getMainLooper());
        }

        nevi_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    LatLng currentLocation = new LatLng(currentLat, currentLng);
                    updateDirections(currentLocation);
                } else {
                    Toast.makeText(NeviActivity2.this, "지도가 준비되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    /*

    @Override
    public void onLocationChanged(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        updateDirections(currentLocation);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Optional to implement
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Optional to implement
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Optional to implement
    }

     */

    private void updateDirections(LatLng currentLocation) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCRmidlm35d9b3axL3gnJpCgIu38XYSHug")
                .build();

        DirectionsApiRequest req = DirectionsApi.newRequest(context)
                .mode(TravelMode.DRIVING)
                .origin(new com.google.maps.model.LatLng(currentLocation.latitude, currentLocation.longitude))
                .destination(destinationAddress)
                .language("ko");  // 한국어로 설정

        req.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                if (result.routes != null && result.routes.length > 0) {
                    DirectionsRoute route = result.routes[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (DirectionsLeg leg : route.legs) {
                                for (DirectionsStep step : leg.steps) {
                                    System.out.println(step.distance.humanReadable + " 미터 앞에서 " + step.htmlInstructions);
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable e) {
                runOnUiThread(() -> Toast.makeText(NeviActivity2.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우, 위치 업데이트 시작
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
                }
            } else {
                // 권한이 거부된 경우, 유저에게 권한 필요성 설명
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
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
                            long totalDuration = 0;

                            // 경로 추출
                            PolylineOptions lineOptions = new PolylineOptions();
                            List<com.google.maps.model.LatLng> path = route.overviewPolyline.decodePath();
                            for (DirectionsLeg leg : route.legs){
                                totalDuration += leg.duration.inSeconds;
                            }

                            for (com.google.maps.model.LatLng point : path) {
                                lineOptions.add(new LatLng(point.lat, point.lng));
                            }
                            lineOptions.width(10);
                            lineOptions.color(Color.RED);

                            mMap.addPolyline(lineOptions);  // 경로 그리기

                            walking.setText("목적지: " + destinationAddress + "\n예상시간: " + (totalDuration / 60) + " minutes");

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