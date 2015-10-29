package vivz.slidenerd.agriculture.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.facebook.appevents.AppEventsLogger;

import vivz.slidenerd.agriculture.R;


public class IntroActivity extends Activity {
    LinearLayout introbtn;
    Handler introHandler;
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        introHandler = new Handler();
        introHandler.postDelayed(irun, 1000);//약 1.0초동안 인트로 화면
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor= setting.edit();

        Log.d("seojang", "gogogogogogo : " + setting.getString("info_Id", ""));
    }

    Runnable irun = new Runnable() {
        @Override
        public void run() {
            Intent introIntent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(introIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    };

    @Override
    protected void onDestroy() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.intro).setBackground(null);
        }else{
            findViewById(R.id.intro).setBackgroundResource(0);
        }
        System.gc();
        super.onDestroy();
    }

    @Override
    protected void onStop() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.intro).setBackground(null);
        }else{
            findViewById(R.id.intro).setBackgroundResource(0);
        }
        System.gc();
        super.onStop();
    }
    @Override
    protected void onPause() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.intro).setBackground(null);
        }else{
            findViewById(R.id.intro).setBackgroundResource(0);
        }
        System.gc();
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        introHandler.removeCallbacks(irun);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intro, menu);
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

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

}
