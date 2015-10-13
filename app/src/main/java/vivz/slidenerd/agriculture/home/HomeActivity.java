package vivz.slidenerd.agriculture.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import vivz.slidenerd.agriculture.list.Item;
import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.list.ListDetailActivity;
import vivz.slidenerd.agriculture.mydiary.MyDiaryActivity__;
import vivz.slidenerd.agriculture.navigate.NavigateActivity;
import vivz.slidenerd.agriculture.recruit.Recruit;
import vivz.slidenerd.agriculture.region_theme.RegionChoiceActivity;
import vivz.slidenerd.agriculture.region_theme.ThemeChoiceActivity;
import vivz.slidenerd.agriculture.sign.SignChoiceActivity;
import vivz.slidenerd.agriculture.sign.SignupActivity;


public class HomeActivity extends ActionBarActivity{
    //sharedPreference 선언부
    public SharedPreferences setting;
    public SharedPreferences.Editor editor;

    private Button goTheme,goRegion,goGathering,goEtcetera,menuButton,myinfoButton, btnChangeMyinfo;

    //페이지가 열려 있는지 알기 위한 플래그

    //애니메이션 객체
    private Animation translateLeftAnim;
    private Animation translateRightAnim;

    //슬라이딩으로 보여지는 페이지 레이아웃
    private LinearLayout slidingPage01;

    // 홈에 추천 마을(체험) 이미지
    WebView webvHomeImage;
    phpGetInfo getInfo;
    ArrayList<Item> recommendItems = new ArrayList<>();
    ArrayList<Item> recommendItems10 = new ArrayList<>();

    // 홈 왼쪽, 오른쪽 체험 선택
    Button btnLeftVilage;
    Button btnRightVilage;
    Button btnMyDiary;
    public int recommendNum = 0;


    private boolean isPageOpen = false;
    //두번 눌러 종료
    private BackPressCloseHandler backPressCloseHandler;

    // 프로필 사진
    WebView webvProfile;

    LinearLayout HomeBackGround;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //sharedPreference로 전역 공유공간을 만듬
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();
        userId = setting.getString("info_Id", "");

        goTheme = (Button)findViewById(R.id.themebutton);
        goRegion = (Button)findViewById(R.id.regionbutton);
        goGathering = (Button)findViewById(R.id.gatheringbutton);
        goEtcetera = (Button)findViewById(R.id.etceterabutton);

        menuButton = (Button)findViewById(R.id.home_menubutton);
        myinfoButton = (Button)findViewById(R.id.home_myinfo);
        btnChangeMyinfo = (Button)findViewById(R.id.btnChangeMyinfo);

