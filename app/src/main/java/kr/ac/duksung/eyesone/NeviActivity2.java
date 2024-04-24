package kr.ac.duksung.eyesone;

import android.content.Intent;
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

public class NeviActivity2 extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private EditText set_destination;
    private Button nevi_start;
    private Geocoder geocoder;
    private String currentAddress;
    private String destinationAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevi2); // XML 파일 이름에 맞게 수정
        nevi_start = findViewById(R.id.nevi_start);

        //set_destination = findViewById(R.id.set_destination);
        geocoder = new Geocoder(this);
        currentAddress = getIntent().getStringExtra("CurrentAddress");
        destinationAddress = getIntent().getStringExtra("Destination");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nevi_fragment);
        mapFragment.getMapAsync(this);

        nevi_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NeviActivity2.this, NeviStartActivity.class);
                startActivity(intent);
            }
        });


        /*
        nevi_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = set_destination.getText().toString();
                try {
                    List<Address> addresses = geocoder.getFromLocationName(location, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    } else {
                        Toast.makeText(NeviActivity2.this, "주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(NeviActivity2.this, "주소 검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

         */


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // 예시 현재 위치 (실제 앱에서는 GPS 위치 사용)
        LatLng currentLocation = new LatLng(37.5665, 126.9780); // Seoul City Hall
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("현재 위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

        // 목적지 위치 표시
        try {
            List<Address> addresses = geocoder.getFromLocationName(destinationAddress, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng destinationLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("목적지"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15));
            } else {
                Toast.makeText(this, "주소를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "주소 검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }

    }
}

