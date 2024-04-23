package kr.ac.duksung.eyesone;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {


    Button nevi;
    Button detect;
    Button parent;
    Button textreader;
    private MyTTS tts;
    private boolean barClicks, navClicks, textClicks;
    private static final int DOUBLE_CLICK_TIME_DELTA = 5000;

    private Handler barHandler = new Handler();
    private Runnable barRunnable = new Runnable() {
        @Override
        public void run() {
            //Toast.makeText(MainActivity.this, "Detect Timer expired, no second click detected.", Toast.LENGTH_SHORT).show();
            barClicks = true;
        }
    };

    private Handler navHandler = new Handler();
    private Runnable navRunnable = new Runnable() {
        @Override
        public void run() {
            //Toast.makeText(MainActivity.this, "Navi Timer expired, no second click detected.", Toast.LENGTH_SHORT).show();
            navClicks = true;
        }
    };

    private Handler textHandler = new Handler();
    private Runnable textRunnable = new Runnable() {
        @Override
        public void run() {
            // Toast.makeText(MainActivity.this, "Text Timer expired, no second click detected.", Toast.LENGTH_SHORT).show();
            textClicks = true;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout detectLayout = findViewById(R.id.detect);
        LinearLayout neviLayout = findViewById(R.id.nevi);
        LinearLayout textreaderLayout = findViewById(R.id.textreader);

        tts = new MyTTS(this, null);
        barClicks = true;
        navClicks = true;
        textClicks = true;

        detectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barClicks) {
                    tts.speak("장애물 탐지");
                    // Toast.makeText(MainActivity.this, "TTS 작동", Toast.LENGTH_SHORT).show();
                    barClicks = false;
                    barHandler.postDelayed(barRunnable, DOUBLE_CLICK_TIME_DELTA);
                } else {
                    barHandler.removeCallbacks(barRunnable);
                    barClicks = true;
                    tts.speak("장애물 탐지 페이지로 넘어갑니다");
                    Intent intent = new Intent(getApplicationContext(), BarrierActivity.class);
                    startActivity(intent);
                }
            }
        });


        neviLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navClicks) {
                    tts.speak("네비게이션");
                    // Toast.makeText(MainActivity.this, "TTS 작동", Toast.LENGTH_SHORT).show();
                    navClicks = false;
                    navHandler.postDelayed(navRunnable, DOUBLE_CLICK_TIME_DELTA);
                } else {
                    navHandler.removeCallbacks(navRunnable);
                    navClicks = true;
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


        textreaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textClicks) {
                    tts.speak("텍스트 읽어주기");
                    // Toast.makeText(MainActivity.this, "TTS 작동", Toast.LENGTH_SHORT).show();
                    textClicks = false;
                    textHandler.postDelayed(textRunnable, DOUBLE_CLICK_TIME_DELTA);
                } else {
                    textHandler.removeCallbacks(textRunnable);
                    textClicks = true;
                    tts.speak("텍스트 읽어주기 페이지로 넘어갑니다");
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
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