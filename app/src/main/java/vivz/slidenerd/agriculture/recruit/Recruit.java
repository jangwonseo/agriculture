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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.list.ListDetailActivity;


public class Recruit extends Activity implements TextWatcher{
    //로그인 정보 가져오기
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    String sharedUserId;

    //EditText search;        // 검색
    EditText missionName;   // 미션이름
    String vilageName = null;      // 마을이름 : 검색시 클릭하면 마을이름을 저장하고, 함께 insert 한다.
    EditText recruitContent;       // 내용
    TextView termStart;     // 기간(시작)
    TextView termEnd;       // 기간 (끝)
    EditText recruitNum;    // 모집인원
    EditText reward;        //보상
    EditText etxtPhone;     // 휴대폰 번호


    // DB에 저장
    phpUp recruitTask;
    RecruitItem recruitItem;

    // 자동완성기능
    phpDown autoComTask;// DB에서 불러오기
    phpListAutoText listAutoComTask;// DB에서 불러오기
    AutoCompleteTextView recruit_autoComplete; // 모집하기 자동완성
    private ArrayList<String> search_item_vilageName = new ArrayList<String>(); // 모집하기 이름목록
    private ArrayList<Item> search_item = new ArrayList<Item>();
    AutoCompleteTextView recruit_list_autoComplete; // 모집 리스트 자동완성
    private ArrayList<String> search_list_item = new ArrayList<String>();


    //모집리스트버튼, 모집하기버튼, 뒤로가기버튼
    Button recruitListBtn, recruitStartBtn, backBtn;
    LinearLayout recruitListLayout,recruitStartLayout;

    // 등록하기 버튼
    Button registButton;
    public static final int registSucceeded = 1; // 0이면 등록미완료, 1이면 등록완료
    public static final int registNoSucceeded = 0;
    private RegistHandler myRegistHandler = null;

    // 모집하기 리스트 부분 -----------------------------------
    Button btnSearchMission; // 찾기 버튼
    private ListView recruit_list;
    ArrayList<RecruitListItem> data=new ArrayList<>();
    List_Adapter adapter;
    phpRecruitList phpList;
    // 정렬을 위한 배열
    ArrayList<RecruitListItem> orderByTerm;
    ArrayList<RecruitListItem> orderByClick;

    // 체크박스 -> 날짜순(최신), 조회순
    // 라디오 버튼으로 하려다가 라디오 그룹과 직속관계가 되어야 하는 구조상, 우리 레이아웃 포맷으로 하기에 시간이
    // 걸릴 것 같아 체크박스로 교체
    CheckBox chkBxBtnTerm, chkBxBtnClick;

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

        // 로그인 정보 가져오는 부분
        // sharedUserId 는 로그인 한 회원의 id 정보
        // 문자열이 "" 일 경우, 미 로그인, 아니라면 로그인 중이라 가정
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();

        sharedUserId = setting.getString("info_Id", "");
        Log.e("userId : ", sharedUserId);

        // 모집하기 글쓰는 부분 ---------------------------------------------------------
        missionName = (EditText)findViewById(R.id.missionName);
        recruitContent = (EditText)findViewById(R.id.content);
        termStart = (TextView)findViewById(R.id.termStart);
        termEnd = (TextView)findViewById(R.id.termEnd);
        recruitNum = (EditText)findViewById(R.id.recruitNum);
        reward = (EditText)findViewById(R.id.reward);
        etxtPhone = (EditText)findViewById(R.id.etxtPhone);

        // 날짜 클릭할 수 있도록 설정함(집적 입력하지 않고, 휠로 날짜 맞춤 (디폴트로 오늘 날짜))
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
        recruit_autoComplete.setAdapter(new ArrayAdapter<String>(this, R.layout.auto_complete_item, search_item_vilageName));
        recruit_autoComplete.setTextColor(Color.BLACK);
        recruit_list_autoComplete = (AutoCompleteTextView)findViewById(R.id.recruit_list_autoComplete);
        recruit_list_autoComplete.addTextChangedListener(this);
        recruit_list_autoComplete.setAdapter(new ArrayAdapter<String>(this, R.layout.auto_complete_item, search_list_item));
        recruit_list_autoComplete.setTextColor(Color.BLACK);

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

