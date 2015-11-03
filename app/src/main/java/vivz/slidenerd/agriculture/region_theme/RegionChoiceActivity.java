package vivz.slidenerd.agriculture.region_theme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

    private LinearLayout mapContent;


    int sdk = android.os.Build.VERSION.SDK_INT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_choice);

        mapContent = (LinearLayout)findViewById(R.id.mapcontent);

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
                    mapContent.setBackgroundResource(R.drawable.map3_);

                    intentTheme.putExtra("themeflag","kangwon");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_kyungki:

                    mapContent.setBackgroundResource(R.drawable.map2_);
                    intentTheme.putExtra("themeflag","kyungki");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_chungnam:

                    mapContent.setBackgroundResource(R.drawable.map9_);
                    intentTheme.putExtra("themeflag","chungnam");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_chungbuk:
                    mapContent.setBackgroundResource(R.drawable.map8_);
                    intentTheme.putExtra("themeflag","chungbuk");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_jeonnam:
                    mapContent.setBackgroundResource(R.drawable.map6_);
                    intentTheme.putExtra("themeflag","jeonnam");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_jeonbuk:
                    mapContent.setBackgroundResource(R.drawable.map7_);
                    intentTheme.putExtra("themeflag","jeonbuk");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_kyungnam:
                    mapContent.setBackgroundResource(R.drawable.map5_);
                    intentTheme.putExtra("themeflag", "kyungnam");
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentTheme.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intentTheme);
                    finish();
                    break;
                case R.id.btn_kyungbuk:
                    mapContent.setBackgroundResource(R.drawable.map4_);
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

    private static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
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
