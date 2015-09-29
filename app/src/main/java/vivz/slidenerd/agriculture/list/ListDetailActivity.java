package vivz.slidenerd.agriculture.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import vivz.slidenerd.agriculture.R;



import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class ListDetailActivity extends ActionBarActivity {
    /*
       TextView vilageName;
       TextView vilageAddr;
       TextView vilageAddrInfo;
       TextView mbphone;
       TextView mbphoneInfo;
       TextView vilageHmpgEnnc;
       TextView vilageHmpgUrl;
       String line;
   */
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    Item i;

    TextView vilageHomepage;
    TextView vilageNameDown;
    View btnView;
    Button call;
    Button backButton;
    Button myDiary;
    Button vod;
    phpUp recruitTask;
    phpDown task;

    WebView main2Web;
    WebView thumb;


    String vodUrls;
    //phpDown phpJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        btnView=(View)findViewById(R.id.btnView);

        // 제스처를 사용하기 위해 미리 선언.
        gestureDetector = new GestureDetector(this, new SwipeGestureDetector(){
            @Override
            void onUpSwipe()
            {
                btnView.setVisibility(View.VISIBLE);
            }
            void onDownSwipe()
            {
                btnView.setVisibility(View.GONE);
            }

        });
        gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };


        // mianActivity에서 넘겨준 인텐트정보를 받는다.
        Intent intent = getIntent();
        Serializable item = intent.getSerializableExtra("item"); // 클래스를 넘길 때는 Serializable을 이용함

        i = (Item)item;


        backButton  = (Button)findViewById(R.id.listDeail_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        vilageNameDown = (TextView)findViewById(R.id.vilageNameDown); // 마을 이름
        vilageNameDown.setText(i.getName());  // Main에서 가져온 마을 이름

        vilageHomepage = (TextView)findViewById(R.id.vilageHomepage); // 마을 홈페이지
        vilageHomepage.setText(i.getVilageHmpgUrl());  // 가져온 마을 홈페이지
        Linkify.addLinks(vilageHomepage, Linkify.WEB_URLS);  // 마을 홈페이지 url 링크 설정

        // 전화 걸기 버튼
        call = (Button)findViewById(R.id.call);

        // 파이널 변수로 만들지 않으면 리스너 함수내부에서 사용이 불가능해서 임시변수 하나 만듦
        final String phoneNumber = i.getPrcafsManMoblphon();

        // 다이얼에 전화번호 올려놓기
        call.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData    // ACTION_CALL로 바꾸면
                        (Uri.parse("tel:" + phoneNumber.toString())));  // 전화 바로 걸린다.
            }
        });

        myDiary= (Button) findViewById(R.id.btn_myDiary);
        myDiary.setOnClickListener(mClickListener);

        vod=(Button) findViewById(R.id.btn_vod);
        vod.setOnClickListener(mClickListener);

        thumb = (WebView)findViewById(R.id.thumb);

        // 웹뷰 설정
        thumb.setVerticalScrollBarEnabled(false);
        thumb.setVerticalScrollbarOverlay(false);
        thumb.setHorizontalScrollBarEnabled(false);
        thumb.setHorizontalScrollbarOverlay(false);
        thumb.setFocusableInTouchMode(false);
        thumb.setHorizontalScrollBarEnabled(false);
        thumb.setVerticalScrollBarEnabled(false);

        thumb.setInitialScale(100);
        thumb.setFocusable(false);

        Log.e("zzzzz", "sdfsdfdsf");
        Log.e("zzzzz", i.getThumbUrl());
        if(thumb != null)
        {
            thumb.loadDataWithBaseURL(null, creHtmlBody("http://www.welchon.com" + i.getThumbUrl()), "text/html", "utf-8", null);

        }

