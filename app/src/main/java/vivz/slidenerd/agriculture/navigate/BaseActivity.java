package vivz.slidenerd.agriculture.navigate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import org.w3c.dom.Text;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.home.HomeActivity;


public class BaseActivity extends Activity implements View.OnClickListener {

    private LinearLayout contentView = null;
    private static Context mCtx = null;
    private Button backButton, menuButton;
    private Typeface yunGothicFont;
    private TextView startPointTxt,endPointTxt;
    public Button searchWay;
    private ImageView tutorialImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_base);
        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");

        startPointTxt = (TextView)findViewById(R.id.srcTextView);
        startPointTxt.setTypeface(yunGothicFont);
        endPointTxt = (TextView)findViewById(R.id.endpointtxt);
        endPointTxt.setTypeface(yunGothicFont);
        searchWay = (Button)findViewById(R.id.submit);
        searchWay.setTypeface(yunGothicFont);
        tutorialImageView = (ImageView) findViewById(R.id.tutorialImageView);

        mCtx = this;

        contentView  = (LinearLayout)findViewById(R.id.contentView);
        menuButton = (Button)findViewById(R.id.base_menubutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(moveToHomeIntent);
            }
        });
        backButton = (Button)findViewById(R.id.navigate_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //이거 위치 의도된거임?
        super.onCreate(savedInstanceState);

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
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public void setContentView(int res)  {
        contentView.removeAllViews();

        LayoutInflater inflater;
        inflater = LayoutInflater.from(this);

        View item = inflater.inflate(res, null);
        contentView.addView(item, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

    }
    @Override
    public void setContentView(View view) {
        contentView.removeAllViews();
        contentView.addView(view, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }
    public void addView(View v)
    {
        contentView.removeAllViews();
        contentView.addView(v, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }
    @Override
    public void onClick(View v) {
    }
}
