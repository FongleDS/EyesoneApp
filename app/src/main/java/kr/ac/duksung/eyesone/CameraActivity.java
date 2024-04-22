package kr.ac.duksung.eyesone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.jetbrains.annotations.NotNull;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class CameraActivity extends AppCompatActivity {

    ImageView imageView;
    File file;
    Button button;
    private MyTTS tts;
    private static final int DOUBLE_CLICK_TIME_DELTA = 5000;
    private boolean Clicks;

    private android.os.Handler Handler = new Handler();
    private Runnable Runnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(CameraActivity.this, "Detect Timer expired, no second click detected.", Toast.LENGTH_SHORT).show();
            Clicks = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        tts = new MyTTS(this, null);

        File sdcard = getExternalFilesDir(null); // Private external directory
        file = new File(sdcard, "capture.jpg");

        imageView = findViewById(R.id.imageView);
        Clicks = true;

        button = findViewById(R.id.button);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Clicks) {
                    tts.speak("카메라 실행 버튼");
                    Toast.makeText(CameraActivity.this, "TTS 작동", Toast.LENGTH_SHORT).show();
                    Clicks = false;
                    Handler.postDelayed(Runnable, DOUBLE_CLICK_TIME_DELTA);
                } else {
                    Handler.removeCallbacks(Runnable);
                    Clicks = true;
                    tts.speak("카메라를 실행합니다");
                    if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        capture();
                    } else {
                        requestCameraPermission();
                    }
                }
            }

            /**
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 있을 경우 사진 촬영 시작
                    capture();
                } else {
                    // 권한이 없을 경우 권한 요청
                    requestCameraPermission();
                }
            }
            **/
        });

    }

    private static final int PERMISSION_REQUEST_CAMERA = 1;

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // 사용자에게 권한 요청의 필요성 설명
            new AlertDialog.Builder(this)
                    .setTitle("카메라 권한 필요")
                    .setMessage("이 앱은 카메라 권한이 필요합니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CameraActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    PERMISSION_REQUEST_CAMERA);
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capture();
            } else {
                Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void capture(){
        Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, 101);
    }

    /**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == Activity.RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            imageView.setImageBitmap(bitmap);
        }
    }
    **/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == Activity.RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            imageView.setImageBitmap(bitmap);

            uploadImage(file);
        }
    }


    private void uploadImage(File imageFile) {

        //String apiURL = "http://10.0.2.2:5000/upload";
        String apiURL = "http://172.20.26.98:5000/upload";
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("image/jpeg");
        RequestBody fileRequestBody = RequestBody.create(mediaType, imageFile);

        MultipartBody.Part bodyPart = MultipartBody.Part.createFormData("file", imageFile.getName(), fileRequestBody);

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(bodyPart)
                .build();

        Request request = new Request.Builder()
                .url(apiURL)
                .post(requestBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "Upload Success: " + responseData, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // 에러 처리
                    throw new IOException("Unexpected code " + response);
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

}