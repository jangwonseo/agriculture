package vivz.slidenerd.agriculture.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import vivz.slidenerd.agriculture.R;

public class MapCategoryPopupActivity extends Activity {


    //숙박,식당,현금,주유소 버튼
    private Button accommodationButton, restaurantButton, moneyButton, oilstationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapcategorypopup);

        accommodationButton = (Button)findViewById(R.id.accommodationbtn);
        accommodationButton.setOnClickListener(categoryClickListener);
        restaurantButton = (Button)findViewById(R.id.restaurantbtn);
        restaurantButton.setOnClickListener(categoryClickListener);
        moneyButton = (Button)findViewById(R.id.moneybtn);
        moneyButton.setOnClickListener(categoryClickListener);
        oilstationButton = (Button)findViewById(R.id.oilstationbtn);
        oilstationButton.setOnClickListener(categoryClickListener);

    }

    Button.OnClickListener categoryClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {

            switch (v.getId())
            {
                case R.id.accommodationbtn:
                    break;
                case R.id.restaurantbtn:
                    break;
                case R.id.moneybtn:
                    break;
                case R.id.oilstationbtn:
                    break;

            }
        }
    };

}
