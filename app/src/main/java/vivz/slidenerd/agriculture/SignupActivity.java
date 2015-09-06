package vivz.slidenerd.agriculture;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageResponse;

import org.json.JSONObject;

import java.util.Calendar;


public class SignupActivity extends FragmentActivity {

    private Intent flagIntent;

    public TextView ProfileImage;
    private Button backButton;

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
    private String info_Id;
    private String info_Name;
    private String info_Pw;
    private String info_PhoneNum;
    private String info_Birthday_year;
    private String info_Birthday_month;
    private String info_Birthday_day;
    private String info_Sex;



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        flagIntent = getIntent();
        String isFacebook = flagIntent.getStringExtra("isFacebookFlag");


        //입력필드 셋팅
        ProfileImage = (TextView) findViewById(R.id.profile_image);
        uName = (EditText)findViewById(R.id.user_name);
        uId = (EditText)findViewById(R.id.user_id);
        uPw = (EditText)findViewById(R.id.user_password);
        uPw_re = (EditText)findViewById(R.id.user_password_re);
        uPhoneNum = (EditText)findViewById(R.id.user_phonenumber);
        uBirthday_year = (EditText) findViewById(R.id.user_birthday_year);
        uBirthday_month = (EditText) findViewById(R.id.user_birthday_month);
        uBirthday_day = (EditText) findViewById(R.id.user_birthday_day);
        uSex = null;
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
                RadioButton rb = (RadioButton)findViewById(checkedId);

                // "남자"를 선택했다면...
                if(checkedId==R.id.radio_male)
                {
                    Log.d("seojang", "userSex : " + rb.getText().toString());
                }

                // "여자"를 선택했다면...
                else if(checkedId==R.id.radio_female)
                {
                    Log.d("seojang", "userSex : " + rb.getText().toString());
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
                if(!((uPw.getText().toString()).equals(uPw_re.getText().toString()))){
                        Log.d("seojang","비밀번호불일치 : "+uPw.getText().toString()+" , "+uPw_re.getText().toString());
                }else{
                    //이부분에 나온 값들을 db 로 연동시켜서 회원정보 저장시킬것!!
                    Log.d("seojang","비밀번호 일치 : "+uPw.getText().toString()+" , "+uPw_re.getText().toString());
                    info_Name = uName.getText().toString();
                    info_Id = uId.getText().toString();
                    info_Pw = uPw.getText().toString();
                    info_PhoneNum = uPhoneNum.getText().toString();
                    info_Birthday_year = uBirthday_year.getText().toString();
                    info_Birthday_month = uBirthday_month.getText().toString();
                    info_Birthday_day = uBirthday_day.getText().toString();
                }
                Intent homeIntent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(homeIntent);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_singup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
