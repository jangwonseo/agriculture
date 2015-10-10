package vivz.slidenerd.agriculture.navigate;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skp.Tmap.MapUtils;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapInfo;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import java.util.ArrayList;

import vivz.slidenerd.agriculture.R;

public class navigateSearchPopup extends Activity {
    Button car;
    Button bicycle;
    Button walk;
    Context mContext;
    private double distance=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_search_popup);
        car = (Button) findViewById((R.id.car));
        bicycle = (Button) findViewById((R.id.bicycle));
        walk = (Button) findViewById((R.id.walk));
        mContext = this;

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Log.i("Asd", "NavigateActivity.srcPoint : " + NavigateActivity.srcPoint + " NavigateActivity.desPoint" + NavigateActivity.desPoint);
                drawCarPath(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
            }
        });

        bicycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Log.i("Asd", "NavigateActivity.srcPoint : " + NavigateActivity.srcPoint + " NavigateActivity.desPoint" + NavigateActivity.desPoint);
                drawBicyclePath(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
            }
        });

        walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Log.i("Asd", "NavigateActivity.srcPoint : " + NavigateActivity.srcPoint + " NavigateActivity.desPoint" + NavigateActivity.desPoint);
                drawPedestrianPath(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
            }
        });
    }

    public void drawCarPath(TMapPoint point1, TMapPoint point2) {

        if(point2 == null)
            return;

        removeMapPath();

        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                NavigateActivity.mMapView.addTMapPath(polyLine);

            }
        });

        ArrayList<TMapPoint> point = new ArrayList<TMapPoint>();
        point.add(point1);
        point.add(point2);

        displayMapInfo(point);

        car();

    }

    public void drawBicyclePath(TMapPoint point1, TMapPoint point2) {

        if(point2 == null)
            return;

        removeMapPath();

        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataWithType(TMapData.TMapPathType.BICYCLE_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                NavigateActivity.mMapView.addTMapPath(polyLine);

            }
        });

        ArrayList<TMapPoint> point = new ArrayList<TMapPoint>();
        point.add(point1);
        point.add(point2);

        displayMapInfo(point);

        bicycle();
    }

    public void drawPedestrianPath(TMapPoint point1, TMapPoint point2) {

        if(point2 == null)
            return;

        removeMapPath();

        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                NavigateActivity.mMapView.addTMapPath(polyLine);

            }
        });

        ArrayList<TMapPoint> point = new ArrayList<TMapPoint>();
        point.add(point1);
        point.add(point2);

        displayMapInfo(point);

        walk();
    }
    /*404m 기준(다산-담헌)
        자동차 - 2분
        자전거 - 3분
        걷기 - 7분*/
    public void car(){
        distance = MapUtils.getDistance(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
        Double d = distance * 0.1 / 60;
        Integer i = d.intValue();

        if(i.intValue()/60!=0){
            Integer hour = i.intValue()/60;
            Integer minute =  i.intValue()%60;
            Toast.makeText(mContext, "예상시간 : " + hour.toString() + "시간 " + minute.toString() + "분", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(mContext,"예상시간 : " + i.toString() + "분", Toast.LENGTH_LONG).show();
        }
    }

    public void bicycle(){
        distance = MapUtils.getDistance(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
        Double d = distance * 0.45 / 60;
        Integer i = d.intValue();

        if(i.intValue()/60!=0){
            Integer hour = i.intValue()/60;
            Integer minute =  i.intValue()%60;
            Toast.makeText(mContext, "예상시간 : " +  hour.toString() + "시간 " + minute.toString() + "분", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(mContext,"예상시간 : " + i.toString() + "분", Toast.LENGTH_LONG).show();
        }
    }
    public void walk(){
        distance = MapUtils.getDistance(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
        Double d = distance * 1.04 / 60;
        Integer i = d.intValue();

        if(i.intValue()/60!=0){
            Integer hour = i.intValue()/60;
            Integer minute =  i.intValue()%60;
            Toast.makeText(mContext, "예상시간 : " + hour.toString() + "시간 " + minute.toString() + "분", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(mContext,"예상시간 : " + i.toString() + "분", Toast.LENGTH_LONG).show();
        }
    }
    public void displayMapInfo(ArrayList<TMapPoint> points) {
        ArrayList<TMapPoint> point = new ArrayList<TMapPoint>();
        for (int i = 0; i < points.size(); i++)
            point.add(points.get(i));

        TMapInfo info = NavigateActivity.mMapView.getDisplayTMapInfo(point);

        String strInfo = "Center Latitude" + info.getTMapPoint().getLatitude() + "Center Longitude" + info.getTMapPoint().getLongitude() +
                "Level " + info.getTMapZoomLevel();

        NavigateActivity.mMapView.setCenterPoint(info.getTMapPoint().getLongitude(), info.getTMapPoint().getLatitude(), true);
        NavigateActivity.mMapView.setZoomLevel(info.getTMapZoomLevel());

        Log.i("zoom", "zoomlevel : " + info.getTMapZoomLevel());

    }


    public void removeMapPath() {
        NavigateActivity.mMapView.removeTMapPath();
    }
}