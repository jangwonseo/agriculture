package vivz.slidenerd.agriculture.mydiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.home.HomeActivity;
import vivz.slidenerd.agriculture.list.Item;
import vivz.slidenerd.agriculture.list.ListDetailActivity;
import vivz.slidenerd.agriculture.recruit.*;

public class MyDiaryActivity__ extends ActionBarActivity {

    private LinearLayout recruitListLayout,interestedListLayout;
    private Button recruitListButton, interestedListButton;
    private Button mydiarybackbutton, mydiaryMoveToHomeButton;

    //sharedPreference 선언부
    public SharedPreferences setting;
    public SharedPreferences.Editor editor;
    String id;


    // mydiary 마이다이어리
    private ListView vilageList;
    ArrayList<Item> data = new ArrayList<>();
    List_Adapter adapter;
    phpDown task;


    // 신청중
    private ListView procList;
    ArrayList<RecruitListItem> pData = new ArrayList<>();
    phpRecruitList pTask;
    proc_Adapter pAdapter;


    // 프로필 사진
    WebView imgvMydiaryProfile;

    // x 버튼 핸들러
    CancelHandler cancelHandler;


    @Override
    protected void onRestart() {
        super.onRestart();
        pTask = new phpRecruitList();
        procList = (ListView) findViewById(R.id.procList);
        pData.clear();
        pAdapter = new proc_Adapter(this, R.layout.mydiary_proc_item, pData);

        procList.setAdapter(pAdapter);

        pTask.execute("http://218.150.181.131/seo/phpMydiaryRecruitList.php?userId=" + id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiary);


        //sharedPreference로 전역 공유공간을 만듬
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();
        id = setting.getString("info_Id", "");


        // 프로필 사진
        imgvMydiaryProfile = (WebView)findViewById(R.id.imgvMydiaryProfile);
        // 웹뷰 설정
        // 배경이 하얕게 나오는데 투명하게 만들어줌
        imgvMydiaryProfile.setBackgroundColor(0);
        imgvMydiaryProfile.setVerticalScrollBarEnabled(false);
        imgvMydiaryProfile.setVerticalScrollbarOverlay(false);
        imgvMydiaryProfile.setHorizontalScrollBarEnabled(false);
        imgvMydiaryProfile.setHorizontalScrollbarOverlay(false);
        imgvMydiaryProfile.setFocusableInTouchMode(false);
        imgvMydiaryProfile.setHorizontalScrollBarEnabled(false);
        imgvMydiaryProfile.setVerticalScrollBarEnabled(false);
        imgvMydiaryProfile.setInitialScale(100);
        imgvMydiaryProfile.setFocusable(false);
        imgvMydiaryProfile.loadDataWithBaseURL(null, creHtmlBody("http://218.150.181.131/seo/image/"+id+"Profile.jpg"), "text/html", "utf-8", null);

        // 뒤로가기 버튼
        mydiarybackbutton = (Button)findViewById(R.id.mydiarybackbutton);
        mydiarybackbutton.setOnClickListener(mClickListener);

        mydiaryMoveToHomeButton = (Button)findViewById(R.id.mydiary_movetohome);
        mydiaryMoveToHomeButton.setOnClickListener(mClickListener);


        recruitListButton = (Button)findViewById(R.id.recruitlistbutton);//신청체험버튼
        interestedListButton = (Button)findViewById(R.id.interestedlistbutton);//관심체험버튼
        recruitListButton.setOnClickListener(mClickListener);
        interestedListButton.setOnClickListener(mClickListener);

        recruitListLayout = (LinearLayout)findViewById(R.id.myrecruitlist_layout);
        interestedListLayout = (LinearLayout)findViewById(R.id.myinterestedlist_layout);


        // 마이다이어리 추가한 리스트 관련 처리
        task = new phpDown();
        vilageList = (ListView) findViewById(R.id.vilageList);

         // ListView에 어댑터 연결
        adapter = new List_Adapter(this, R.layout.mydiary_item, data);
        vilageList.setAdapter(adapter);
        vilageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /** 이부분이 리스트 클릭 시 다른 액티비티를 띄우는 부분 **/

                Intent intent = new Intent(getApplicationContext(),
                        ListDetailActivity.class);
                intent.putExtra("item", adapter.getItem(position)); // 리스트를 클릭하면 현재 클릭한 마을에 대한 Item 클래스를 넘겨준다.
                intent.putExtra("isDiary", true);
                // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
                startActivity(intent);


            }
        });

        if ( id.equals("") || id == null ) {
            Toast.makeText(getApplicationContext(), "로그인 정보가 필요합니다.", Toast.LENGTH_SHORT).show();
        } else {
            task.execute("http://218.150.181.131/seo/getMyDiary.php?userId=" + id);
        }



        // 신청 중 관련 처리
        pTask = new phpRecruitList();
        procList = (ListView) findViewById(R.id.procList);
        pAdapter = new proc_Adapter(this, R.layout.mydiary_proc_item, pData);

        procList.setAdapter(pAdapter);
        procList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), MyDiaryDetailActivity.class);
                intent.putExtra("item", pAdapter.getItem(position));
                startActivity(intent);

            }

        });
        if ( id.equals("") || id == null ) {
            Toast.makeText(getApplicationContext(), "로그인 정보가 필요합니다.", Toast.LENGTH_SHORT).show();
        } else {
            pTask.execute("http://218.150.181.131/seo/phpMydiaryRecruitList.php?userId=" + id);
        }


        cancelHandler = new CancelHandler();
    }

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();

        super.onDestroy();
    }

    // 리스트 뷰 항목에 들어가는 웹뷰 이미지 화면을 웹뷰크기에 맞게 조절
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

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                //신청체험버튼
                case R.id.recruitlistbutton:
                    recruitListButton.setBackgroundResource(R.drawable.mydiary2af);
                    interestedListButton.setBackgroundResource(R.drawable.mydiary3);
                    recruitListLayout.setVisibility(LinearLayout.VISIBLE);
                    interestedListLayout.setVisibility(LinearLayout.GONE);

                    break;
                //관심체험버튼
                case R.id.interestedlistbutton:
                    recruitListButton.setBackgroundResource(R.drawable.mydiary2);
                    interestedListButton.setBackgroundResource(R.drawable.mydiary3af);
                    recruitListLayout.setVisibility(LinearLayout.GONE);
                    interestedListLayout.setVisibility(LinearLayout.VISIBLE);
                    break;
                //뒤로가기버튼
                case R.id.mydiarybackbutton:
                    finish();
                    break;

                case R.id.mydiary_movetohome:
                    Intent moveToHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    startActivity(moveToHomeIntent);
                    break;
            }
        }
    };

    // 찜한 목록(마이다이어리에 추가)
    public class phpDown extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            String line = "";
            try {
                // 텍스트 연결 url 설정
                URL url = new URL(urls[0]);
                // 이미지 url

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


    // 모집 신청 중 리스트출력 부분
    public class phpRecruitList extends AsyncTask<String, Integer,String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            String line ="";
            try{
                // 텍스트 연결 url 설정
                URL url = new URL(urls[0]);
                // 이미지 url
                Log.e("tag", "url : " + urls[0]);
                // URL 페이지 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면.

                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    Log.e("tag", "setUseCaches is false");
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line);
                        }
                        br.close();
                    }
                    conn.disconnect();

                }

            } catch(Exception ex){
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str){
            // JSON 구문을 파싱해서 JSONArray 객체를 생성

            try {
                JSONArray jAr = new JSONArray(str); // doInBackground 에서 받아온 문자열을 JSONArray 객체로 생성
                for (int i = 0; i < jAr.length(); i++) {  // JSON 객체를 하나씩 추출한다.
                    JSONObject RecruitListJson = jAr.getJSONObject(i);
                    RecruitListItem rItem = new RecruitListItem(Integer.parseInt(RecruitListJson.getString("idrecruit")),RecruitListJson.getString("userId")  ,RecruitListJson.getString("missionName"), RecruitListJson.getString("vilageName"), RecruitListJson.getString("recruitContent"),
                            RecruitListJson.getString("termStart"), RecruitListJson.getString("termEnd"), Integer.parseInt(RecruitListJson.getString("recruitNum")), Integer.parseInt(RecruitListJson.getString("joinedNum")), RecruitListJson.getString("reward"), RecruitListJson.getString("ImageURL"), Integer.parseInt(RecruitListJson.getString("clickNum")), RecruitListJson.getString("phoneNum"));
                    Log.e("RecruitItem", "idrecruit : " + RecruitListJson.getString("idrecruit") + " UserId : " + RecruitListJson.getString("userId") + " missionName : " + RecruitListJson.getString("missionName") + " vilageName : " + RecruitListJson.getString("vilageName") + " recruitContent : " + RecruitListJson.getString("recruitContent") + " termStart : " +
                            RecruitListJson.getString("termStart") + " termEnd : " + RecruitListJson.getString("termEnd") + " recruitNum : " + RecruitListJson.getString("recruitNum") + " reward : " + RecruitListJson.getString("reward") + " ImageURL : " + RecruitListJson.getString("ImageURL") + " clickNum : " + RecruitListJson.getString("clickNum") + " phoneNum : " + RecruitListJson.getString("phoneNum"));
                    pData.add(rItem);
                    Log.e("data<RecruitItem> : ", Integer.toString(pData.size())); // 몇개 인지 확인
                }
                procList.setAdapter(pAdapter);


            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    // Handler 클래스
    class CancelHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 2:
                    Toast.makeText(getApplicationContext(), "모집이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "모집취소에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), "관심체험이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "관심체험 삭제를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    };

    class List_Adapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<Item> data;
        private int layout;
        WebView thumb;

        public List_Adapter(Context context, int layout, ArrayList<Item> data) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data = data;
            this.layout = layout;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(layout, parent, false);
            }
            Item listviewitem = data.get(position);
            thumb = (WebView) convertView.findViewById(R.id.thumb);
            //웹뷰가 둥글게 처리되었을 때 뒤에 하얗게 나오는데 이걸 투명하게 만들어줌
            thumb.setBackgroundColor(0);
            // 웹뷰 설정
            thumb.setVerticalScrollBarEnabled(false);
            thumb.setVerticalScrollbarOverlay(false);
            thumb.setHorizontalScrollBarEnabled(false);
            thumb.setHorizontalScrollbarOverlay(false);
            thumb.setFocusableInTouchMode(false);
            thumb.setHorizontalScrollBarEnabled(false);
            thumb.setVerticalScrollBarEnabled(false);
            thumb.setInitialScale(100);
            thumb.setFocusable(false);

            if (thumb != null) {
                thumb.loadDataWithBaseURL(null, creHtmlBody("http://www.welchon.com" + listviewitem.getThumbUrlCours1()), "text/html", "utf-8", null);

            }
            //icon.setImageResource(listviewitem.getIcon());

            // 마을 이름
            TextView name = (TextView) convertView.findViewById(R.id.vilageName);
            name.setText(listviewitem.getExprnProgrmNm());
