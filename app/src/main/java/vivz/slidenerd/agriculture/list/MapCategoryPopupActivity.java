package vivz.slidenerd.agriculture.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.navigate.NavigateActivity;

public class MapCategoryPopupActivity extends Activity {


    //숙박,식당,현금,주유소 버튼
    private Button accommodationButton, restaurantButton, moneyButton, oilstationButton;

    String addr;

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

        Intent intent = getIntent();
        Log.e("addr..", intent.getExtras().getString("addr"));
        addr = intent.getExtras().getString("addr");
    }

    Button.OnClickListener categoryClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {

            switch (v.getId())
            {
                case R.id.accommodationbtn:
                    NavigateActivity.isClicked_menu1 = NavigateActivity.isClicked_accommodation;
                    Intent intentAccommodation = new Intent(getApplicationContext(), NavigateActivity.class);
                    intentAccommodation.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentAccommodation.putExtra("addr", addr);
                    // Log.i("asd", "addr : " + i.getAddr());
                    intentAccommodation.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentAccommodation);
                    break;
                case R.id.restaurantbtn:
                    NavigateActivity.isClicked_menu1 = NavigateActivity.isClicked_restaurant;
                    Intent intentRestaurant = new Intent(getApplicationContext(), NavigateActivity.class);
                    intentRestaurant.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentRestaurant.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intentRestaurant.putExtra("addr", addr);
                    startActivity(intentRestaurant);
                    finish();
                    break;
                case R.id.moneybtn:
                    NavigateActivity.isClicked_menu1 = NavigateActivity.isClicked_bank;
                    Intent intentBank = new Intent(getApplicationContext(), NavigateActivity.class);
                    intentBank.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentBank.putExtra("addr", addr);
                    // Log.i("asd", "addr : " + i.getAddr());
                    intentBank.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentBank);
                    break;
                case R.id.oilstationbtn:
                    NavigateActivity.isClicked_menu1 = NavigateActivity.isClicked_gasStation;
                    Intent intentGasStation = new Intent(getApplicationContext(), NavigateActivity.class);
                    intentGasStation.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentGasStation.putExtra("addr", addr);
                    Log.i("asd", "addr : " + addr);
                    intentGasStation.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentGasStation);
                    break;
            }
        }
    };

}