        // 등록하기에 관한 핸들러 - 등록 성공/실패에 관한 것
        myRegistHandler = new RegistHandler();


        autoComTask = new phpDown();
        autoComTask.execute("http://218.150.181.131/seo/SearchVilage.php");
        listAutoComTask = new phpListAutoText();
        listAutoComTask.execute("http://218.150.181.131/seo/recruitListAutoText.php");

        recruit_autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String vilageNm = (String) parent.getItemAtPosition(position); // 클릭한 체험이름 미션이름으로 넣기

                for ( int i=0 ; i<search_item.size() ; i++ ) {
                    Item searchItem = search_item.get(i);
                    if ( searchItem.getName().equals(vilageNm)) { // 현재 클릭한 체험을 찾아서
                        missionName.setText(searchItem.getName()); // 미션이름 넣고
                        vilageName = searchItem.getName(); // 마을이름에도 체험이름 저장해두기
                        recruitContent.setText(searchItem.getVilageSlgn() + "\n주소 : " + searchItem.getAddr() + "\n전화번호 : " + searchItem.getPrcafsManMoblphon() + "\n홈페이지 : " + searchItem.getVilageHmpgUrl()); // 세부내용에 마을설명, 홈페이지, 주소, 전화번호 삽입
                        etxtPhone.setText(searchItem.getPrcafsManMoblphon().replace("-", ""));
                        break;
                    }
                }
            }
        });

        recruit_list_autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String getString = (String) adapterView.getItemAtPosition(position); // 미션이름을 가지고 온다.
                Log.e("recLstAutoClk", getString);

                // 클릭한 아이템의 미션이름을 가져와서, 그 이름이 들어간 모든 체험들을 찾는다.
                ArrayList<RecruitListItem> searchMission = new ArrayList<RecruitListItem>();
                RecruitListItem searchMissionItem;

                for (int i = 0; i < data.size(); i++) {
                    searchMissionItem = data.get(i); // data(현재 모집리스트)에 있는 각 하나 하나의 요소들을 꺼내어
                    if (searchMissionItem.getMissionName().contains(getString)) { // 선택한 미션의 이름을 포함하고 있다면
                        searchMission.add(searchMissionItem); // 새로운 배열에 추가
                    }
                }
                List_Adapter searchAdapter = new List_Adapter(getApplicationContext(), R.layout.recruit_item, searchMission);
                recruit_list.setAdapter(searchAdapter);
            }
        });

        btnSearchMission = (Button)findViewById(R.id.btnSearchMission);
        btnSearchMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = recruit_list_autoComplete.getText().toString(); // 현재 입력한 검색어를 가지고

                // 클릭한 아이템의 미션이름을 가져와서, 그 이름이 들어간 모든 체험들을 찾는다.
                ArrayList<RecruitListItem> searchMission = new ArrayList<RecruitListItem>();
                RecruitListItem searchMissionItem;

                for (int i = 0; i < data.size(); i++) {
                    searchMissionItem = data.get(i); // data(현재 모집리스트)에 있는 각 하나 하나의 요소들을 꺼내어
                    if (searchMissionItem.getMissionName().contains(searchStr)) { // 선택한 미션의 이름을 포함하고 있다면
                        searchMission.add(searchMissionItem); // 새로운 배열에 추가
                    }
                }
                List_Adapter searchAdapter = new List_Adapter(getApplicationContext(), R.layout.recruit_item, searchMission);
                recruit_list.setAdapter(searchAdapter);
            }
        });



        // 모집하기 리스트  ---------------------------------------------------------------
        recruit_list = (ListView)findViewById(R.id.recruit_listview); // 리스트뷰
        adapter = new List_Adapter(this, R.layout.recruit_item, data);

        recruit_list.setAdapter(adapter);

        phpList = new phpRecruitList();
        phpList.execute("http://218.150.181.131/seo/recruitList.php");


        // 라디오 버튼 확인, 날짜순/조회순 (디폴트 날짜순)
        chkBxBtnTerm = (CheckBox)findViewById(R.id.chkBxBtnTerm);
        chkBxBtnClick = (CheckBox)findViewById(R.id.chkBxBtnClick);

        CheckBox.OnCheckedChangeListener chkBxListener = new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                orderByTerm = new ArrayList<>();
                orderByClick = new ArrayList<>();

                // 정렬을 위한 sort 과정
                for (int i=0 ; i < data.size() ; i++) {
                    // 일단 데이터를 복사한다.
                    orderByTerm.add(data.get(i)); // 날짜순
                    orderByClick.add(data.get(i)); // 조회순
                }

                // 정렬을 위한 Comparator
                Comparator<RecruitListItem> termComparator= new Comparator<RecruitListItem>() {
                    //private Collator collator = Collator.getInstance();

                    @Override
                    // 등록순으로 정렬 - 인트형으로 비교
                    public int compare(RecruitListItem a, RecruitListItem b) {
                        return b.getIdRecruit() < a.getIdRecruit() ? -1 : b.getIdRecruit() > a.getIdRecruit() ? 1:0;
                    }
                };

                Comparator<RecruitListItem> clickComparator= new Comparator<RecruitListItem>() {
                    private Collator collator = Collator.getInstance();

                    @Override
                    public int compare(RecruitListItem a, RecruitListItem b) {
                        // 자리수가 똑같기 때문에 인트형으로 비교할 필요 없음. 스트링형으로 비교
                        return collator.compare(Integer.toString(b.getClickNum()), Integer.toString(a.getClickNum()));
                    }
                };

                switch(btn.getId()) {
                    case R.id.chkBxBtnTerm:
                        Toast.makeText(getApplicationContext(), "날짜순으로 정렬되었습니다.", Toast.LENGTH_SHORT).show();
                        chkBxBtnTerm.setBackgroundColor(Color.BLACK);
                        chkBxBtnClick.setBackgroundColor(Color.WHITE);

                        Collections.sort(orderByTerm, termComparator);
                        Log.e("orderByTerm", orderByTerm.toString());

                        adapter = new List_Adapter(getApplicationContext(), R.layout.recruit_item, orderByTerm);
                        ListView listTerm = (ListView)findViewById(R.id.recruit_listview);
                        listTerm.setAdapter(adapter);
                        break;

                    case R.id.chkBxBtnClick:
                        Toast.makeText(getApplicationContext(), "조회순으로 정렬되었습니다.", Toast.LENGTH_SHORT).show();
                        chkBxBtnClick.setBackgroundColor(Color.BLACK);
                        chkBxBtnTerm.setBackgroundColor(Color.WHITE);

                        Collections.sort(orderByClick, clickComparator);
                        Log.e("orderByClick", orderByClick.toString());

                        adapter = new List_Adapter(getApplicationContext(), R.layout.recruit_item, orderByClick);
                        ListView listClick = (ListView)findViewById(R.id.recruit_listview);
                        listClick.setAdapter(adapter);
                        break;
                }
            }
        };

        chkBxBtnTerm.setOnCheckedChangeListener(chkBxListener);
        chkBxBtnClick.setOnCheckedChangeListener(chkBxListener);


        // 모집 리스트 항목 클릭시 팝업뷰가 뜨는 작업 ----------------------------------------------
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float deviceDensityDIP = displayMetrics.densityDpi;


        // 리스트 항목 (참가할 미션)클릭시 이벤트
        recruit_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /** 이부분이 리스트 클릭 시 다른 액티비티를 띄우는 부분 **/

                RecruitListItem cleckedListItem;
                cleckedListItem = adapter.getItem(position); // 클릭한 아이템을 가져온다.

                phpRecListClickUpdate recruitClick = new phpRecListClickUpdate();
                recruitClick.execute("http://218.150.181.131/seo/RecruitClickUpdate.php?" + cleckedListItem.toString());

                Intent intent = new Intent(getApplicationContext(), RecruitPopupActivity.class);
                intent.putExtra("item", cleckedListItem); // 리스트를 클릭하면 현재 클릭한 모집에 대한 Item 클래스를 넘겨준다.
                // 인텐트로 넘겨주기 위해서는 Item 클레스에 implements Serializable 을 해줘야 함
                startActivity(intent);
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
                    // 로그인 정보 확인하여 미로그인시 로그인 권유 창

                    if ( sharedUserId.equals("") ) { // 미로그인 시 Toast
                        Toast.makeText(getApplicationContext(), "모집을 위해 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        recruitListBtn.setBackgroundResource(R.drawable.recruit_button1);
                        recruitStartBtn.setBackgroundResource(R.drawable.recruit_button2);
                        recruitListLayout.setVisibility(LinearLayout.GONE);  //gone은 눈에 안보일뿐 아니라 영역도 없어짐
                        recruitStartLayout.setVisibility(LinearLayout.VISIBLE);
                    }
                    break;

                case R.id.backbtn:
                    onBackPressed();
                    break;
                case R.id.regist_button: // 등록 버튼

                    try {
                        Log.e("regist", "button");
                        // 입력한 정보를 Item 객체에 담는다.
                        String lineEnding = recruitContent.getText().toString().replace("\n", "99line99end99");
                        String phoneNumber = etxtPhone.getText().toString();
                        String phoneNumber1 = phoneNumber.substring(0, 3);
                        String phoneNumber2 = phoneNumber.substring(3, 7);
                        String phoneNumber3 = phoneNumber.substring(7, 11);
                        String phoneNumbers = phoneNumber1 + "-" + phoneNumber2 + "-" + phoneNumber3;
                        recruitItem = new RecruitItem(URLEncoder.encode(recruit_autoComplete.getText().toString(), "UTF-8"), sharedUserId ,URLEncoder.encode( missionName.getText().toString(), "UTF-8"), URLEncoder.encode( vilageName, "UTF-8"), URLEncoder.encode(lineEnding, "UTF-8"), URLEncoder.encode(termStart.getText().toString(), "UTF-8"), URLEncoder.encode(termEnd.getText().toString(), "UTF-8"), Integer.parseInt(recruitNum.getText().toString()), 0 ,URLEncoder.encode(reward.getText().toString(), "UTF-8"), uploadFileName, URLEncoder.encode(phoneNumbers, "UTF-8"));

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
                    //intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);

                    break;
            }
        }
    };

    // Handler 클래스
    class RegistHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case registSucceeded:

                    // 입력한 정보 초기화
                    recruit_autoComplete.setText("");
                    missionName.setText("");
                    recruitContent.setText("");
                    recruitNum.setText("");
                    reward.setText("");
                    etxtPhone.setText("");

                    final Calendar c = Calendar.getInstance();
                    mStartYear = c.get(Calendar.YEAR);
                    mStartMonth= c.get(Calendar.MONTH);
                    mStartDay  = c.get(Calendar.DAY_OF_MONTH);

                    termStart.setText(new StringBuilder()
                                    //월은 시스템에서 0~11로 인식하기 때문에 1을 더해줌
                                    .append(mStartYear).append("-")
                                    .append(mStartMonth + 1).append("-")
                                    .append(mStartDay).append(" ")
                    );
                    termEnd.setText(new StringBuilder()
                                    //월은 시스템에서 0~11로 인식하기 때문에 1을 더해줌
                                    .append(mStartYear).append("-")
                                    .append(mStartMonth + 1).append("-")
                                    .append(mStartDay).append(" ")
                    );

                    Toast.makeText(getApplicationContext(), "모집 등록을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                    // 등록에 성공했기 때문에 등록 미완료로 초기화
                    break;

                case registNoSucceeded:
                    Toast.makeText(getApplicationContext(), "모집 등록을 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

    };

    // 모집하기 insert 부분
    public class phpUp extends AsyncTask<String, Integer,String> {

        Message msg = myRegistHandler.obtainMessage(); // 등록 성공/실패를 위한 핸들러

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
                msg.what = registNoSucceeded;
            }
            msg.what = registSucceeded;
            return jsonHtml.toString();


        }

        protected void onPostExecute(String str){
            myRegistHandler.sendMessage(msg);
        }
    }

    // 조회수 추가하는 php
    public class phpRecListClickUpdate extends AsyncTask<String, Integer,String> {

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
            Log.e("RecLstClick", str);
        }
    }

    // 모집하기 검색(자동완성)에서 체험 이름을 가져오는 부분
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
                    Item item = new Item(vilageName.getString("vilageNm"), vilageName.getString("adres1"), vilageName.getString("prcafsManMoblphon"), vilageName.getString("vilageHmpgUrl"), vilageName.getString("vilageSlgn"));
                    search_item_vilageName.add(item.getName()); // 자동완성을 위한 마을 이름
                    search_item.add(item); // 나중에 정보를 넘겨받기 위해 item을 따로 저장
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    // 리스트 목록에서 검색(자동완성) php 부르는 부분
    public class phpListAutoText extends AsyncTask<String, Integer,String> {

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
                    JSONObject recruitListAutoText = jAr.getJSONObject(i);

                    // 모집하기 리스트의 검색하기에서, 미션이름들을 DB에서 가져와 search_item에 추가한다.
                    String recruitListAutoTextStr;
                    recruitListAutoTextStr = recruitListAutoText.getString("missionName");
                    Log.e("recruitListAuto", recruitListAutoTextStr);
                    search_list_item.add(recruitListAutoTextStr);
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
                    RecruitListItem item = new RecruitListItem(Integer.parseInt(RecruitListJson.getString("idrecruit")),RecruitListJson.getString("userId")  ,RecruitListJson.getString("missionName"), RecruitListJson.getString("vilageName"), RecruitListJson.getString("recruitContent"),
                            RecruitListJson.getString("termStart"), RecruitListJson.getString("termEnd"), Integer.parseInt(RecruitListJson.getString("recruitNum")), Integer.parseInt(RecruitListJson.getString("joinedNum")), RecruitListJson.getString("reward"), RecruitListJson.getString("ImageURL"), Integer.parseInt(RecruitListJson.getString("clickNum")), RecruitListJson.getString("phoneNum"));
                    Log.e("RecruitItem", "idrecruit : " + RecruitListJson.getString("idrecruit") + " UserId : " + RecruitListJson.getString("userId") + " missionName : " + RecruitListJson.getString("missionName") + " vilageName : " + RecruitListJson.getString("vilageName") + " recruitContent : " + RecruitListJson.getString("recruitContent") + " termStart : " +
                            RecruitListJson.getString("termStart") + " termEnd : " + RecruitListJson.getString("termEnd") + " recruitNum : " + RecruitListJson.getString("recruitNum") + " reward : " + RecruitListJson.getString("reward") + " ImageURL : " + RecruitListJson.getString("ImageURL") + " clickNum : " + RecruitListJson.getString("clickNum") + " phoneNum : " + RecruitListJson.getString("phoneNum"));
                    data.add(item);
                    Log.e("data<RecruitItem> : ", Integer.toString(data.size())); // 몇개 인지 확인
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
                            String msg = "등록이 완료 되었습니다.";
                            Log.e("upload message : ",msg);
                            Toast.makeText(Recruit.this, "사진 업로드 완료", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Recruit.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("upload message : ","Got Exception : see logcat ");
                        Toast.makeText(Recruit.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
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

    // 리스트에 들어갈 이미지를 가져올때 쓰이는 변수들
    TextView txtvRecListTerm;
    TextView txtvRecListRecNum;
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

        return convertView;
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

class RecruitItem implements Serializable {
    private String search;
    private String userId;
    private String missionName;
    private String vilageName;
    private String recruitContent;       // 내용
    private String termStart;     // 기간(시작)
    private String termEnd;       // 기간 (끝)
    private int recruitNum;    // 모집인원
    private int joinedNum = 0;       // 참가인원
    private String reward;        //보상
    private String ImageURL ="";         // 이미지 경로
    private String phoneNum;
    public String getsearch(){return search;}
    public String getUserId() {return userId;}
    public String getmissionName(){return missionName;}
    public String getVilageName() {return vilageName;}
    public String getrecruitContent(){return recruitContent;}
    public String gettermStart(){return termStart;}
    public String gettermEnd(){return termEnd;}
    public int getrecruitNum(){return recruitNum;}
    public int getJoinedNum() {return joinedNum;}
    public String getreward(){return reward;}
    public String getimageURl() {return ImageURL;}
    public String getPhoneNum() {return phoneNum;}

    public RecruitItem(String search, String userId, String missionName, String vilageName, String recruitContent, String termStart, String termEnd, int recruitNum, int joinedNum, String reward, String ImageURL, String phoneNum){
        this.search = search;
        this.userId = userId;
        this.missionName = missionName;
        this.vilageName = vilageName;
        this.recruitContent = recruitContent;
        this.termStart = termStart;
        this.termEnd = termEnd;
        this.recruitNum = recruitNum;
        this.joinedNum = joinedNum;
        this.reward = reward;
        this.ImageURL = ImageURL;
        this.phoneNum = phoneNum;
    }

    public String toString() {

        return "search=" + search + "&UserId=" + userId + "&missionName=" + missionName + "&vilageName=" + vilageName + "&recruitContent=" +recruitContent + "&termStart=" + termStart + "&termEnd=" + termEnd + "&" + "recruitNum=" + Integer.toString(recruitNum) + "&joinedNum=" + Integer.toString(joinedNum) + "&reward=" + reward + "&ImageURL=" + ImageURL + "&phoneNum=" + phoneNum;
    }
}

class RecruitListItem implements Serializable {
    private int idRecruit;
    private String userId;
    private String missionName;
    private String vilageName;
    private String recruitContent;
    private String termStart;
    private String termEnd;
    private int recruitNum;
    private int joinedNum;
    private String reward;
    private String ImageURL;
    private int clickNum;
    private String phoneNum;
    public int getIdRecruit() {return idRecruit;}
    public String getUserId() {return userId;}
    public String getMissionName(){return missionName;}
    public String getVilageName() {return vilageName;}
    public String getRecruitContent(){return recruitContent;}
    public String getTermStart(){return termStart;}
    public String getTermEnd() {return termEnd;}
    public int getRecruitNum(){return recruitNum;}
    public int getJoinedNum() {return joinedNum;}
    public String getReward(){return reward;}
    public String getImageURL() {return ImageURL;}
    public int getClickNum() {return clickNum;}
    public String getPhoneNum() {return phoneNum;}
    public RecruitListItem(int idRecruit, String userId ,String missionName, String vilageName, String recruitContent, String termStart, String termEnd, int recruitNum, int joinedNum, String reward, String ImageURL, int clickNum, String phoneNum){
        this.idRecruit = idRecruit;
        this.userId = userId;
        this.missionName = missionName;
        this.vilageName = vilageName;
        this.recruitContent = recruitContent;
        this.termStart = termStart;
        this.termEnd = termEnd;
        this.recruitNum = recruitNum;
        this.joinedNum = joinedNum;
        this.reward = reward;
        this.ImageURL = ImageURL;
        this.clickNum = clickNum;
        this.phoneNum = phoneNum;
    }
    public String toString() {

        return "idrecruit=" + idRecruit + "&UserId=" + userId + "&missionName=" + missionName + "&vilageName" + vilageName + "&recruitContent=" +recruitContent + "&termStart=" + termStart + "&termEnd=" + termEnd + "&" + "recruitNum=" + Integer.toString(recruitNum) + "&joinedNum=" + Integer.toString(joinedNum) + "&reward=" + reward + "&ImageURL=" + ImageURL + "&clickNum=" + clickNum + "&phoneNum=" + phoneNum;
    }
}

class Item implements Serializable{
    private String name;
    private String addr;
    private String prcafsManMoblphon;
    private String vilageHmpgUrl;
    private String vilageSlgn;
    public String getName(){return name;}
    public String getAddr(){return addr;}
    public String getPrcafsManMoblphon() {return prcafsManMoblphon;}
    public String getVilageHmpgUrl(){return vilageHmpgUrl;}
    public String getVilageSlgn() {return vilageSlgn;}
    public Item(String name, String addr, String prcafsManMoblphon, String vilageHmpgUrl, String vilageSlgn){
        this.name = name;
        this.addr = addr;
        this.prcafsManMoblphon = prcafsManMoblphon;
        this.vilageHmpgUrl = vilageHmpgUrl;
        this.vilageSlgn = vilageSlgn;
    }
}