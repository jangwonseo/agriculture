package vivz.slidenerd.agriculture.recruit;


import java.io.Serializable;
import java.util.Calendar;

public class MissionItem implements Serializable {
    private String UserId;
    private int recruitId;
    private int submitYear;
    private int submitMonth;
    private int submitDay;
    private String submitDate;
    public String getUserId(){return UserId;}
    public int getRecruitId(){return recruitId;}
    public String getSubmitDate(){return submitDate;}

    public MissionItem(String UserId, int recruitId){
        this.recruitId = recruitId;
        this.UserId = UserId;
    }
    public MissionItem(String UserId, int recruitId,String submitDate){
        this.recruitId = recruitId;
        this.UserId = UserId;
        this.submitDate=submitDate;
    }

    public String updateDate()
    {
        final Calendar c = Calendar.getInstance();
        submitYear = c.get(Calendar.YEAR);
        submitMonth= c.get(Calendar.MONTH)+1;
        submitDay  = c.get(Calendar.DAY_OF_MONTH);
        submitDate=submitYear + "-" + submitMonth + "-" + submitDay;
        return submitDate;
    }

    public String toString() {
        updateDate();
        return "UserId=" + UserId + "&recruitId=" + Integer.toString(recruitId) + "&submitDate=" + submitDate;
    }


}
