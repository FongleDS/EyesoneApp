package kr.ac.duksung.eyesone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

public class ReviseBookmarkActivity extends AppCompatActivity {
    String FILENAME; // 파일명
    EditText edit1; // 출발지
    EditText edit2; // 목적지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_bookmark);
        edit1 = (EditText) findViewById(R.id.start); // EditText id를 받아옴
        edit2 = (EditText) findViewById(R.id.end);

        Intent intent = getIntent(); // MainActivity에서 전달된 값을 받아옴(=파일명 map@에서 번호인 @가 전달됨)
        int idx = intent.getIntExtra("idx", -1);
        if (idx != -1) { // 전달된 값이 있는 경우, 이는 파일을 수정해야하는 경우임
            try {
                FILENAME = "/data/data/com.example.navigation/files/map" + String.valueOf(idx);
                // 전달된 값으로 파일명의 경로를 설정
                FileInputStream rf = new FileInputStream(FILENAME); // 파일에 읽기 위해 엶
                StringBuffer sb = new StringBuffer();
                byte dataBuffer[] = new byte[rf.available()];
                rf.read(dataBuffer); // 바이트 단위로 값을 읽고
                sb.append(new String(dataBuffer)); // String으로 저장

                StringTokenizer st = new StringTokenizer(sb.toString(), "->");
                // "->"을 구분자로 출발지와 목적지를 EditText에 설정
                edit1.setText(st.nextToken());
                edit2.setText(st.nextToken());
                rf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Button okBtn = (Button) findViewById(R.id.addButton);
        okBtn.setOnClickListener(new View.OnClickListener() { // 수정완료 버튼 클릭 이벤트
            @Override
            public void onClick(View v) {
                try {
                    File df = new File(FILENAME);
                    df.delete();
                    FILENAME = "map" + FILENAME.substring(43);
                    // 파일명을 substring으로 경로에서 빼오고 숫자만 가져와서 map에 붙임

                    if (!edit1.getText().toString().equals("") && !edit2.getText().toString().equals("")) { // 값이 없으면 저장하지 않음
                        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                        // 파일에 쓰기 위해 엶
                        fos.write((edit1.getText().toString() + "->" + edit2.getText().toString()).getBytes());
                        // 출발지->목적지 형태로 내용을 저장
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }
    @Override
    public String[] fileList() { // 파일 목록을 반환하는 함수
        return super.fileList();
    }

}