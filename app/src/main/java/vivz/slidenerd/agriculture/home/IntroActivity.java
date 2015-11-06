package vivz.slidenerd.agriculture.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import vivz.slidenerd.agriculture.R;


public class IntroActivity extends Activity {
    LinearLayout introbtn;
    Handler introHandler;
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    String rtn, verSion;
    AlertDialog.Builder alt_bld;
    Boolean isUpdate = false;

    Runnable irun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        alt_bld = new AlertDialog.Builder(this);
        new Version().execute();


        introHandler = new Handler();
        introHandler.postDelayed(irun, 1000);//약 1.0초동안 인트로 화면
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        Log.d("seojang", "gogogogogogo : " + setting.getString("info_Id", ""));
    }

    private class Version extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override

        protected String doInBackground(Void... params) {
            // Confirmation of market information in the Google Play Store
            try {
                Document doc = Jsoup
                        .connect(
                                "https://play.google.com/store/apps/details?id=vivz.slidenerd.agriculture")
                        .get();

                Elements Version = doc.select(".content");

                for (Element v : Version) {
                    if (v.attr("itemprop").equals("softwareVersion")) {
                        rtn = v.text();
                    }
                }
                return rtn;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override

        protected void onPostExecute(String result) {
            // Version check the execution application.
            PackageInfo pi = null;
            try {
                pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            verSion = pi.versionName;
            rtn = result;

            if (!verSion.equals(rtn)) {
                isUpdate = true;
                alt_bld.setMessage("업데이트 후 사용해주세요.")
                        .setCancelable(false)
                        .setPositiveButton("업데이트 바로가기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                                        marketLaunch.setData(Uri.parse("https://play.google.com/store/apps/details?id=vivz.slidenerd.agriculture"));
                                        startActivity(marketLaunch);
                                        finish();
                                    }
                                });

                AlertDialog alert = alt_bld.create();
                alert.setTitle("안 내");
                alert.show();
            }else{
                irun = new Runnable() {
                    @Override
                    public void run() {
                        if(!isUpdate) {
                            Intent introIntent = new Intent(IntroActivity.this, HomeActivity.class);
                            startActivity(introIntent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    }
                };
            }
            super.onPostExecute(result);

        }

    }



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
