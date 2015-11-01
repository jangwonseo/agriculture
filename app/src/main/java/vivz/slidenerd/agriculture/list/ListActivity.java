package vivz.slidenerd.agriculture.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vivz.slidenerd.agriculture.DownloadImageTask;
import vivz.slidenerd.agriculture.NonLeakingWebView;
import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.home.HomeActivity;
import vivz.slidenerd.agriculture.navigate.NavigateActivity;
import vivz.slidenerd.agriculture.navigate.navigateSettingPopupActivity;
import vivz.slidenerd.agriculture.recruit.RecruitListItem;
import vivz.slidenerd.agriculture.service_prepare;

public class ListActivity extends ActionBarActivity {
    ImageView imView;
    TextView txtView;
    Button addItem; // 리스트뷰에 있는 관심있는 항목 추가 버튼
    Button backButton, menuButton;
    TextView keys;
    phpDown task;
    String themeName;
    String tempThemeName;
    public static Context mContext;
    // listview
    private ListView vilageList;
    ArrayList<Item> data = new ArrayList<>();
    List_Adapter adapter;
    //폰트설정
    public Typeface yunGothicFont;

    // 검색하기
    // phpListAutoText listAutoComTask;// DB에서 불러오기 => 기존의 검색한 php를 이용하면???
    private ArrayList<String> search_list_item = new ArrayList<String>();
    AutoCompleteTextView list_autoComplete; // 리스트 검색 자동완성
    String clickPosition = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        task = new phpDown();
        mContext = this;
        Intent themeIntent = getIntent();
        themeName = themeIntent.getExtras().getString("themeflag");
        //폰트 설정 초기화(윤고딕330)
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

        // 어떤 값이 넘어오는가에 따라서 액티비티 소제목 변경
        if (themeName.equals("experience"))
            tempThemeName = "체험여행";
        else if (themeName.equals("nature"))
            tempThemeName = "자연여행";
        else if (themeName.equals("traditional"))
            tempThemeName = "전통문화여행";
        else if (themeName.equals("wellBeing"))
            tempThemeName = "웰빙여행";
        else if (themeName.equals("kangwon"))
            tempThemeName = "강원도";
        else if (themeName.equals("kyungki"))
            tempThemeName = "경기/인천";
        else if (themeName.equals("chungnam"))
            tempThemeName = "충남/대전";
        else if (themeName.equals("chungbuk"))
            tempThemeName = "충북";
        else if (themeName.equals("jeonnam"))
            tempThemeName = "전남/광주";
        else if (themeName.equals("jeonbuk"))
            tempThemeName = "전북";
        else if (themeName.equals("kyungnam"))
            tempThemeName = "경남/부산";
        else if (themeName.equals("kyungbuk"))
            tempThemeName = "경북/대구";
        else if (themeName.equals("video"))
            tempThemeName = "생생한 체험 이야기";
        else
            tempThemeName = "기타";

        //listview
        //m_Adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
        //m_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        vilageList = (ListView) findViewById(R.id.vilageList);

        //addItem = (Button) findViewById(R.id.addItem);

        keys = (TextView) findViewById(R.id.themeKey);
        keys.setTypeface(yunGothicFont);  //윤고딕 적용


