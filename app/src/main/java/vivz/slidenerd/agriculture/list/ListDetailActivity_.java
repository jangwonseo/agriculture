package vivz.slidenerd.agriculture.list;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import vivz.slidenerd.agriculture.R;
import vivz.slidenerd.agriculture.RecycleUtils;
import vivz.slidenerd.agriculture.home.HomeActivity;

public class ListDetailActivity_ extends ActionBarActivity {
    //폰트
    Typeface yunGothicFont;

    //프로그램정보, 마을홈페이지, 체험마을
    TextView informationProgram,vilageHomepage, experienceVilage;

    //전화버튼,마이다이어리버튼,부대시설지도버튼
    Button phoneCall, addMydiary, findMap;

    //뒤로가기버튼, 홈버튼
    Button menuButton,backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listdetail_);

        //텍스트뷰 윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(getAssets(), "fonts/yungothic330.ttf");
        informationProgram = (TextView)findViewById(R.id.information_program);
        informationProgram.setTypeface(yunGothicFont);
        //vilageHomepage = (TextView)findViewById(R.id.homepagevilage);
        vilageHomepage.setTypeface(yunGothicFont);
        experienceVilage = (TextView)findViewById(R.id.experienceVilage);
        experienceVilage.setTypeface(yunGothicFont);


        //버튼 인스턴스 화
        //phoneCall = (Button)findViewById(R.id.phonecall);
        phoneCall.setOnClickListener(detailClickListener);
        addMydiary = (Button)findViewById(R.id.addmydiary);
        addMydiary.setOnClickListener(detailClickListener);
        findMap = (Button)findViewById(R.id.findmap);
        findMap.setOnClickListener(detailClickListener);

        //홈버튼, 뒤로가기버튼
        menuButton = (Button)findViewById(R.id.listdetail_menubutton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToHomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                moveToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(moveToHomeIntent);
            }
        });
        backButton = (Button)findViewById(R.id.listdeail_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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

    Button.OnClickListener detailClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {

            switch (v.getId())
            {
                //case R.id.phonecall:

                    //break;
                case R.id.addmydiary:

                    break;
                case R.id.findmap:

                    break;

            }
        }
    };

}
