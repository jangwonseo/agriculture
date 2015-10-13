package vivz.slidenerd.agriculture.home;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.recruit.RecruitListItem;
import vivz.slidenerd.agriculture.sign.DatePickerFragment;

public class ChangeMyinfoActivity extends ActionBarActivity {

    //sharedPreference 선언부
    public SharedPreferences setting;
    public SharedPreferences.Editor editor;
    String userId = null;

    Button BackBtnChangeMyinfo; // back button

    //유저가 입력한 회원가입 정보
    private EditText ChangeUserName = null;
    private TextView MyinfoUserId = null;
    private EditText ChangePassword = null;
    private EditText ChangePassword_re = null;
    private EditText ChangePhoneNum = null;
    private EditText ChangeBirthYear;
    private EditText ChangeBirthMonth;
    private EditText ChangeBirthDay;

    private String uSex;
    public RadioGroup ChangeRadiogroup1;

    private Button ChangeMyinfoConfirm;
    private Button ChangeMyinfoCancel;

    //DB로 전송할 정보
    private String info_Id = null;
    private String info_Name = null;
    private String info_Pw = null;
    private String info_PhoneNum = null;
    private String info_Birthday = null;
    private String info_Sex = null;

    private MemberItem memberItem; //회원정보를 담은 클래스
    private phpInsert phpTask;  //php insert 연동 소스


    // 사진업로드 부분 ------------------------------------------
    private ImageView imgvSelectpic;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    private String upLoadServerUri = "http://218.150.181.131/seo/UploadToServer.php";
    private String imagepath=null;
    String uploadFileName=null;
    //-----------------------------------------------------------