        goTheme.setOnClickListener(mClickListener);
        goRegion.setOnClickListener(mClickListener);
        goGathering.setOnClickListener(mClickListener);
        goEtcetera.setOnClickListener(mClickListener);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 애니메이션 적용
                if (isPageOpen){
                    slidingPage01.startAnimation(translateRightAnim);
                } else {
                    slidingPage01.setVisibility(View.VISIBLE);
                    slidingPage01.startAnimation(translateLeftAnim);
                }

            }
        });
        HomeBackGround = (LinearLayout)findViewById(R.id.homebackground);
        HomeBackGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPageOpen){
                    slidingPage01.startAnimation(translateRightAnim);
                }
            }
        });

        myinfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignChoice = new Intent(getApplication(), SignChoiceActivity.class);
                startActivity(intentSignChoice);
            }
        });

        btnChangeMyinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( userId.equals("") || userId == null) {
                    Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 내정보 수정 myinfo
                    Intent myInfo = new Intent(getApplicationContext(), ChangeMyinfoActivity.class);
                    startActivity(myInfo);
                }
            }
        });

        // 슬라이딩으로 보여질 레이아웃 객체 참조
        slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);

        // 애니메이션 객체 로딩
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        // 애니메이션 객체에 리스너 설정
        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        // 웹뷰 이미지 가져오는 부분
        webvHomeImage = (WebView)findViewById(R.id.webvHomeImage);
        // 배경이 하얕게 나오는데 투명하게 만들어줌
        webvHomeImage.setBackgroundColor(0);
        // 웹뷰 설정
        webvHomeImage.setVerticalScrollBarEnabled(false);
        webvHomeImage.setVerticalScrollbarOverlay(false);
        webvHomeImage.setHorizontalScrollBarEnabled(false);
        webvHomeImage.setHorizontalScrollbarOverlay(false);
        //webvHomeImage.setFocusableInTouchMode(false);
        webvHomeImage.setHorizontalScrollBarEnabled(false);
        webvHomeImage.setVerticalScrollBarEnabled(false);
        webvHomeImage.setInitialScale(100);
        //webvHomeImage.setFocusable(false);

        webvHomeImage.loadDataWithBaseURL(null, creHtmlBody("http://218.150.181.131/seo/image/default.png"), "text/html", "utf-8", null);

        getInfo = new phpGetInfo();
        getInfo.execute("http://218.150.181.131/seo/phpRecommendVilage.php");

        btnLeftVilage = (Button)findViewById(R.id.btnLeftVilage);
        btnRightVilage = (Button)findViewById(R.id.btnRightVilage);

        btnMyDiary=(Button)findViewById(R.id.btn_myDiary);
        btnMyDiary.setOnClickListener(mClickListener);

        btnLeftVilage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recommendNum > 0 && recommendNum < 10) {
                    recommendNum--;
                } else if(recommendNum == 0){
                    recommendNum = 9;
                }

                webvHomeImage.loadDataWithBaseURL(null, creHtmlBody("http://www.welchon.com" + recommendItems10.get(recommendNum).getThumbUrl()), "text/html", "utf-8", null);
            }
        });

        btnRightVilage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recommendNum >= 0 && recommendNum < 9) {
                    recommendNum++;
                } else if (recommendNum == 9) {
                    recommendNum = 0;
                }

                webvHomeImage.loadDataWithBaseURL(null, creHtmlBody("http://www.welchon.com" + recommendItems10.get(recommendNum).getThumbUrl()), "text/html", "utf-8", null);
            }
        });

        webvHomeImage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        try{
                            /** 이부분 클릭 시 다른 액티비티를 띄우는 부분 **/
                            Intent HomeIntent = new Intent(getApplicationContext(), ListDetailActivity.class);
                            HomeIntent.putExtra("item", recommendItems10.get(recommendNum)); // 리스트를 클릭하면 현재 클릭한 마을에 대한 Item 클래스를 넘겨준다.
                            // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
                            startActivity(HomeIntent);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });

        //두번눌러 종료
        backPressCloseHandler = new BackPressCloseHandler(this);


        // 프로필사진
        webvProfile = (WebView)findViewById(R.id.webvProfile);

        // 웹뷰 설정
        // 배경이 하얕게 나오는데 투명하게 만들어줌
        webvProfile.setBackgroundColor(0);
        webvProfile.setVerticalScrollBarEnabled(false);
        webvProfile.setVerticalScrollbarOverlay(false);
        webvProfile.setHorizontalScrollBarEnabled(false);
        webvProfile.setHorizontalScrollbarOverlay(false);
        webvProfile.setFocusableInTouchMode(false);
        webvProfile.setHorizontalScrollBarEnabled(false);
        webvProfile.setVerticalScrollBarEnabled(false);
        webvProfile.setInitialScale(100);
        webvProfile.setFocusable(false);
        webvProfile.loadDataWithBaseURL(null, creHtmlBody("http://218.150.181.131/seo/image/"+userId+"Profile.jpg"), "text/html", "utf-8", null);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    // 웹뷰 이미지 화면을 웹뷰크기에 맞게 조절
    public String creHtmlBody(String imgUrl) {
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");
        //sb.append("<img src = \"" + imgUrl + "\">"); // 자기 비율에 맞게 나온다.
        sb.append("<img width='100%' height='100%' style='-moz-border-radius: 220px;" +
                "-webkit-border-radius: 220px; ' src = \"" + imgUrl + "\">"); // 꽉 채운 화면으로 나온다.

        sb.append("</BODY>");
        sb.append("</HTML>");
        return sb.toString();
    }

    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.themebutton:
                    Intent intentTheme = new Intent(getApplication(), ThemeChoiceActivity.class);
                    startActivity(intentTheme);
                    break;
                case R.id.regionbutton:
                    Intent intentRegion = new Intent(getApplication(), RegionChoiceActivity.class);
                    startActivity(intentRegion);
                    break;
                case R.id.gatheringbutton:
                    Intent intentRecruit = new Intent(getApplicationContext(), Recruit.class);
                    startActivity(intentRecruit);
                    break;
                case R.id.etceterabutton:
                    Intent intentNavigate = new Intent(getApplicationContext(), NavigateActivity.class);
                    startActivity(intentNavigate);
                    break;
                case R.id.btn_myDiary:
                    if ( userId.equals("") || userId == null ) {
                        Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intentMyDiary = new Intent(getApplicationContext(), MyDiaryActivity__.class);
                        startActivity(intentMyDiary);
                    }
            }
        }
    };

    // 추천할 마을 정보
    public class phpGetInfo extends AsyncTask<String, Integer,String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            String line ="";
            try{
                // 텍스트 연결 url 설정
                URL url = new URL(urls[0]);
                // 이미지 url
                Log.e("tag", "url : " + urls[0]);
                // URL 페이지 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면.

                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    Log.e("tag", "setUseCaches is false");
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line);
                        }
                        br.close();
                    }
                    conn.disconnect();

                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
            return jsonHtml.toString();


        }

        protected void onPostExecute(String str){
            // JSON 구문을 파싱해서 JSONArray 객체를 생성

            try {
                JSONArray jAr = new JSONArray(str); // doInBackground 에서 받아온 문자열을 JSONArray 객체로 생성
                for (int i = 0; i < jAr.length(); i++) {  // JSON 객체를 하나씩 추출한다.
                    JSONObject vilageName = jAr.getJSONObject(i);
                    Item item = new Item(vilageName.getString("thumbUrlCours1"), vilageName.getString("vilageNm"),
                            vilageName.getString("adres1"), vilageName.getString("prcafsManMoblphon"),
                            vilageName.getString("vilageHmpgEnnc"), vilageName.getString("vilageHmpgUrl"),
                            vilageName.getString("vilageSlgn"), vilageName.getString("tableName"), vilageName.getString("id"));
                    recommendItems.add(item);
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // 모든 체험정보를 가져오면 임의로 10개의 체험을 뽑는다.
            Log.e("recomdItemsSize: ", Integer.toString(recommendItems.size()));
            try {
                for (int i = 0; i < 10; i++) {
                    recommendItems10.add(recommendItems.get(i));
                    Log.e("recomdItemsAdded: ", recommendItems.get(i).getName());
                }
                webvHomeImage.loadDataWithBaseURL(null, creHtmlBody("http://www.welchon.com" + recommendItems10.get(0).getThumbUrl()), "text/html", "utf-8", null);
            }catch(Exception ex){
                Toast.makeText(getApplicationContext(), "인터넷 연결이 되어있지 않습니다.", Toast.LENGTH_SHORT);
            }

        }
    }
//
    /**
     * 애니메이션 리스너 정의
     */
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        /**
         * 애니메이션이 끝날 때 호출되는 메소드
         */
        public void onAnimationEnd(Animation animation) {
            if (isPageOpen) {
                slidingPage01.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            } else {
                isPageOpen = true;
            }
        }

        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationStart(Animation animation) {

        }
    }
}