//
//        // 텍스트 짤림방지
//        if (listviewitem.getName().length() <= 9)
//            name.setText(listviewitem.getName());.
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
            vilageAccount.setText(listviewitem.getExprnLiverStgDc());
            // 마을 주소
            TextView addr = (TextView) convertView.findViewById(R.id.vilageAddr);
            addr.setText(listviewitem.getAdres1());

            Button btnMydiaryListCancle = (Button)convertView.findViewById(R.id.btnMydiaryListCancle);
            btnMydiaryListCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String VilageName = null;
                    try {
                        VilageName  = URLEncoder.encode(data.get(position).getExprnProgrmNm(), "UTF-8");
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    phpMyDiaryInterestCancel myCancel = new phpMyDiaryInterestCancel();

                    myCancel.execute("http://218.150.181.131/seo/phpMydiaryVilageCancel.php?userId=" + id + "&name=" + VilageName);
                    data.remove(position);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        // 리스트 뷰 항목에 들어가는 웹뷰 이미지 화면을 웹뷰크기에 맞게 조절
        // + 웹뷰에 둥근 모서리 처리를 하기 위해서 style을 추가함.
        public String creHtmlBody(String imgUrl) {
            StringBuffer sb = new StringBuffer("<HTML>");
            sb.append("<HEAD>");
            sb.append("</HEAD>");
            sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");
            //sb.append("<img src = \"" + imgUrl + "\">"); // 자기 비율에 맞게 나온다.
            sb.append("<img width='100%' height='100%' style='border-radius: 470px; -moz-border-radius: 470px; -khtml-border-radius: 470px;" +
                    "-webkit-border-radius: 470px; ' src = \"" + imgUrl + "\">"); // 꽉 채운 화면으로 나온다.
            sb.append("</BODY>");
            sb.append("</HTML>");
            return sb.toString();
        }

        // MydiaryDetail 부분에서 취소하기
        public class phpMyDiaryInterestCancel extends AsyncTask<String, Integer,String> {

            Message msg = cancelHandler.obtainMessage(); // 취소 성공/실패를 위한 핸들러

            @Override
            protected String doInBackground(String... urls) {
                StringBuilder jsonHtml = new StringBuilder();
                String line ="";
                try{
                    // 텍스트 연결 url 설정
                    URL url = new URL(urls[0]);
                    // 이미지 url
                    Log.e("tag", "url : " + urls[0]);
                    // URL 페이지 커넥션 객체 생성
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    // 연결되었으면.

                    if(conn != null){
                        conn.setConnectTimeout(10000);
                        conn.setUseCaches(false);
                        // 연결되었음 코드가 리턴되면.
                        Log.e("tag", "setUseCaches is false");
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            for(;;){
                                // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                                line = br.readLine();
                                if(line == null) break;
                                // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                                jsonHtml.append(line);
                            }
                            br.close();
                        }
                        conn.disconnect();

                    }
                } catch(Exception ex){
                    ex.printStackTrace();
                    msg.what = 3; // 체험 취소 실패
                }
                msg.what = 4; // 체험 취소 성공
                return jsonHtml.toString();


            }

            protected void onPostExecute(String str){
                cancelHandler.sendMessage(msg);
            }
        }
    }

    class proc_Adapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<vivz.slidenerd.agriculture.recruit.RecruitListItem> data;
        private int layout;

        // 리스트에 들어갈 이미지를 가져올때 쓰이는 변수들
        TextView txtvRecListTerm;
        TextView txtvRecListRecNum;
        WebView webView ;

        //phpGetImage getImage = new phpGetImage();
        String imgUrl = "http://218.150.181.131/seo/image/";
        Bitmap bmImg;

        public proc_Adapter(Context context, int layout, ArrayList<vivz.slidenerd.agriculture.recruit.RecruitListItem> data){
            this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data=data;
            this.layout=layout;
        }
        @Override
        public int getCount(){return data.size();}
        @Override
        public vivz.slidenerd.agriculture.recruit.RecruitListItem getItem(int position){return data.get(position);}
        @Override
        public long getItemId(int position){return position;}
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            if(convertView==null){
                convertView=inflater.inflate(layout,parent,false);
            }
            vivz.slidenerd.agriculture.recruit.RecruitListItem listviewitem=data.get(position);

            webView = (WebView)convertView.findViewById(R.id.recruit_list_webView);

            // 배경이 하얕게 나오는데 투명하게 만들어줌
            webView.setBackgroundColor(0);
            // 웹뷰 설정
            webView.setVerticalScrollBarEnabled(false);
            webView.setVerticalScrollbarOverlay(false);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setHorizontalScrollbarOverlay(false);
            webView.setFocusableInTouchMode(false);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setVerticalScrollBarEnabled(false);
            webView.setInitialScale(100);
            webView.setFocusable(false);

            String ImageURL = listviewitem.getImageURL();
            String loadingURL = null;
            if (ImageURL.equals("null") || ImageURL == null) {
                loadingURL = imgUrl + "default.png";
            } else {
                loadingURL = imgUrl + listviewitem.getImageURL();
            }

            webView.loadDataWithBaseURL(null, creHtmlBody(loadingURL), "text/html", "utf-8", null);
            Log.e("list image path", loadingURL);

            TextView name=(TextView)convertView.findViewById(R.id.list_missionName);
            name.setText(listviewitem.getMissionName());
            txtvRecListTerm = (TextView)convertView.findViewById(R.id.txtvRecListTerm);
            txtvRecListTerm.setText(listviewitem.getTermStart() + "\n\t ~ " + listviewitem.getTermEnd());
            txtvRecListRecNum = (TextView)convertView.findViewById(R.id.txtvRecListRecNum);
            txtvRecListRecNum.setText(Integer.toString(listviewitem.getJoinedNum()) + " / " + Integer.toString(listviewitem.getRecruitNum()) + " 명");


            /// 마이다이어리 모집 취소 버튼
            Button btnMydiaryProcCancle = (Button)convertView.findViewById(R.id.btnMydiaryProcCancle);
            btnMydiaryProcCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int recId  = data.get(position).getIdRecruit();
                    phpMyDiaryListCancel myCancel = new phpMyDiaryListCancel();

                    myCancel.execute("http://218.150.181.131/seo/phpMydiaryListCancel.php?userId=" + id + "&recruitId=" + recId);
                    data.remove(position);
                    notifyDataSetChanged(); // 내용 초기화인듯 정말 유용한거 같음!
                }
            });

            return convertView;
        }

        // MydiaryDetail 부분에서 취소하기
        public class phpMyDiaryListCancel extends AsyncTask<String, Integer,String> {

            Message msg = cancelHandler.obtainMessage(); // 취소 성공/실패를 위한 핸들러

            @Override
            protected String doInBackground(String... urls) {
                StringBuilder jsonHtml = new StringBuilder();
                String line ="";
                try{
                    // 텍스트 연결 url 설정
                    URL url = new URL(urls[0]);
                    // 이미지 url
                    Log.e("tag", "url : " + urls[0]);
                    // URL 페이지 커넥션 객체 생성
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    // 연결되었으면.

                    if(conn != null){
                        conn.setConnectTimeout(10000);
                        conn.setUseCaches(false);
                        // 연결되었음 코드가 리턴되면.
                        Log.e("tag", "setUseCaches is false");
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            for(;;){
                                // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                                line = br.readLine();
                                if(line == null) break;
                                // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                                jsonHtml.append(line);
                            }
                            br.close();
                        }
                        conn.disconnect();

                    }
                } catch(Exception ex){
                    ex.printStackTrace();
                    msg.what = 1; // 실패
                }
                msg.what = 2; // 성공
                return jsonHtml.toString();


            }

            protected void onPostExecute(String str){
                cancelHandler.sendMessage(msg);
            }
        }

        // 리스트 뷰 항목에 들어가는 웹뷰 이미지 화면을 웹뷰크기에 맞게 조절
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

        // 서버에서 URL 경로에 있는 이미지를 가져온다. 에러가 나는 바람에 웹뷰로 경로에 있는 이미지 가져온다.
        private class phpGetImage extends AsyncTask<String, Integer,Bitmap>{
            @Override
            protected Bitmap doInBackground(String... urls) {
                // TODO Auto-generated method stub
                try{
                    URL myFileUrl = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();

                    bmImg = BitmapFactory.decodeStream(is);


                }catch(IOException e){
                    e.printStackTrace();
                }
                return bmImg;
            }

            protected void onPostExecute(Bitmap img){
                //imView.setImageBitmap(bmImg);
            }
        }
    }


}


