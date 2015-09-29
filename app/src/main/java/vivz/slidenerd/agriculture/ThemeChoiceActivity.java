package vivz.slidenerd.agriculture;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import vivz.slidenerd.agriculture.list.ListActivity;


public class ThemeChoiceActivity extends ActionBarActivity {
    private Button backButton;
    private Button natureButton;
    private Button exprienceButton;
    private Button traditionalButton;
    private Button wellBeingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_choice);

        natureButton = (Button)findViewById(R.id.btn_nature);
        natureButton.setOnClickListener(mClickListener);

        exprienceButton = (Button)findViewById(R.id.btn_exprience);
        exprienceButton.setOnClickListener(mClickListener);

        traditionalButton = (Button)findViewById(R.id.btn_traditional);
        traditionalButton.setOnClickListener(mClickListener);

        wellBeingButton = (Button)findViewById(R.id.btn_wellBeing);
        wellBeingButton.setOnClickListener(mClickListener);


        backButton  = (Button)findViewById(R.id.themechoice_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                case R.id.btn_nature:
                    intentTheme.putExtra("themeflag","nature");
                    startActivity(intentTheme);
                    break;
                case R.id.btn_traditional:
                    intentTheme.putExtra("themeflag","traditional");
                    startActivity(intentTheme);
                    break;
                case R.id.btn_exprience:
                    intentTheme.putExtra("themeflag","experience");
                    startActivity(intentTheme);
                    break;
                case R.id.btn_wellBeing:
                    intentTheme.putExtra("themeflag","wellBeing");
                    startActivity(intentTheme);
                    break;

            }
        }
    };



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
