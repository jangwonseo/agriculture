package vivz.slidenerd.agriculture.navigate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skp.Tmap.TMapPoint;

import java.io.Serializable;

import vivz.slidenerd.agriculture.R;

/**
 * Created by makejin on 2015-09-20.
 */
public class MainActivity2Activity extends Activity {
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
        setContentView(R.layout.activity_main_activity2);


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
        //  srcPoint = i.getPoint();
        distance.setText(""+i.getDistance());
        lat.setText(""+i.getLat());
        lon.setText(""+i.getLon());
        distance2 = Double.parseDouble(distance.getText().toString());
        btn1.setOnClickListener(btn1Listener);
        btn2.setOnClickListener(btn2Listener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action b ar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
/*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }


}
