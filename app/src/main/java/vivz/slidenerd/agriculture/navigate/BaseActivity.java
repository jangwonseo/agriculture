package vivz.slidenerd.agriculture.navigate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.home.HomeActivity;


public class BaseActivity extends Activity implements View.OnClickListener {

    private LinearLayout contentView = null;
    private static Context mCtx = null;
    private Button backButton, menuButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.setContentView(R.layout.activity_base);
        mCtx = this;

        contentView  = (LinearLayout)findViewById(R.id.contentView);
        menuButton = (Button)findViewById(R.id.base_menubutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToHomeIntent  = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(moveToHomeIntent);
            }
        });


        super.onCreate(savedInstanceState);

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
