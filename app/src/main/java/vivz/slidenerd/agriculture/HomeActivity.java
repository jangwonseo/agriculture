package vivz.slidenerd.agriculture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import vivz.slidenerd.agriculture.navigate.BaseActivity;
import vivz.slidenerd.agriculture.navigate.NavigateActivity;
import vivz.slidenerd.agriculture.recruit.Recruit;


public class HomeActivity extends ActionBarActivity{
    private Button goTheme,goRegion,goGathering,goEtcetera,menuButton,myinfoButton;

    //페이지가 열려 있는지 알기 위한 플래그
    private boolean isPageOpen = false;

    //애니메이션 객체
    private Animation translateLeftAnim;
    private Animation translateRightAnim;

     //슬라이딩으로 보여지는 페이지 레이아웃
    private LinearLayout slidingPage01;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        goTheme = (Button)findViewById(R.id.themebutton);
        goRegion = (Button)findViewById(R.id.regionbutton);
        goGathering = (Button)findViewById(R.id.gatheringbutton);
        goEtcetera = (Button)findViewById(R.id.etceterabutton);

        menuButton = (Button)findViewById(R.id.home_menubutton);
        myinfoButton = (Button)findViewById(R.id.home_myinfo);

        goTheme.setOnClickListener(mClickListener);
        goRegion.setOnClickListener(mClickListener);
        goGathering.setOnClickListener(mClickListener);
        goEtcetera.setOnClickListener(mClickListener);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 애니메이션 적용
                if (isPageOpen){
                    slidingPage01.startAnimation(translateRightAnim);
                } else {
                    slidingPage01.setVisibility(View.VISIBLE);
                    slidingPage01.startAnimation(translateLeftAnim);
                }

            }
        });

        myinfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignChoice = new Intent(getApplication(), SignChoiceActivity.class);
                startActivity(intentSignChoice);
            }
        });

        // 슬라이딩으로 보여질 레이아웃 객체 참조
        slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);

        // 애니메이션 객체 로딩
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        // 애니메이션 객체에 리스너 설정
        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

    }

    Button.OnClickListener mClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.themebutton:
                    Intent intentTheme = new Intent(getApplication(), ThemeChoiceActivity.class);
                    startActivity(intentTheme);
                    break;
                case R.id.regionbutton:
                    Intent intentRegion = new Intent(getApplication(), RegionChoiceActivity.class);
                    startActivity(intentRegion);
                    break;
                case R.id.gatheringbutton:
                    Intent intentRecruit = new Intent(getApplicationContext(), Recruit.class);
                    startActivity(intentRecruit);
                    break;
                case R.id.etceterabutton:
                    Intent intentNavigate = new Intent(getApplicationContext(), NavigateActivity.class);
                    startActivity(intentNavigate);
                    break;

            }
        }
    };

    /**
     * 애니메이션 리스너 정의
     */
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        /**
         * 애니메이션이 끝날 때 호출되는 메소드
         */
        public void onAnimationEnd(Animation animation) {
            if (isPageOpen) {
                slidingPage01.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            } else {
                isPageOpen = true;
            }
        }

        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationStart(Animation animation) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
