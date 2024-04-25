package kr.ac.duksung.eyesone;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.io.IOException;
import java.util.List;

import android.location.LocationListener;

public class NeviActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 101;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private TextView originText;
    private Button btn_setdes;
    private Geocoder geocoder;
    private ImageView voicerecoder;
    private EditText set_address_Text;
    private Location mCurrentLocation;
    private String mCurrentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevi);
        originText = findViewById(R.id.origin);
        btn_setdes = findViewById(R.id.btn_setdes);
        voicerecoder = findViewById(R.id.voicerecoder);
        set_address_Text = findViewById(R.id.set_address_Text);
        geocoder = new Geocoder(this);

        btn_setdes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destinationAddress = set_address_Text.getText().toString();
                Intent intent = new Intent(NeviActivity.this, NeviActivity2.class);
                intent.putExtra("CurrentLocationLat", mCurrentLocation.getLatitude());
                intent.putExtra("CurrentLocationLng", mCurrentLocation.getLongitude());
                intent.putExtra("CurrentAddress", mCurrentAddress);
                intent.putExtra("Destination", destinationAddress);
                startActivity(intent);
            }
        });

        voicerecoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NeviActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 위치 서비스 활성화 확인
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "위치 서비스가 비활성화되어 있습니다. 위치 서비스를 활성화해주세요.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Nevi);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
        } else {
            requestLocationUpdates();
        }

        geocoder = new Geocoder(this);
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 없는 경우, 사용자에게 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
        } else {
            // 권한이 이미 있는 경우, 위치 업데이트 시작
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        try {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateLocationUI(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        } catch (SecurityException e) {
            Toast.makeText(this, "위치 서비스 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여된 경우, 위치 업데이트 시작
                startLocationUpdates();
            } else {
                // 권한이 거부된 경우, 사용자에게 권한 필요성 설명
                Toast.makeText(this, "위치 정보 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateLocationUI(Location location) {
        if (location != null) {
            mCurrentLocation = location;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String addressLine = address.getAddressLine(0); // 첫 번째 주소 가져오기
                    originText.setText(addressLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            LatLng currentLocation = new LatLng(latitude, longitude);

            if (mMap != null) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("현재 위치"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}




  /*

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        directionsApi = retrofit.create(DirectionsApi.class);

        // Check and request location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set a default location to display on map
        LatLng defaultLocation = new LatLng(37, 127); // Example: Sydney
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
    }

    public void getDirections(View view) {
        EditText originEditText = findViewById(R.id.origin);
        EditText destinationEditText = findViewById(R.id.destination);

        String origin = originEditText.getText().toString().trim(); // Remove leading and trailing spaces
        String destination = destinationEditText.getText().toString().trim(); // Remove leading and trailing spaces

        if (origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(this, "출발지와 목적지를 입력하세요.", Toast.LENGTH_LONG).show();
        } else {
            fetchDirections(origin, destination);
        }
    }

    private void fetchDirections(String origin, String destination) {
        directionsApi.getDirections(origin, destination, getString(R.string.google_maps_key))
                .enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().routes.isEmpty()) {
                            List<LatLng> path = PolyUtil.decode(response.body().routes.get(0).overviewPolyline.points);
                            mMap.addPolyline(new PolylineOptions().addAll(path).width(10).color(android.graphics.Color.BLUE));
                        } else {
                            Toast.makeText(NeviActivity.this, "유효한 경로가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Toast.makeText(NeviActivity.this, "경로를 불러오는데 실패했습니다: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Directions API interface
    public interface DirectionsApi {
        @GET("maps/api/directions/json")
        Call<DirectionsResponse> getDirections(
                @Query("origin") String origin,
                @Query("destination") String destination,
                @Query("key") String apiKey
        );
    }

    // Classes to handle Directions API response
    class DirectionsResponse {
        List<Route> routes;
    }

    class Route {
        OverviewPolyline overviewPolyline;
    }

    class OverviewPolyline {
        String points;
    }
}

   */


