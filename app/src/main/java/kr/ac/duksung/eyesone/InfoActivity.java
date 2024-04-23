package kr.ac.duksung.eyesone;

import android.content.Intent;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class InfoActivity extends AppCompatActivity {
    private long lastClickTime = 0; // 마지막 클릭 시간
    private int clickTime = 0;
    private final int TIMES_REQUIRED = 2; // 총 필요한 클릭 횟수
    private final int TIME_TIMEOUT = 2000;  // 마지막 클릭후 제한시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        findViewById(R.id.voicerecoder).setOnClickListener(v -> touchContinuously());
    }

    private void touchContinuously() {
        if (SystemClock.elapsedRealtime() - lastClickTime < TIME_TIMEOUT) {
            clickTime++;
        } else {
            clickTime = 1;
        }
        lastClickTime = SystemClock.elapsedRealtime();

        if (clickTime == TIMES_REQUIRED) {
            // TODO 연속 클릭 완료 후 메소드 구현
            Toast.makeText(this, "연속 클릭 완료", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}