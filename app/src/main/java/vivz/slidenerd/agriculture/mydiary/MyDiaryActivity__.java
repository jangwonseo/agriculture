package vivz.slidenerd.agriculture.mydiary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.list.Item;
import vivz.slidenerd.agriculture.list.ListDetailActivity;

public class MyDiaryActivity__ extends ActionBarActivity {

    private LinearLayout recruitListLayout,interestedListLayout;
    private Button recruitListButton, interestedListButton;
    private Button mydiarybackbutton;


    phpDown task;

    // listview
    private ListView vilageList;

    ArrayList<Item> data = new ArrayList<>();
    List_Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiary);

        // 뒤로가기 버튼
        mydiarybackbutton = (Button)findViewById(R.id.mydiarybackbutton);
        mydiarybackbutton.setOnClickListener(mClickListener);


        recruitListButton = (Button)findViewById(R.id.recruitlistbutton);//신청체험버튼
        interestedListButton = (Button)findViewById(R.id.interestedlistbutton);//관심체험버튼
        recruitListButton.setOnClickListener(mClickListener);
        interestedListButton.setOnClickListener(mClickListener);

        recruitListLayout = (LinearLayout)findViewById(R.id.myrecruitlist_layout);
        interestedListLayout = (LinearLayout)findViewById(R.id.myinterestedlist_layout);


        // 어떤 값이 넘어오는가에 따라서 액티비티 소제목 변경
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
                // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
                startActivity(intent);


            }
        });
        task.execute("http://218.150.181.131/seo/getMyDiary.php?userId=321kj");
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

                case R.id.mydiarybackbutton:
                    Log.d("seojang", "heheheheheihihi");
                    onBackPressed();
                    break;
            }
        }
    };
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


                    Item item = new Item(vilageName.getString("thumbUrl"), vilageName.getString("name"),
                            vilageName.getString("adres1"), vilageName.getString("prcafsManMoblphon"),
                            vilageName.getString("vilageHmpgEnnc"), vilageName.getString("vilageHmpgUrl"),
                            vilageName.getString("vilageSlgn"), vilageName.getString("tableName"), vilageName.getString("vilageId"));
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
        public View getView(int position, View convertView, ViewGroup parent) {
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
                thumb.loadDataWithBaseURL(null, creHtmlBody("http://www.welchon.com" + listviewitem.getThumbUrl()), "text/html", "utf-8", null);

            }
            //icon.setImageResource(listviewitem.getIcon());

            // 마을 이름
            TextView name = (TextView) convertView.findViewById(R.id.vilageName);
            name.setText(listviewitem.getName());
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
            vilageAccount.setText(listviewitem.getVilageSlgn());
            // 마을 주소
            TextView addr = (TextView) convertView.findViewById(R.id.vilageAddr);
            addr.setText(listviewitem.getAddr());


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
    }