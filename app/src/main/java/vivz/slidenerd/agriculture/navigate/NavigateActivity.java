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
import android.graphics.Typeface;
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
import vivz.slidenerd.agriculture.RecycleUtils;
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

    public static TMapView		mMapView = null;

    public static Context 		mContext;
    private ArrayList<Bitmap> mOverlayList;

    public static String mApiKey = "53cc71ce-5537-3535-b078-2bac2d238772"; // 발급받은 appKey
    public static String mBizAppID; // 발급받은 BizAppID (TMapTapi로 TMap앱 연동을 할 때 BizAppID 꼭 필요)

    private static final int[] mArrayMapButton = {
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
            R.id.srcButton,
            R.id.desButton,
            R.id.submit,
    };
    //
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
    public static TextView time = null;
    Button btn = null;
    LinearLayout menu1;
    public static TMapPoint srcPoint = new TMapPoint(37.566474D, 126.985022D);
    public static TMapPoint desPoint = new TMapPoint(37.566474D, 126.985022D);;
    private double distance = 0;
    private TMapPoint tempPoint = null;
    private ArrayList<TMapPOIItem> tempPoiItem = null;
    private int getMyPositionCnt = 0;
    private TMapPoint pathPoint = null;
    ArrayList<Item> data=new ArrayList<>();
    List_Adapter_Marker adapterMarker;
    private int nRadius = 1;
    private TextView search_radius;
    private Boolean showMyPositionMode = false;
    private LinearLayout _accommodation;
    private LinearLayout _restaurant;
    private LinearLayout _gasStation;
    private LinearLayout _getMyPosition;
    private LinearLayout _setMyPosition;
    private LinearLayout _car;
    private LinearLayout _bicycle;
    private LinearLayout _walk;

    private boolean menu1_pressed = false;
    private boolean menu2_pressed = false;
    private boolean setMyPosition_pressed = false;
    private boolean getMyPosition_pressed = false;
    private boolean zoomIn_pressed = false;
    private boolean zoomOut_pressed = false;
    private boolean searchClean_pressed = false;

    public static Button srcButton;
    public static Button desButton;

    private TextView srcTextView;

    public final static  int isClicked_none = 0;
    public final static  int isClicked_accommodation = 1;
    public final static  int isClicked_bank = 2;
    public final static  int isClicked_gasStation = 3;
    public final static  int isClicked_restaurant = 4;
    public static int isClicked_menu1 = isClicked_none;
    Intent intent = null;

    Typeface yunGothicFont;

    public static Boolean isSubmit = false;

    /**
     * onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

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

        try {
            intent  = getIntent();
            if (intent == null) {
                Log.i("menu1", "step1");
                return;
            }

            switch(isClicked_menu1){
                case isClicked_accommodation:
                    Toast.makeText(getApplicationContext(), "해당 마을의 <숙박> 위치정보 로딩 중....", Toast.LENGTH_SHORT).show();
                    search_accommodation(intent.getStringExtra("addr"));
                    break;
                case isClicked_bank:
                    Toast.makeText(getApplicationContext(), "해당 마을의 <은행> 위치정보 로딩 중....", Toast.LENGTH_SHORT).show();
                    search_bank(intent.getStringExtra("addr"));
                    break;
                case isClicked_gasStation:
                    Toast.makeText(getApplicationContext(), "해당 마을의 <주유소> 위치정보 로딩 중....", Toast.LENGTH_SHORT).show();
                    search_gasStation(intent.getStringExtra("addr"));
                    break;
                case isClicked_restaurant:
                    Toast.makeText(getApplicationContext(), "해당 마을의 <식당> 위치정보 로딩 중....", Toast.LENGTH_SHORT).show();
                    search_restaurant(intent.getStringExtra("addr"));
                    break;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }


				/*
		btn = (Button) findViewById(R.id.navi);
		btn.bringToFront();
		*/

