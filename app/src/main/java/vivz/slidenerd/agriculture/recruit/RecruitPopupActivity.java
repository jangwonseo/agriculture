package vivz.slidenerd.agriculture.recruit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vivz.slidenerd.agriculture.DownloadImageTask;
import vivz.slidenerd.agriculture.DownloadImageTask_NoCircle;
import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.service_prepare;
import vivz.slidenerd.agriculture.sign.SignupActivity;

public class RecruitPopupActivity extends Activity implements View.OnClickListener {

    private Typeface yunGothicFont; //윤고딕폰트

    //로그인 정보 가져오기
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    String sharedUserId;

    RecruitListItem item;

    // 페이스북 객체
    ShareDialog shareDialog;
    LoginButton loginButton;
    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    //정적 텍스트뷰(고정)
    TextView static_txtvVilageName, static_txtvMissionName, static_txtvRecruitNum, static_txtvRecruitContent, static_txtvRecruitTerm, static_txtvReward;

    //동적인 텍스트뷰
    TextView txtvVilageName, txtvMissionName, txtvRecruitNum, txtvRecruitContent, txtvRecruitTerm, txtvReward;

    // 줄바꿈
    String lineEnding = null;

    ImageView webvRecPopup;
    private List<WeakReference<ImageView>> mRecycleList2 = new ArrayList<WeakReference<ImageView>>();

    Button btnPhoneCall;
    Button btnMissionJoin;
    Button shareButton;

    String imgUrl = "http://218.150.181.131/seo/image/"; // 사진이 없을 경우 디폴트 사진 띄우는 경로
    String loadingURL = null;
    phpMissionJoin missionJoin;

    // 참가 성공여부
    public static final int joinSucceed = 1;
    public static final int joinNoSucceed = 0;
    public static final int joinAlreadyIn = 2;

    JoinHandler myJoinHandler;
    DownloadImageTask_NoCircle downloadImageTask_NoCircle;

