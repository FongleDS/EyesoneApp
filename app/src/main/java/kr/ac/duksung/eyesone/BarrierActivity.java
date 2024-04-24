package kr.ac.duksung.eyesone;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class BarrierActivity extends AppCompatActivity {
    ImageView voicerecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrier);
        voicerecoder = findViewById(R.id.voicerecoder);

        voicerecoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarrierActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}