/*
        TMapPoint point =  mMapView.getCenterPoint();

        TMapData tmapdata = new TMapData();
        if (mMapView.isValidTMapPoint(point)) {
            tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new ConvertGPSToAddressListenerCallback() {
                @Override
                public void onConvertToGPSToAddress(String strAddress) {
                    LogManager.printLog("선택한 위치의 주소는 " + strAddress);
                    time.setText(strAddress);
                    //navigateSetMyPositionPopup.address.setText(strAddress);

                }
            });
        }
*/
        final Handler handler = new Handler();

        time = (TextView) findViewById(R.id.time);
        time.setTypeface(yunGothicFont);
        time.setText("현재");
        search_radius = (TextView) findViewById(R.id.search_radius);
        search_radius.setTypeface(yunGothicFont);
        adapterMarker=new List_Adapter_Marker(mContext,R.layout.item,data);
        _accommodation = (LinearLayout) findViewById(R.id._accommodation);
        _restaurant = (LinearLayout) findViewById(R.id._restaurant);
        _gasStation = (LinearLayout) findViewById(R.id._gasStation);
        _setMyPosition = (LinearLayout) findViewById(R.id._setMyPosition);
        _getMyPosition = (LinearLayout) findViewById(R.id._getMyPosition);
        _car = (LinearLayout) findViewById(R.id._car);
        _bicycle = (LinearLayout) findViewById(R.id._bicycle);
        _walk = (LinearLayout) findViewById(R.id._walk);
        srcButton = (Button) findViewById(R.id.srcButton);
        desButton = (Button) findViewById(R.id.desButton);

        srcTextView =(TextView) findViewById(R.id.srcTextView);
        srcTextView.setTypeface(yunGothicFont);

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        String[] items = new String[]{"선택", "버스", "은행", "병원 / 약국", "편의점"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (parentView.getItemAtPosition(position).toString()) {
                    case "선택":
                        removeMarker();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                setText();
                            }
                        }, 3000);
                        break;
                    case "버스":
                        removeMarker();
                        busStation();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                setText();
                            }
                        }, 3000);
                        break;
                    case "은행":
                        removeMarker();
                        bank();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                setText();
                            }
                        }, 3000);
                        break;
                    case "병원 / 약국":
                        removeMarker();
                        hospital();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                setText();
                            }
                        }, 3000);
                        break;
                    case "편의점":
                        removeMarker();
                        convenience();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                setText();
                            }
                        }, 3000);
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
        adapter2.setDropDownViewResource(R.layout.dropdown_item);

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



                TMapPoint point = srcPoint;

                TMapData tmapdata = new TMapData();
                if (mMapView.isValidTMapPoint(point)) {
                    tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new ConvertGPSToAddressListenerCallback() {
                        @Override
                        public void onConvertToGPSToAddress(String strAddress) {
                            LogManager.printLog("선택한 위치의 주소는 " + strAddress);
                            time.setText(strAddress);
                            refresh();
                        }
                    });
                }


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
                if(tempPoiItem == null){
                    return false;
                }

                Intent intent = new Intent(NavigateActivity.this, navigateSettingPopupActivity.class);


                for (int i = 0; i < markerlist.size(); i++) {
                    TMapMarkerItem item = markerlist.get(i);
                    for(int j=0; j<tempPoiItem.size(); j++) {
                        if (tempPoiItem.get(j).getPOIPoint().equals(item.getTMapPoint())) {
                            distance = tempPoiItem.get(j).getDistance(gps.getLocation());
                            pathPoint = tempPoiItem.get(j).getPOIPoint();
                            Log.i("asd", "makejin3201"+pathPoint);

                            Log.i("asd", " adapterMarker.getItem(position) : " + adapterMarker.getItem(j));
                            intent.putExtra("item", adapterMarker.getItem(j)); // 리스트를 클릭하면 현재 클릭한 마을에 대한 Item 클래스를 넘겨준다.
                            // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
                            Log.i("asd", "intent " + intent);
                            startActivity(intent);

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
        System.gc();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        gps.CloseGps();
        if(mOverlayList != null){
            mOverlayList.clear();
        }
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {

        System.gc();
        super.onStop();
    }

    /**
     * onClick Event
     */
    @Override
    public void onClick(View v) {
        final Handler handler = new Handler();
        switch(v.getId()) {
            case R.id.accommodation:
                menu1_pressed = !menu1_pressed;
                if(!menu1_pressed) {
                    _accommodation.setBackgroundResource(R.drawable.accommodation_food2);
                    break;
                }else {
                    _accommodation.setBackgroundResource(R.drawable.accommodation_food34af);
                    _restaurant.setBackgroundResource(R.drawable.accommodation_food3);
                    _gasStation.setBackgroundResource(R.drawable.accommodation_food4);
                    removeMarker();
                    accommodation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            setText();
                        }
                    }, 3000);
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
                    removeMarker();
                    restaurant();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            setText();
                        }
                    }, 3000);
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
                    removeMarker();
                    gasStation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            setText();
                        }
                    }, 3000);
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
                refresh();
                break;
            }
            case R.id.setMyPosition:
                setMyPosition_pressed = !setMyPosition_pressed;
                setMyPosition();
                if(!setMyPosition_pressed){
                    _setMyPosition.setBackgroundResource(R.drawable.accommodation_food6);
                }else{
                    _setMyPosition.setBackgroundResource(R.drawable.accommodation_food41af);
                }

                break;
            case R.id.zoom_In:mapZoomIn(); break;
            case R.id.zoom_Out: mapZoomOut(); break;
            case R.id.srcButton:
                goNavigateSearch();
                break;
            case R.id.desButton:
                goNavigateSearch();
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    public static void submit() {
        if(srcButton.getText() == "") {
            Toast.makeText(mContext, "출발지를 설정해주세요.", Toast.LENGTH_LONG).show();
        } else if(desButton.getText() == "") {
            Toast.makeText(mContext, "도착지를 설정해주세요.", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(mContext, navigateSearchPopup.class);
            mContext.startActivity(intent);
        }
    }
    public void goNavigateSearch(){
        Intent intent = new Intent(NavigateActivity.this,
                NavigateSearch.class);
        startActivity(intent);
    }
    public void setMyPosition(){
        showMyPositionMode = !showMyPositionMode;
        if(showMyPositionMode){
            removeMarker2();
            Toast.makeText(mContext, "한번 더 누르시면 현재 마커 위치에 \n출발지/도착지를 설정할 수 있습니다.", Toast.LENGTH_LONG).show();
            showMarkerPoint(mMapView.getLatitude(), mMapView.getLongitude());
        }else {
            TMapPoint tempPoint2 =  new TMapPoint(srcPoint.getLatitude(), srcPoint.getLongitude());
            Intent intent = new Intent(NavigateActivity.this, navigateSetMyPositionPopup.class);
            Log.i("asd", "convertToAddress : " + convertToAddress());
            Log.i("asd", "time.getText().toString() : " + time.getText().toString());
            convertToAddress();
            Item tempItem = new Item(null, time.getText().toString(), MapUtils.getDistance(new TMapPoint(mMapView.getLatitude(), mMapView.getLongitude()), gps.getLocation()) , mMapView.getLatitude(), mMapView.getLongitude());
            Log.i("asd", "time.getText().toString() : " + time.getText().toString());
            intent.putExtra("item", tempItem);
            startActivity(intent);

        }
        srcTextView.setText("출발지");
    }

    public void refresh(){
        adapterMarker.notifyDataSetChanged();
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
        distance = MapUtils.getDistance(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
        Double d = distance * 0.1 / 60;
        Integer i = d.intValue();

        if(i.intValue()/60!=0){
            Integer hour = i.intValue()/60;
            Integer minute =  i.intValue()%60;
            Toast.makeText(mContext, hour.toString() + "시간 " + minute.toString() + "분", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(mContext,"      " + i.toString() + "분", Toast.LENGTH_LONG).show();
        }
    }

    public void bicycle(){
        distance = MapUtils.getDistance(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
        Double d = distance * 0.45 / 60;
        Integer i = d.intValue();

        if(i.intValue()/60!=0){
            Integer hour = i.intValue()/60;
            Integer minute =  i.intValue()%60;
            Toast.makeText(mContext, hour.toString() + "시간 " + minute.toString() + "분", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(mContext,"      " + i.toString() + "분", Toast.LENGTH_LONG).show();
        }
    }
    public void walk(){
        distance = MapUtils.getDistance(navigateSettingPopupActivity.srcPoint, navigateSettingPopupActivity.desPoint);
        Double d = distance * 1.04 / 60;
        Integer i = d.intValue();

        if(i.intValue()/60!=0){
            Integer hour = i.intValue()/60;
            Integer minute =  i.intValue()%60;
            Toast.makeText(mContext, hour.toString() + "시간 " + minute.toString() + "분", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(mContext,"      " + i.toString() + "분", Toast.LENGTH_LONG).show();
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
                    displayMapInfo2(poiItem);
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
                    displayMapInfo2(poiItem);
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
                    displayMapInfo2(poiItem);
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
                    displayMapInfo2(poiItem);
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
                    displayMapInfo2(poiItem);
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
                    displayMapInfo2(poiItem);

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
                    displayMapInfo2(poiItem);
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

    public void search_accommodation(String addr) {
        if(addr==null)
            return;
        final String addr2 = addr;
        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI(addr2, new FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem.size() == 0) {
                    return;
                }

                mMapView.setCenterPoint(poiItem.get(0).getPOIPoint().getLongitude(), poiItem.get(0).getPOIPoint().getLatitude(), true);

                TMapData tmapdata = new TMapData();
                tmapdata.findAroundNamePOI(poiItem.get(0).getPOIPoint(), "숙박", nRadius, 33, new FindAroundNamePOIListenerCallback() {

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
                            displayMapInfo2(poiItem);
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                        + item.getPOIAddress().replace("null", ""));
                            }
                        }
                    }
                });
            }
        });
    }
    public void search_bank(String addr) {
        if(addr==null)
            return;
        final String addr2 = addr;
        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI(addr2, new FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem.size() == 0) {
                    return;
                }

                mMapView.setCenterPoint(poiItem.get(0).getPOIPoint().getLongitude(), poiItem.get(0).getPOIPoint().getLatitude(), true);

                TMapData tmapdata = new TMapData();
                tmapdata.findAroundNamePOI(poiItem.get(0).getPOIPoint(), "은행;ATM", nRadius, 33, new FindAroundNamePOIListenerCallback() {

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
                            displayMapInfo2(poiItem);
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                        + item.getPOIAddress().replace("null", ""));
                            }
                        }
                    }
                });
            }
        });
    }
    public void search_gasStation(String addr) {
        if(addr==null)
            return;
        final String addr2 = addr;
        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI(addr2, new FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem.size() == 0) {
                    return;
                }

                mMapView.setCenterPoint(poiItem.get(0).getPOIPoint().getLongitude(), poiItem.get(0).getPOIPoint().getLatitude(), true);

                TMapData tmapdata = new TMapData();
                tmapdata.findAroundNamePOI(poiItem.get(0).getPOIPoint(), "주유소;충전소", nRadius, 33, new FindAroundNamePOIListenerCallback() {

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
                            displayMapInfo2(poiItem);
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                        + item.getPOIAddress().replace("null", ""));
                            }
                        }
                    }
                });
            }
        });
    }
    public void search_restaurant(String addr) {
        if(addr==null)
            return;
        final String addr2 = addr;
        TMapData tmapdata = new TMapData();
        tmapdata.findAllPOI(addr2, new FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                if (poiItem.size() == 0) {
                    return;
                }

                mMapView.setCenterPoint(poiItem.get(0).getPOIPoint().getLongitude(), poiItem.get(0).getPOIPoint().getLatitude(), true);

                TMapData tmapdata = new TMapData();
                tmapdata.findAroundNamePOI(poiItem.get(0).getPOIPoint(), "한식;중식;일식;TV맛집;양식", nRadius, 33, new FindAroundNamePOIListenerCallback() {

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
                            displayMapInfo2(poiItem);
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                LogManager.printLog("POI Name: " + item.getPOIName() + "," + "Address: "
                                        + item.getPOIAddress().replace("null", ""));
                            }
                        }
                    }
                });
            }
        });
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
    public void displayMapInfo2(ArrayList<TMapPOIItem> poiItem) {
        ArrayList<TMapPoint> point = new ArrayList<TMapPoint>();

        for (int i = 0; i < poiItem.size(); i++)
            point.add(poiItem.get(i).getPOIPoint());

        TMapInfo info = NavigateActivity.mMapView.getDisplayTMapInfo(point);

        String strInfo = "Center Latitude" + info.getTMapPoint().getLatitude() + "Center Longitude" + info.getTMapPoint().getLongitude() +
                "Level " + info.getTMapZoomLevel();

        NavigateActivity.mMapView.setCenterPoint(info.getTMapPoint().getLongitude(), info.getTMapPoint().getLatitude(), true);
        NavigateActivity.mMapView.setZoomLevel(info.getTMapZoomLevel());

        Log.i("zoom", "zoomlevel : " + info.getTMapZoomLevel());

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
    public void getCenterPoint() {
        TMapPoint point = mMapView.getCenterPoint();
    }

    public String convertToAddress() {
        TMapPoint point = mMapView.getCenterPoint();

        TMapData tmapdata = new TMapData();
        if (mMapView.isValidTMapPoint(point)) {
            tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude(), new ConvertGPSToAddressListenerCallback() {
                @Override
                public void onConvertToGPSToAddress(String strAddress) {
                    LogManager.printLog("선택한 위치의 주소는 " + strAddress);
                    time.setText(strAddress);
                    Log.i("asd", "time : " + time.getText().toString());

                }
            });
        }
        return time.getText().toString();
    }


}

