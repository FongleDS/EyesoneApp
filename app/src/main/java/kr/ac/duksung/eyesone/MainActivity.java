package kr.ac.duksung.eyesone;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button nevi;
    Button detect;
    Button parent;
    Button textreader;
    private MyTTS tts;
    private int clickCount;
    private boolean barClicks, navClicks, textClicks;
    private static final int DOUBLE_CLICK_TIME_DELTA = 5000;

    private Handler barHandler = new Handler();
    private Runnable barRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MainActivity.this, "Detect Timer expired, no second click detected.", Toast.LENGTH_SHORT).show();
            barClicks = true;
        }
    };

    private Handler navHandler = new Handler();
    private Runnable navRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MainActivity.this, "Navi Timer expired, no second click detected.", Toast.LENGTH_SHORT).show();
            navClicks = true;
        }
    };

    private Handler textHandler = new Handler();
    private Runnable textRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MainActivity.this, "Text Timer expired, no second click detected.", Toast.LENGTH_SHORT).show();
            textClicks = true;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nevi = findViewById(R.id.nevi);
        detect = findViewById(R.id.detect);
        //parent = findViewById(R.id.parent);
        textreader = findViewById(R.id.textreader);

        tts = new MyTTS(this, null);
        barClicks = true;
        navClicks = true;
        textClicks = true;

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barClicks) {
                    tts.speak("장애물 탐지");
                    Toast.makeText(MainActivity.this, "TTS 작동", Toast.LENGTH_SHORT).show();
                    barClicks = false;
                    barHandler.postDelayed(barRunnable, DOUBLE_CLICK_TIME_DELTA);
                    Toast.makeText(MainActivity.this, "Click again within 3 seconds to execute action.", Toast.LENGTH_SHORT).show();
                } else {
                    barHandler.removeCallbacks(barRunnable);
                    barClicks = true; // 클릭 상태 초기화
                    tts.speak("장애물 탐지 페이지로 넘어갑니다");
                    Intent intent = new Intent(getApplicationContext(), BarrierActivity.class);
                    startActivity(intent);
                }
            }
        });


        nevi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navClicks) {
                    tts.speak("네비게이션");
                    Toast.makeText(MainActivity.this, "TTS 작동", Toast.LENGTH_SHORT).show();
                    navClicks = false;
                    navHandler.postDelayed(navRunnable, DOUBLE_CLICK_TIME_DELTA);
                    // Toast.makeText(MainActivity.this, "Click again within 3 seconds to execute action.", Toast.LENGTH_SHORT).show();
                } else {
                    navHandler.removeCallbacks(navRunnable);
                    navClicks = true; // 클릭 상태 초기화
                    tts.speak("네비게이션 페이지로 넘어갑니다");
                    Intent intent = new Intent(getApplicationContext(), NeviActivity.class);
                    startActivity(intent);
                }
            }
        });
/*
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
            }
        });
*/


        textreader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textClicks) {
                    tts.speak("텍스트 읽어주기");
                    Toast.makeText(MainActivity.this, "TTS 작동", Toast.LENGTH_SHORT).show();
                    textClicks = false;
                    textHandler.postDelayed(textRunnable, DOUBLE_CLICK_TIME_DELTA);
                    // Toast.makeText(MainActivity.this, "Click again within 3 seconds to execute action.", Toast.LENGTH_SHORT).show();
                } else {
                    textHandler.removeCallbacks(textRunnable);
                    textClicks = true; // 클릭 상태 초기화
                    tts.speak("텍스트 읽어주기 페이지로 넘어갑니다");
                    Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.destroy();
        textHandler.removeCallbacks(textRunnable);
        navHandler.removeCallbacks(navRunnable);
        textHandler.removeCallbacks(textRunnable);
    }
}