package vivz.slidenerd.agriculture.region_theme;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import vivz.slidenerd.agriculture.R;
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
                onBackPressed();
            }
        });
        menuButton = (Button)findViewById(R.id.regionchoice_menubutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToHomeIntent  = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(moveToHomeIntent);
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
                    startActivity(intentTheme);
                    break;
                case R.id.btn_kyungki:

                    intentTheme.putExtra("themeflag","kyungki");
                    startActivity(intentTheme);
                    break;
                case R.id.btn_chungnam:

                    intentTheme.putExtra("themeflag","chungnam");
                    startActivity(intentTheme);
                    break;
                case R.id.btn_chungbuk:

                    intentTheme.putExtra("themeflag","chungbuk");
                    startActivity(intentTheme);
                    break;
                case R.id.btn_jeonnam:

                    intentTheme.putExtra("themeflag","jeonnam");
                    startActivity(intentTheme);
                    break;
                case R.id.btn_jeonbuk:

                    intentTheme.putExtra("themeflag","jeonbuk");
                    startActivity(intentTheme);
                    break;
                case R.id.btn_kyungnam:

                    intentTheme.putExtra("themeflag","kyungnam");
                    startActivity(intentTheme);
                    break;
                case R.id.btn_kyungbuk:

                    intentTheme.putExtra("themeflag","kyungbuk");
                    startActivity(intentTheme);
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
}
