package vivz.slidenerd.agriculture.recruit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import vivz.slidenerd.agriculture.R;

public class RecruitPopupActivity extends Activity implements View.OnClickListener{

    //로그인 정보 가져오기
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    String sharedUserId;

    RecruitListItem item;

    TextView txtvVilageName;
    TextView txtvMissionName;
    TextView txtvRecruitNum;
    TextView txtvRecruitContent;
    TextView txtvRecruitTerm;
    TextView txtvReward;
    WebView webvRecPopup;

    Button btnPhoneCall;
    Button btnMissionJoin;


    String imgUrl = "http://218.150.181.131/seo/image/"; // 사진이 없을 경우 디폴트 사진 띄우는 경로

    phpMissionJoin missionJoin;

    // 참가 성공여부
    public static final int joinSucceed = 1;
    public static final int joinNoSucceed = 0;
    public static final int joinAlreadyIn = 2;

    JoinHandler myJoinHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recruit_popup);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();

        sharedUserId = setting.getString("info_Id", "");
        Log.e("userId : ", sharedUserId);


        Intent intent = getIntent();
        item = (RecruitListItem)intent.getSerializableExtra("item");
        Log.e("popup", "successed");

        txtvVilageName = (TextView)findViewById(R.id.txtvVilageName);
        txtvVilageName.setText(item.getVilageName());
        txtvMissionName = (TextView)findViewById(R.id.txtvMissionName);
        txtvMissionName.setText(item.getMissionName());
        txtvRecruitTerm = (TextView)findViewById(R.id.txtvRecruitTerm);
        txtvRecruitTerm.setText(item.getTermStart() + " ~ " + item.getTermEnd());
        txtvRecruitNum = (TextView)findViewById(R.id.txtvRecruitNum);
        txtvRecruitNum.setText(item.getRecruitNum());

        btnPhoneCall = (Button)findViewById(R.id.btnPhoneCall);
        btnMissionJoin = (Button)findViewById(R.id.btnMissionJoin);

        btnPhoneCall.setOnClickListener(this);
        btnMissionJoin.setOnClickListener(this);


        // 줄바꿈
        String lineEnding = item.getRecuritContent().replace("99line99end99", "\n");
        txtvRecruitContent = (TextView)findViewById(R.id.txtvRecruitContent);
        txtvRecruitContent.setText(lineEnding);
        Log.e("txtvRecruitContent", item.getRecuritContent());
        txtvReward = (TextView)findViewById(R.id.txtvReward);
        txtvReward.setText(item.getReward());

        webvRecPopup = (WebView)findViewById(R.id.webvRecPopup);

        webvRecPopup.setVerticalScrollBarEnabled(false);
        webvRecPopup.setVerticalScrollbarOverlay(false);
        webvRecPopup.setHorizontalScrollBarEnabled(false);
        webvRecPopup.setHorizontalScrollbarOverlay(false);
        webvRecPopup.setFocusableInTouchMode(false);
        webvRecPopup.setHorizontalScrollBarEnabled(false);
        webvRecPopup.setVerticalScrollBarEnabled(false);
        webvRecPopup.setInitialScale(100);
        webvRecPopup.setFocusable(false);

        String ImageURL = item.getImageURL();
        String loadingURL = null;
        if (ImageURL.equals("null") || ImageURL == null) {
            loadingURL = imgUrl + "default.png";
        } else {
            loadingURL = imgUrl + item.getImageURL();
        }

        webvRecPopup.loadDataWithBaseURL(null, creHtmlBody(loadingURL), "text/html", "utf-8", null);
        Log.e("list image path", loadingURL);


        myJoinHandler = new JoinHandler();

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPhoneCall:
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:"+item.getPhoneNum())));
                break;

            case R.id.btnMissionJoin:
                missionJoin = new phpMissionJoin();
                if ( sharedUserId.equals("") ) {
                    Toast.makeText(getApplicationContext(), "로그인을 하십시오", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    MissionItem missionItem = new MissionItem(sharedUserId, item.getIdRecruit());
                    missionJoin.execute("http://218.150.181.131/seo/phpMissionJoin.php?" + missionItem.toString());
                }
                break;
        }
    }

    // 참가하기 php
    public class phpMissionJoin extends AsyncTask<String, Integer,String> {

        Message msg = myJoinHandler.obtainMessage(); // 참가 성공 실패 핸들러

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
                msg.what = joinSucceed;
            } catch(Exception ex){
                ex.printStackTrace();
                msg.what = joinNoSucceed;
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str){

            if ( !str.contains("1 record added") ) {
                msg.what = joinNoSucceed;
            }

            if ( str.contains("key 'PRIMARY'")) {
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

    };
}
class MissionItem implements Serializable{
    private String UserId;
    private int recruitId;
    public String getUserId(){return UserId;}
    public int getRecruitId(){return recruitId;}

    public MissionItem(String UserId, int recruitId){
        this.recruitId = recruitId;
        this.UserId = UserId;
    }

    public String toString() {
        return "UserId=" + UserId + "&recruitId=" + Integer.toString(recruitId);
    }
}
