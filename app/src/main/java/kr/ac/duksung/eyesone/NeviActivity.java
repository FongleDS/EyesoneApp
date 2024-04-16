package kr.ac.duksung.eyesone;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class NeviActivity extends AppCompatActivity {
    String FILENAME; // 파일명
    private ListView m_ListView; // 리스트뷰
    private ArrayAdapter<String> m_Adapter; // 어댑터뷰
    ArrayList<String> values = new ArrayList<>(); // 리스트뷰에 담을 값 동적 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevi);

        m_ListView = findViewById(R.id.listview1); // 리스트뷰의 id를 받아옴
        registerForContextMenu(m_ListView); // 리스트뷰를 컨텍스트 메뉴에 등록
    }
    @Override
    protected void onResume() { // 어플이 onResum 될 때마다
        super.onResume();
        updateList(); // 리스트를 업데이트 해줌
    }
    private void updateList() { // 리스트를 업데이트 하는 메소드
        values.clear(); // 일단 리스트를 비움
        try {
            int tmp = 0;
            while (tmp < fileList().length) { // 리스트의 수 만큼 반복
                FILENAME = fileList()[tmp]; // 해당 파일명을 구함(map + @)
                FileInputStream fis = openFileInput(FILENAME); // 파일을 읽기용으로 열어서
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer); // 값을 읽어옴

                values.add(new String(buffer)); // 읽은 값을 리스트에 추가
                fis.close();
                tmp++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        m_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values); // 리스트로 어댑터 생성
        m_ListView = findViewById(R.id.listview1); // 리스트뷰 객체
        m_ListView.setAdapter(m_Adapter); // 어댑터 설정
        m_ListView.setOnItemClickListener(onClickListItem); // 리스트에 이벤트 발생 시 호출
    }

    public boolean onCreateOptionsMenu(Menu menu) { // add옵션이 있는 액션바
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        // 리스트뷰 이벤트 처리
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            StringTokenizer st = new StringTokenizer(m_Adapter.getItem(position), "->"); // 해당 아이템을 "->"로 구분
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.google.com/maps/dir/" + st.nextToken() + "/" + st.nextToken()));
            // "->" 기준으로 앞은 출발지 뒤는 목적지이므로 그에 맞게 암시적 인텐트로 구글맵을 엶
            if (intent != null) {
                if (intent.resolveActivity(getPackageManager()) != null) { // 인텐트 호출
                    startActivity(intent);
                }
            }
        }
    };
    public boolean onOptionsItemSelected(MenuItem item) { // 액션바가 선택된 경우
        switch (item.getItemId()) {
            case R.id.add: // add버튼이 추가하는 intent 호출
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // 플로팅 컨텍스트 메뉴 설정
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("메뉴");
        menu.add(0, 1, 0, "수정");
        menu.add(0, 2, 0, "삭제");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1: // 수정이 선택되는 경우
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("idx", Integer.parseInt(fileList()[info.position].substring(3)));
                // 해당 리스트의 위치에 파일번호를 int형으로 intent에 넘겨줌
                startActivity(intent); // intent 호출
                return true;
            case 2: // 삭제가 선택되는 경우
                File df = new File("/data/data/com.example.navigation/files/map" + fileList()[info.position].substring(3));
                // 파일 경로에 있는 파일을 찾아서 삭제
                df.delete();
                updateList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}