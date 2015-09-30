package vivz.slidenerd.agriculture;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;

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
import java.lang.reflect.Member;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;


public class SignupActivity extends FragmentActivity {

    //sharedPreference 선언부
    public SharedPreferences setting;
    public SharedPreferences.Editor editor;

    private Intent flagIntent;

    public TextView ProfileImage;
    private Button backButton;


    private MemberItem memberItem; //회원정보를 담은 클래스
    private phpInsert phpTask;  //php insert 연동 소스

    //유저가 입력한 회원가입 정보
    private EditText uName = null;
    private EditText uId = null;
    private EditText uPw = null;
    private EditText uPw_re = null;
    private EditText uPhoneNum = null;
    private EditText uBirthday_year;
    private EditText uBirthday_month;
    private EditText uBirthday_day;
    private String uSex;
    public RadioGroup uSexRadioGroup;
    private Button confirmButton;
    private Button cancelButton;


    //페이스북에서 가져온 회원정보
    public String fbName;
    public String fbId;
    public String fbPicture;
    public String fbGender;
    public String fbEmail;
    public String fbBirthday;
    private JSONObject user;

    //DB로 전송할 정보
    private String info_Id = null;
    private String info_Name = null;
    private String info_Pw = null;
    private String info_PhoneNum = null;
    private String info_Birthday = null;
    private String info_Sex = null;

    private Drawable userProfilePic;
    private String userProfilePicID;

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String PICTURE = "picture";
    private static final String GENDER = "gender";
    private static final String EMAIL = "email";
    private static final String BIRTHDAY = "birthday";


    private static final String FIELDS = "fields";

    private static final String REQUEST_FIELDS =
            TextUtils.join(",", new String[]{ID, NAME, PICTURE, GENDER, EMAIL, BIRTHDAY});


    // 사진업로드 부분 ------------------------------------------
    private ImageView imgvSelectpic;
    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;
    private String upLoadServerUri = "http://218.150.181.131/seo/UploadToServer.php";
    private String imagepath=null;
    String uploadFileName=null;
    //-----------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        //sharedPreference로 전역 공유공간을 만듬
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();


        flagIntent = getIntent();
        String isFacebook = flagIntent.getStringExtra("isFacebookFlag");

        //입력필드 셋팅
        uName = (EditText)findViewById(R.id.user_name);
        uId = (EditText)findViewById(R.id.user_id);
        uPw = (EditText)findViewById(R.id.user_password);
        uPw_re = (EditText)findViewById(R.id.user_password_re);
        uPhoneNum = (EditText)findViewById(R.id.user_phonenumber);
        uBirthday_year = (EditText) findViewById(R.id.user_birthday_year);
        uBirthday_month = (EditText) findViewById(R.id.user_birthday_month);
        uBirthday_day = (EditText) findViewById(R.id.user_birthday_day);
        uSex = "1";
        uSexRadioGroup = (RadioGroup) findViewById(R.id.radiogroup1);
        confirmButton = (Button)findViewById(R.id.confirm);
        cancelButton = (Button)findViewById(R.id.cancel);

        backButton  = (Button)findViewById(R.id.signup_backbutton);

        //페이스북을 통한 회원가입일경우
        if(isFacebook.equals("1")){
            //세션으로부터 회원정보를 가져옴(페이스북)

            fetchUserInfo();
            //페이스북 세션토큰
            final AccessToken accessToken = AccessToken.getCurrentAccessToken();
            fbId = accessToken.getUserId();
            //페이스북 프로필 이미지를 가져온다.
            ImageRequest request = getImageRequest();
            if (request != null) {
                Uri requestUri = request.getImageUri();
                Log.d("seojang", "size : " + requestUri);

                // Do we already have the right picture? If so, leave it alone.
                if (!requestUri.equals(ProfileImage.getTag())) {
                    if (fbId.equals(userProfilePicID)) {
                        ProfileImage.setTag(requestUri);
                    } else {
                        ImageDownloader.downloadAsync(request);
                    }
                }
            }
        }
        //일반 회원가입일 경우
        else{
            Log.d("seojang","isFacebooknormal : "+isFacebook);
        }

