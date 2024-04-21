package kr.ac.duksung.eyesone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TextActivity extends AppCompatActivity {

    private boolean Clicks;
    private MyTTS tts;
    private static final int DOUBLE_CLICK_TIME_DELTA = 5000;



    private Handler Handler = new Handler();
    private Runnable Runnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(TextActivity.this, "Detect Timer expired, no second click detected.", Toast.LENGTH_SHORT).show();
            Clicks = true;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_activity);

        tts = new MyTTS(this, null);
        Clicks = true;

        Button captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Clicks) {
                    tts.speak("카메라 실행 버튼");
                    Toast.makeText(TextActivity.this, "TTS 작동", Toast.LENGTH_SHORT).show();
                    Clicks = false;
                    Handler.postDelayed(Runnable, DOUBLE_CLICK_TIME_DELTA);
                } else {
                    Handler.removeCallbacks(Runnable);
                    Clicks = true;
                    tts.speak("카메라를 실행합니다");
                    Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}