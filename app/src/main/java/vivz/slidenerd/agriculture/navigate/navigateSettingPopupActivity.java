package vivz.slidenerd.agriculture.navigate;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skp.Tmap.TMapPoint;

import java.io.Serializable;

import vivz.slidenerd.agriculture.R;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_setting_popup);


        Intent intent = getIntent();
        Serializable item = intent.getSerializableExtra("item");

        Item i = (Item)item;
        final Item i2 = i;
        btn1 = (Button) findViewById(R.id.startPoint);
        btn2 = (Button) findViewById(R.id.endPoint);
        View.OnClickListener btn1Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateActivity.srcText.setText(poiName.getText().toString());
                srcPoint = new TMapPoint(i2.getLat(), i2.getLon());
                onBackPressed();
            }
        };
        View.OnClickListener btn2Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateActivity.desText.setText(poiName.getText().toString());
                desPoint = new TMapPoint(i2.getLat(), i2.getLon());
                onBackPressed();
            }
        };

        poiName = (TextView)findViewById(R.id.poiName);
        poiAddr = (TextView)findViewById(R.id.poiAddr);
        distance = (TextView)findViewById(R.id.distance);
        lat = (TextView)findViewById(R.id.lat);
        lon = (TextView)findViewById(R.id.lon);

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