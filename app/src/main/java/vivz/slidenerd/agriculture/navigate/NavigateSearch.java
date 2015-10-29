package vivz.slidenerd.agriculture.navigate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.home.HomeActivity;

public class NavigateSearch extends Activity {
    private ListView listView2;
    List_Adapter_Marker adapterMarker;
    private Context 		mContext;
    ArrayList<Item> data=new ArrayList<>();
    private Button searchButton;
    private EditText navigateText;
    private TMapView mMapView = null;
    private Typeface yunGothicFont;

    private Button backButton,moveToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_search);

        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");


        mContext = this;
        listView2 = (ListView) findViewById(R.id.listView2);
        adapterMarker=new List_Adapter_Marker(mContext, R.layout.item,data);
        listView2.setAdapter(adapterMarker);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setTypeface(yunGothicFont);
        navigateText = (EditText) findViewById(R.id.navigateText);
        navigateText.setTypeface(yunGothicFont);
        mMapView = new TMapView(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;


        backButton = (Button)findViewById(R.id.navigatesearchbackbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        moveToHomeButton = (Button)findViewById(R.id.navigatesearch_menubutton);
        moveToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(moveToHomeIntent);
                finish();
            }
        });

        navigateText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(keyCode ==  KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction())
                {
                    final Handler handler = new Handler();
                    listView2.invalidateViews();
                    search();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            navigateText.setText("");
                            listView2.invalidateViews();
                        }
                    }, 3000);
                    listView2.invalidateViews();
                    Toast toast = Toast.makeText(getApplicationContext(), "검색 중입니다.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP| Gravity.CENTER_HORIZONTAL, 0, height/4);
                    toast.show();
                    return true;
                }
                // TODO Auto-generated method stub
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                listView2.invalidateViews();
                search();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        navigateText.setText("");
                        listView2.invalidateViews();
                    }
                }, 3000);
                listView2.invalidateViews();
                Toast toast = Toast.makeText(getApplicationContext(), "검색 중입니다.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, height/4);
                toast.show();
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //이부분이 리스트 클릭 시 다른 액티비티를 띄우는 부분

                Intent intent = new Intent(NavigateSearch.this, navigateSettingPopupActivity.class);
                Log.i("asd", " adapterMarker.getItem(position) : " + adapterMarker.getItem(position));
                intent.putExtra("item", adapterMarker.getItem(position)); // 리스트를 클릭하면 현재 클릭한 마을에 대한 Item 클래스를 넘겨준다.
                // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
                Log.i("asd", "intent " + intent);
                startActivity(intent);

            }

        });
    }

    public void search(){
        final String strData = navigateText.getText().toString();
        TMapData tmapdata = new TMapData();
        Log.i("str", "makejin : " + strData);
        tmapdata.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                listRead(poiItem);
                adapterMarker = new List_Adapter_Marker(mContext, R.layout.item, data);
                adapterMarker.notifyDataSetChanged();
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
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();

        super.onDestroy();
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