        backButton = (Button) findViewById(R.id.list_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

        menuButton = (Button) findViewById(R.id.list_menubutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(moveToHomeIntent);
                finish();
            }
        });


        keys.setText(tempThemeName);


        // ListView에 어댑터 연결
        adapter = new List_Adapter(this, R.layout.list_item, data);
        vilageList.setAdapter(adapter);

        vilageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /** 이부분이 리스트 클릭 시 다른 액티비티를 띄우는 부분 **/

                Intent intent = new Intent(getApplicationContext(),
                        ListDetailActivity.class);

                for ( int i=0 ; i < data.size() ; i++ ) {
                    if ( adapter.getItem(position).getExprnDstncId().equals(data.get(i).getExprnDstncId())) {
                        Log.e("positnItem", adapter.getItem(position).getExprnDstncId() + ", " + data.get(i).getExprnDstncId());
                        intent.putExtra("item", data.get(i));
                        break;
                    }
                }
                adapter = new List_Adapter(getApplicationContext(), R.layout.list_item, data);
                intent.putExtra("isDairy", false);
                // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();

            }
        });
        // 만약에 테마 쪽에서 넘어오면 테마에 관련된 php로 연결하기! 지도쪽에서 넘어왔다면 지도 관련 php로 연결하기
        if (themeName.equals("experience") || themeName.equals("nature") || themeName.equals("traditional") ||
                themeName.equals("wellBeing"))
            task.execute("http://218.150.181.131/seo/dataEx.php?theme=" + themeName + "");

        else if (themeName.equals("kangwon") || themeName.equals("kyungki") || themeName.equals("chungnam") ||
                themeName.equals("chungbuk") || themeName.equals("jeonnam") || themeName.equals("jeonbuk") ||
                themeName.equals("kyungnam") || themeName.equals("kyungbuk"))
            task.execute("http://218.150.181.131/seo/mapList.php?theme=" + themeName + "");

        list_autoComplete = (AutoCompleteTextView)findViewById(R.id.list_autoComplete);
        list_autoComplete.setTypeface(yunGothicFont);
        //list_autoComplete.addTextChangedListener(this);
        list_autoComplete.setAdapter(new ArrayAdapter<String>(this, R.layout.auto_complete_item, search_list_item));
        //list_autoComplete.setTextColor(Color.BLACK);
        list_autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String getString = (String) adapterView.getItemAtPosition(position); // 미션이름을 가지고 온다.
                Log.e("recLstAutoClk", getString);

                // 클릭한 아이템의 미션이름을 가져와서, 그 이름이 들어간 모든 체험들을 찾는다.
                ArrayList<Item> searchExpn = new ArrayList<Item>();
                Item searchExpnItem;

                for (int i = 0; i < data.size(); i++) {
                    Log.e("data", Integer.toString(data.size()) + ", " + data.get(i).getExprnProgrmNm() + ", " + i);
                    searchExpnItem = data.get(i); // data(현재 체험리스트)에 있는 각 하나 하나의 요소들을 꺼내어
                    if (searchExpnItem.getExprnProgrmNm().contains(getString)) { // 선택한 체험의 이름을 포함하고 있다면
                        searchExpn.add(searchExpnItem); // 새로운 배열에 추가
                    }
                }
                adapter = new List_Adapter(getApplicationContext(), R.layout.list_item, searchExpn);
                vilageList.setAdapter(adapter);
            }
        });

    }

    @Override
    protected void onDestroy() {

//Adapter가 있으면 어댑터에서 생성한 recycle메소드를 실행

        if (adapter != null) {
            adapter.recycle();
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



    // AsyncTask는 generic 클래스이기 때문에 타입을 지정해주어야 한다. < Params, Progress, Result > 부분
    /*
        AsyncTask 사용해 background작업을 구현 시 꼭 지켜야 하는 사항
        AsyncTask클래스는 항상 "subclassing" 하여 사용하여야 한다.
        AsyncTask 인스턴스는 항상 UI 스레드에서 생성한다.
        AsyncTask:execute(…) 메소드는 항상 UI 스레드에서 호출한다.
        AsyncTask:execute(…) 메소드는 생성된 AsyncTask 인스턴스 별로 꼭 한번만 사용 가능하다. 같은 인스턴스가 또 execute(…)를 실행하면 exception이 발생하며, 이는 AsyncTask:cancel(…) 메소드에 의해 작업완료 되기 전 취소된 AsyncTask 인스턴스라도 마찬가지이다. 그럼으로 background 작업이 필요할 때마다 new 연산자를 이용해 해당 작업에 대한 AsyncTask 인스턴스를 새로 생성해야 한다.
        AsyncTask의 callback 함수 onPreExecute(), doInBackground(…), onProgressUpdate(…), onPostExecute(…)는 직접 호출 하면 안 된다. (꼭 callback으로만 사용)
     */
    class phpDown extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            String line = "";
            try {
                // 텍스트 연결 url 설정
                URL url = new URL(urls[0]);
                // 이미지 url
                Log.e("tag", "url : " + urls[0]);
                // URL 페이지 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    Log.e("tag", "setUseCaches is false");
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line);
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            // JSON 구문을 파싱해서 JSONArray 객체를 생성
            try {
                Log.d("seojang", "this is a apple55555");
                JSONArray jAr = new JSONArray(str); // doInBackground 에서 받아온 문자열을 JSONArray 객체로 생성
                Log.d("seojang", "JAr 갯수 : " + jAr.length());
                for (int i = 0; i < jAr.length(); i++) {  // JSON 객체를 하나씩 추출한다.
                    JSONObject vilageName = jAr.getJSONObject(i);

                    Log.d("seojang", "정보확인하기 : " + vilageName.getString("exprnProgrmNm"));

                    Item item = new Item(vilageName.getString("thumbUrlCours1"), vilageName.getString("exprnDstncId"), vilageName.getString("chargerMoblphonNo"),
                            vilageName.getString("exprnProgrmNm"), vilageName.getString("exprnLiverStgDc"), vilageName.getString("adres1"),
                            vilageName.getString("vilageHmpgUrl"), vilageName.getString("vilageNm"), vilageName.getString("tableName"),
                            vilageName.getString("operEraBegin"), vilageName.getString("operEraEnd"), vilageName.getString("nmprCoMumm")
                            , vilageName.getString("nmprCoMxmm"), vilageName.getString("operTimeMnt"), vilageName.getString("pc"),
                            vilageName.getString("onlineResvePosblAt"));
                    Log.d("seojang", "정보확인하기 : 끝 ");


                    data.add(item);
                    search_list_item.add(item.getExprnProgrmNm());
                }
                vilageList.setAdapter(adapter);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}

class List_Adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Item> data;

    //멤버변수로 해제할 Set을 생성
    private List<WeakReference<View>> mRecycleList = new ArrayList<WeakReference<View>>();
    private List<WeakReference<ImageView>> mRecycleList2 = new ArrayList<WeakReference<ImageView>>();

    public int selectedIndex = -1;
    int i = 0;
    private int layout;
    //NonLeakingWebView thumb;
    ImageView thumb;
    Typeface yunGothicFont;
    Button isRecruit;

    // 오늘 날짜
    Date date = new Date();
    SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyyMMdd");
    String strCurDate = CurDateFormat.format(date);
    DownloadImageTask downloadImageTask;
    public List_Adapter(Context context, int layout, ArrayList<Item> data) {
        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(context.getAssets(), "fonts/yungothic330.ttf");
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    // onDestroy에서 쉽게 해제할 수 있도록 함수 생성
     public void recycle() {
         for (WeakReference<View> ref : mRecycleList) {
             RecycleUtils.recursiveRecycle(ref.get());
         }
         for (WeakReference<ImageView> ref : mRecycleList2) {
             RecycleUtils.recursiveRecycle(ref.get());
         }
     }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Item getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        isRecruit = (Button) convertView.findViewById(R.id.isrecruit);

        isRecruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.mContext, service_prepare.class);
                ListActivity.mContext.startActivity(intent);
            }
        });

        Item listviewitem = data.get(position);

        // 깃발 활성화/비활성화
        LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.listDateFlag);
        if (strCurDate.compareTo(listviewitem.getOperEraBegin()) >= 0 && strCurDate.compareTo(listviewitem.getOperEraEnd()) <= 0) {
            linearLayout.setBackground(convertView.getResources().getDrawable(R.drawable.list8_));
        } else {
            linearLayout.setBackground(convertView.getResources().getDrawable(R.drawable.list9_));
        }

        thumb = (ImageView) convertView.findViewById(R.id.thumb);
        downloadImageTask = new DownloadImageTask(thumb);
        //thumb = (NonLeakingWebView) convertView.findViewById(R.id.thumb);

        //i++;
       // Log.i("asd123", " i : " + i + " W : " + thumb.getWidth() + " H : " + thumb.getHeight());
        //웹뷰가 둥글게 처리되었을 때 뒤에 하얗게 나오는데 이걸 투명하게 만들어줌
        thumb.setBackgroundColor(0);
        // 웹뷰 설정
        thumb.setVerticalScrollBarEnabled(false);
     //  thumb.setVerticalScrollbarOverlay(false);
        thumb.setHorizontalScrollBarEnabled(false);
        //thumb.setHorizontalScrollbarOverlay(false);
        thumb.setFocusableInTouchMode(false);
        thumb.setHorizontalScrollBarEnabled(false);
        thumb.setVerticalScrollBarEnabled(false);
       // thumb.setInitialScale(100);
        thumb.setFocusable(false);

        //if (thumb != null) {
            //new DownloadImageTask(thumb)
                    //.execute("http://www.welchon.com" + listviewitem.getThumbUrlCours1());
        //thumb.setImageBitmap(processBitmap("http://www.welchon.com" + listviewitem.getThumbUrlCours1()));
       //}
        downloadImageTask.execute("http://www.welchon.com" + listviewitem.getThumbUrlCours1());
        //icon.setImageResource(listviewitem.getIcon());

        // 마을 이름
        TextView Ename = (TextView) convertView.findViewById(R.id.ExpgName);
        Ename.setTypeface(yunGothicFont);
        Ename.setText(listviewitem.getExprnProgrmNm());
        Log.e("ExpgName", listviewitem.getExprnProgrmNm());
