package vivz.slidenerd.agriculture;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class SignChoiceActivityFragment extends Fragment {

    private LoginButton mButtonFacebookLogin;
    private Button mButtonNormalLogin;
    private Button mSignuupButton;

    private Drawable userProfilePic;
    private String userProfilePicID;

    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String PICTURE = "picture";
    private static final String GENDER = "gender";
    private static final String EMAIL = "email";
    private static final String BIRTHDAY = "birthday";

    public String name;
    public String id;
    public String picture;
    public String gender;
    public String email;
    public String birthday;
    private JSONObject user;

    private static final String FIELDS = "fields";

    private static final String REQUEST_FIELDS =
            TextUtils.join(",", new String[]{ID, NAME, PICTURE, GENDER, EMAIL, BIRTHDAY});


    private TextView mTextDetails;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;

    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d("VIVZ", "onSuccess");
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            fetchUserInfo();
            mTextDetails.setText(constructWelcomeMessage(profile));

            //move to HomeActivity
            if(profile != null){
                Intent intent = new Intent(getActivity(),HomeActivity.class);
                startActivity(intent);
            }
        }


        @Override
        public void onCancel() {
            Log.d("VIVZ", "onCancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.d("VIVZ", "onError " + e);
        }
    };


    public SignChoiceActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signchoiceactivityfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupTextDetails(view);
        setupFacebookLoginButton(view);
        setupNormalLoginButton(view);
        setupSignupButton(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();


//        ImageRequest request = getImageRequest();
//        if (request != null) {
//            Uri requestUri = request.getImageUri();
//            // Do we already have the right picture? If so, leave it alone.
//            if (!requestUri.equals(mTextDetails.getTag())) {
//                if (id.equals(userProfilePicID)) {
//                    mTextDetails.setCompoundDrawables(
//                            null, userProfilePic, null, null);
//                    mTextDetails.setTag(requestUri);
//                } else {
//                    ImageDownloader.downloadAsync(request);
//                }
//            }
//        }




        mTextDetails.setText(constructWelcomeMessage(profile));


    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupTextDetails(View view) {
        mTextDetails = (TextView) view.findViewById(R.id.text_details);
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
                mTextDetails.setText(constructWelcomeMessage(currentProfile));
            }
        };
    }

    private void setupFacebookLoginButton(View view) {
        mButtonFacebookLogin = (LoginButton) view.findViewById(R.id.facebooklogin_button);
        mButtonFacebookLogin.setFragment(this);
//        if (Build.VERSION.SDK_INT >= 16)
//            mButtonLogin.setBackground(null);
//        else
//            mButtonLogin.setBackgroundDrawable(null);
        mButtonFacebookLogin.setCompoundDrawables(null, null, null, null);
        mButtonFacebookLogin.setReadPermissions("user_friends", "email", "user_birthday");
        mButtonFacebookLogin.registerCallback(mCallbackManager, mFacebookCallback);
        mButtonFacebookLogin.setBackgroundResource(R.drawable.guidedesign1_5);
    }
    private void setupNormalLoginButton(View view){
        mButtonNormalLogin = (Button) view.findViewById(R.id.normallogin_button);
        mButtonNormalLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent NormalLoginIntent = new Intent(getActivity(), NormalLoginActivity.class);
                startActivity(NormalLoginIntent);

            }
        });
    }

    private void setupSignupButton(View view){
        mSignuupButton = (Button)view.findViewById(R.id.signup_button);
        mSignuupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignupIntent = new Intent(getActivity(),SignupChoiceActivity.class);
                startActivity(SignupIntent);

            }
        });
    }

    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            stringBuffer.append("Welcome " + profile.getName());
            Log.d("seo",profile.getId());
        }
        return stringBuffer.toString();
    }

//    private ImageRequest getImageRequest() {
//        ImageRequest request = null;
//        ImageRequest.Builder requestBuilder = new ImageRequest.Builder(getActivity(),
//                ImageRequest.getProfilePictureUri(id, getResources().getDimensionPixelSize(
//                        R.dimen.MainActivity_fragment_profile_picture_height),getResources().getDimensionPixelSize(
//                        R.dimen.MainActivity_fragment_profile_picture_height)));
//        request = requestBuilder.setCallerTag(this).setCallback( new ImageRequest.Callback() {
//            @Override
//            public void onCompleted(ImageResponse response) {
//                processImageResponse(id, response);
//            }
//        }).build();
//        return request;
//    }
//
//    private void processImageResponse(String id, ImageResponse response) {
//        if (response != null) {
//            Bitmap bitmap = response.getBitmap();
//            if (bitmap != null) {
//                BitmapDrawable drawable = new BitmapDrawable(
//                        getActivity().getResources(), bitmap);
//                drawable.setBounds(0, 0,
//                        getResources().getDimensionPixelSize(
//                                R.dimen.MainActivity_fragment_profile_picture_height),
//                        getResources().getDimensionPixelSize(
//                                R.dimen.MainActivity_fragment_profile_picture_height));
//                userProfilePic = drawable;
//                userProfilePicID = id;
//                mTextDetails.setCompoundDrawables(null, drawable, null, null);
//                mTextDetails.setTag(response.getRequest().getImageUri());
//            }
//        }
//    }
//
    private void fetchUserInfo() {
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            user = me;

                            id = user.optString("id");
                            picture = user.optString("picture");
                            name = user.optString("name");
                            gender = user.optString("gender");
                            email = user.optString("email");
                            birthday = user.optString("birthday");

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

}
