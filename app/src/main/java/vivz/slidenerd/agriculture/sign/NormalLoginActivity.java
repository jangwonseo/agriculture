package vivz.slidenerd.agriculture.sign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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


        id_Insert = (EditText)findViewById(R.id.id_insert);
        id_Insert.setTypeface(yunGothicFont);
        pw_Insert = (EditText)findViewById(R.id.pw_insert);
        pw_Insert.setTypeface(yunGothicFont);
        //윤고딕 폰트

        //sharedPreference로 전역 공유공간을 만듬
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();
        userId = setting.getString("info_Id", "");

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
                if ( userId.equals("") || userId == null) { // 로그인을 안했다면
                    // 로그인 코드
                } else { // 로그인을 했다면
                    // 로그아웃
                    //sharedPreference 입력부분
                    editor.putString("info_Id", "");
                    editor.putString("info_Pw", "");
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
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
}
