package vivz.slidenerd.agriculture.home;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

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
import java.util.HashMap;

import vivz.slidenerd.agriculture.DownloadImageTask;
import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.list.Item;
import vivz.slidenerd.agriculture.list.ListDetailActivity;
import vivz.slidenerd.agriculture.mydiary.MyDiaryActivity__;
import vivz.slidenerd.agriculture.navigate.NavigateActivity;
import vivz.slidenerd.agriculture.recruit.Recruit;
import vivz.slidenerd.agriculture.region_theme.RegionChoiceActivity;
import vivz.slidenerd.agriculture.region_theme.ThemeChoiceActivity;
import vivz.slidenerd.agriculture.sign.SHA256;
import vivz.slidenerd.agriculture.sign.SignChoiceActivity;

public class HomeActivity extends Activity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    //sharedPreference 선언부
    public SharedPreferences setting;
    public SharedPreferences.Editor editor;

    private Button goTheme, goRegion, goGathering, goEtcetera, menuButton, myinfoButton, btnChangeMyinfo;

    //페이지가 열려 있는지 알기 위한 플래그
    //애니메이션 객체
    private Animation translateLeftAnim;
    private Animation translateRightAnim;

    //슬라이딩으로 보여지는 페이지 레이아웃
    private LinearLayout slidingPage01;

    // 홈에 추천 마을(체험) 이미지
    private ImageView webvHomeImage;
    private ArrayList<Item> recommendItems = new ArrayList<>();
    //ArrayList<Item> recommendItems10 = new ArrayList<>();

    private Button btnMyDiary;
    public int recommendNum = 0;
    private phpGetInfo getInfo;

    private boolean isPageOpen = false;
    //두번 눌러 종료
    private BackPressCloseHandler backPressCloseHandler;

    // 프로필 사진
    private ImageView webvProfile;

    private LinearLayout HomeBackGround;
    private String userId;
    private Button eventButton;

    private Typeface yunGothicFont; //윤고딕폰트

    //이미지 슬라이더 관련
    private SliderLayout mDemoSlider;
    int selectedSlide;
    private HashMap<String, Integer> file_maps;
    private TextSliderView textSliderView;

    private SlidingPageAnimationListener animListener;

    private SHA256 sha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //imageSlider
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        if (file_maps == null) {
            file_maps = new HashMap<String, Integer>();

            file_maps.put("Hannibal", R.drawable.aathumbnail1);
            file_maps.put("Big Bang Theory", R.drawable.aathumbnail2);
            file_maps.put("House of Cards", R.drawable.aathumbnail3);
            file_maps.put("Game of Thrones", R.drawable.aathumbnail4);
            file_maps.put("Blueberry", R.drawable.aathumbnail5);
            file_maps.put("Dadm", R.drawable.aathumbnail6);
            file_maps.put("Flower vilage", R.drawable.aathumbnail7);
            file_maps.put("Slow day", R.drawable.aathumbnail8);
            Log.e("mapsKetset : ", file_maps.keySet().toString());
            for (String name : file_maps.keySet()) {
                textSliderView = new TextSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        //                    .description(name)  // 이미지에대해 이름을 표시해줌.
                        .image(file_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putString("extra", name);

                mDemoSlider.addSlider(textSliderView);
            }
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        //mDemoSlider.setCustomAnimation(new DescriptionAnimation());//이미지에 대한 설명이 애니메이션으로 나옴
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        //////////////////////////

        sha = new SHA256();

        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

        //sharedPreference로 전역 공유공간을 만듬
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        userId = setting.getString("info_Id", "");

        goTheme = (Button) findViewById(R.id.themebutton);
        goRegion = (Button) findViewById(R.id.regionbutton);
        goGathering = (Button) findViewById(R.id.gatheringbutton);
        goEtcetera = (Button) findViewById(R.id.etceterabutton);

        menuButton = (Button) findViewById(R.id.home_menubutton);
        myinfoButton = (Button) findViewById(R.id.home_myinfo);
        btnChangeMyinfo = (Button) findViewById(R.id.btnChangeMyinfo);

        goTheme.setOnClickListener(mClickListener);
        goRegion.setOnClickListener(mClickListener);
        goGathering.setOnClickListener(mClickListener);
        goEtcetera.setOnClickListener(mClickListener);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 애니메이션 적용
                if (isPageOpen) {
                    slidingPage01.startAnimation(translateRightAnim);
                } else {
                    slidingPage01.setVisibility(View.VISIBLE);
                    slidingPage01.startAnimation(translateLeftAnim);
                }

            }
        });
        HomeBackGround = (LinearLayout) findViewById(R.id.homebackground);
        HomeBackGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPageOpen) {
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
                if (userId.equals("") || userId == null) {
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
        animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        getInfo = new phpGetInfo();
        getInfo.execute("http://218.150.181.131/seo/phpHomeSlider.php");

        btnMyDiary = (Button) findViewById(R.id.btn_myDiary);
        btnMyDiary.setOnClickListener(mClickListener);

        //두번눌러 종료
        backPressCloseHandler = new BackPressCloseHandler(this);

        // 프로필사진
        webvProfile = (ImageView) findViewById(R.id.webvProfile);

        // 웹뷰 설정
        // 배경이 하얕게 나오는데 투명하게 만들어줌
        webvProfile.setBackgroundColor(0);
        webvProfile.setVerticalScrollBarEnabled(false);
        //webvProfile.setVerticalScrollbarOverlay(false);
        webvProfile.setHorizontalScrollBarEnabled(false);
        //webvProfile.setHorizontalScrollbarOverlay(false);
        webvProfile.setFocusableInTouchMode(false);
        webvProfile.setHorizontalScrollBarEnabled(false);
        webvProfile.setVerticalScrollBarEnabled(false);
        //webvProfile.setInitialScale(100);
        webvProfile.setFocusable(false);

        webvProfile.setImageDrawable(null);
        new DownloadImageTask(webvProfile).execute("http://218.150.181.131/seo/image/" + userId + "Profile.jpg");
        //webvProfile.loadDataWithBaseURL(null, creHtmlBody("http://218.150.181.131/seo/image/" + userId + "Profile.jpg"), "text/html", "utf-8", null);

        /*
        eventButton = (Button) findViewById(R.id.eventbutton);
        eventButton.setTypeface(yunGothicFont);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EventWindow.class);
                startActivity(intent);
            }
        });
        */
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    // 웹뷰 이미지 화면을 웹뷰크기에 맞게 조절
    public String creHtmlBody(String imgUrl) {
        return "<HTML>" + "<HEAD>" + "</HEAD>" + "<BODY style='margin:0; padding:0; text-align:center;'>" +
                "<img width='100%' height='100%' style='-moz-border-radius: 220px;" +
                "-webkit-border-radius: 220px; ' src = \"" + imgUrl + "\">" + "</BODY>" + "</HTML>";
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
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
                    if (userId.equals("") || userId == null) {
                        Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intentMyDiary = new Intent(getApplicationContext(), MyDiaryActivity__.class);
                        startActivity(intentMyDiary);
                    }
            }
        }
    };

    @Override
    protected void onResume() {
        mDemoSlider.startAutoCycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.homebackground).setBackground(getResources().getDrawable(R.drawable.home1));
        }else{
            findViewById(R.id.homebackground).setBackgroundResource(R.drawable.home1);
        }
        System.gc();
        super.onResume();
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.homebackground).setBackground(null);
        }else{
            findViewById(R.id.homebackground).setBackgroundResource(R.drawable.home1);
        }
        System.gc();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mDemoSlider.stopAutoCycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.homebackground).setBackground(null);
        }else{
            findViewById(R.id.homebackground).setBackgroundResource(R.drawable.home1);
        }
        System.gc();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mDemoSlider.destroyDrawingCache();
        mDemoSlider.stopAutoCycle();
        recycleBitmap(mDemoSlider);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.homebackground).setBackground(null);
        }else{
            findViewById(R.id.homebackground).setBackgroundResource(R.drawable.home1);
        }
        System.gc();
        super.onDestroy();
    }

    private static void recycleBitmap(SliderLayout iv) {
        Drawable d = iv.getBackground();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable) d).getBitmap();
            b.recycle();
        }
        d = null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
        selectedSlide = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Log.e("Slider : ", Integer.toString(selectedSlide));

        try {
            /** 이부분 클릭 시 다른 액티비티를 띄우는 부분 **/
            Intent HomeIntent = new Intent(getApplicationContext(), ListDetailActivity.class);

            int i = 0;

            switch (selectedSlide) {
                case 0:
                    i = 0;
                    break;
                case 1:
                    i = 3;
                    break;
                case 2:
                    i = 6;
                    break;
                case 3:
                    i = 7;
                    break;
                case 4:
                    i = 2;
                    break;
                case 5:
                    i = 4;
                    break;
                case 6:
                    i = 5;
                    break;
                case 7:
                    i = 1;
                    break;
            }

            HomeIntent.putExtra("item", recommendItems.get(i)); // 리스트를 클릭하면 현재 클릭한 마을에 대한 Item 클래스를 넘겨준다.
            // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
            startActivity(HomeIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 추천할 마을 정보
    public class phpGetInfo extends AsyncTask<String, Integer, String> {

        //doInBackground
        private URL url;
        private HttpURLConnection conn;
        private BufferedReader br;
        private StringBuilder jsonHtml;
        private String line;

        //onPostExecute
        private JSONArray jAr;
        private JSONObject vilageName;
        private Item item;

        @Override
        protected String doInBackground(String... urls) {
            jsonHtml = new StringBuilder();
            line = "";
            try {
                // 텍스트 연결 url 설정
                url = new URL(urls[0]);
                // 이미지 url
                Log.e("tag", "url : " + urls[0]);
                // URL 페이지 커넥션 객체 생성
                conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    Log.e("tag", "setUseCaches is false");
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
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
                jAr = new JSONArray(str); // doInBackground 에서 받아온 문자열을 JSONArray 객체로 생성
                for (int i = 0; i < jAr.length(); i++) {  // JSON 객체를 하나씩 추출한다.
                    vilageName = jAr.getJSONObject(i);
                    item = new Item(vilageName.getString("thumbUrlCours1"), vilageName.getString("exprnDstncId"), vilageName.getString("chargerMoblphonNo"),
                            vilageName.getString("exprnProgrmNm"), vilageName.getString("exprnLiverStgDc"), vilageName.getString("adres1"),
                            vilageName.getString("vilageHmpgUrl"), vilageName.getString("vilageNm"), vilageName.getString("tableName"),
                            vilageName.getString("operEraBegin"), vilageName.getString("operEraEnd"), vilageName.getString("nmprCoMumm")
                            , vilageName.getString("nmprCoMxmm"), vilageName.getString("operTimeMnt"), vilageName.getString("pc"),
                            vilageName.getString("onlineResvePosblAt"));
                    recommendItems.add(item);
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

            this.cancel(true);
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
            vilageSlgnEncoded = URLEncoder.encode(vilageSlgn, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("regist", "thumbUrl=" + thumbUrl + "&name=" + name + "&addr=" + addrEncoded + "&prcafsManMoblphon=" + prcafsManMoblphon +
                "&vilageHmpgEnnc=" + vilageHmpgEnnc + "&vilageHmpgUrl=" + vilageHmpgUrl +
                "&vilageSlgn=" + vilageSlgnEncoded + "&tableName=" + tableName + "&vilageId=" + vilageId);
        return ("thumbUrl=" + thumbUrl + "&name=" + name + "&addr=" + addrEncoded + "&prcafsManMoblphon=" + prcafsManMoblphon +
                "&vilageHmpgEnnc=" + vilageHmpgEnnc + "&vilageHmpgUrl=" + vilageHmpgUrl +
                "&vilageSlgn=" + vilageSlgnEncoded + "&tableName=" + tableName + "&vilageId=" + vilageId);

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

