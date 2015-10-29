package vivz.slidenerd.agriculture.sign;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.service_prepare;


public class SignupChoiceActivity extends ActionBarActivity {

    private LoginButton mButtonFacebookSignup;
    private Button mButtonNormalSignup;
    private Button tempFBButton;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;

    FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();


            Log.d("jangwon", "id : " + profile.getId() + " name : " + profile.getName() + " uri : " + profile.getLinkUri() + " pricture : " + profile.getProfilePictureUri(65, 65));

            //페이스북 회원가입 클릭시
            //move to SignupActivity
            if (profile != null) {
                Intent SignupIntent = new Intent(getApplicationContext(), SignupActivity.class);
                SignupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SignupIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                SignupIntent.putExtra("isFacebookFlag","1");
                startActivity(SignupIntent);
                finish();
            }
        }

        @Override
        public void onCancel() {Log.d("jangwon", "onCancel");}
        @Override
        public void onError(FacebookException e) {Log.d("jangwon", "onError " + e);}
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signupchoice);

        mCallbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();


        //페이스북 회원가입 버튼 클릭시
        //mButtonFacebookSignup = (LoginButton)findViewById(R.id.facebooklogin_button);
        //mButtonFacebookSignup.setCompoundDrawables(null, null, null, null);
        //mButtonFacebookSignup.setReadPermissions("user_friends", "email", "public_profile", "user_birthday");
        //mButtonFacebookSignup.registerCallback(mCallbackManager, mFacebookCallback);
        //mButtonFacebookSignup.setBackgroundResource(R.drawable.facebooksignup_button);

        //임시 페북 회원가입버튼과 이벤트
        tempFBButton = (Button)findViewById(R.id.fbbutton);
        tempFBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), service_prepare.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        //일반 회원가입버튼 클릭시
        mButtonNormalSignup = (Button)findViewById(R.id.normallogin_button);
        mButtonNormalSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent normalSingupIntent = new Intent(getApplication(),SignupActivity.class);
                normalSingupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                normalSingupIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                normalSingupIntent.putExtra("isFacebookFlag","0");
                startActivity(normalSingupIntent);
                finish();
            }
        });


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("VIVZ", "" + currentAccessToken);
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("VIVZ", "" + currentProfile);
            }
        };
    }
    @Override
    public void onStart(){
        super.onStart();

//        setupTextDetails(view);
//        setupfFacebookLoginButton(view);
//        setupfNormalLoginButton(view);
//        setupSignupButton(view);
    }


    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();


    }
    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
