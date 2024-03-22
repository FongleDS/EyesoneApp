package kr.ac.duksung.eyesone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.appcompat.R;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button nevigation;
    Button detect;
    Button parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nevigation = findViewById(R.id.nevigation);
        detect = findViewById(R.id.detect);
        parent = findViewById(R.id.parent);

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BarrierActivity.class);
                startActivity(intent);
            }
        });
        nevigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NeviActivity.class);
                startActivity(intent);
            }
        });
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
            }
        });


    }
}