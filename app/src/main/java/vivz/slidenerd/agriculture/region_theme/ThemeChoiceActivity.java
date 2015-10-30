package vivz.slidenerd.agriculture.region_theme;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.home.HomeActivity;
import vivz.slidenerd.agriculture.list.ListActivity;


public class ThemeChoiceActivity extends ActionBarActivity {
    private Button backButton;
    private Button natureButton;
    private Button exprienceButton;
    private Button traditionalButton;
    private Button wellBeingButton;
    private Button menuButton;
    private Button btnVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_choice);

        natureButton = (Button)findViewById(R.id.btn_nature);
        natureButton.setOnClickListener(mClickListener);

        exprienceButton = (Button)findViewById(R.id.btn_experience);
        exprienceButton.setOnClickListener(mClickListener);

        traditionalButton = (Button)findViewById(R.id.btn_traditional);
        traditionalButton.setOnClickListener(mClickListener);

        wellBeingButton = (Button)findViewById(R.id.btn_wellBeing);
        wellBeingButton.setOnClickListener(mClickListener);


        backButton  = (Button)findViewById(R.id.themechoice_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        menuButton = (Button)findViewById(R.id.themechoice_menubutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToHomeIntent  = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToHomeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(moveToHomeIntent);
                finish();
            }
        });

        btnVideo = (Button)findViewById(R.id.btnVideo);
        btnVideo.setOnClickListener(mClickListener);


    }

    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            Intent intentTheme = new Intent(getApplication(),ListActivity.class);
            switch (v.getId())
            {
                case R.id.btn_nature:
                    intentTheme.putExtra("themeflag","nature");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_traditional:
                    intentTheme.putExtra("themeflag","traditional");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_experience:
                    intentTheme.putExtra("themeflag","experience");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_wellBeing:
                    intentTheme.putExtra("themeflag","wellBeing");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btnVideo:
                    intentTheme.putExtra("themeflag", "video");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;

            }
        }
    };

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                Intent moveToHomeIntent  = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToHomeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(moveToHomeIntent);
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_theme_choice, menu);
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
