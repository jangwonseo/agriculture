package vivz.slidenerd.agriculture.navigate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapPoint;

import java.io.Serializable;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.home.HomeActivity;

public class navigateSettingPopupActivity extends Activity implements View.OnClickListener {
    TextView poiName;
    TextView poiAddr;
    TextView distance;
    TextView lat;
    TextView lon;

    Button btn1;
    Button btn2;
    public static TMapPoint srcPoint = null;
    public static TMapPoint desPoint = null;
    public static double distance2 = 0;

    private Typeface yunGothicFont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_setting_popup);

        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

        Intent intent = getIntent();
        Serializable item = intent.getSerializableExtra("item");

        Item i = (Item)item;
        final Item i2 = i;
        btn1 = (Button) findViewById(R.id.startPoint);
        btn2 = (Button) findViewById(R.id.endPoint);
        View.OnClickListener btn1Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateActivity.srcButton.setText(poiName.getText().toString());
                srcPoint = new TMapPoint(i2.getLat(), i2.getLon());
                onBackPressed();
                Toast.makeText(getApplicationContext(), "출발지로 설정되었습니다.", Toast.LENGTH_LONG).show();
                Log.i("asd", "NavigateActivity.srcButton.getText() : " + NavigateActivity.srcButton.getText() + " NavigateActivity.desButton.getText() : " + NavigateActivity.desButton.getText());
                if(NavigateActivity.srcButton.getText() != "" && NavigateActivity.desButton.getText() != "")
                    onBackPressed();
            }
        };
        View.OnClickListener btn2Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateActivity.desButton.setText(poiName.getText().toString());
                desPoint = new TMapPoint(i2.getLat(), i2.getLon());
                onBackPressed();
                Toast.makeText(getApplicationContext(), "도착지로 설정되었습니다.", Toast.LENGTH_LONG).show();
                Log.i("asd", "NavigateActivity.srcButton.getText() : " + NavigateActivity.srcButton.getText() + " NavigateActivity.desButton.getText() : " + NavigateActivity.desButton.getText());
                if(NavigateActivity.srcButton.getText() != "" && NavigateActivity.desButton.getText() != "") {
                    NavigateActivity.submit();
                    onBackPressed();
                    /*
                    Intent moveToMapIntent  = new Intent(getApplicationContext(), NavigateActivity.class);
                    moveToMapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(moveToMapIntent);
                    */
                }
            }
        };

        poiName = (TextView)findViewById(R.id.poiName);
        poiName.setTypeface(yunGothicFont);
        poiAddr = (TextView)findViewById(R.id.poiAddr);
        poiAddr.setTypeface(yunGothicFont);
        distance = (TextView)findViewById(R.id.distance);
        distance.setTypeface(yunGothicFont);
        lat = (TextView)findViewById(R.id.lat);
        lat.setTypeface(yunGothicFont);
        lon = (TextView)findViewById(R.id.lon);
        lon.setTypeface(yunGothicFont);

        poiName.setText(i.getName());
        poiAddr.setText(i.getAddr());
        distance.setText(String.format("%.3fkm", i.getDistance()/1000.0));
        lat.setText(""+i.getLat());
        lon.setText(""+i.getLon());
        distance2 = i.getDistance()/1000.0;
        btn1.setOnClickListener(btn1Listener);
        btn2.setOnClickListener(btn2Listener);

    }
    @Override
    public void onClick(View v) {

    }
}