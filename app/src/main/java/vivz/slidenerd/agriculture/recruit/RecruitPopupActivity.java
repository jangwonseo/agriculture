package vivz.slidenerd.agriculture.recruit;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;

import vivz.slidenerd.agriculture.R;

public class RecruitPopupActivity extends Activity implements View.OnClickListener{

    TextView txtvVilageName;
    TextView txtvMissionName;
    TextView txtvRecruitNum;
    TextView txtvRecruitContent;
    TextView txtvRecruitTerm;
    TextView txtvReward;
    WebView webvRecPopup;

    String imgUrl = "http://218.150.181.131/seo/image/"; // 사진이 없을 경우 디폴트 사진 띄우는 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recruit_popup);

        Intent intent = getIntent();
        RecruitListItem item = (RecruitListItem)intent.getSerializableExtra("item");
        Log.e("popup", "successed");

        txtvVilageName = (TextView)findViewById(R.id.txtvVilageName);
        txtvVilageName.setText(item.getVilageName());
        txtvMissionName = (TextView)findViewById(R.id.txtvMissionName);
        txtvMissionName.setText(item.getMissionName());
        txtvRecruitTerm = (TextView)findViewById(R.id.txtvRecruitTerm);
        txtvRecruitTerm.setText(item.getTermStart() + " ~ " + item.getTermEnd());
        txtvRecruitNum = (TextView)findViewById(R.id.txtvRecruitNum);
        txtvRecruitNum.setText(item.getRecruitNum());

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
    public void onClick(View v) {

    }
}