    ChangeHandler changeHandler;
    phpGetUserInfo getUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_myinfo);

        //sharedPreference로 전역 공유공간을 만듬
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();
        userId = setting.getString("info_Id", "");

        //입력필드 셋팅
        ChangeUserName = (EditText)findViewById(R.id.ChangeUserName);
        MyinfoUserId = (TextView)findViewById(R.id.MyinfoUserId);
        ChangePassword = (EditText)findViewById(R.id.ChangePassword);
        ChangePassword_re = (EditText)findViewById(R.id.ChangePassword_re);
        ChangePhoneNum = (EditText)findViewById(R.id.ChangePhoneNum);
        ChangeBirthYear = (EditText) findViewById(R.id.ChangeBirthYear);
        ChangeBirthMonth = (EditText) findViewById(R.id.ChangeBirthMonth);
        ChangeBirthDay = (EditText) findViewById(R.id.ChangeBirthDay);

        uSex = "1";
        ChangeRadiogroup1 = (RadioGroup) findViewById(R.id.ChangeRadiogroup1);

        ChangeMyinfoConfirm = (Button)findViewById(R.id.ChangeMyinfoConfirm);
        ChangeMyinfoCancel = (Button)findViewById(R.id.ChangeMyinfoCancel);

        BackBtnChangeMyinfo  = (Button)findViewById(R.id.BackBtnChangeMyinfo);

        MyinfoUserId.setText(userId);

        //성별 입력부분에 대한 리스너
        ChangeRadiogroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);

                // "남자"를 선택했다면...
                if (checkedId == R.id.radio_male) {
                    Log.d("seojang", "userSex1 : " + rb.getText().toString());
                    info_Sex = "1";
                }

                // "여자"를 선택했다면...
                else if (checkedId == R.id.radio_female) {
                    Log.d("seojang", "userSex0 : " + rb.getText().toString());
                    info_Sex = "0";
                }
            }
        });

        //생일 입력부분에 dialog창 뜨도록 이벤트걸어줌
        ChangeBirthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사용자가 날짜를 선택할수 있는 dialog 창을 띄우는 메소드
                showDatePicker();
            }
        });

        //확인 버튼을 눌렀을때 값 저장
        ChangeMyinfoConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //비밀번호 입력란과 비밀번호재입력란이 다르면 다르다고 해야함
                if (!((ChangePassword.getText().toString()).equals(ChangePassword_re.getText().toString()))) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 달라요!", Toast.LENGTH_SHORT).show();
                } else {
                    //이부분에 나온 값들을 db 로 연동시켜서 회원정보 저장시킬것!!
                    Log.d("seojang", "비밀번호 일치 : " + ChangePassword.getText().toString() + " , " + ChangePassword_re.getText().toString());
                    try {
                        info_Name = URLEncoder.encode(ChangeUserName.getText().toString(), "UTF-8");
                        info_Id = userId;
                        info_Pw = ChangePassword.getText().toString();
                        info_PhoneNum = ChangePhoneNum.getText().toString();
                        info_Birthday = ChangeBirthYear.getText().toString() + "_" + ChangeBirthMonth.getText().toString() + "_" + ChangeBirthDay.getText().toString();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //저장된 값들을 php를 통해 서버에 db화 시킴
                    memberItem = new MemberItem(info_Name, info_Id, info_Pw, info_PhoneNum, info_Birthday, info_Sex);
                    phpTask = new phpInsert();

                    phpTask.execute("http://218.150.181.131/seo/phpChangeInfo.php?username=" + memberItem.getInfo_Name() + "&userpw=" + memberItem.getInfo_Pw() + "&userphonenumber=" + memberItem.getInfo_PhoneNum() + "&userbirthday=" + memberItem.getinfo_Birthday() + "&usersex=" + memberItem.getinfo_Sex() + "&userid=" + userId);

                    // 사진 업로드 부분----------------------------------------------
                    dialog = ProgressDialog.show(ChangeMyinfoActivity.this, "", "Uploading file...", true);
                    Log.e("upload message : ", "uploading started.....");
                    new Thread(new Runnable() {
                        public void run() {

                            if (imagepath == null) {
                                imagepath = "";
                                dialog.dismiss();
                                Log.e("Uploading file : ", "file is null, User don't select picture.");
                                return;
                            } else {
                                uploadFile(imagepath);
                            }
                        }
                    }).start();
                    ///////////////////////////////////////////////////////

                    Toast.makeText(getApplicationContext(), "회원정보를 수정하였습니다.", Toast.LENGTH_SHORT).show();

                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(homeIntent);
                }
            }
        });

        //취소버튼눌럿을때 액티비티 종료
        ChangeMyinfoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 백버튼
        BackBtnChangeMyinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // 사진 업로드 부분
        imgvSelectpic  = (ImageView)findViewById(R.id.ChangeProfile);
        imgvSelectpic.setBackgroundColor(0);

        imgvSelectpic.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, 1);

                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });

        // 기존 회원 정보 가져오기
        changeHandler = new ChangeHandler();
        getUserInfo = new phpGetUserInfo();
        getUserInfo.execute("http://218.150.181.131/seo/phpGetUserInfo.php?userId="+userId);

    }

    // 모집 리스트 출력 부분
    public class phpGetUserInfo extends AsyncTask<String, Integer,String> {

        Message msg = changeHandler.obtainMessage(); // 회원 정보를 가져와 UI에 적용시키기 위한 핸들러 메세지

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
                JSONObject UserJson = jAr.getJSONObject(0);

                MemberItem item = new MemberItem(UserJson.getString("username"), userId, null ,UserJson.getString("userphonenumber"), UserJson.getString("userbirthday"), UserJson.getString("usersex"));
                    Log.e("UserJson ", UserJson.toString());


                msg.obj = item;
                changeHandler.sendMessage(msg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    // Handler 클래스
    class ChangeHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MemberItem useritem = (MemberItem)msg.obj;


            ChangePhoneNum.setText(useritem.getInfo_PhoneNum());
            ChangeUserName.setText(useritem.getInfo_Name());

            String birth = useritem.getinfo_Birthday();
            int a = birth.indexOf("_");
            int b = birth.lastIndexOf("_");
            ChangeBirthYear.setText(birth.substring(0, 4));
            ChangeBirthMonth.setText(birth.substring(a+1, b));
            ChangeBirthDay.setText(birth.substring(b + 1, birth.length()));
        }

    };

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
            ChangeBirthYear.setText(String.valueOf(year));
            ChangeBirthMonth.setText(String.valueOf(monthOfYear+1));// 월은 -1로 계산되므로 +1해줌.
            ChangeBirthDay.setText(String.valueOf(dayOfMonth));
        }
    };

    class MemberItem implements Serializable {
        private String info_Name; //이름
        private String info_Id;  //아이디
        private String info_Pw;       // 비번
        private String info_PhoneNum;     // 폰번호
        private String info_Birthday; //생년월일
        private String info_Sex;

        public String getInfo_Name() {
            return info_Name;
        }
        public String getInfo_Id() {
            return info_Id;
        }
        public String getInfo_Pw() {
            return info_Pw;
        }
        public String getInfo_PhoneNum() {
            return info_PhoneNum;
        }
        public String getinfo_Birthday() {
            return info_Birthday;
        }
        public String getinfo_Sex(){ return info_Sex; }

        public MemberItem(String info_Name, String info_Id, String info_Pw, String info_PhoneNum, String info_Birthday,String info_Sex) {
            this.info_Name = info_Name;
            this.info_Id = info_Id;
            this.info_Pw = info_Pw;
            this.info_PhoneNum = info_PhoneNum;
            this.info_Birthday = info_Birthday;
            this.info_Sex = info_Sex;
        }

        public String toString() {
            return "info_Name=" + info_Name + "&" + "info_Id=" + info_Id + "&" + "info_Pw=" +info_Pw + "&" + "info_PhoneNum=" + info_PhoneNum + "&" + "info_Birthday=" + info_Birthday + "&" + "info_Sex="+info_Sex;
        }
    }

    public class phpInsert extends AsyncTask<String, Integer,String> {

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

            Log.d("seojang","phpInsert : "+str);
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
                String uploadingFileName = userId+"Profile.jpg";
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + uploadingFileName + "\"" + lineEnd);
                Log.e("fileName : ", uploadingFileName);

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
                            Toast.makeText(ChangeMyinfoActivity.this, "사진 업로드 완료", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ChangeMyinfoActivity.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("upload message : ","Got Exception : see logcat ");
                        Toast.makeText(ChangeMyinfoActivity.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
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

            uploadFileName = info_Id + "Profile.jpg";
            //Log.e("fileName : ", uploadFileName);

            try {
                Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imgvSelectpic.setBackgroundColor(Color.BLACK);
                imgvSelectpic.setImageBitmap(image_bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e)
            {
                e.printStackTrace();
            }


        }
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
