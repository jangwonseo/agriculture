package vivz.slidenerd.agriculture.region_theme;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.home.HomeActivity;
import vivz.slidenerd.agriculture.list.ListActivity;


public class RegionChoiceActivity extends ActionBarActivity {
    private Button backButton,menuButton;
    private View kangwonBtn;
    private View kyungkiBtn;
    private View chungnamBtn;
    private View chungbukBtn;
    private View jeonnamBtn;
    private View jeonbukBtn;
    private View kyungnamBtn;
    private View kyungbukBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_choice);

        kangwonBtn=findViewById(R.id.btn_kangwon);
        kangwonBtn.setOnClickListener(mClickListener);

        kyungkiBtn=findViewById(R.id.btn_kyungki);
        kyungkiBtn.setOnClickListener(mClickListener);

        chungnamBtn=findViewById(R.id.btn_chungnam);
        chungnamBtn.setOnClickListener(mClickListener);

        chungbukBtn=findViewById(R.id.btn_chungbuk);
        chungbukBtn.setOnClickListener(mClickListener);

        jeonnamBtn=findViewById(R.id.btn_jeonnam);
        jeonnamBtn.setOnClickListener(mClickListener);

        jeonbukBtn=findViewById(R.id.btn_jeonbuk);
        jeonbukBtn.setOnClickListener(mClickListener);

        kyungnamBtn=findViewById(R.id.btn_kyungnam);
        kyungnamBtn.setOnClickListener(mClickListener);

        kyungbukBtn=findViewById(R.id.btn_kyungbuk);
        kyungbukBtn.setOnClickListener(mClickListener);

        backButton  = (Button)findViewById(R.id.regionchoice_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        menuButton = (Button)findViewById(R.id.regionchoice_menubutton);
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
    }

    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            Intent intentTheme = new Intent(getApplication(),ListActivity.class);
            switch (v.getId())
            {
                case R.id.btn_kangwon:

                    intentTheme.putExtra("themeflag","kangwon");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_kyungki:

                    intentTheme.putExtra("themeflag","kyungki");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_chungnam:

                    intentTheme.putExtra("themeflag","chungnam");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_chungbuk:

                    intentTheme.putExtra("themeflag","chungbuk");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_jeonnam:

                    intentTheme.putExtra("themeflag","jeonnam");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_jeonbuk:

                    intentTheme.putExtra("themeflag","jeonbuk");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_kyungnam:

                    intentTheme.putExtra("themeflag", "kyungnam");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_kyungbuk:

                    intentTheme.putExtra("themeflag","kyungbuk");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_region_choice, menu);
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
}
