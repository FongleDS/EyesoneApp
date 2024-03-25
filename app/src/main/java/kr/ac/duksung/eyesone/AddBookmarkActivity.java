package kr.ac.duksung.eyesone;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddBookmarkActivity extends AppCompatActivity {
    String FILENAME; // 파일명
    EditText edit1; // 출발지
    EditText edit2; // 목적지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bookmark);
        edit1 = (EditText) findViewById(R.id.start); // EditText id를 받아옴
        edit2 = (EditText) findViewById(R.id.end);

        Button okBtn = (Button) findViewById(R.id.addButton);
        okBtn.setOnClickListener(new View.OnClickListener() { // 추가하기 버튼 클릭 이벤트
            @Override
            public void onClick(View v) {
                try {
                    FILENAME = setFilename(); // setFilename()을 통해 파일명 설정
                    if (!edit1.getText().toString().equals("") && !edit2.getText().toString().equals("")) {
                        // 값이 없으면 저장하지 않음
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
    public String setFilename() { // 파일명 설정하는 함수
        int tmp = 0;
        int max = 0; // 있는 파일 명 중 가장 큰 번호 @ (map + @에서)
        if (fileList().length != 0) {
            for (int i = 0; i < fileList().length; i++) {
                tmp = Integer.parseInt(fileList()[i].substring(3)); // 파일명에서 숫자만 저장
                if (max < tmp) // 값을 하나씩 비교해서 최대값을 구함
                    max = tmp;
            }
        } else // 파일이 하나도 없으면 0번부터 시작
            max = -1;
        return "map" + String.valueOf(max + 1);
    }
}
