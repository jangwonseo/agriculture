package vivz.slidenerd.agriculture.list;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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
import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.home.HomeActivity;

public class VideoListDetailActivity extends ActionBarActivity {

    Button videolistDeail_backbutton, videolistdetail_menubutton, videocall, showvideo,
            findmap;
    WebView webvContent;
    ImageView videothumb;
    TextView videovilageNameDown, videovilageHomepage;

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    private List<WeakReference<ImageView>> mRecycleList2 = new ArrayList<WeakReference<ImageView>>();

    //폰트
    Typeface yunGothicFont;

    VideoItem videoItem;

    String vodUrls;

    phpDown task;

    DownloadImageTask downloadImageTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list_detail);

        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

        // mianActivity에서 넘겨준 인텐트정보를 받는다.
        Intent intent = getIntent();
        Serializable item = intent.getSerializableExtra("item"); // 클래스를 넘길 때는 Serializable을 이용함
        videoItem = (VideoItem)item;

        try {
            task=new phpDown();
            //String str = URLEncoder.encode(i.getName(), "UTF-8");
            task.execute("http://218.150.181.131/seo/getUrl.php?vilageId=" + URLEncoder.encode(videoItem.getVilageNm(), "UTF-8") + "");
        } catch(Exception e) {
            e.printStackTrace();
        }

        videolistDeail_backbutton = (Button)findViewById(R.id.videolistDeail_backbutton);
        videolistDeail_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        videolistdetail_menubutton = (Button)findViewById(R.id.videolistdetail_menubutton);
        videolistdetail_menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(moveToHomeIntent);
                finish();
            }
        });

        videothumb = (ImageView)findViewById(R.id.videothumb);

        // 웹뷰 설정
        videothumb.setVerticalScrollBarEnabled(false);
        //thumb.setVerticalScrollbarOverlay(false);
        videothumb.setHorizontalScrollBarEnabled(false);
        // thumb.setHorizontalScrollbarOverlay(false);
        videothumb.setFocusableInTouchMode(false);
        videothumb.setHorizontalScrollBarEnabled(false);
        videothumb.setVerticalScrollBarEnabled(false);

        // thumb.setInitialScale(100);
        videothumb.setFocusable(false);

/*
        if(videothumb != null)
        {
            //thumb.loadDataWithBaseURL(null, creHtmlBody("http://www.welchon.com" + i.getThumbUrlCours1()), "text/html", "utf-8", null);
            new DownloadImageTask_NoCircle(videothumb).execute("http://www.welchon.com" + videoItem.getThumbUrlCours1());
        }
*/
        downloadImageTask = new DownloadImageTask(videothumb);
        downloadImageTask.execute("http://www.welchon.com" + videoItem.getThumbUrlCours1());

        videothumb.setOnTouchListener(gestureListener);

        gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        mRecycleList2.add(new WeakReference<ImageView>(videothumb));

        videovilageNameDown = (TextView)findViewById(R.id.videovilageNameDown);
        videovilageNameDown.setTypeface(yunGothicFont);
        videovilageNameDown.setText(videoItem.getVilageNm());  // Main에서 가져온 마을 이름

        videovilageHomepage = (TextView)findViewById(R.id.videovilageHomepage);
        videovilageHomepage.setTypeface(yunGothicFont);
        videovilageHomepage.setText(videoItem.getVilageHmpgUrl());  // Main에서 가져온 마을 이름
        Linkify.addLinks(videovilageHomepage, Linkify.WEB_URLS);  // 마을 홈페이지 url 링크 설정


        videocall = (Button)findViewById(R.id.videocall);
        final String phoneNumber = videoItem.getPrcafsManMoblphon();
        videocall.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData    // ACTION_CALL로 바꾸면
                        (Uri.parse("tel:" + phoneNumber.toString())));  // 전화 바로 걸린다.
            }
        });

        showvideo = (Button)findViewById(R.id.showvideo);
        showvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vodUrls==null) {
                    Toast toasts = Toast.makeText(getApplicationContext(), "동영상 정보가 없습니다.", Toast.LENGTH_SHORT);
                    toasts.show();
                }
                else
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://" + vodUrls )));
            }
        });

        findmap = (Button)findViewById(R.id.findmap);
        findmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(getApplicationContext(), MapCategoryPopupActivity.class);
                mapIntent.putExtra("addr", videoItem.getAdres1());
                startActivity(mapIntent);
            }
        });

        webvContent = (WebView)findViewById(R.id.webvContent);
        webvContent.setBackgroundColor(0);
        // 웹뷰 설정
        webvContent.setVerticalScrollBarEnabled(false);
        //webView.setVerticalScrollbarOverlay(false);
        webvContent.setHorizontalScrollBarEnabled(false);
        //webView.setHorizontalScrollbarOverlay(false);
        webvContent.setFocusableInTouchMode(false);
        webvContent.setHorizontalScrollBarEnabled(false);
        webvContent.setVerticalScrollBarEnabled(false);
        //webView.setInitialScale(100);
        webvContent.setFocusable(false);

        try {
            webvContent.loadDataWithBaseURL(null, creHtmlBody("http://218.150.181.131/seo/videoContent?vilageName="+ URLEncoder.encode(videoItem.getVilageNm(), "UTF-8")), "text/html", "utf-8", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String creHtmlBody(String imgUrl) {
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");
        sb.append("<img width='100%' height='100%' src = \"" + imgUrl + "\">"); // 꽉 채운 화면으로 나온다.
        sb.append("</BODY>");
        sb.append("</HTML>");
        return sb.toString();
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
                    Log.e("zzzzzzzz", vodUrls);

                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}
