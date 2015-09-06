package vivz.slidenerd.agriculture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;


public class IntroActivity extends Activity {
    LinearLayout introbtn;
    Handler introHandler;
    //intro activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        introHandler = new Handler();
        introHandler.postDelayed(irun, 1000);//약 1.0초동안 인트로 화면
    }
    Runnable irun = new Runnable() {
        @Override
        public void run() {
            Intent introIntent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(introIntent);
            finish();

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

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
}
