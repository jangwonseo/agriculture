package vivz.slidenerd.agriculture.navigate;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skp.Tmap.BizCategory;
import com.skp.Tmap.MapUtils;
import com.skp.Tmap.TMapCircle;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapData.BizCategoryListenerCallback;
import com.skp.Tmap.TMapData.ConvertGPSToAddressListenerCallback;
import com.skp.Tmap.TMapData.FindAllPOIListenerCallback;
import com.skp.Tmap.TMapData.FindAroundNamePOIListenerCallback;
import com.skp.Tmap.TMapData.FindPathDataListenerCallback;
import com.skp.Tmap.TMapData.TMapPathType;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skp.Tmap.TMapInfo;
import com.skp.Tmap.TMapLabelInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapMarkerItem2;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapPolygon;
import com.skp.Tmap.TMapTapi;
import com.skp.Tmap.TMapView;
import com.skp.Tmap.TMapView.MapCaptureImageListenerCallback;
import com.skp.Tmap.TMapView.TMapLogoPositon;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.navigate.LogManager;

public class NavigateActivity extends BaseActivity implements onLocationChangedCallback
{
    @Override
    public void onLocationChange(Location location) {
        LogManager.printLog("onLocationChange " + location.getLatitude() +  " " + location.getLongitude() + " " + location.getSpeed() + " " + location.getAccuracy());
        if(m_bTrackingMode) {
            mMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    private TMapView		mMapView = null;

    private Context 		mContext;
    private ArrayList<Bitmap> mOverlayList;

    public static String mApiKey = "53cc71ce-5537-3535-b078-2bac2d238772"; // 발급받은 appKey
    public static String mBizAppID; // 발급받은 BizAppID (TMapTapi로 TMap앱 연동을 할 때 BizAppID 꼭 필요)

    private static final int[] mArrayMapButton = {
            R.id.search,
            R.id.searchCancel,
            R.id.accommodation,
            R.id.restaurant,
            R.id.gasStation,
            R.id.getMyPosition,
            R.id.setMyPosition,
            R.id.zoom_In,
            R.id.zoom_Out,
            R.id.car,
            R.id.bicycle,
            R.id.walk,
            R.id.wideView,
    };

    private 	int 		m_nCurrentZoomLevel = 0;
    private 	double 		m_Latitude  = 0;
    private     double  	m_Longitude = 0;
    private 	boolean 	m_bShowMapIcon = false;

    private 	boolean 	m_bTrafficeMode = false;
    private 	boolean 	m_bSightVisible = false;
    private 	boolean 	m_bTrackingMode = false;

    private 	boolean 	m_bOverlayMode = false;

    ArrayList<String>		mArrayID;

    ArrayList<String>		mArrayCircleID;
    private static 	int 	mCircleID;

    ArrayList<String>		mArrayLineID;
    private static 	int 	mLineID;

    ArrayList<String>		mArrayPolygonID;
    private static  int 	mPolygonID;

    ArrayList<String>       mArrayMarkerID;
    private static int 		mMarkerID;

    ArrayList<String>       mArrayMarkerID2;
    private static int 		mMarkerID2;

    TMapGpsManager gps = null;
    EditText searchTotal = null;
    TextView time = null;
    Button btn = null;
    LinearLayout menu1;
    public static TMapPoint srcPoint = new TMapPoint(37.566474D, 126.985022D);
    public static TMapPoint desPoint = new TMapPoint(37.566474D, 126.985022D);;
    private double distance = 0;
    private TMapPoint tempPoint = null;
    private ArrayList<TMapPOIItem> tempPoiItem = null;
    private int getMyPositionCnt = 0;
    private TMapPoint pathPoint = null;
    private ListView listView2;
    ArrayList<Item> data=new ArrayList<>();
    List_Adapter_Marker adapterMarker;
    public static TextView srcText;
    public static TextView desText;
    private int nRadius = 1;
    private TextView search_radius;
    private int cntBus=0;
    private Boolean showMyPositionMode = false;
    private LinearLayout _accommodation;
    private LinearLayout _restaurant;
    private LinearLayout _gasStation;
    private LinearLayout _getMyPosition;
    private LinearLayout _setMyPosition;
    private LinearLayout _car;
    private LinearLayout _bicycle;
    private LinearLayout _walk;
    private LinearLayout _searchCancel;

    private boolean menu1_pressed = false;
    private boolean menu2_pressed = false;
    private boolean setMyPosition_pressed = false;
    private boolean getMyPosition_pressed = false;
    private boolean zoomIn_pressed = false;
    private boolean zoomOut_pressed = false;
    private boolean searchClean_pressed = false;

    //phpDown task;

    /**
     * onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigate);

        //task = new phpDown();
        mContext = this;
        mMapView = new TMapView(this);

        addView(mMapView);

        gps = new TMapGpsManager(NavigateActivity.this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps();

        navigateSettingPopupActivity.srcPoint = new TMapPoint(mMapView.getLatitude(), mMapView.getLongitude());

        configureMapView();

        initView();
				/*
		btn = (Button) findViewById(R.id.navi);
		btn.bringToFront();
		*/
        final Handler handler = new Handler();
        searchTotal = (EditText) findViewById(R.id.searchTotal);
        searchTotal.setOnKeyListener(
                new View.OnKeyListener()
                {
                    public boolean onKey(View v, int keyCode, KeyEvent event)
                    {
                        if(keyCode ==  KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction())
                        {
                            listView2.invalidateViews();
                            removeMarker();
                            search();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    setText();
                                    listView2.invalidateViews();
                                }
                            }, 3000);
                            listView2.invalidateViews();
                            return true;
                        }
                        return false;
                    }
                });




        time = (TextView) findViewById(R.id.time);
        listView2 = (ListView) findViewById(R.id.listView2);
        srcText = (TextView) findViewById(R.id.srcText);
        desText = (TextView) findViewById(R.id.desText);
        search_radius = (TextView) findViewById(R.id.search_radius);
        adapterMarker=new List_Adapter_Marker(mContext,R.layout.item,data);
        listView2.setAdapter(adapterMarker);
        _accommodation = (LinearLayout) findViewById(R.id._accommodation);
        _restaurant = (LinearLayout) findViewById(R.id._restaurant);
        _gasStation = (LinearLayout) findViewById(R.id._gasStation);
        _setMyPosition = (LinearLayout) findViewById(R.id._setMyPosition);
        _getMyPosition = (LinearLayout) findViewById(R.id._getMyPosition);
        _car = (LinearLayout) findViewById(R.id._car);
        _bicycle = (LinearLayout) findViewById(R.id._bicycle);
        _walk = (LinearLayout) findViewById(R.id._walk);
        _searchCancel = (LinearLayout) findViewById(R.id._searchCancel);

        Log.i("asd123", "listView2.setAdapter(adapterMarker);");

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //이부분이 리스트 클릭 시 다른 액티비티를 띄우는 부분

                Intent intent = new Intent(NavigateActivity.this, navigateSettingPopupActivity.class);
                Log.i("asd", " adapterMarker.getItem(position) : " + adapterMarker.getItem(position));
                intent.putExtra("item", adapterMarker.getItem(position)); // 리스트를 클릭하면 현재 클릭한 마을에 대한 Item 클래스를 넘겨준다.
                // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
                Log.i("asd", "intent " + intent);
                startActivity(intent);

            }

        });

        Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        String[] items = new String[]{"버스", "은행", "병원 / 약국", "편의점"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.dropdown_item);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (parentView.getItemAtPosition(position).toString()) {
                    case "버스":
                        listView2.invalidateViews();
                        removeMarker();
                        busStation();
                        listView2.invalidateViews();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                setText();
                                listView2.invalidateViews();
                            }
                        }, 3000);
                        listView2.invalidateViews();
                        break;
                    case "은행":
                        listView2.invalidateViews();
                        removeMarker();
                        bank();
                        listView2.invalidateViews();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                setText();
                                listView2.invalidateViews();
                            }
                        }, 3000);
                        listView2.invalidateViews();
                        break;
                    case "병원 / 약국":
                        listView2.invalidateViews();
                        removeMarker();
                        hospital();
                        listView2.invalidateViews();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                setText();
                                listView2.invalidateViews();
                            }
                        }, 3000);
                        listView2.invalidateViews();
                        break;
                    case "편의점":
                        listView2.invalidateViews();
                        removeMarker();
                        convenience();
                        busStation();
                        listView2.invalidateViews();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                setText();
                                listView2.invalidateViews();
                            }
                        }, 3000);
                        listView2.invalidateViews();
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                removeMarker();
            }

        });

        Spinner spinner2 = (Spinner)findViewById(R.id.spinner2);
        String[] items2 = new String[]{"1km", "3km", "5km", "10km"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        spinner2.setAdapter(adapter2);
        adapter.setDropDownViewResource(R.layout.dropdown_item);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (parentView.getItemAtPosition(position).toString()) {
                    case "1km":
                        nRadius = 1;
                        search_radius.setText("1km");
                        break;
                    case "3km":
                        nRadius = 3;
                        search_radius.setText("3km");
                        break;
                    case "5km":
                        nRadius = 5;
                        search_radius.setText("5km");
                        break;
                    case "10km":
                        nRadius = 10;
                        search_radius.setText("10km");
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                removeMarker();
            }

        });

        mArrayID = new ArrayList<String>();

        mArrayCircleID = new ArrayList<String>();
        mCircleID = 0;

        mArrayLineID = new ArrayList<String>();
        mLineID = 0;

        mArrayPolygonID = new ArrayList<String>();
        mPolygonID = 0;

        mArrayMarkerID = new ArrayList<String>();
        mMarkerID = 0;

        mArrayMarkerID2 = new ArrayList<String>();
        mMarkerID2 = 0;

        //removeMarker();
        mMapView.setTMapLogoPosition(TMapLogoPositon.POSITION_BOTTOMRIGHT);
        //mMapView.setTMapPoint(gps.getLocation().getLatitude(), gps.getLocation().getLongitude());
    }
    public void listRead(ArrayList<TMapPOIItem> poiItem){
        data.clear();
		/*int j = data.size();
		while(!data.isEmpty()){
			data.remove(j--);
		}*/
        try{
            for(int i=0; i<poiItem.size();i++) {
                Item item = new Item(poiItem.get(i).getPOIName(), poiItem.get(i).getPOIAddress().replace("null", ""),
                        poiItem.get(i).getDistance(mMapView.getLocationPoint()), poiItem.get(i).getPOIPoint().getLatitude(), poiItem.get(i).getPOIPoint().getLongitude());
                data.add(item);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * setSKPMapApiKey()에 ApiKey를 입력 한다.
     * setSKPMapBizappId()에 mBizAppID를 입력한다.
     * -> setSKPMapBizappId는 TMapTapi(TMap앱 연동)를 사용할때 BizAppID 설정 해야 한다. TMapTapi 사용하지 않는다면 setSKPMapBizappId를 하지 않아도 된다.
     */
    private void configureMapView() {
        mMapView.setSKPMapApiKey(mApiKey);
        mMapView.setSKPMapBizappId(mBizAppID);
    }
    /**
     * initView - 버튼에 대한 리스너를 등록한다.
     */
    private void initView() {
        for (int btnMapView : mArrayMapButton) {
            Button ViewButton = (Button)findViewById(btnMapView);
            try {
                ViewButton.setOnClickListener(this);
            }catch(NullPointerException ex){
                ex.printStackTrace();
            }
        }

        mMapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKPMapApikeySucceed() {
                LogManager.printLog("MainActivity SKPMapApikeySucceed");
            }

            @Override
            public void SKPMapApikeyFailed(String errorMsg) {
                LogManager.printLog("MainActivity SKPMapApikeyFailed " + errorMsg);
            }
        });

        mMapView.setOnBizAppIdListener(new TMapView.OnBizAppIdListenerCallback() {
            @Override
            public void SKPMapBizAppIdSucceed() {
                LogManager.printLog("MainActivity SKPMapBizAppIdSucceed");
            }

            @Override
            public void SKPMapBizAppIdFailed(String errorMsg) {
                LogManager.printLog("MainActivity SKPMapBizAppIdFailed " + errorMsg);
            }
        });


        mMapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
            @Override
            public void onEnableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
                getMyPositionCnt = 3;
                _getMyPosition.setBackgroundResource(R.drawable.accommodation_food7);
                LogManager.printLog("MainActivity onEnableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
            }
        });

        mMapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
            @Override
            public void onDisableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
                if(!showMyPositionMode)
                    return;

                removeMarker2();
                srcPoint = new TMapPoint(centerPoint.getLatitude(), centerPoint.getLongitude());
                showMarkerPoint(centerPoint.getLatitude(),centerPoint.getLongitude());
                LogManager.printLog("MainActivity onDisableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
            }
        });

        mMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                LogManager.printLog("MainActivity onPressUpEvent " + markerlist.size());
                return false;
            }

            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                LogManager.printLog("MainActivity onPressEvent " + markerlist.size());


                for (int i = 0; i < markerlist.size(); i++) {
                    TMapMarkerItem item = markerlist.get(i);
                    for(int j=0; j<tempPoiItem.size(); j++) {
                        if (tempPoiItem.get(j).getPOIPoint().equals(item.getTMapPoint())) {
                            distance = tempPoiItem.get(j).getDistance(gps.getLocation());
                            pathPoint = tempPoiItem.get(j).getPOIPoint();
                            Log.i("asd", "makejin3201"+pathPoint);
                        }
                    }
                    LogManager.printLog("MainActivity onPressEvent " + item.getName() + " " + item.getTMapPoint().getLatitude() + " " + item.getTMapPoint().getLongitude());

                }
                return false;
            }
        });

        mMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point) {
                LogManager.printLog("MainActivity onLongPressEvent " + markerlist.size());
            }
        });

        mMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                String strMessage = "";
                strMessage = "ID: " + markerItem.getID() + " " + "Title " + markerItem.getCalloutTitle();
            }
        });

        mMapView.setOnClickReverseLabelListener(new TMapView.OnClickReverseLabelListenerCallback() {
            @Override
            public void onClickReverseLabelEvent(TMapLabelInfo findReverseLabel) {
                if(findReverseLabel != null) {
                    LogManager.printLog("MainActivity setOnClickReverseLabelListener " + findReverseLabel.id + " / " + findReverseLabel.labelLat
                            + " / " + findReverseLabel.labelLon + " / " + findReverseLabel.labelName);

                }
            }
        });

        m_nCurrentZoomLevel = -1;
        m_bShowMapIcon = false;
        m_bTrafficeMode = false;
        m_bSightVisible = false;
        m_bTrackingMode = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps.CloseGps();
        if(mOverlayList != null){
            mOverlayList.clear();
        }
    }
    /**
     * onClick Event
     */
    @Override
    public void onClick(View v) {
        final Handler handler = new Handler();
        switch(v.getId()) {
            case R.id.search:
                listView2.invalidateViews();
                removeMarker();
                search();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        setText();
                        listView2.invalidateViews();
                    }
                }, 3000);
                listView2.invalidateViews();
                break;
            case R.id.searchCancel:
                searchClean(); break;
            case R.id.accommodation:
                menu1_pressed = !menu1_pressed;
                if(!menu1_pressed) {
                    _accommodation.setBackgroundResource(R.drawable.accommodation_food2);
                    break;
                }else {
                    _accommodation.setBackgroundResource(R.drawable.accommodation_food34af);
                    _restaurant.setBackgroundResource(R.drawable.accommodation_food3);
                    _gasStation.setBackgroundResource(R.drawable.accommodation_food4);
                    listView2.invalidateViews();
                    removeMarker();
                    accommodation();
                    listView2.invalidateViews();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            setText();
                            listView2.invalidateViews();
                        }
                    }, 3000);
                    listView2.invalidateViews();
                }
                break;
            case R.id.restaurant:
                menu1_pressed = !menu1_pressed;
                if(!menu1_pressed) {
                    _restaurant.setBackgroundResource(R.drawable.accommodation_food3);
                    break;
                }else {
                    _restaurant.setBackgroundResource(R.drawable.accommodation_food35af);
                    _accommodation.setBackgroundResource(R.drawable.accommodation_food2);
                    _gasStation.setBackgroundResource(R.drawable.accommodation_food4);
                    listView2.invalidateViews();
                    removeMarker();
                    restaurant();
                    listView2.invalidateViews();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            setText();
                            listView2.invalidateViews();
                        }
                    }, 3000);
                    listView2.invalidateViews();
                }
                break;
            case R.id.gasStation:
                menu1_pressed = !menu1_pressed;
                if(!menu1_pressed) {
                    _gasStation.setBackgroundResource(R.drawable.accommodation_food4);
                    break;
                }else {
                    _gasStation.setBackgroundResource(R.drawable.accommodation_food36af);
                    _restaurant.setBackgroundResource(R.drawable.accommodation_food3);
                    _accommodation.setBackgroundResource(R.drawable.accommodation_food2);
                    listView2.invalidateViews();
                    removeMarker();
                    gasStation();
                    listView2.invalidateViews();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            setText();
                            listView2.invalidateViews();
                        }
                    }, 3000);
                    listView2.invalidateViews();
                }
                break;
            case R.id.getMyPosition: {
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
                break;
            }
            case R.id.setMyPosition:
                setMyPosition_pressed = !setMyPosition_pressed;
                if(!setMyPosition_pressed){
                    _setMyPosition.setBackgroundResource(R.drawable.accommodation_food6);
                }else{
                    _setMyPosition.setBackgroundResource(R.drawable.accommodation_food41af);
                    setMyPosition();
                    break;
                }
            case R.id.zoom_In:mapZoomIn(); break;
            case R.id.zoom_Out: mapZoomOut(); break;
            case R.id.car:
                menu2_pressed = !menu2_pressed;
                if(!menu2_pressed) {
                    _car.setBackgroundResource(R.drawable.accommodation_food11);
                    break;
                }else {
                    _car.setBackgroundResource(R.drawable.accommodation_food44af);
                    _bicycle.setBackgroundResource(R.drawable.accommodation_food12);
                    _walk.setBackgroundResource(R.drawable.accommodation_food14);
                }
                car(); drawCarPath(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint); break;
            case R.id.bicycle:
                menu2_pressed = !menu2_pressed;
                if(!menu2_pressed) {
                    _bicycle.setBackgroundResource(R.drawable.accommodation_food12);
                    break;
                }else {
                    _bicycle.setBackgroundResource(R.drawable.accommodation_food45af);
                    _car.setBackgroundResource(R.drawable.accommodation_food11);
                    _walk.setBackgroundResource(R.drawable.accommodation_food14);
                }
                bicycle(); drawBicyclePath(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint); break;
            case R.id.walk:
                menu2_pressed = !menu2_pressed;
                if(!menu2_pressed) {
                    _walk.setBackgroundResource(R.drawable.accommodation_food14);
                    break;
                }else {
                    _walk.setBackgroundResource(R.drawable.accommodation_food47af);
                    _car.setBackgroundResource(R.drawable.accommodation_food11);
                    _bicycle.setBackgroundResource(R.drawable.accommodation_food12);
                }
                walk(); drawPedestrianPath(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint); break;
            case R.id.wideView: wideView(); break;
        }
    }
    public void wideView(){
        TMapData tmapdata = new TMapData();
        Intent intent = new Intent(NavigateActivity.this,
                FullScreenMapActivity.class);
        //Log.i("asd", "lat : " + mMapView.getCenterPoint().getLatitude() +  "lon : " + mMapView.getCenterPoint().getLongitude());
        double[] point = new double[2];
        point[0] = mMapView.getCenterPoint().getLatitude();
        point[1] = mMapView.getCenterPoint().getLongitude();
        String position = "";
        try {
            position = tmapdata.convertGpsToAddress(point[0], point[1]);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "다시 시도해주세요", Toast.LENGTH_LONG).show();
        }
        intent.putExtra("point", point); // 리스트를 클릭하면 현재 클릭한 마을에 대한 Item 클래스를 넘겨준다.
        intent.putExtra("position", position);
        // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
        Log.i("asd", "intent " + intent);
        startActivity(intent);
    }
    public void setMyPosition(){
        showMyPositionMode = !showMyPositionMode;
        if(showMyPositionMode){
            removeMarker2();
            showMarkerPoint(mMapView.getLatitude(),mMapView.getLongitude());
        }else {
            Log.i("sad", "navigateActivity.srcPoint :" + navigateSettingPopupActivity.srcPoint + "srcPoint : "+srcPoint);
            navigateSettingPopupActivity.srcPoint = new TMapPoint(srcPoint.getLatitude(), srcPoint.getLongitude());
        }
        srcText.setText("현재 위치");
    }
    public void refresh(){
        adapterMarker.notifyDataSetChanged();
    }
    public void setStartPoint(){
        Log.i("setStartPoint", "setStartPoint123");
        srcText.setText("asd");
    }
    public void choose(TMapPoint point){
        tempPoint = point;
    }
    public void setText(){
        time.setText("");
    }
    /*404m 기준(다산-담헌)
    자동차 - 2분
    자전거 - 3분
    걷기 - 7분*/
    public void car(){
        try {
            distance = MapUtils.getDistance(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
            Double d = distance * 0.1 / 60;
            Integer i = d.intValue();

            if(i.intValue()/60!=0){
                Integer hour = i.intValue()/60;
                Integer minute =  i.intValue()%60;
                time.setText( hour.toString() + "시간 " + minute.toString() + "분");
            }else {

                time.setText("      "+ i.toString() + "분");
            }
        }catch(Exception ex){
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "출발지와 목적지를 설정해주세요.", Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    public void bicycle(){
        try{
            distance = MapUtils.getDistance(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
            Double d = distance * 0.45 / 60;
            Integer i = d.intValue();
            if(i.intValue()/60!=0){
                Integer hour = i.intValue()/60;
                Integer minute =  i.intValue()%60;
                time.setText( hour.toString() + "시간 " + minute.toString() + "분");
            }else {
                time.setText("      "+i.toString() + "분");
            }
        }catch(Exception ex){
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "출발지와 목적지를 설정해주세요.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void walk(){
        try{
            distance = MapUtils.getDistance(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
            Double d = distance * 1.04 / 60;
            Integer i = d.intValue();
            if(i.intValue()/60!=0){
                Integer hour = i.intValue()/60;
                Integer minute =  i.intValue()%60;
                time.setText( hour.toString() + "시간 " + minute.toString() + "분");
            }else {
                time.setText("      "+i.toString() + "분");
            }
        }catch(Exception ex){
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "출발지와 목적지를 설정해주세요.", Toast.LENGTH_LONG).show();
                }
            });
        }
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
    public void convenience() {
        TMapData tmapdata = new TMapData();

        TMapPoint point = mMapView.getCenterPoint();

        tmapdata.findAroundNamePOI(point, "편의점", nRadius, 10, new FindAroundNamePOIListenerCallback() {

            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "요청 정보에 대한 결과값이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    showMarkerPoint2(poiItem);
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                + item.getPOIAddress().replace("null", ""));
                    }
                }
            }
        });
    }
    public void hospital() {
        TMapData tmapdata = new TMapData();

        TMapPoint point = mMapView.getCenterPoint();

        tmapdata.findAroundNamePOI(point, "약국;내과;소아과;보건소;한의원", nRadius, 10, new FindAroundNamePOIListenerCallback() {

            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "요청 정보에 대한 결과값이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    showMarkerPoint2(poiItem);
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                + item.getPOIAddress().replace("null", ""));
                    }
                }
            }
        });
    }
    public void bank() {
        TMapData tmapdata = new TMapData();

        TMapPoint point = mMapView.getCenterPoint();

        tmapdata.findAroundNamePOI(point, "은행;ATM", nRadius, 10, new FindAroundNamePOIListenerCallback() {

            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "요청 정보에 대한 결과값이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    showMarkerPoint2(poiItem);
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                + item.getPOIAddress().replace("null", ""));
                    }
                }
            }
        });
    }
    public void busStation() {
        cntBus++;
        if(cntBus==1)
            return;
        TMapData tmapdata = new TMapData();

        TMapPoint point = mMapView.getCenterPoint();

        tmapdata.findAroundNamePOI(point, "버스", nRadius, 10, new FindAroundNamePOIListenerCallback() {

            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "요청 정보에 대한 결과값이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    showMarkerPoint2(poiItem);
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                + item.getPOIAddress().replace("null", ""));
                    }
                }
            }
        });
    }
    public void gasStation() {
        TMapData tmapdata = new TMapData();

        TMapPoint point = mMapView.getCenterPoint();

        tmapdata.findAroundNamePOI(point, "주유소;충전소", nRadius, 10, new FindAroundNamePOIListenerCallback() {

            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "요청 정보에 대한 결과값이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    showMarkerPoint2(poiItem);
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                + item.getPOIAddress().replace("null", ""));
                    }
                }
            }
        });
    }
    public void restaurant() {
        TMapData tmapdata = new TMapData();

        TMapPoint point = mMapView.getCenterPoint();

        tmapdata.findAroundNamePOI(point, "한식;중식;일식;TV맛집;양식", nRadius, 10, new FindAroundNamePOIListenerCallback() {

            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "요청 정보에 대한 결과값이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    showMarkerPoint2(poiItem);


                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                + item.getPOIAddress().replace("null", ""));
                    }
                }
            }

        });
    }
    public void accommodation() {
        TMapData tmapdata = new TMapData();

        TMapPoint point = mMapView.getCenterPoint();

        tmapdata.findAroundNamePOI(point, "숙박", nRadius, 10, new FindAroundNamePOIListenerCallback() {

            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem == null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "요청 정보에 대한 결과값이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    showMarkerPoint2(poiItem);
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                + item.getPOIAddress().replace("null", ""));
                    }
                }
            }
        });
    }
    public void searchClean() {
        searchTotal.setText("");
    }
    public void search() {

        final String strData = searchTotal.getText().toString();
        TMapData tmapdata = new TMapData();
        Log.i("str", "makejin : " + strData);
        tmapdata.findAllPOI(strData, new FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                showMarkerPoint2(poiItem);
                if (poiItem.size() == 0) {
                    return;
                }
                mMapView.setCenterPoint(poiItem.get(0).getPOIPoint().getLongitude(), poiItem.get(0).getPOIPoint().getLatitude(), true);
                for (int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = poiItem.get(i);

                    LogManager.printLog("POI Name: " + item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                            "Point: " + item.getPOIPoint().toString());
                }
            }
        });
    }
    public TMapPoint randomTMapPoint() {
        double latitude = ((double)Math.random() ) * (37.575113-37.483086) + 37.483086;
        double longitude = ((double)Math.random() ) * (127.027359-126.878357) + 126.878357;

        latitude = Math.min(37.575113, latitude);
        latitude = Math.max(37.483086, latitude);

        longitude = Math.min(127.027359, longitude);
        longitude = Math.max(126.878357, longitude);

        LogManager.printLog("randomTMapPoint" + latitude + " " + longitude);

        TMapPoint point = new TMapPoint(latitude, longitude);

        return point;
    }
    public void animateTo() {
        TMapPoint point = randomTMapPoint();
        mMapView.setCenterPoint(point.getLongitude(), point.getLatitude(), true);
    }
    public Bitmap overlayMark(Bitmap bmp1, Bitmap bmp2, int width, int height) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());

        int marginLeft = 7;
        int marginTop = 5;

        if(width >= 1500 || height > 1500) {
            bmp2 = Bitmap.createScaledBitmap(bmp2, bmp1.getWidth() - 40, bmp1.getHeight() - 50, true);
            marginLeft = 20;
            marginTop = 10;
        } else if(width >= 1200 || height > 1200) {
            bmp2 = Bitmap.createScaledBitmap(bmp2, bmp1.getWidth() - 22, bmp1.getHeight() - 35, true);
            marginLeft = 11;
            marginTop = 7;
        } else {
            bmp2 = Bitmap.createScaledBitmap(bmp2, bmp1.getWidth() - 15, bmp1.getHeight() - 25, true);
        }

        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, marginLeft, marginTop, null);
        return bmOverlay;
    }
    /**
     * mapZoomIn
     * 지도를 한단계 확대한다.
     */
    public void mapZoomIn() {
        mMapView.MapZoomIn();
    }
    /**
     * mapZoomOut
     * 지도를 한단계 축소한다.
     */
    public void mapZoomOut() {
        mMapView.MapZoomOut();
    }
    /**
     * getZoomLevel
     * 현재 줌의 레벨을 가지고 온다.
     */
    public void getZoomLevel() {
        int nCurrentZoomLevel = mMapView.getZoomLevel();
    }
    /**
     * setZoomLevel
     * Zoom Level을 설정한다.
     */
    public void setZoomLevel() {
        final String[] arrString = getResources().getStringArray(R.array.a_zoomlevel);
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Select Zoom Level")
                .setSingleChoiceItems(R.array.a_zoomlevel, m_nCurrentZoomLevel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        m_nCurrentZoomLevel = item;
                        dialog.dismiss();
                        mMapView.setZoomLevel(Integer.parseInt(arrString[item]));
                    }
                }).show();
    }
    /**
     * seetMapType
     * Map의 Type을 설정한다.
     */
    public void setMapType() {
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Select MAP Type")
                .setSingleChoiceItems(R.array.a_maptype, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        LogManager.printLog("Set Map Type " + item);
                        dialog.dismiss();
                        mMapView.setMapType(item);
                    }
                }).show();
    }
    /**
     * setLocationPoint
     * 현재위치로 표시될 좌표의 위도,경도를 설정한다.
     */
    public void setLocationPoint() {
        double 	Latitude  = gps.getLocation().getLatitude();
        double  Longitude =  gps.getLocation().getLongitude();

        LogManager.printLog("setLocationPoint " + Latitude + " " + Longitude);

        mMapView.setLocationPoint(Longitude, Latitude);
    }
    /**
     * setMapIcon
     * 현재위치로 표시될 아이콘을 설정한다.
     */
    public void setMapIcon(TMapView mMapView) {
        m_bShowMapIcon = !m_bShowMapIcon;

        if (m_bShowMapIcon) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.accommodation_food33);
            mMapView.setIcon(bitmap);
        }
        mMapView.setIconVisibility(m_bShowMapIcon);
    }
    /**
     * setCompassMode
     * 단말의 방항에 따라 움직이는 나침반모드로 설정한다.
     */
    public void setCompassMode(boolean bool) {
        mMapView.setCompassMode(bool);
    }
    /**
     * getIsCompass
     * 나침반모드의 사용여부를 반환한다.
     */
    public void getIsCompass() {
        Boolean bGetIsCompass = mMapView.getIsCompass();
    }
    /**
     * setTrafficeInfo
     * 실시간 교통정보를 표출여부를 설정한다.
     */
    public void setTrafficeInfo() {
        m_bTrafficeMode = !m_bTrafficeMode;
        mMapView.setTrafficInfo(m_bTrafficeMode);
    }
    /**
     * setSightVisible
     * 시야표출여부를 설정한다.
     */
    public void setSightVisible(boolean bool) {
        setLocationPoint();
        mMapView.setTMapPoint(gps.getLocation().getLatitude(), gps.getLocation().getLongitude(), true);
        m_bSightVisible = !m_bSightVisible;
        mMapView.setSightVisible(bool);
    }
    /**
     * setTrackingMode
     * 화면중심을 단말의 현재위치로 이동시켜주는 트래킹모드로 설정한다.
     */
    public void setTrackingMode() {
        m_bTrackingMode = !m_bTrackingMode;
        mMapView.setTrackingMode(m_bTrackingMode);
    }
    /**
     * getIsTracking
     * 트래킹모드의 사용여부를 반환한다.
     */
    public void getIsTracking() {
        Boolean bIsTracking = mMapView.getIsTracking();
    }
    /**
     * addTMapCircle()
     * 지도에 서클을 추가한다.
     */
    public void addTMapCircle() {
        TMapCircle circle = new TMapCircle();

        circle.setRadius(300);
        circle.setLineColor(Color.BLUE);
        circle.setAreaAlpha(50);
        circle.setCircleWidth((float)10);
        circle.setRadiusVisible(true);

        TMapPoint point = randomTMapPoint();
        circle.setCenterPoint(point);

        String strID = String.format("circle%d", mCircleID++);
        mMapView.addTMapCircle(strID, circle);
        mArrayCircleID.add(strID);
    }
    /**
     * removeTMapCircle
     * 지도상의 해당 서클을 제거한다.
     */
    public void removeTMapCircle() {
        if(mArrayCircleID.size() <= 0)
            return;

        String strCircleID = mArrayCircleID.get(mArrayCircleID.size() - 1);
        mMapView.removeTMapCircle(strCircleID);
        mArrayCircleID.remove(mArrayCircleID.size() - 1);
    }
    public void showMarkerPoint2(ArrayList<TMapPOIItem> poiItem) {
        if(poiItem == null) {
            return;
        }
        ArrayList<Bitmap> list = null;

        tempPoiItem =  poiItem;
        listRead(poiItem);
        adapterMarker=new List_Adapter_Marker(mContext,R.layout.item,data);
        adapterMarker.notifyDataSetChanged();

//adapterMarker.updateResults(data);


        for (int i = 0; i < poiItem.size(); i++) {

            TMapMarkerItem marker1 = new TMapMarkerItem();
            String strID = String.format("%s", poiItem.get(i).getPOIName());
            //String strID = String.format("%02d", i);

            marker1.setID(strID);
            marker1.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.poi_dot));
            //marker1.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_red));
            marker1.setTMapPoint(poiItem.get(i).getPOIPoint());
            // marker1.setTMapPoint(randomTMapPoint());
            marker1.setName(poiItem.get(i).getPOIName());
            marker1.setVisible(marker1.VISIBLE);
            marker1.setCalloutTitle(poiItem.get(i).getPOIName());
            marker1.setCalloutSubTitle(String.format("거리 : %.3fkm", poiItem.get(i).getDistance(mMapView.getLocationPoint())/1000.0));

            marker1.setCanShowCallout(true);
            if (list == null) {
                list = new ArrayList<Bitmap>();
            }

            list.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_dot));
            list.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.end));

            //marker1.setAnimationIcons(list);
            mMapView.addMarkerItem(strID, marker1);
            mArrayMarkerID.add(strID);
        }

        mMapView.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
            @Override
            public void onCalloutMarker2ClickEvent(String id, TMapMarkerItem2 markerItem2) {
                LogManager.printLog("ClickEvent " + " id " + id + " " + markerItem2.latitude + " " + markerItem2.longitude);
                String strMessage = "ClickEvent " + " id " + id + " " + markerItem2.latitude + " " + markerItem2.longitude;

            }
        });

    }
    public void showMarkerPoint(double lat, double lon) {
        Bitmap bitmap = null;

        TMapPoint point = new TMapPoint(lat, lon);

        TMapMarkerItem item1 = new TMapMarkerItem();

        bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.accommodation_food55);

        item1.setTMapPoint(point);
        item1.setVisible(item1.VISIBLE);

        item1.setIcon(bitmap);

        String strID2 = String.format("pmarker%d", mMarkerID2++);

        mMapView.addMarkerItem(strID2, item1);
        mArrayMarkerID2.add(strID2);

    }
    public void removeMarker() {
        Log.i("asd", "cnt : " + mArrayMarkerID.size());
        if(mArrayMarkerID.size() <= 0 )
            return;
        int cnt=0;
        String strMarkerID;
        while(mArrayMarkerID.size() > 0){
            cnt++;
            strMarkerID = mArrayMarkerID.get(mArrayMarkerID.size() - 1);
            Log.i("asd", "strMarkerID : " + strMarkerID + "mMapView : " + mMapView );
            mMapView.removeMarkerItem(strMarkerID);
            mArrayMarkerID.remove(mArrayMarkerID.size() - 1);
        }
        Log.i("asd", "cnt : " + cnt);
    }
    public void removeMarker2() {
        Log.i("asd", "cnt : " + mArrayMarkerID2.size());
        if(mArrayMarkerID2.size() <= 0 )
            return;
        int cnt=0;
        String strMarkerID2;
        while(mArrayMarkerID2.size() > 0){
            cnt++;
            strMarkerID2 = mArrayMarkerID2.get(mArrayMarkerID2.size() - 1);
            //Log.i("asd", "strMarkerID : " + strMarkerID + "mMapView : " + mMapView );
            mMapView.removeMarkerItem(strMarkerID2);
            mArrayMarkerID2.remove(mArrayMarkerID2.size() - 1);
        }
        Log.i("asd", "cnt : " + cnt);
    }
    /**
     * moveFrontMarker
     * 마커를 맨 앞으로 표시 하도록 한다.
     * showMarkerPoint() 함수를 먼저 클릭을 한 후, 클릭을 해야 함.
     */
    public void moveFrontMarker() {
        TMapMarkerItem item = mMapView.getMarkerItemFromID("1");
        mMapView.bringMarkerToFront(item);
    }
    /**
     * moveBackMarker
     * 마커를 맨 뒤에 표시하도록 한다.
     * showMarkerPoint() 함수를 먼저 클릭을 한 후, 클릭을 해야 함.
     */
    public void moveBackMarker() {
        TMapMarkerItem item = mMapView.getMarkerItemFromID("1");
        mMapView.sendMarkerToBack(item);
    }
    /**
     * drawLine
     * 지도에 라인을 추가한다.
     */
    public void drawLine() {
        TMapPolyLine polyLine = new TMapPolyLine();
        polyLine.setLineColor(Color.BLUE);
        polyLine.setLineWidth(5);

        for (int i = 0; i < 5; i++) {
            TMapPoint point = randomTMapPoint();
            polyLine.addLinePoint(point);
        }

        String strID = String.format("line%d", mLineID++);
        mMapView.addTMapPolyLine(strID, polyLine);
        mArrayLineID.add(strID);
    }
    /**
     * erasePolyLine
     * 지도에 라인을 제거한다.
     */
    public void erasePolyLine() {
        if(mArrayLineID.size() <= 0)
            return;

        String strLineID = mArrayLineID.get(mArrayLineID.size() - 1);
        mMapView.removeTMapPolyLine(strLineID);
        mArrayLineID.remove(mArrayLineID.size() - 1);
    }
    /**
     * drawPolygon
     * 지도에 폴리곤에 그린다.
     */
    public void drawPolygon() {
        int Min = 3;
        int Max = 10;
        int rndNum = (int)(Math.random() * ( Max - Min ));

        LogManager.printLog("drawPolygon" + rndNum);

        TMapPolygon polygon = new TMapPolygon();
        polygon.setLineColor(Color.BLUE);
        polygon.setPolygonWidth((float)4);
        polygon.setAreaAlpha(2);

        TMapPoint point = null;

        if (rndNum < 3) {
            rndNum = rndNum + (3 - rndNum);
        }

        for (int i = 0; i < rndNum; i++) {
            point = randomTMapPoint();
            polygon.addPolygonPoint(point);
        }

        String strID = String.format("polygon%d", mPolygonID++);
        mMapView.addTMapPolygon(strID, polygon);
        mArrayPolygonID.add(strID);
    }
    /**
     * erasePolygon
     * 지도에 그려진 폴리곤을 제거한다.
     */
    public void removeTMapPolygon() {
        if(mArrayPolygonID.size() <= 0)
            return;

        String strPolygonID = mArrayPolygonID.get(mArrayPolygonID.size() - 1);

        LogManager.printLog("erasePolygon " + strPolygonID);

        mMapView.removeTMapPolygon(strPolygonID);
        mArrayPolygonID.remove(mArrayPolygonID.size() - 1);
    }
    /**
     * drawMapPath
     * 지도에 시작-종료 점에 대해서 경로를 표시한다.
     */
	/*
	public void drawMapPath(TMapPoint point2) {
		TMapPoint point1 = gps.getLocation();

		TMapData tmapdata = new TMapData();

		tmapdata.findPathData(point1, point2, new FindPathDataListenerCallback() {

			@Override
			public void onFindPathData(TMapPolyLine polyLine) {
				mMapView.addTMapPath(polyLine);
			}
		});
	}
*/
    private String getContentFromNode(Element item, String tagName){
        NodeList list = item.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            if (list.item(0).getFirstChild() != null) {
                return list.item(0).getFirstChild().getNodeValue();
            }
        }
        return null;
    }
    /**
     * displayMapInfo()
     * POI들이 모두 표시될 수 있는 줌레벨 결정함수와 중심점리턴하는 함수
     */
    public void displayMapInfo() {
		/*
		TMapPoint point1 = mMapView.getCenterPoint();
		TMapPoint point2 = randomTMapPoint();
		*/
        TMapPoint point1 = new TMapPoint(37.541642248630524, 126.99599611759186);
        TMapPoint point2 = new TMapPoint(37.541243493556976, 126.99659830331802);
        TMapPoint point3 = new TMapPoint(37.540909826755524, 126.99739581346512);
        TMapPoint point4 = new TMapPoint(37.541080713272095, 126.99874675273895);

        ArrayList<TMapPoint> point = new ArrayList<TMapPoint>();

        point.add(point1);
        point.add(point2);
        point.add(point3);
        point.add(point4);

        TMapInfo info = mMapView.getDisplayTMapInfo(point);

        String strInfo = "Center Latitude" + info.getTMapPoint().getLatitude() + "Center Longitude" + info.getTMapPoint().getLongitude() +
                "Level " + info.getTMapZoomLevel();

    }
    /**
     * removeMapPath
     * 경로 표시를 삭제한다.
     */
    public void removeMapPath() {
        mMapView.removeTMapPath();
    }
    /**
     * naviGuide
     * 길안내
     */
    public void naviGuide(ArrayList<TMapPOIItem> poiItem) {
        int i;
        TMapData tmapdata = new TMapData();
        TMapPoint point1 = mMapView.getCenterPoint();
        TMapPoint point2 = null;
        if(poiItem.size() != 0) {
            for (i = 0; i < poiItem.size(); i++) {
                point2 = poiItem.get(i).getPOIPoint();
                tmapdata.findPathData(point1, point2, new FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        mMapView.addTMapPath(polyLine);
                    }
                });
            }
        }
    }
    public void drawCarPath(TMapPoint point1, TMapPoint point2) {

        if(point2 == null)
            return;

        removeMapPath();

        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataWithType(TMapPathType.CAR_PATH, point1, point2, new FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                mMapView.addTMapPath(polyLine);

            }
        });

    }
    /*
        public void drawCarPath(TMapPoint point2) {

            if(point2 == null)
                return;

            removeMapPath();
            TMapPoint point1 = gps.getLocation();

            TMapData tmapdata = new TMapData();

            tmapdata.findPathDataWithType(TMapPathType.CAR_PATH, point1, point2, new FindPathDataListenerCallback() {
                @Override
                public void onFindPathData(TMapPolyLine polyLine) {
                    mMapView.addTMapPath(polyLine);

                }
            });

        }
        */
    public void drawPedestrianPath(TMapPoint point1,TMapPoint point2) {
        if(point2 == null)
            return;

        removeMapPath();

        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataWithType(TMapPathType.PEDESTRIAN_PATH, point1, point2, new FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.BLUE);
                mMapView.addTMapPath(polyLine);
            }
        });
    }

    public void drawBicyclePath(TMapPoint point1,TMapPoint point2) {
        if(point2 == null)
            return;

        removeMapPath();

        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataWithType(TMapPathType.BICYCLE_PATH, point1, point2, new FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.GREEN);
                mMapView.addTMapPath(polyLine);
            }
        });
    }

    /**
     * getCenterPoint
     * 지도의 중심점을 가지고 온다.
     */
    public void getCenterPoint() {
        TMapPoint point = mMapView.getCenterPoint();
    }

    /**
     * findAllPoi
     * 통합검색 POI를 요청한다.
     */
	/*
	public void findAllPoi() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("POI 통합 검색");

		final EditText input = new EditText(this);
		builder.setView(input);

		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String strData = input.getText().toString();
				TMapData tmapdata = new TMapData();

				tmapdata.findAllPOI(strData, new FindAllPOIListenerCallback() {
					@Override
					public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
						showMarkerPoint2(poiItem);
						Log.i("Str", "showMarkerPoint2: " + poiItem);
						for (int i = 0; i < poiItem.size(); i++) {
							TMapPOIItem  item = poiItem.get(i);
							LogManager.printLog("POI Name: " + item.getPOIName().toString() + ", " +
									"Address: " + item.getPOIAddress().replace("null", "") + ", " +
									"Point: " + item.getPOIPoint().toString());
						}
					}
				});
			}
		});
		builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.show();

	}

*/
    /**
     * convertToAddress
     * 지도에서 선택한 지점을 주소를 변경요청한다.
     */
    public void convertToAddress() {
        TMapPoint point = mMapView.getCenterPoint();

        TMapData tmapdata = new TMapData();

        if (mMapView.isValidTMapPoint(point)) {
            tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new ConvertGPSToAddressListenerCallback() {
                @Override
                public void onConvertToGPSToAddress(String strAddress) {
                    LogManager.printLog("선택한 위치의 주소는 " + strAddress);
                }
            });

//		    tmapdata.geoCodingWithAddressType("F02", "서울시", "구로구", "새말로", "6", "", new GeoCodingWithAddressTypeListenerCallback() {
//
//				@Override
//				public void onGeoCodingWithAddressType(TMapGeocodingInfo geocodingInfo) {
//					LogManager.printLog(">>> strMatchFlag : " + geocodingInfo.strMatchFlag);
//					LogManager.printLog(">>> strLatitude : " + geocodingInfo.strLatitude);
//					LogManager.printLog(">>> strLongitude : " + geocodingInfo.strLongitude);
//					LogManager.printLog(">>> strCity_do : " + geocodingInfo.strCity_do);
//					LogManager.printLog(">>> strGu_gun : " + geocodingInfo.strGu_gun);
//					LogManager.printLog(">>> strLegalDong : " + geocodingInfo.strLegalDong);
//					LogManager.printLog(">>> strAdminDong : " + geocodingInfo.strAdminDong);
//					LogManager.printLog(">>> strBunji : " + geocodingInfo.strBunji);
//					LogManager.printLog(">>> strNewMatchFlag : " + geocodingInfo.strNewMatchFlag);
//					LogManager.printLog(">>> strNewLatitude : " + geocodingInfo.strNewLatitude);
//					LogManager.printLog(">>> strNewLongitude : " + geocodingInfo.strNewLongitude);
//					LogManager.printLog(">>> strNewRoadName : " + geocodingInfo.strNewRoadName);
//					LogManager.printLog(">>> strNewBuildingIndex : " + geocodingInfo.strNewBuildingIndex);
//					LogManager.printLog(">>> strNewBuildingName : " + geocodingInfo.strNewBuildingName);
//				}
//			});
        }
    }
    /**
     * getBizCategory
     * 업종별 category를 요청한다.
     */
    public void getBizCategory() {
        TMapData tmapdata = new TMapData();

        tmapdata.getBizCategory(new BizCategoryListenerCallback() {
            @Override
            public void onGetBizCategory(ArrayList<BizCategory> poiItem) {
                for (int i = 0; i < poiItem.size(); i++) {
                    BizCategory item = poiItem.get(i);
                    LogManager.printLog("UpperBizCode " + item.upperBizCode + " " + "UpperBizName " + item.upperBizName);
                    LogManager.printLog("MiddleBizcode " + item.middleBizCode + " " + "MiddleBizName " + item.middleBizName);
                }
            }
        });
    }
    /**
     * getAroundBizPoi
     * 업종별 주변검색 POI 데이터를 요청한다.
     */
    public void getAroundBizPoi() {
        TMapData tmapdata = new TMapData();

        TMapPoint point = mMapView.getCenterPoint();
        tmapdata.findAroundNamePOI(point, "주유소", 1, 10, new FindAroundNamePOIListenerCallback() {
            //tmapdata.findAroundNamePOI(point, "주유소;숙박;TV맛집;한식;중식", 1, 99, new FindAroundNamePOIListenerCallback() {

            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                if(poiItem == null){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "요청 정보에 대한 결과값이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                    //Toast.makeText(getApplicationContext(), "요청 정보에 대한 결과값이 없습니다.", Toast.LENGTH_LONG).show();
                }else {
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        showMarkerPoint2(poiItem);
                        //naviGuide(poiItem);
                        LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                + item.getPOIAddress().replace("null", "") + ", " + "Point: Lat " + item.getPOIPoint().getLatitude() + " Lon " + item.getPOIPoint().getLongitude());
                    }
                }
            }
        });
    }
    public void setTileType() {
        AlertDialog dlg = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Select MAP Tile Type")
                .setSingleChoiceItems(R.array.a_tiletype, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        LogManager.printLog("Set Map Tile Type " + item);
                        dialog.dismiss();
                        mMapView.setTileType(item);
                    }
                }).show();
    }
    public void setBicycle() {
        mMapView.setBicycleInfo(!mMapView.IsBicycleInfo());
    }
    public void setBicycleFacility() {
        mMapView.setBicycleFacilityInfo(!mMapView.isBicycleFacilityInfo());
    }
    public void invokeRoute() {
        final TMapPoint point = mMapView.getCenterPoint();
        TMapData tmapdata = new TMapData();

        if(mMapView.isValidTMapPoint(point)) {
            tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new ConvertGPSToAddressListenerCallback() {
                @Override
                public void onConvertToGPSToAddress(String strAddress) {
                    TMapTapi tmaptapi = new TMapTapi(NavigateActivity.this);
                    float fY = (float)point.getLatitude();
                    float fX = (float)point.getLongitude();
                    tmaptapi.invokeRoute(strAddress, fX, fY);
                }
            });
        }
    }
    public void invokeSetLocation() {
        final TMapPoint point = mMapView.getCenterPoint();
        TMapData tmapdata = new TMapData();

        tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new ConvertGPSToAddressListenerCallback() {
            @Override
            public void onConvertToGPSToAddress(String strAddress) {
                TMapTapi tmaptapi = new TMapTapi(NavigateActivity.this);
                float fY = (float) point.getLatitude();
                float fX = (float) point.getLongitude();
                tmaptapi.invokeSetLocation(strAddress, fX, fY);
            }
        });
    }
    public void invokeSearchProtal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("T MAP 통합 검색");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strSearch = input.getText().toString();

                new Thread() {
                    @Override
                    public void run() {
                        TMapTapi tmaptapi = new TMapTapi(NavigateActivity.this);
                        if (strSearch.trim().length() > 0)
                            tmaptapi.invokeSearchPortal(strSearch);
                    }
                }.start();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public void tmapInstall() {
        new Thread() {
            @Override
            public void run() {
                TMapTapi tmaptapi = new TMapTapi(NavigateActivity.this);
                Uri uri = Uri.parse(tmaptapi.getTMapDownUrl().get(0));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        }.start();
    }
    public void captureImage() {
        mMapView.getCaptureImage(20, new MapCaptureImageListenerCallback() {

            @Override
            public void onMapCaptureImage(Bitmap bitmap) {

                String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

                File path = new File(sdcard + File.separator + "image_write");
                if (!path.exists())
                    path.mkdir();

                File fileCacheItem = new File(path.toString() + File.separator + System.currentTimeMillis() + ".png");
                OutputStream out = null;

                try {
                    fileCacheItem.createNewFile();
                    out = new FileOutputStream(fileCacheItem);

                    bitmap.compress(CompressFormat.JPEG, 90, out);

                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private boolean bZoomEnable = false;
    public void disableZoom() {
        bZoomEnable = !bZoomEnable;
        mMapView.setUserScrollZoomEnable(bZoomEnable);
    }
    public void timeMachine() {
        TMapData tmapdata = new TMapData();

        HashMap<String, String> pathInfo = new HashMap<String, String>();
        pathInfo.put("rStName", "T Tower");
        pathInfo.put("rStlat", Double.toString(37.566474));
        pathInfo.put("rStlon", Double.toString(126.985022));
        pathInfo.put("rGoName", "신도림");
        pathInfo.put("rGolat", "37.50861147");
        pathInfo.put("rGolon", "126.8911457");
        pathInfo.put("type", "arrival");

        Date currentTime = new Date();
        tmapdata.findTimeMachineCarPath(pathInfo,  currentTime, null);
    }
}

