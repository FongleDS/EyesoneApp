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
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.ExifInterface;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

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
    ImageView voicerecoder;
    File file;
    Button button;
    private MyTTS tts;
    private static final int DOUBLE_CLICK_TIME_DELTA = 5000;
    private boolean Clicks;

    private android.os.Handler Handler = new Handler();
    private Runnable Runnable = new Runnable() {
        @Override
        public void run() {
            Clicks = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        tts = new MyTTS(this, null);

        File sdcard = getExternalFilesDir(null);
        file = new File(sdcard, "capture.jpg");

        imageView = findViewById(R.id.imageView);
        voicerecoder = findViewById(R.id.voicerecoder);
        Clicks = true;

        button = findViewById(R.id.button);
        Button button = findViewById(R.id.button);

        voicerecoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Clicks) {
                    tts.speak("카메라 실행 버튼");
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
        });

    }

    private static final int PERMISSION_REQUEST_CAMERA = 1;

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8; // 이미지 크기를 줄이기 위해 샘플링 사이즈 설정
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            try {
                bitmap = rotateImageIfRequired(bitmap, Uri.fromFile(file)); // 이미지 회전 처리
                file = saveBitmapToFile(bitmap, file); // 회전된 이미지를 파일로 저장
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (file != null) {
                imageView.setImageBitmap(bitmap); // 이미지뷰에 회전된 이미지 표시
                uploadImage(file); // 서버에 이미지 파일 업로드
            }
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 101 && resultCode == Activity.RESULT_OK){
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 8;
//            // Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//            // imageView.setImageBitmap(bitmap);
//
//            uploadImage(file);
//        }
//    }


    private void uploadImage(File imageFile) {

        // String apiURL = "http://10.0.2.2:5000/upload";
        // String apiURL = "http://192.168.137.1:5000/upload";
        // String apiURL = "http://192.168.0.142:8000/upload";
        String apiURL = "http://172.30.1.1:8000/upload";

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
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        final String textResult = jsonObject.getString("text");
                        final String imageBase64 = jsonObject.getString("image");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = findViewById(R.id.text_view);
                                textView.setText(textResult);

                                byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                ImageView imageView = findViewById(R.id.imageView);
                                imageView.setImageBitmap(decodedByte);

                                tts.speak("이것은 "+textResult + "입니다");
                            }
                        });
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CameraActivity.this, "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(e);
                        // Toast.makeText(CameraActivity.this, "Request Failed: " + e.getMessage(), // Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private File saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}