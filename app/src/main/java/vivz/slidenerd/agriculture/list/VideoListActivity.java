package vivz.slidenerd.agriculture.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
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
import vivz.slidenerd.agriculture.service_prepare;

public class VideoListActivity extends ActionBarActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        task = new phpDown();
        mContext = this;

        //폰트 설정 초기화(윤고딕330)
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

        vilageList = (ListView) findViewById(R.id.video_vilageList);

        keys = (TextView) findViewById(R.id.video_themeKey);
        keys.setTypeface(yunGothicFont);  //윤고딕 적용

        backButton = (Button) findViewById(R.id.list_video_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        menuButton = (Button) findViewById(R.id.list_video_menubutton);
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

        keys.setText("생생한 체험 이야기");

        // ListView에 어댑터 연결
        adapter = new List_Adapter(this, R.layout.list_item, data);
        vilageList.setAdapter(adapter);

        vilageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /** 이부분이 리스트 클릭 시 다른 액티비티를 띄우는 부분 **/

                Intent intent = new Intent(getApplicationContext(), VideoListDetailActivity.class);
                intent.putExtra("item", adapter.getItem(position)); // 리스트를 클릭하면 현재 클릭한 마을에 대한 Item 클래스를 넘겨준다.
                intent.putExtra("isDairy", false);
                // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();

            }
        });

        task.execute("http://218.150.181.131/seo/dataEx.php?theme=" + themeName + "");
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
                JSONArray jAr = new JSONArray(str); // doInBackground 에서 받아온 문자열을 JSONArray 객체로 생성
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
                }
                vilageList.setAdapter(adapter);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
}

class Video_List_Adapter extends BaseAdapter {
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

    public Video_List_Adapter(Context context, int layout, ArrayList<Item> data) {
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
        isRecruit.setBackground(null);

        Item listviewitem = data.get(position);

        thumb = (ImageView) convertView.findViewById(R.id.thumb);
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

        if (thumb != null) {
            new DownloadImageTask(thumb)
                    .execute("http://www.welchon.com" + listviewitem.getThumbUrlCours1());
        }

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
