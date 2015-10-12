package vivz.slidenerd.agriculture.mydiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.recruit.RecruitListItem;
import vivz.slidenerd.agriculture.recruit.MissionItem;

public class MyDiaryDetailActivity extends ActionBarActivity {

    RecruitListItem i;
    TextView recruiter;
    TextView recruitDate;
    TextView submitDate;
    TextView mission;
    TextView colleagueList;
    MissionItem missionItem;
    TextView missionName;
    Button phoneNum;

    getRecruit getRecruit;

    String collegue;
    String date;
    String id;

    //sharedPreference 선언부
    public SharedPreferences setting;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiarydetail);


        // 초기화
        collegue="";
        date = "";
        id = "";

        // mianActivity에서 넘겨준 인텐트정보를 받는다.
        Intent intent = getIntent();
        Serializable item = intent.getSerializableExtra("item"); // 클래스를 넘길 때는 Serializable을 이용함
        // 다이어리에서 넘어오면 1 넘어옴

        i=(RecruitListItem)item;


        //sharedPreference로 전역 공유공간을 만듬
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();
        id = setting.getString("info_Id", "");
        Log.e("aaaaaa", id);

        // 미션 참가자 가져오기
        getRecruit = new getRecruit();
        getRecruit.execute("http://218.150.181.131/seo/getRecruitDetail.php?idrecruit=" + i.getIdRecruit());

        // 미션 이름
        missionName = (TextView) findViewById(R.id.missionName);
        missionName.setText(i.getMissionName());

        // 주최자
        recruiter =(TextView) findViewById(R.id.recruiter);
        recruiter.setText(i.getUserId());

        // 미션 날짜
        recruitDate = (TextView)findViewById(R.id.recruitDate);
        recruitDate.setText(i.getTermStart() + " - " + i.getTermEnd());

        // 신청 날짜
        submitDate = (TextView)findViewById(R.id.submitDate);


        // 미션 내용
        mission = (TextView)findViewById(R.id.mission);
        mission.setText(i.getRecruitContent());

        // 참가자
        colleagueList = (TextView)findViewById(R.id.colleague);

        // 전화걸기 버튼
        phoneNum = (Button) findViewById(R.id.phoneNum);

        // 파이널 변수로 만들지 않으면 리스너 함수내부에서 사용이 불가능해서 임시변수 하나 만듦
        final String phoneNumber = i.getPhoneNum();

        // 다이얼에 전화번호 올려놓기
        phoneNum.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData    // ACTION_CALL로 바꾸면
                        (Uri.parse("tel:" + phoneNumber.toString())));  // 전화 바로 걸린다.
            }
        });


    }



    // 체험 모집 중인 정보 가져오기
    public class getRecruit extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            String line = "";
            try {
                // 텍스트 연결 url 설정
                URL url = new URL(urls[0]);
                // 이미지 url

                // URL 페이지 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    Log.e("tag", "setUseCaches is false");
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line);

                        }

                        br.close();
                    }
                    conn.disconnect();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();


        }

        protected void onPostExecute(String str) {
            // JSON 구문을 파싱해서 JSONArray 객체를 생성
            try {

                JSONArray jAr = new JSONArray(str); // doInBackground 에서 받아온 문자열을 JSONArray 객체로 생성

                for (int i = 0; i < jAr.length(); i++) {  // JSON 객체를 하나씩 추출한다.
                    JSONObject getCollegu = jAr.getJSONObject(i);

                    collegue+=getCollegu.getString("UserId") + "\n";

                    if(id.compareTo(getCollegu.getString("UserId"))==0)
                        date=getCollegu.getString("submitDate");
                }
                colleagueList.setText(collegue);
                submitDate.setText(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
;
        }
    }

}
