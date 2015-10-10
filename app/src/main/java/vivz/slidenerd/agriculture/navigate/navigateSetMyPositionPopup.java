package vivz.slidenerd.agriculture.navigate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;

import java.io.Serializable;

import vivz.slidenerd.agriculture.R;

public class navigateSetMyPositionPopup extends Activity {
    Button src;
    Button des;
    Context mContext;
    private double distance=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_setmyposition_popup);
        src = (Button) findViewById((R.id.src));
        des = (Button) findViewById((R.id.des));
        mContext = this;

        Intent intent = getIntent();
        Serializable item = intent.getSerializableExtra("item");

        Item i = (Item)item;
        final Item i2 = i;


        src.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateActivity.srcButton.setText("지정 출발지");
                navigateSettingPopupActivity.srcPoint = new TMapPoint(i2.getLat(),i2.getLon());
                onBackPressed();
                Toast.makeText(getApplicationContext(), "출발지로 설정되었습니다.", Toast.LENGTH_LONG).show();

                TMapPoint point = NavigateActivity.mMapView.getCenterPoint();

                TMapData tmapdata = new TMapData();
                if (NavigateActivity.mMapView.isValidTMapPoint(point)) {
                    tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                        @Override
                        public void onConvertToGPSToAddress(String strAddress) {
                            LogManager.printLog("선택한 위치의 주소는 " + strAddress);
                            NavigateActivity.time.setText(strAddress);
                            Log.i("asd", "time : " + NavigateActivity.time.getText().toString());

                        }
                    });
                }
            }
        });

        des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigateActivity.desButton.setText("지정 도착지");
                navigateSettingPopupActivity.desPoint = new TMapPoint(i2.getLat(), i2.getLon());
                onBackPressed();
                Toast.makeText(getApplicationContext(), "도착지로 설정되었습니다.", Toast.LENGTH_LONG).show();

                TMapPoint point = NavigateActivity.mMapView.getCenterPoint();

                TMapData tmapdata = new TMapData();
                if (NavigateActivity.mMapView.isValidTMapPoint(point)) {
                    tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                        @Override
                        public void onConvertToGPSToAddress(String strAddress) {
                            LogManager.printLog("선택한 위치의 주소는 " + strAddress);
                            NavigateActivity.time.setText(strAddress);
                            Log.i("asd", "time : " + NavigateActivity.time.getText().toString());

                        }
                    });
                }
            }
        });
    }

}