        //성별 입력부분에 대한 리스너
        uSexRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
        uBirthday_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사용자가 날짜를 선택할수 있는 dialog 창을 띄우는 메소드
                showDatePicker();
            }
        });



        //확인 버튼을 눌렀을때 값 저장
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //비밀번호 입력란과 비밀번호재입력란이 다르면 다르다고 해야함
                if((uId.getText().toString().equals("")) || (uName.getText().toString().equals("")) || (uPw.getText().toString().equals("")) || (uPhoneNum.getText().toString().equals("")) || (uBirthday_year.getText().toString().equals("")) || (uBirthday_month.getText().toString().equals("")) || (uBirthday_day.getText().toString().equals(""))){
                    Toast.makeText(getApplicationContext(), "빠뜨린 정보가 있어요!", Toast.LENGTH_SHORT).show();
                }
                else if(!((uPw.getText().toString()).equals(uPw_re.getText().toString()))){
                    Toast.makeText(getApplicationContext(), "비밀번호가 달라요!", Toast.LENGTH_SHORT).show();
                }else{
                    //이부분에 나온 값들을 db 로 연동시켜서 회원정보 저장시킬것!!
                    Log.d("seojang","비밀번호 일치 : "+uPw.getText().toString()+" , "+uPw_re.getText().toString());
                    try {
                        info_Name = URLEncoder.encode(uName.getText().toString(), "UTF-8");
                        info_Id = uId.getText().toString();
                        info_Pw = uPw.getText().toString();
                        info_PhoneNum = uPhoneNum.getText().toString();
                        info_Birthday = uBirthday_year.getText().toString() + "_" + uBirthday_month.getText().toString() + "_" + uBirthday_day.getText().toString();



                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }


                    //저장된 값들을 php를 통해 서버에 db화 시킴
                    memberItem = new MemberItem(info_Name,info_Id,info_Pw,info_PhoneNum,info_Birthday,info_Sex);
                    phpTask = new phpInsert();

                    phpTask.execute("http://218.150.181.131/seo/signup.php?" + memberItem.toString());


                    // 사진 업로드 부분----------------------------------------------
                    dialog = ProgressDialog.show(SignupActivity.this, "", "Uploading file...", true);
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



                    //sharedPreference 입력부분
                    editor.putString("info_Id", "" + info_Id);
                    editor.putString("info_Pw", "" + info_Pw);
                    editor.commit();


                    //sharedPreference 출력부분
                    Log.d("seojang", "gogogogogogo : " + setting.getString("info_Id", ""));

                    Toast.makeText(getApplicationContext(), "회원가입 완료!.", Toast.LENGTH_SHORT).show();

                    Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(homeIntent);
                }

            }
        });
        //취소버튼눌럿을때 액티비티 종료
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // 사진 업로드 부분
        //uploadButton = (Button)findViewById(R.id.uploadButton); ////////////// 이부분은 등록하기 버튼에 추가되어야 함
        imgvSelectpic  = (ImageView)findViewById(R.id.profile_image);
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

    }

    // 웹뷰 이미지 화면을 웹뷰크기에 맞게 조절
    public String creHtmlBody(String imgUrl) {
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");
        //sb.append("<img src = \"" + imgUrl + "\">"); // 자기 비율에 맞게 나온다.
        sb.append("<img width='100%' height='100%' style='-moz-border-radius: 220px;" +
                "-webkit-border-radius: 220px; ' src = \"" + imgUrl + "\">"); // 꽉 채운 화면으로 나온다.

        sb.append("</BODY>");
        sb.append("</HTML>");
        return sb.toString();
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


    private void fetchUserInfo() {
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();


        if (accessToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            user = me;

                            fbId = user.optString("id");
                            fbPicture = user.optString("picture");
                            fbName = user.optString("name");
                            fbGender = user.optString("gender");
                            fbEmail = user.optString("email");
                            fbBirthday = user.optString("birthday");
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString(FIELDS, REQUEST_FIELDS);
            request.setParameters(parameters);
            GraphRequest.executeBatchAsync(request);
        } else {
            user = null;
        }
    }
    private ImageRequest getImageRequest() {
        ImageRequest request = null;
        //getProfilePrictureUri 메소드의 파라미터는 id, 가져올때 이미지 가로,세로 크기만큼 가져옴.
        ImageRequest.Builder requestBuilder = new ImageRequest.Builder(getApplication(), ImageRequest.getProfilePictureUri(fbId, 156, 156));

        request = requestBuilder.setCallerTag(this).setCallback(new ImageRequest.Callback() {
            @Override
            public void onCompleted(ImageResponse response) {
                processImageResponse(fbId, response);
            }
        }).build();
        return request;
    }
    private void processImageResponse(String id, ImageResponse response) {
        if (response != null) {
            Bitmap bitmap = getCircleBitmap(response.getBitmap());
            if (bitmap != null) {
                BitmapDrawable drawable = new BitmapDrawable(getApplication().getResources(), bitmap);
                //getProfilePrictureUri 의 2,3번째 파라미터의 2배로 값을 잡으면 2배 확대된다. setBounds에 의해서,
                drawable.setBounds(0, 0, 312, 312);
                userProfilePic = drawable;
                userProfilePicID = id;
                ProfileImage.setCompoundDrawables(drawable, null, null, null);
                ProfileImage.setTag(response.getRequest().getImageUri());
            }
        }
    }
    public Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int size = (bitmap.getWidth() / 2);
        canvas.drawCircle(size, size, size, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
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
            uBirthday_year.setText(String.valueOf(year));
            uBirthday_month.setText(String.valueOf(monthOfYear+1));// 월은 -1로 계산되므로 +1해줌.
            uBirthday_day.setText(String.valueOf(dayOfMonth));
        }
    };


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
                String uploadingFileName = uId.getText().toString()+"Profile.jpg";
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
                            Toast.makeText(SignupActivity.this, "사진 업로드 완료", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SignupActivity.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("upload message : ","Got Exception : see logcat ");
                        Toast.makeText(SignupActivity.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show();
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
