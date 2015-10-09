package vivz.slidenerd.agriculture.mydiary;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import vivz.slidenerd.agriculture.R;

public class MyDiaryActivity__ extends ActionBarActivity {

    private LinearLayout recruitListLayout,interestedListLayout;
    private Button recruitListButton, interestedListButton;
    private Button mydiarybackbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiary);

        // 뒤로가기 버튼
        mydiarybackbutton = (Button)findViewById(R.id.mydiarybackbutton);
        mydiarybackbutton.setOnClickListener(mClickListener);


        recruitListButton = (Button)findViewById(R.id.recruitlistbutton);//신청체험버튼
        interestedListButton = (Button)findViewById(R.id.interestedlistbutton);//관심체험버튼
        recruitListButton.setOnClickListener(mClickListener);
        interestedListButton.setOnClickListener(mClickListener);

        recruitListLayout = (LinearLayout)findViewById(R.id.myrecruitlist_layout);
        interestedListLayout = (LinearLayout)findViewById(R.id.myinterestedlist_layout);
    }


    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                //신청체험버튼
                case R.id.recruitlistbutton:
                    recruitListButton.setBackgroundResource(R.drawable.mydiary2af);
                    interestedListButton.setBackgroundResource(R.drawable.mydiary3);
                    recruitListLayout.setVisibility(LinearLayout.VISIBLE);
                    interestedListLayout.setVisibility(LinearLayout.GONE);

                    break;
                //관심체험버튼
                case R.id.interestedlistbutton:
                    recruitListButton.setBackgroundResource(R.drawable.mydiary2);
                    interestedListButton.setBackgroundResource(R.drawable.mydiary3af);
                    recruitListLayout.setVisibility(LinearLayout.GONE);
                    interestedListLayout.setVisibility(LinearLayout.VISIBLE);
                    break;

                case R.id.mydiarybackbutton:
                    Log.d("seojang","heheheheheihihi");
                    onBackPressed();
                    break;
            }
        }
    };


}
