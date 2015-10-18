package vivz.slidenerd.agriculture.sign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.home.HomeActivity;


public class NormalLoginActivity extends ActionBarActivity {
    private Typeface yunGothicFont;
    private EditText id_Insert,pw_Insert;
    private Button nomalLoginBackButton, btnLogin;

    //sharedPreference 선언부
    public SharedPreferences setting;
    public SharedPreferences.Editor editor;
    String userId;
    String id;
    String pw;

    SHA256 sha;
    signIn signin;

    public boolean isSucceed;

    SignHandler signHandler = new SignHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_login);
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

        nomalLoginBackButton = (Button)findViewById(R.id.normallogin_back);
        nomalLoginBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // 암호화 클래스 선언
        sha = new SHA256();

        // id, pw 일치 여부 확인 php
        signin = new signIn();

        isSucceed=false;

        id_Insert = (EditText)findViewById(R.id.id_insert);
        id_Insert.setTypeface(yunGothicFont);
        pw_Insert = (EditText)findViewById(R.id.pw_insert);
        pw_Insert.setTypeface(yunGothicFont);
        //윤고딕 폰트

        //sharedPreference로 전역 공유공간을 만듬
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();
        userId = setting.getString("info_Id", "");

        // 로그인을 했다면
        if ( !userId.equals("")) {

            Toast.makeText(getApplicationContext(), "이미 로그인이 되어 있습니다.\n " +
                    "로그아웃 버튼 터치시 로그아웃됩니다..", Toast.LENGTH_SHORT).show();

        }



        LinearLayout ll = (LinearLayout)findViewById(R.id.linear_login_out);
        if ( userId.equals("") || userId==null ) {
            ll.setBackgroundResource(R.drawable.login7);
        } else {
            ll.setBackgroundResource(R.drawable.login7_1);
        }

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(userId.equals("") || userId == null)){

                    // 로그아웃
                    //sharedPreference 입력부분
                    editor.putString("info_Id", "");
                    editor.putString("info_Pw", "");
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent homeIntent = new Intent(NormalLoginActivity.this, NormalLoginActivity.class);
                    startActivity(homeIntent);
                }
                else if(!isIdEmpty()&&!isPwEmpty()&&(userId.equals("") || userId == null)) {

                    // 로그인 코드
                    id = id_Insert.getText().toString();
                    pw = sha.testSHA256(pw_Insert.getText().toString());

                    signin.execute("http://218.150.181.131/seo/signin.php?userId=" + id + "&userPw=" + pw);


                }
                else if(isIdEmpty()||isPwEmpty())
                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal_login, menu);
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

    // 아이디 비어있는지 검사
    boolean isIdEmpty()
    {
        // 공백문자 제거
        String str =id_Insert.getText().toString().replaceAll("\\p{Z}", "");
        return str.equals("");

    }

    // 비밀번호 비어있는지 검사
    boolean isPwEmpty()
    {
        // 공백문자 제거
        String str = pw_Insert.getText().toString().replaceAll("\\p{Z}", "");
        return str.equals("");
    }


    // 로그인 php
    public class signIn extends AsyncTask<String, Integer, String> {

        Message msg = signHandler.obtainMessage(); // 취소 성공/실패를 위한 핸들러

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
            if (str.contains("Correct Id and Password")) {
                msg.what = 1;
            }
            else {
                msg.what = 2;
            }

            signHandler.sendMessage(msg);
        }
    }

    // Handler 클래스
    class SignHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1: // 로그인 성공
                    Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("aaaaa", "로그인됨");
                    // sharedPreference 입력
                    editor.putString("info_Id", "" + id);
                    editor.putString("info_Pw", "" + pw);
                    editor.commit();

                    // 로그인을 했다면
                    finish();
                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(homeIntent);
                    break;
                case 2: // 로그인 실패
                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    // sharedPreference 입력
                    editor.putString("info_Id", "");
                    editor.putString("info_Pw", "");
                    editor.commit();
                    finish();
                    Intent homeIntent1 = new Intent(NormalLoginActivity.this, NormalLoginActivity.class);
                    startActivity(homeIntent1);
                    break;
            }
        }

    };
}
