package vivz.slidenerd.agriculture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import vivz.slidenerd.agriculture.navigate.NavigateActivity;
import vivz.slidenerd.agriculture.navigate.navigateSettingPopupActivity;

public class service_prepare extends Activity {
    Button check;
    Typeface yunGothicFont;
    TextView preparePopup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_prepare);

        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");
        preparePopup.setTypeface(yunGothicFont);

        check = (Button) findViewById(R.id.preparecheck);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
