package kr.ac.duksung.eyesone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TextActivity extends AppCompatActivity {
    private TextView textView;
    MyTTS tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_activity);

        textView = findViewById(R.id.textView);
        tts = new MyTTS(this, null);

        String url = "http://10.0.2.2:5000/upload";
        //String url = "http://192.168.0.148:5000/upload";

        new FetchDataTask().execute(url);
    }

    private class FetchDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                System.out.println("Hello");
                String jsonData = NetworkUtils.getServerData(urls[0]);
                JSONObject jsonObject = new JSONObject(jsonData);
                return jsonObject.getString("text");
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("Hello");
            super.onPostExecute(result);
            if (result != null) {
                // 유니코드 문자열을 정상적인 문자열로 변환하여 TextView에 표시
                textView.setText(unescapeUnicode(result));
                Toast.makeText(TextActivity.this, "speak start", Toast.LENGTH_SHORT).show();
                tts.speak(unescapeUnicode(result).toString());
                Toast.makeText(TextActivity.this, "speak Complete", Toast.LENGTH_SHORT).show();

            } else {
                // 에러 처리
                textView.setText("Error fetching data.");
            }
        }

        private String unescapeUnicode(String data) {
            StringBuilder decoded = new StringBuilder();
            for (int i = 0; i < data.length(); i++) {
                char ch = data.charAt(i);
                if (ch == '\\' && data.charAt(i + 1) == 'u') {
                    String hexStr = data.substring(i + 2, i + 6);
                    char unicodeChar = (char) Integer.parseInt(hexStr, 16);
                    decoded.append(unicodeChar);
                    i += 5; // 유니코드 이스케이프 시퀀스 넘김
                } else {
                    decoded.append(ch);
                }
            }
            return decoded.toString();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        tts.destroy();
    }

}