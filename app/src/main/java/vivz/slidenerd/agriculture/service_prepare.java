package vivz.slidenerd.agriculture;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class service_prepare extends Activity {
    Button check;
    Typeface yunGothicFont;
    TextView preparePopup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_prepare);

        preparePopup = (TextView)findViewById(R.id.prepareinfo);


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