/*
        vilageName = (TextView)findViewById(R.id.txt1); // 마을 이름
        vilageName.setText(i.getName());  // Main에서 가져온 마을 이름

        vilageAddr = (TextView)findViewById(R.id.vilageAddr2); // 주소
        vilageAddrInfo = (TextView)findViewById(R.id.vilageAddrInfo); // 주소 내용
        vilageAddrInfo.setText(i.getAddr());

        mbphone = (TextView)findViewById(R.id.phone); // 폰번호
        mbphoneInfo = (TextView)findViewById(R.id.phoneInfo); // 폰번호 내용
        mbphoneInfo.setText(i.getPrcafsManMoblphon());

        vilageHmpgEnnc = (TextView)findViewById(R.id.hmpgUrl); // 마을 홈페이지
        vilageHmpgUrl = (TextView)findViewById(R.id.hmpgUrlInfo); // 마을 홈페이지 내용
        vilageHmpgUrl.setText(i.getVilageHmpgUrl());
        Linkify.addLinks(vilageHmpgUrl, Linkify.WEB_URLS);  // 마을 홈페이지 url 링크 설정
*/

        main2Web = (WebView)findViewById(R.id.main2Web);

        //웹뷰의 글씨들 크기 조정해줌. good;
        main2Web.getSettings().setDefaultFontSize(40);

        // 웹뷰 내용이 스마트폰 크기에 맞춰지도록 세팅
        main2Web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        main2Web.getSettings().setLoadWithOverviewMode(true);
        main2Web.getSettings().setUseWideViewPort(true);
        main2Web.getSettings().setJavaScriptEnabled(true);


        main2Web.loadUrl("http://218.150.181.131/seo/infomation.php?tableName=" + i.getTableName() + "&vilageId=" + i.getVilageId());



        // 제스처 등록

        main2Web.setOnTouchListener(gestureListener);
        thumb.setOnTouchListener(gestureListener);
        vilageHomepage.setOnTouchListener(gestureListener);
        vilageNameDown.setOnTouchListener(gestureListener);


        // 보류
        //phpJson = new phpDown();
        //phpJson.execute("http://218.150.181.131/seo/infomation.php?vilageName="+vilageName);

        task=new phpDown();
        Log.e("zzzzzzzz","Agriculture!");
        task.execute("http://218.150.181.131/seo/getUrl.php?vilageId=" + i.getVilageId() + "");
        Log.e("zzzzzzzz", "Agriculture1!");
    }
    public  String creHtmlBody(String imagUrl){
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");    //중앙정렬
        sb.append("<img  width='100%' height='100%'  src=\"" + imagUrl+"\">"); //가득차게 나옴
        sb.append("</BODY>");
        sb.append("</HTML>");
        Log.e("zzzzz", "z");
        return sb.toString();
    }

    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {

            switch (v.getId())
            {
                case R.id.btn_myDiary:

                    try {
                        Log.e("regist", "정보입력!");
                        // 입력한 정보를 Item 객체에 담는다.

                        // 입력한 정보들을 php에 get방식으로 보낸다.
                        recruitTask = new phpUp();

                        recruitTask.execute("http://218.150.181.131/seo/insert_myDiary.php?userId=321kj&" + i.toString());
                        Log.e("regist", i.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("URLEncoder", "PHP params Encoder error");
                    }

                    break;
                case R.id.btn_vod:
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://" + vodUrls )));
                    break;

            }
        }
    };

/* // json 파싱하려고 했지만 잘 모르겠음
   // MainActivity 에서는 line = br.. 이 잘 되는데, 여기서는 안됨 url에 접속해서 코드를 하나도 가져오지 못하는데 원인을 모르겠음
   // 나중에 수정하려면 information.php 에서 css 불러오는 코드 삭제해야함,,,,
    public class phpDown extends AsyncTask<String, Integer,String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            String line ="";
            try{
                // 텍스트 연결 url 설정
                URL url = new URL(urls[0]);
                // url 로그 출력
                Log.e("tag", "url : "+urls[0]);
                // URL 페이지 커넥션 객체 생성
                HttpURLConnection conn1 = (HttpURLConnection)url.openConnection();
                // 연결되었으면.

                if(conn1 != null){
                    conn1.setConnectTimeout(10000);
                    conn1.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    Log.e("tag", "setUseCaches is false");
                    if(conn1.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn1.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line);
                        }
                        br.close();
                    }
                    conn1.disconnect();
                }
            } catch(Exception ex){
                ex.printStackTrace();
                Log.e("Tag", "Main2_doinBacground_ERR");
            }
            return jsonHtml.toString();


        }

        protected void onPostExecute(String str){

            // JSON 구문을 파싱해서 JSONArray 객체를 생성
            Log.e("Tag", str);
            try {
                JSONArray jAr = new JSONArray(str); // doInBackground 에서 받아온 문자열을 JSONArray 객체로 생성
                for (int i = 0; i < jAr.length(); i++) {  // JSON 객체를 하나씩 추출한다.
                    JSONObject vilageName = jAr.getJSONObject(i);
                    String scnOrgn = vilageName.getString("scnOrgn");
                    main2Web.loadData(scnOrgn, "text/html", "utf-8");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("Tag", "Main2_onPostExcute_ERR");
            }

        }
    }
*/


    public class phpDown extends AsyncTask<String, Integer, String> {

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
                    JSONObject vodUrl = jAr.getJSONObject(i);

                    vodUrls=vodUrl.getString("cn");
                    Log.e("zzzzzzzz",vodUrls);

                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

// 모집하기 insert 부분
class phpUp extends AsyncTask<String, Integer,String> {

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
}


// 이건 새로 만든 프로젝트 테스트
class SwipeGestureDetector extends SimpleOnGestureListener {

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float velocityX, float velocityY) {

        switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
            case 1:
                onDownSwipe();
                Log.d("aaaaaaa", "top");
                return true;
            case 2:
                onRightSwipe();
                Log.d("aaaaaaa", "left");
                return true;
            case 3:
                onUpSwipe();
                Log.d("aaaaaaa", "down");
                return true;
            case 4:
                onLeftSwipe();
                Log.d("aaaaaaa", "right");
                return true;
        }
        return false;
    }

    private int getSlope(float x1, float y1, float x2, float y2) {
        Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
        if (angle > 45 && angle <= 135)
            // top
            return 1;
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
            // left
            return 2;
        if (angle < -45 && angle >= -135)
            // down
            return 3;
        if (angle > -45 && angle <= 45)
            // right
            return 4;
        return 0;
    }

    void onRightSwipe()
    {


    }
    void onLeftSwipe()
    {


    }
    void onUpSwipe()
    {


    }
    void onDownSwipe()
    {


    }


}
