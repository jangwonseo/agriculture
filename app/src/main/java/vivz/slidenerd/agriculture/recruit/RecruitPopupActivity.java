package vivz.slidenerd.agriculture.recruit;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;

import vivz.slidenerd.agriculture.R;

public class RecruitPopupActivity extends Activity implements View.OnClickListener{

    TextView txtvMissionName;
    TextView txtvRecruitNum;
    TextView txtvRecruitContent;
    TextView txtvRecruitTerm;
    TextView txtvReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recruit_popup);

        Intent intent = getIntent();
        RecruitListItem item = (RecruitListItem)intent.getSerializableExtra("item");
        Log.e("popup", "successed");

        txtvMissionName = (TextView)findViewById(R.id.txtvMissionName);
        txtvMissionName.setText(item.getMissionName());
        txtvRecruitTerm = (TextView)findViewById(R.id.txtvRecruitTerm);
        txtvRecruitTerm.setText(item.getTermStart() + " ~ " + item.getTermEnd());
        txtvRecruitNum = (TextView)findViewById(R.id.txtvRecruitNum);
        txtvRecruitNum.setText(item.getRecruitNum());
        txtvRecruitContent = (TextView)findViewById(R.id.txtvRecruitContent);
        txtvRecruitContent.setText(item.getRecuritContent());
        txtvReward = (TextView)findViewById(R.id.txtvReward);
        txtvReward.setText(item.getReward());
    }

    @Override
    public void onClick(View v) {

    }
}
