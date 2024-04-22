package kr.ac.duksung.eyesone;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class NeviActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private DirectionsApi directionsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevi);

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


