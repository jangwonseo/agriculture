package vivz.slidenerd.agriculture.recruit;

// 모집리스트, 모집하기가 FrameLayout으로 되어 있다.
// php 클래스는
// 1. 검색할 때, 체험명 리스트를 보기위한 phpDown과 (AutoCompleteTextView)
// 2. 모집글을 등록하는 phpUp,
// 3. 모집(미션) 리스트를 보기위한 phpRecruitList
// 4.사진을 서버에서 가져오는

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import vivz.slidenerd.agriculture.R;


public class Recruit extends Activity implements TextWatcher{

    //EditText search;        // 검색
    EditText missionName;   // 미션이름
    EditText recruitContent;       // 내용
    TextView termStart;     // 기간(시작)
    TextView termEnd;       // 기간 (끝)
    EditText recruitNum;    // 모집인원
    EditText reward;        //보상

    // DB에 저장
    phpUp recruitTask;
    RecruitItem recruitItem;

    // DB에서 불러오기
    phpDown autoComTask;

    // 자동완성기능
    AutoCompleteTextView recruit_autoComplete;

    // test
    private ArrayList<String> search_item = new ArrayList<String>();

    //모집리스트버튼, 모집하기버튼, 뒤로가기버튼
    Button recruitListBtn, recruitStartBtn, backBtn;
    LinearLayout recruitListLayout,recruitStartLayout;

    // 등록하기 버튼
    Button registButton;

    // 모집하기 리스트 부분 -----------------------------------
    private ListView recruit_list;
    ArrayList<RecruitListItem> data=new ArrayList<>();
    List_Adapter adapter;
    phpRecruitList phpList;


    // 사진업로드 부분 ------------------------------------------
    private Button uploadButton, btnselectpic;
    private ImageView imageview;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    private String upLoadServerUri = null;
    private String imagepath=null;
    String uploadFileName=null;


    // TextView 클릭시 날짜 선택
    static final int DATE_START_ID = 0;
    static final int DATE_END_ID = 1;
    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    private int mEndYear;
    private int mEndMonth;
    private int mEndDay;

    TextView filePath;

    // 서버에 있는 사진을 가져오기 위한 부분 -------------------------
    ImageView imView;
    String imgUrl = "http://218.150.181.131/seo/image/";
    //Bitmap bmImg;
    //phpGetImage task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        missionName = (EditText)findViewById(R.id.missionName);
        recruitContent = (EditText)findViewById(R.id.content);
        termStart = (TextView)findViewById(R.id.termStart);
        termEnd = (TextView)findViewById(R.id.termEnd);
        recruitNum = (EditText)findViewById(R.id.recruitNum);
        reward = (EditText)findViewById(R.id.reward);

        termStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_START_ID);
            }
        });

        termEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_END_ID);
            }
        });

        // (3) 현재 날짜 인식
        final Calendar c = Calendar.getInstance();
        mStartYear = c.get(Calendar.YEAR);
        mStartMonth= c.get(Calendar.MONTH);
        mStartDay  = c.get(Calendar.DAY_OF_MONTH);

        final Calendar c2 = Calendar.getInstance();
        mEndYear = c2.get(Calendar.YEAR);
        mEndMonth= c2.get(Calendar.MONTH);
        mEndDay  = c2.get(Calendar.DAY_OF_MONTH);
        // (4) 인식된 날짜를 출력
        updateDisplay();

        // 설정된 날짜를 TextView에 출력

        recruit_autoComplete = (AutoCompleteTextView)findViewById(R.id.recruit_autoComplete);
        recruit_autoComplete.addTextChangedListener(this);
        recruit_autoComplete.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, search_item));
        recruit_autoComplete.setTextColor(Color.BLACK);

        // 모집, 모집하기 버튼
        backBtn = (Button)findViewById(R.id.backbtn);
        recruitListBtn = (Button)findViewById(R.id.recruit_list);
        recruitStartBtn = (Button)findViewById(R.id.recruit_start);
        recruitListBtn.setOnClickListener(recruitClickListener);
        recruitStartBtn.setOnClickListener(recruitClickListener);
        recruitListLayout = (LinearLayout)findViewById(R.id.recruit_list_portion);
        recruitStartLayout = (LinearLayout)findViewById(R.id.recruit_start_portion);

        // 등록하기 버튼
        registButton = (Button)findViewById(R.id.regist_button);
        registButton.setOnClickListener(recruitClickListener);

        autoComTask = new phpDown();
        autoComTask.execute("http://218.150.181.131/seo/publicData.php");

        recruit_autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String str = (String) parent.getItemAtPosition(position); // 클릭한 체험이름 미션이름으로 넣기
                missionName.setText(str);
            }
        });


        // 모집하기 리스트
        recruit_list = (ListView)findViewById(R.id.recruit_listview); // 리스트뷰
        adapter = new List_Adapter(this, R.layout.recruit_item, data);

        recruit_list.setAdapter(adapter);

        phpList = new phpRecruitList();
        phpList.execute("http://218.150.181.131/seo/recruitList.php");


        // 리스트 항목 (참가할 미션)클릭시 이벤트
        recruit_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        // 사진 업로드 부분
        //uploadButton = (Button)findViewById(R.id.uploadButton); ////////////// 이부분은 등록하기 버튼에 추가되어야 함
        btnselectpic = (Button)findViewById(R.id.selectPicture); // 파일 업로드 버튼, 이미지를 선택하여 보낼 준비

        //imageview = (ImageView)findViewById(R.id.imagePreView); // 파일을 선택하면 이미지가 보인다.
        // 아래 출력하는 소스 있음 나중에 사진 미리보기 할거면 참고할것 현재 주석되어있음 ( action 메소드)

        btnselectpic.setOnClickListener(recruitClickListener);
        //uploadButton.setOnClickListener(recruitClickListener);
        upLoadServerUri = "http://218.150.181.131/seo/UploadToServer.php";

        filePath = (TextView)findViewById(R.id.filePath);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id){
            case DATE_START_ID : return new DatePickerDialog(this,mStartDateSetListener,mStartYear,mStartMonth,mStartDay);
            case DATE_END_ID : return new DatePickerDialog(this,mEndDateSetListener,mEndYear,mEndMonth,mEndDay);
        }
        return null;
    }

    // (8) 다이어로그에 있는 날짜를 설정(set)하면 실행됨
    private DatePickerDialog.OnDateSetListener mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mStartYear = year;
            mStartMonth=monthOfYear;
            mStartDay=dayOfMonth;
            // 사용자가 지정한 날짜를 출력
            updateDisplay();
        }
    };

    private DatePickerDialog.OnDateSetListener mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mEndYear = year;
            mEndMonth=monthOfYear;
            mEndDay=dayOfMonth;
            // 사용자가 지정한 날짜를 출력
            updateDisplay();
        }
    };

    private void updateDisplay() {
        // main.xml의 레이아웃에 배치된 날짜 입력 TextView에 인식된 날짜 출력
        termStart.setText(
                new StringBuilder()
                        //월은 시스템에서 0~11로 인식하기 때문에 1을 더해줌
                        .append(mStartYear).append("-")
                        .append(mStartMonth+1).append("-")
                        .append(mStartDay).append(" ")
        );

        termEnd.setText(
                new StringBuilder()
                        //월은 시스템에서 0~11로 인식하기 때문에 1을 더해줌
                        .append(mEndYear).append("-")
                        .append(mEndMonth+1).append("-")
                        .append(mEndDay).append(" ")
        );

    }

    public void afterTextChanged(Editable arg0) {/*AODO Auto-generated method stub*/}
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    //모집,모집하기 버튼누름에 따라 framelayout이 보였다 안보엿다 바뀜
    Button.OnClickListener recruitClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.recruit_list:
                    recruitListBtn.setBackgroundResource(R.drawable.recruit_button3);
                    recruitStartBtn.setBackgroundResource(R.drawable.recruit_button4);
                    recruitListLayout.setVisibility(LinearLayout.VISIBLE);
                    recruitStartLayout.setVisibility(LinearLayout.GONE);
                    data.clear();
                    phpList = new phpRecruitList();
                    phpList.execute("http://218.150.181.131/seo/recruitList.php");

                    break;
                case R.id.recruit_start:
                    recruitListBtn.setBackgroundResource(R.drawable.recruit_button1);
                    recruitStartBtn.setBackgroundResource(R.drawable.recruit_button2);
                    recruitListLayout.setVisibility(LinearLayout.GONE);  //gone은 눈에 안보일뿐 아니라 영역도 없어짐
                    recruitStartLayout.setVisibility(LinearLayout.VISIBLE);
                    break;
                case R.id.backbtn:
                    onBackPressed();
                    break;
                case R.id.regist_button: // 등록 버튼

                    try {
                        Log.e("regist", "button");
                        // 입력한 정보를 Item 객체에 담는다.
                        recruitItem = new RecruitItem(URLEncoder.encode(recruit_autoComplete.getText().toString(), "UTF-8"), URLEncoder.encode(missionName.getText().toString(), "UTF-8"), URLEncoder.encode(recruitContent.getText().toString(), "UTF-8"), URLEncoder.encode(termStart.getText().toString(), "UTF-8"), URLEncoder.encode(termEnd.getText().toString(), "UTF-8"), URLEncoder.encode(recruitNum.getText().toString(), "UTF-8"), URLEncoder.encode(reward.getText().toString(), "UTF-8"), uploadFileName);

                        // 입력한 정보들을 php에 get방식으로 보낸다.
                        recruitTask = new phpUp();

                        String params = "";
                        params = URLEncoder.encode(recruitItem.toString(), "utf-8");

                        recruitTask.execute("http://218.150.181.131/seo/recruit.php?" + recruitItem.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("URLEncoder", "PHP params Encoder error");
                    }

                    dialog = ProgressDialog.show(Recruit.this, "", "Uploading file...", true);
                    Log.e("upload message : ", "uploading started.....");
                    new Thread(new Runnable() {
                        public void run() {

                            if (imagepath == null ) {
                                imagepath = "";
                                dialog.dismiss();
                                Log.e("Uploading file : ", "file is null, User don't select picture.");
                                return;
                            } else {
                                uploadFile(imagepath);
                            }
                        }
                    }).start();
                    break;


                case R.id.selectPicture : // 사진을 선택
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);

                    break;
            }
        }
    };

    public void autoFillContent(View v) {
        //String str = v.getResources().toString();
        //missionName.setText(str);
    }

    // 모집하기 insert 부분
    public class phpUp extends AsyncTask<String, Integer,String> {

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
/*
            try {
                JSONArray jAr = new JSONArray(str); // doInBackground 에서 받아온 문자열을 JSONArray 객체로 생성
                for (int i = 0; i < jAr.length(); i++) {  // JSON 객체를 하나씩 추출한다.
                    JSONObject vilageName = jAr.getJSONObject(i);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }*/

        }
    }

    // 모집하기 검색에서 체험 이름을 가져오는 부분
    public class phpDown extends AsyncTask<String, Integer,String> {

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
                    JSONObject vilageName = jAr.getJSONObject(i);
                    Item item = new Item(vilageName.getString("thumbUrlCours1"), vilageName.getString("vilageNm"),
                            vilageName.getString("adres1"), vilageName.getString("prcafsManMoblphon"), vilageName.getString("vilageHmpgEnnc"), vilageName.getString("vilageHmpgUrl"));
                    search_item.add(item.getName());
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    // 모집 리스트 출력 부분
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
                    RecruitListItem item = new RecruitListItem(RecruitListJson.getString("missionName"), RecruitListJson.getString("recruitContent"),
                            RecruitListJson.getString("termStart"), RecruitListJson.getString("termEnd"), RecruitListJson.getString("recruitNum"), RecruitListJson.getString("reward"), RecruitListJson.getString("ImageURL"));
                    Log.e("RecruitItem", "missionName : " + RecruitListJson.getString("missionName") + " recruitContent : " + RecruitListJson.getString("recruitContent") + " termStart : " +
                            RecruitListJson.getString("termStart") + " termEnd : " + RecruitListJson.getString("termEnd") + " recruitNum : " + RecruitListJson.getString("recruitNum") + " reward : " + RecruitListJson.getString("reward") + " ImageURL : " + RecruitListJson.getString("ImageURL"));
                    data.add(item);
                    Log.e("data<RecruitItem> : ", Integer.toString(data.size()));
                }
                recruit_list.setAdapter(adapter);


            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getData().getPath();

            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            //Bitmap bitmap= BitmapFactory.decodeFile(imagepath);

            Log.e("imagepath : ", imagepath);
            //imageview.setImageBitmap(bitmap);
            Log.e("upload message : ", "Uploading file path:" + imagepath);

            // 현재시간을 얻는다.
            Long now = System.currentTimeMillis();

            // 원래 파일의 확장자를 얻기 위한 파싱 "." 뒤에 있는 확장자를 가져온다.

            String str = imagepath.substring(imagepath.indexOf("."));

            uploadFileName = now.toString() + str;
            Log.e("fileName : ", uploadFileName);

            filePath.setText(uploadFileName);

        }
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public int uploadFile(String sourceFileUri) {
        Log.e("sourceFIle : ", sourceFileUri.toString());


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);




        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"+imagepath);

            runOnUiThread(new Runnable() {
                public void run() {
                    Log.e("upload message : ", "Source File not exist :" + imagepath);
                }
            });

            return 0;

        }
        else
        {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri + "?fileName=" + uploadFileName);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);


                //dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                //+ fileName + "\"" + lineEnd);


                // 파일이름 : 현재시간과 확장자의 조합으로 파일 이름을 만든다.
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + uploadFileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed";
                            Log.e("upload message : ",msg);
                            Toast.makeText(Recruit.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("upload message : ", "MalformedURLException Exception : check script url.");
                        Toast.makeText(Recruit.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("upload message : ","Got Exception : see logcat ");
                        Toast.makeText(Recruit.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

}


class List_Adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<RecruitListItem> data;
    private int layout;
    WebView thumb;

    // 리스트에 들어갈 이미지를 가져올때 쓰이는 변수들
    WebView webView ;
    //phpGetImage getImage = new phpGetImage();
    String imgUrl = "http://218.150.181.131/seo/image/";
    Bitmap bmImg;

    public List_Adapter(Context context, int layout, ArrayList<RecruitListItem> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public RecruitListItem getItem(int position){return data.get(position);}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }
        RecruitListItem listviewitem=data.get(position);

        webView = (WebView)convertView.findViewById(R.id.recruit_list_webView);

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

        return convertView;
    }

    // 리스트 뷰 항목에 들어가는 웹뷰 이미지 화면을 웹뷰크기에 맞게 조절
    public String creHtmlBody(String imgUrl) {
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");
        //sb.append("<img src = \"" + imgUrl + "\">"); // 자기 비율에 맞게 나온다.
        sb.append("<img width='100%' height='100%' src = \"" + imgUrl + "\">"); // 꽉 채운 화면으로 나온다.
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

class RecruitItem implements Serializable {
    private String search;
    private String missionName;
    private String recruitContent;       // 내용
    private String termStart;     // 기간(시작)
    private String termEnd;       // 기간 (끝)
    private String recruitNum;    // 모집인원
    private String reward;        //보상
    private String ImageURL ="";         // 이미지 경로
    public String getsearch(){return search;}
    public String getmissionName(){return missionName;}
    public String getrecruitContent(){return recruitContent;}
    public String gettermStart(){return termStart;}
    public String gettermEnd(){return termEnd;}
    public String getrecruitNum(){return recruitNum;}
    public String getreward(){return reward;}
    public String getimageURl() {return ImageURL;}

    public RecruitItem(String search,String missionName, String recruitContent, String termStart, String termEnd, String recruitNum, String reward, String ImageURL){
        this.search = search;
        this.missionName = missionName;
        this.recruitContent = recruitContent;
        this.termStart = termStart;
        this.termEnd = termEnd;
        this.recruitNum = recruitNum;
        this.reward = reward;
        this.ImageURL = ImageURL;
    }

    public String toString() {

        return "search=" + search + "&missionName=" + missionName + "&recruitContent=" +recruitContent + "&termStart=" + termStart + "&termEnd=" + termEnd + "&" + "recruitNum=" + recruitNum + "&reward=" + reward + "&ImageURL=" + ImageURL;
    }
}

class RecruitListItem implements Serializable {
    private String missionName;
    private String recruitContent;
    private String termStart;
    private String termEnd;
    private String recruitNum;
    private String reward;
    private String ImageURL;
    public String getMissionName(){return missionName;}
    public String getRecuritContent(){return recruitContent;}
    public String getTermStart(){return termStart;}
    public String getTermEnd() {return termEnd;}
    public String getRecruitNum(){return recruitNum;}
    public String getReward(){return reward;}
    public String getImageURL() {return ImageURL;}
    public RecruitListItem(String missionName,String recruitContent, String termStart, String termEnd, String recruitNum, String reward, String ImageURL){
        this.missionName = missionName;
        this.recruitContent = recruitContent;
        this.termStart = termStart;
        this.termEnd = termEnd;
        this.recruitNum = recruitNum;
        this.reward = reward;
        this.ImageURL = ImageURL;
    }
}

class Item implements Serializable{
    private String thumbUrl;
    private String name;
    private String addr;
    private String prcafsManMoblphon;
    private String vilageHmpgEnnc;
    private String vilageHmpgUrl;
    public String getThumbUrl(){return thumbUrl;}
    public String getName(){return name;}
    public String getAddr(){return addr;}
    public String getPrcafsManMoblphon() {return prcafsManMoblphon;}
    public String getVilageHmpgEnnc(){return vilageHmpgEnnc;}
    public String getVilageHmpgUrl(){return vilageHmpgUrl;}
    public Item(String thumbUrl,String name, String addr, String prcafsManMoblphon, String vilageHmpgEnnc, String vilageHmpgUrl){
        this.thumbUrl = thumbUrl;
        this.name = name;
        this.addr = addr;
        this.prcafsManMoblphon = prcafsManMoblphon;
        this.vilageHmpgEnnc = vilageHmpgEnnc;
        this.vilageHmpgUrl = vilageHmpgUrl;
    }
}