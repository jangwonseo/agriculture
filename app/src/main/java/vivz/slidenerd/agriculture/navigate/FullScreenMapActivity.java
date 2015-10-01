package vivz.slidenerd.agriculture.navigate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import vivz.slidenerd.agriculture.R;

public class FullScreenMapActivity extends Activity implements TMapGpsManager.onLocationChangedCallback {
    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode) {
            mMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    private Context mContext;
    private 	boolean 	m_bTrackingMode = false;
    private TMapView mMapView = null;
    private RelativeLayout contentView;
    private TextView myPosition;
    private Button getMyPosition;
    private int getMyPositionCnt=0;
    private LinearLayout _getMyPosition;
    private boolean m_bShowMapIcon;
    TMapGpsManager gps = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_map);

        Intent intent = getIntent();
        //Serializable item = intent.getSerializableExtra("point");

        gps = new TMapGpsManager(FullScreenMapActivity.this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps();

        m_bShowMapIcon = false;
        mContext = this;
        myPosition = (TextView) findViewById(R.id.myPosition);
        getMyPosition = (Button) findViewById(R.id.getMyPosition);
        _getMyPosition = (LinearLayout) findViewById(R.id._getMyPosition);
        double point[];
        String position;
        point = intent.getDoubleArrayExtra("point");
        position = intent.getStringExtra("position");
        contentView = (RelativeLayout) findViewById(R.id.contentView);
        mMapView = new TMapView(this);
        contentView.addView(mMapView);
        mMapView.setTMapPoint(point[0], point[1], true);
        myPosition.setText(position);
        getMyPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyPositionCnt++;
                getMyPosition();
                if(getMyPositionCnt%3 == 0) { //기본 (0단계)
                    _getMyPosition.setBackgroundResource(R.drawable.accommodation_food7);
                    setCompassMode(false);
                    setSightVisible(false);
                }else if(getMyPositionCnt%3 == 1) { //1단계 : 현재 위치화면
                    _getMyPosition.setBackgroundResource(R.drawable.accommodation_food42af);
                }else if(getMyPositionCnt%3 == 2){ //2단계 : 나침반모드
                    _getMyPosition.setBackgroundResource(R.drawable.accommodation_food58);
                    setCompassMode(true);
                    setSightVisible(true);
                }
            }
        });
    }

    public void getMyPosition() {
        try {
            TMapPoint point = gps.getLocation();
            mMapView.setCenterPoint(point.getLongitude(), point.getLatitude(), true);
            setMapIcon(mMapView);
            mMapView.setIconVisibility(true);
        }catch(Exception ex){
            Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
        }
    }

    public void setLocationPoint() {
        double 	Latitude  = gps.getLocation().getLatitude();
        double  Longitude =  gps.getLocation().getLongitude();

        LogManager.printLog("setLocationPoint " + Latitude + " " + Longitude);

        mMapView.setLocationPoint(Longitude, Latitude);
    }

    public void setMapIcon(TMapView mMapView) {
        m_bShowMapIcon = !m_bShowMapIcon;

        if (m_bShowMapIcon) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.accommodation_food33);
            mMapView.setIcon(bitmap);
        }
        mMapView.setIconVisibility(m_bShowMapIcon);
    }

    public void setSightVisible(boolean bool) {
        setLocationPoint();
        mMapView.setTMapPoint(gps.getLocation().getLatitude(), gps.getLocation().getLongitude(), true);
        mMapView.setSightVisible(bool);
    }

    public void setCompassMode(boolean bool) {
        mMapView.setCompassMode(bool);
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