//
//        // 텍스트 짤림방지
//        if (listviewitem.getName().length() <= 9)
//            name.setText(listviewitem.getName());
//        else if (listviewitem.getName().length() > 9 && listviewitem.getName().length() <= 14) {
//            int stringEnd = listviewitem.getName().length() - 1;
//            name.setText(listviewitem.getName().substring(0, stringEnd - 4)
//                    + "\n" + listviewitem.getName().substring(stringEnd - 4));
//        } else
//            name.setText(listviewitem.getName().substring(0, 8)
//                    + "\n" + listviewitem.getName().substring(9));
//

        // 마을 종류
        //jangwon
        //TextView vilageKind =(TextView)convertView.findViewById(R.id.vilageKind);
//        if(listviewitem.getVilageKndNm().contains(","))
//        {
//            String[] result = listviewitem.getVilageKndNm().split(",");
//            vilageKind.setText(result[0]);
//        }
//        else
//            vilageKind.setText(listviewitem.getVilageKndNm());


        // 마을 간단 소개
        TextView vilageAccount = (TextView) convertView.findViewById(R.id.vilageAccount);
        vilageAccount.setTypeface(yunGothicFont);
        vilageAccount.setText(listviewitem.getOperEraBegin() + " ~ " + listviewitem.getOperEraEnd());
        // 마을 주소
        TextView addr = (TextView) convertView.findViewById(R.id.vilageAddr);
        addr.setTypeface(yunGothicFont);
        addr.setText(listviewitem.getAdres1());


        //메모리 해제할 View를 추가
        mRecycleList.add(new WeakReference<View>(convertView));
        mRecycleList2.add(new WeakReference<ImageView>(thumb));

        return convertView;
    }


    private static void recycleBitmap(ImageView iv) {
        if(iv == null)
            return;
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
    }
    // 리스트 뷰 항목에 들어가는 웹뷰 이미지 화면을 웹뷰크기에 맞게 조절
    // + 웹뷰에 둥근 모서리 처리를 하기 위해서 style을 추가함.
    public String creHtmlBody(String imgUrl) {
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");
        //sb.append("<img src = \"" + imgUrl + "\">"); // 자기 비율에 맞게 나온다.
        sb.append("<img width='100%' height='100%' style='border-radius: 220px; -moz-border-radius: 220px; -khtml-border-radius: 220px;" +
                "-webkit-border-radius: 220px; ' src = \"" + imgUrl + "\">"); // 꽉 채운 화면으로 나온다.
        sb.append("</BODY>");
        sb.append("</HTML>");
        return sb.toString();
    }

}


/**
 * 리스트의 데이터 클래스
 */