    // 삭제 또는 참여하기 버튼
    boolean recruitUser; // true 면 삭제, false면 참여하기
    CancelHandler cancelHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_recruit_popup);
        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");


        shareDialog = new ShareDialog(this);


        shareButton = (Button) findViewById(R.id.shareButton);

        // 페이스북 공유버튼 누를 시
        //로그인 된 경우 바로 포스팅 화면
        // 안되어있는 경우 로그인 화면
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isLoggedIn())
                    feed();
                else
                     loginButton.performClick();
            }
        });


        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        sharedUserId = setting.getString("info_Id", "");
        Log.e("userId : ", sharedUserId);


        Intent intent = getIntent();
        item = (RecruitListItem) intent.getSerializableExtra("item");
        Log.e("popup", "successed");

        //고정 textview
        static_txtvMissionName = (TextView) findViewById(R.id.static_missionname);
        static_txtvMissionName.setTypeface(yunGothicFont);
        static_txtvRecruitNum = (TextView) findViewById(R.id.static_recuitnum);
        static_txtvRecruitNum.setTypeface(yunGothicFont);
        static_txtvRecruitContent = (TextView) findViewById(R.id.static_recuitcontent);
        static_txtvRecruitContent.setTypeface(yunGothicFont);
        static_txtvRecruitTerm = (TextView) findViewById(R.id.static_recruitterm);
        static_txtvRecruitTerm.setTypeface(yunGothicFont);
        static_txtvReward = (TextView) findViewById(R.id.static_txtreward);
        static_txtvReward.setTypeface(yunGothicFont);


        txtvVilageName = (TextView) findViewById(R.id.txtvVilageName);
        txtvVilageName.setTypeface(yunGothicFont);
        txtvVilageName.setText(item.getVilageName());
        txtvMissionName = (TextView) findViewById(R.id.txtvMissionName);
        txtvMissionName.setTypeface(yunGothicFont);
        txtvMissionName.setText(item.getMissionName());
        txtvRecruitTerm = (TextView) findViewById(R.id.txtvRecruitTerm);
        txtvRecruitTerm.setTypeface(yunGothicFont);
        txtvRecruitTerm.setText(item.getTermStart() + " ~ " + item.getTermEnd());
        txtvRecruitNum = (TextView) findViewById(R.id.txtvRecruitNum);
        txtvRecruitNum.setTypeface(yunGothicFont);
        txtvRecruitNum.setText(Integer.toString(item.getJoinedNum()) + " / " + Integer.toString(item.getRecruitNum()) + " 명");

        btnPhoneCall = (Button) findViewById(R.id.btnPhoneCall);
        btnMissionJoin = (Button) findViewById(R.id.btnMissionJoin);

        if ( sharedUserId.equals(item.getUserId()) ) {
            recruitUser = true;
            btnMissionJoin.setBackground(getResources().getDrawable(R.drawable.joinin10_));
        } else {
            recruitUser = false;
            btnMissionJoin.setBackground(getResources().getDrawable(R.drawable.joinin10));
        }

        btnPhoneCall.setOnClickListener(this);
        btnMissionJoin.setOnClickListener(this);


        // 줄바꿈
        lineEnding = item.getRecruitContent().replace("99line99end99", "\n");
        txtvRecruitContent = (TextView) findViewById(R.id.txtvRecruitContent);
        txtvRecruitContent.setTypeface(yunGothicFont);
        txtvRecruitContent.setText(lineEnding);
        Log.e("txtvRecruitContent", item.getRecruitContent());
        txtvReward = (TextView) findViewById(R.id.txtvReward);
        txtvReward.setTypeface(yunGothicFont);
        txtvReward.setText(item.getReward());

        webvRecPopup = (ImageView) findViewById(R.id.webvRecPopup);
        downloadImageTask_NoCircle = new DownloadImageTask_NoCircle(webvRecPopup);

        webvRecPopup.setVerticalScrollBarEnabled(false);
        //webvRecPopup.setVerticalScrollbarOverlay(false);
        webvRecPopup.setHorizontalScrollBarEnabled(false);
        //webvRecPopup.setHorizontalScrollbarOverlay(false);
        webvRecPopup.setFocusableInTouchMode(false);
        webvRecPopup.setHorizontalScrollBarEnabled(false);
        webvRecPopup.setVerticalScrollBarEnabled(false);
        //webvRecPopup.setInitialScale(100);
        webvRecPopup.setFocusable(false);

        String ImageURL = item.getImageURL();

        if (ImageURL.equals("null") || ImageURL == null) {
            loadingURL = imgUrl + "default.png";
        } else {
            loadingURL = imgUrl + item.getImageURL();
        }
        downloadImageTask_NoCircle.execute(loadingURL);
      //  new DownloadImageTask_NoCircle(webvRecPopup)
        //        .execute(loadingURL);
       // webvRecPopup.loadDataWithBaseURL(null, creHtmlBody(loadingURL), "text/html", "utf-8", null);
        Log.e("list image path", loadingURL);


        myJoinHandler = new JoinHandler();

        mRecycleList2.add(new WeakReference<ImageView>(webvRecPopup));


        // 페이스북 로그인
        loginButton = (LoginButton)findViewById(R.id.facebooklogin_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                // 정보 받아오는 graph api
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e("aaa", " 로그인");


                                // 로그인 성공, 완료시 포스팅화면으로 이동
                                feed();


                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {

            }
        });
        Log.e("aaa", " 로그인 직전4");
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {


                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }
        };

        cancelHandler = new CancelHandler();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void recycle() {
        for (WeakReference<ImageView> ref : mRecycleList2) {
            RecycleUtils.recursiveRecycle(ref.get());
        }
    }

    // 리스트 뷰 항목에 들어가는 웹뷰 이미지 화면을 웹뷰크기에 맞게 조절
    public String creHtmlBody(String imgUrl) {
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");
        //sb.append("<img src = \"" + imgUrl + "\">"); // 자기 비율에 맞게 나온다.
        sb.append("<img width='100%' height='100%' src = \"" + imgUrl + "\">"); // 꽉 채운 화면으로 나온다.
        sb.append("</BODY>");
        sb.append("</HTML>");
        return sb.toString();
    }
    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
        System.gc();
        recycle();

        super.onDestroy();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPhoneCall:
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + item.getPhoneNum())));
                break;

            case R.id.btnMissionJoin:
                if ( recruitUser ) { // 모집하기한 본인이라면
                    phpMyDiaryListCancel myCancel = new phpMyDiaryListCancel();
                    myCancel.execute("http://218.150.181.131/seo/recruitDelete.php?userId=" + sharedUserId + "&recruitId=" + item.getIdRecruit());
                } else { // 모집하기 본인이 아니라면
                    missionJoin = new phpMissionJoin();
                    if (sharedUserId.equals("")) {
                        Toast.makeText(getApplicationContext(), "로그인을 하십시오", Toast.LENGTH_SHORT).show();
                        break;
                    }else if(item.getRecruitNum() <= item.getJoinedNum() && !(item.getRecruitNum()==0))  {
                        Toast.makeText(getApplicationContext(), "참가인원을 초과하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    else {
                        MissionItem missionItem = new MissionItem(sharedUserId, item.getIdRecruit());
                        missionJoin.execute("http://218.150.181.131/seo/phpMissionJoin.php?" + missionItem.toString());
                    }
                }
                break;
        }
    }

    // MydiaryDetail 부분에서 취소하기
    public class phpMyDiaryListCancel extends AsyncTask<String, Integer,String> {

        Message msg = cancelHandler.obtainMessage(); // 취소 성공/실패를 위한 핸들러

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
            if ( str.contains("recruit deleted") )
                msg.what = 2;
            else
                msg.what = 1;
            cancelHandler.sendMessage(msg);
            this.cancel(false);
        }
    }

    // Handler 클래스
    class CancelHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 2:
                    Toast.makeText(getApplicationContext(), "모집이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "모집취소에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    // 참가하기 php
    public class phpMissionJoin extends AsyncTask<String, Integer, String> {

        Message msg = myJoinHandler.obtainMessage(); // 참가 성공 실패 핸들러

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            String line = "";
            try {
                // 텍스트 연결 url 설정
                URL url = new URL(urls[0]);
                // 이미지 url
                Log.e("tag", "url : " + urls[0]);
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
                msg.what = joinSucceed;
            } catch (Exception ex) {
                ex.printStackTrace();
                msg.what = joinNoSucceed;
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {

            if (!str.contains("1 record added")) {
                msg.what = joinNoSucceed;
            }

            if (str.contains("key 'PRIMARY'")) {
                msg.what = joinAlreadyIn;
            }

            myJoinHandler.sendMessage(msg);
            this.cancel(false);
        }
    }

    // 참가하기 php
    public class phpMissionDelete extends AsyncTask<String, Integer, String> {

        Message msg = myJoinHandler.obtainMessage(); // 참가 성공 실패 핸들러

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            String line = "";
            try {
                // 텍스트 연결 url 설정
                URL url = new URL(urls[0]);
                // 이미지 url
                Log.e("tag", "url : " + urls[0]);
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
                msg.what = joinSucceed;
            } catch (Exception ex) {
                ex.printStackTrace();
                msg.what = joinNoSucceed;
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {

            if (!str.contains("1 record added")) {
                msg.what = joinNoSucceed;
            }

            if (str.contains("key 'PRIMARY'")) {
                msg.what = joinAlreadyIn;
            }

            myJoinHandler.sendMessage(msg);

        }
    }

    // Handler 클래스
    class JoinHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case joinSucceed:
                    Toast.makeText(getApplicationContext(), "참가하였습니다.", Toast.LENGTH_SHORT).show();
                    break;

                case joinNoSucceed:
                    Toast.makeText(getApplicationContext(), "참가에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    break;

                case joinAlreadyIn:
                    Toast.makeText(getApplicationContext(), "이미 참가하셨습니다.", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

    }

    // 페이스북 로그인 확인
    public boolean isLoggedIn() {
        if(AccessToken.getCurrentAccessToken()!=null)
            return true;

        return false;

    }


    // 페이스북 포스팅 부분
    private void feed()
    {
        Log.e("aaa","포스트 준비");
        StringBuilder messageData = new StringBuilder().append("모집기간 : ")
                .append(item.getTermStart()).append(" ~ ").append(item.getTermEnd()).append('\n')
                .append("모집인원 : ").append(item.getJoinedNum()).append(" / ").append(Integer.toString(item.getJoinedNum()))
                .append("\n").append(item.getRecruitContent());
// Message
        try
        {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(item.getVilageName() + "에 함께 가요!!")    // 제목
                    .setContentDescription("모집기간 : " +item.getTermStart() + " ~ " + item.getTermEnd() +
                            "\r\n" + "모집인원 : " + item.getJoinedNum() + " / " +
                            Integer.toString(item.getRecruitNum()) + " 명\r\n" + System.getProperty("line.separator") +
                            item.getRecruitContent())                           // 내용
                    .setImageUrl(Uri.parse(loadingURL))                         // 이미지 URL
                    .build();
            Log.e("aaa","내용 준비");
            shareDialog.show(linkContent);
            Log.e("aaa", "포스트");
            Toast.makeText(getApplicationContext(), "페이스북에 공유하였습니다.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}