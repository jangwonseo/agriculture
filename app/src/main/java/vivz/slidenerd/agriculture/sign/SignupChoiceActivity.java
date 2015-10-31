package vivz.slidenerd.agriculture.sign;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.service_prepare;


public class SignupChoiceActivity extends ActionBarActivity {

    // 새로운 컴포넌트들
    CallbackManager callbackManager;
    private LoginButton loginButton;
    TextView info;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    Intent fbLoginIntent;


    private LoginButton mButtonFacebookSignup;
    private Button mButtonNormalSignup;
    private Button tempFBButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signupchoice);
        callbackManager = CallbackManager.Factory.create();


        loginButton = (LoginButton)findViewById(R.id.facebooklogin_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        loginButton.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);

        info = (TextView)findViewById(R.id.info);



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // 로그인되어있는지 임시적으로 확인하려고 사용
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()

                );

                // 정보 받아오는 graph api
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                Log.v("LoginActivity", response.toString());
                                info.setText(object.optString("email"));
                                Log.e("aaa",object.optString("birthday"));
                                Log.e("aaa", AccessToken.getCurrentAccessToken().getUserId().toString());

                                fbLoginIntent = new Intent( getApplicationContext(),SignupActivity.class);
                                Log.e("aaa", "1");
                                fbLoginIntent.putExtra("isFacebookFlag", "1");
                                Log.e("aaa", "2");
                                startActivity(fbLoginIntent);
                                Log.e("aaa", "3");
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");

            }

            @Override
            public void onError(FacebookException e) {

                info.setText("Login attempt failed.");

            }
        });


        //일반 회원가입버튼 클릭시
        mButtonNormalSignup = (Button)findViewById(R.id.normallogin_button);
        mButtonNormalSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent normalSingupIntent = new Intent(getApplication(),SignupActivity.class);
                normalSingupIntent.putExtra("isFacebookFlag","0");
                startActivity(normalSingupIntent);
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {


                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }
        };





    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
