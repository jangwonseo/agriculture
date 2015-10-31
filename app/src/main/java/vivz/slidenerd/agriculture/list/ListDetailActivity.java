package vivz.slidenerd.agriculture.list;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import vivz.slidenerd.agriculture.DownloadImageTask;
import vivz.slidenerd.agriculture.DownloadImageTask_NoCircle;
import vivz.slidenerd.agriculture.NonLeakingWebView;
import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.home.HomeActivity;
import vivz.slidenerd.agriculture.navigate.NavigateActivity;
import vivz.slidenerd.agriculture.service_prepare;


import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class ListDetailActivity extends ActionBarActivity {

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    Item i;
    boolean isDiary;
    TextView vilageHomepage;
    TextView vilageNameDown;
    View btnView;
    Button call;
    Button backButton,menuButton;
    Button myDiary;
    Button vod;
    phpUp recruitTask;
    dupChecker dupChecker;
    Button accommodation;
    Button bank;
    Button gasStation;
    Button findmap;

    NonLeakingWebView main2Web;
    ImageView thumb;

    private List<WeakReference<ImageView>> mRecycleList2 = new ArrayList<WeakReference<ImageView>>();

    public static int width = 0;
    public static int height = 0;


    boolean dupChk;
    String vilageId;
    String id;

    //sharedPreference 선언부
    public SharedPreferences setting;
    public SharedPreferences.Editor editor;

    //폰트
    Typeface yunGothicFont;

    // 세부내용 리스트
    private ListView contentList;
    ArrayList<ContentItem> data = new ArrayList<>();
    Content_Adapter adapter;

    //phpDown phpJson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

        btnView=(View)findViewById(R.id.btnView);

        findmap = (Button) findViewById(R.id.findmap);
        findmap.setOnClickListener(mClickListener);
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

        // 일단은 다이어리에서 넘어온게 아니라고 가정하고 초기화
        isDiary = false;

        // 중복 체크값 초기화
        dupChk=false;

        // mianActivity에서 넘겨준 인텐트정보를 받는다.
        Intent intent = getIntent();
        Serializable item = intent.getSerializableExtra("item"); // 클래스를 넘길 때는 Serializable을 이용함
        isDiary = intent.getExtras().getBoolean("isDiary"); // int형 데이터 값 받아옴.
                                                        // 다이어리에서 넘어오면 1 넘어옴

        Log.e("aaa","다이어리 넘어왔냐는 건 "+isDiary);
        i = (Item)item;
        vilageId=i.getExprnDstncId();

        // 세부내용 리스트
        contentList = (ListView)findViewById(R.id.contentList);
        data = new ArrayList<>();
        adapter = new Content_Adapter(this, R.layout.content_list_item, data);

        // 체험 세부내용 리스트에 넣기
        data.add(new ContentItem("세부내용", i.getExprnLiverStgDc()));
        data.add(new ContentItem("체험기간", i.getOperEraBegin() + " ~ " + i.getOperEraEnd()));
        data.add(new ContentItem("체험인원", i.getNmprCoMumm() + " ~ " + i.getNmprCoMxmm() + " 명"));
        data.add(new ContentItem("소요시간", i.getOperTimeMnt() + " 분"));
        data.add(new ContentItem("가격", i.getPc() + " 원"));
        String online;
        if (i.getOnlineResvePosblAt().contains("Y")) {
            online = "가능";
        } else {
            online = "불가능";
        }
        data.add(new ContentItem("온라인예약", online));
        data.add(new ContentItem("체험마을주소", i.getAdres1()));

        contentList.setAdapter(adapter);

        //sharedPreference로 전역 공유공간을 만듬
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();
        id = setting.getString("info_Id", "");


        backButton  = (Button)findViewById(R.id.listDeail_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        menuButton = (Button)findViewById(R.id.listdetail_menubutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(moveToHomeIntent);
                finish();
            }
        });

        vilageNameDown = (TextView)findViewById(R.id.vilageNameDown); // 마을 이름
        vilageNameDown.setTypeface(yunGothicFont);
        vilageNameDown.setText(i.getExprnProgrmNm());  // Main에서 가져온 마을 이름

        vilageHomepage = (TextView)findViewById(R.id.vilageHomepage); // 마을 홈페이지
        vilageHomepage.setTypeface(yunGothicFont);
        if (i.getVilageHmpgUrl().equals("") ) {
            vilageHomepage.setText("없음");
        } else {
            vilageHomepage.setText(i.getVilageHmpgUrl());  // 가져온 마을 홈페이지
            Linkify.addLinks(vilageHomepage, Linkify.WEB_URLS);  // 마을 홈페이지 url 링크 설정
        }

        // 전화 걸기 버튼
        call = (Button)findViewById(R.id.call);

        // 파이널 변수로 만들지 않으면 리스너 함수내부에서 사용이 불가능해서 임시변수 하나 만듦
        final String phoneNumber = i.getChargerMoblphonNo();

        // 다이얼에 전화번호 올려놓기
        call.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData    // ACTION_CALL로 바꾸면
                        (Uri.parse("tel:" + phoneNumber.toString())));  // 전화 바로 걸린다.
            }
        });

        myDiary= (Button) findViewById(R.id.addmydiary);
        myDiary.setOnClickListener(mClickListener);

        //vod=(Button) findViewById(R.id.btn_vod);
        //vod.setOnClickListener(mClickListener);

        thumb = (ImageView)findViewById(R.id.thumb);

        // 웹뷰 설정
        thumb.setVerticalScrollBarEnabled(false);
        //thumb.setVerticalScrollbarOverlay(false);
        thumb.setHorizontalScrollBarEnabled(false);
       // thumb.setHorizontalScrollbarOverlay(false);
        thumb.setFocusableInTouchMode(false);
        thumb.setHorizontalScrollBarEnabled(false);
        thumb.setVerticalScrollBarEnabled(false);

       // thumb.setInitialScale(100);
        thumb.setFocusable(false);


        if(thumb != null)
        {
            //thumb.loadDataWithBaseURL(null, creHtmlBody("http://www.welchon.com" + i.getThumbUrlCours1()), "text/html", "utf-8", null);
            new DownloadImageTask_NoCircle(thumb)
                   .execute("http://www.welchon.com" + i.getThumbUrlCours1());
        }

        thumb.setOnTouchListener(gestureListener);
        vilageHomepage.setOnTouchListener(gestureListener);
        vilageNameDown.setOnTouchListener(gestureListener);


        // 체험 비디오 가져오기가져오기

        // 마이다이어리에 추가할 때 마을이 중복되는지 체크하기 위해 실행
        dupChecker = new dupChecker();
        dupChecker.execute("http://218.150.181.131/seo/SearchDiaryDup.php?userId=" + id);

        mRecycleList2.add(new WeakReference<ImageView>(thumb));
    }

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        recycle();

        super.onDestroy();
    }

    public void recycle() {
        for (WeakReference<ImageView> ref : mRecycleList2) {
            RecycleUtils.recursiveRecycle(ref.get());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
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
                case R.id.addmydiary:

                    // 로그인이 안돼있다면
                    if(id.compareTo("")==0 || id == null) {
                        Toast.makeText(getApplicationContext(), "로그인이 필요한 기능입니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // 만약 로그인 되어있다면 다이어리에서 넘어온 데이터인지 검사
                        if (!isDiary) {
                            try {
                                // 이미 마이다이어리에 있는지 중복 검사
                                if (!dupChk) {

                                    // 입력한 정보를 Item 객체에 담는다.
                                    // 입력한 정보들을 php에 get방식으로 보낸다.
                                    // 이름은 recuitTask 지만 하는 일은 정보입력용 변수임
                                    // 마이다이어리를 추가
                                    recruitTask = new phpUp();

                                    recruitTask.execute("http://218.150.181.131/seo/insert_myDiary.php?userId=" + id + "&tableName="
                                            + i.getTableName() + "&vilageId=" + i.getExprnDstncId());
                                    Log.e("myDiary", "http://218.150.181.131/seo/insert_myDiary.php?userId=" + id + "&tableName="
                                            + i.getTableName() + "&vilageId=" + i.getExprnDstncId());

                                    // MyDiary 담기 누르면 그 액티비티로 바로 이동되도록 하는 소스

//                        Log.e("regist", i.toString());
//                        //searchingseojang
//                        Log.d("seojang","11111");
//                        Intent moveIntent = new Intent(getApplicationContext(), MyDiaryActivity.class);
//                        startActivity(moveIntent);
//                        Log.d("seojang", "22222");
                                    Log.e("data<RecruitItem>", i.toString());
                                    Toast.makeText(getApplicationContext(), "해당 내용이 다이어리에 추가됐습니다.", Toast.LENGTH_SHORT).show();
                                    // 마이다이어리에 추가할 때 마을이 중복되는지 체크하기 위해 실행
                                    dupChecker = new dupChecker();
                                    dupChecker.execute("http://218.150.181.131/seo/SearchDiaryDup.php?userId=" + id);

                                } else
                                    Toast.makeText(getApplicationContext(), "이미 추가된 마을입니다.", Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("URLEncoder", "PHP params Encoder error");
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 추가된 마을입니다.", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;

                case R.id.findmap:
                    Intent mapIntent = new Intent(getApplicationContext(), MapCategoryPopupActivity.class);
                    mapIntent.putExtra("addr", i.getAdres1());
                    startActivity(mapIntent);
                    break;

            }


        }
    };


    public class dupChecker extends AsyncTask<String, Integer, String> {

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
                    JSONObject vilageName = jAr.getJSONObject(i);

                    if(vilageId.compareTo(vilageName.getString("vilageId"))==0)
                    {
                        dupChk=true;
                        break;
                    }


                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

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

class Content_Adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ContentItem> data;

    //멤버변수로 해제할 Set을 생성
    private List<WeakReference<View>> mRecycleList = new ArrayList<WeakReference<View>>();
    private List<WeakReference<ImageView>> mRecycleList2 = new ArrayList<WeakReference<ImageView>>();

    int i = 0;
    private int layout;
    //NonLeakingWebView thumb;
    Typeface yunGothicFont;

    TextView name, content;

    public Content_Adapter(Context context, int layout, ArrayList<ContentItem> data) {
        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(context.getAssets(), "fonts/yungothic330.ttf");
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

//onDestory에서 쉽게 해제할 수 있도록 메소드 생성
    public void recycle() {
        for (WeakReference<View> ref : mRecycleList) {
            RecycleUtils.recursiveRecycle(ref.get());
        }
        for (WeakReference<ImageView> ref : mRecycleList2) {
            RecycleUtils.recursiveRecycle(ref.get());
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ContentItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        name = (TextView)convertView.findViewById(R.id.txtvName);
        name.setTypeface(yunGothicFont);
        name.setText(data.get(position).getName());
        content = (TextView)convertView.findViewById(R.id.txtvContent);
        content.setTypeface(yunGothicFont);
        content.setText(data.get(position).getContent());

        return convertView;
    }
}

class ContentItem {
    private String name;
    private String content;

    public ContentItem(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}