class RecommendItem implements Serializable {
    private String thumbUrl;            // 이미지 경로
    private String name;                // 마을 이름
    private String addr;                // 주소
    private String prcafsManMoblphon;   // 실무자 전화번호
    private String vilageHmpgEnnc;      // 마을 홈피 유무
    private String vilageHmpgUrl;       // 마을 홈피 주소
    private String vilageSlgn;          // 마을 간단 소개
    private String tableName;           // 테마
    private String vilageId;

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }

    public String getPrcafsManMoblphon() {
        return prcafsManMoblphon;
    }

    public String getVilageHmpgEnnc() {
        return vilageHmpgEnnc;
    }

    public String getVilageHmpgUrl() {
        return vilageHmpgUrl;
    }

    public String getVilageSlgn() {
        return vilageSlgn;
    }

    public String getTableName() {
        return tableName;
    }

    public String getVilageId() {
        return vilageId;
    }

    public String toString() {

        String addrEncoded = null;
        String vilageSlgnEncoded = null;
//        String vilageHmpgEnncEncoded = null;
//        String vilageHmpgUrlEncoded = null;
//        String vilageKndNmEncoded = null;
        try {
            addrEncoded = URLEncoder.encode(addr, "UTF-8");
            vilageSlgnEncoded = URLEncoder.encode(vilageSlgn,"UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("regist", "thumbUrl=" + thumbUrl + "&name=" + name + "&addr=" + addrEncoded + "&prcafsManMoblphon=" + prcafsManMoblphon +
                "&vilageHmpgEnnc=" + vilageHmpgEnnc + "&vilageHmpgUrl=" + vilageHmpgUrl +
                "&vilageSlgn=" + vilageSlgnEncoded + "&tableName=" + tableName + "&vilageId=" + vilageId);
        return ("thumbUrl=" + thumbUrl + "&name=" + name + "&addr=" + addrEncoded + "&prcafsManMoblphon=" + prcafsManMoblphon +
                "&vilageHmpgEnnc=" + vilageHmpgEnnc + "&vilageHmpgUrl=" + vilageHmpgUrl +
                "&vilageSlgn=" + vilageSlgnEncoded +  "&tableName=" + tableName +  "&vilageId=" + vilageId);

    }

    public RecommendItem(String thumbUrl, String name, String addr, String prcafsManMoblphon, String vilageHmpgEnnc, String vilageHmpgUrl,
                String vilageSlgn, String tableName, String vilageId) {

        this.thumbUrl = thumbUrl;
        this.name = name;
        this.addr = addr;
        this.prcafsManMoblphon = prcafsManMoblphon;
        this.vilageHmpgEnnc = vilageHmpgEnnc;
        this.vilageHmpgUrl = vilageHmpgUrl;
        this.vilageSlgn = vilageSlgn;
        this.tableName = tableName;
        this.vilageId = vilageId;
    }
}

