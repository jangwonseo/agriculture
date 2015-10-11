package vivz.slidenerd.agriculture.recruit;


import java.io.Serializable;

public class RecruitListItem implements Serializable {
    private int idRecruit;
    private String userId;
    private String missionName;
    private String vilageName;
    private String recruitContent;
    private String termStart;
    private String termEnd;
    private int recruitNum;
    private int joinedNum;
    private String reward;
    private String ImageURL;
    private int clickNum;
    private String phoneNum;
    public int getIdRecruit() {return idRecruit;}
    public String getUserId() {return userId;}
    public String getMissionName(){return missionName;}
    public String getVilageName() {return vilageName;}
    public String getRecruitContent(){return recruitContent;}
    public String getTermStart(){return termStart;}
    public String getTermEnd() {return termEnd;}
    public int getRecruitNum(){return recruitNum;}
    public int getJoinedNum() {return joinedNum;}
    public String getReward(){return reward;}
    public String getImageURL() {return ImageURL;}
    public int getClickNum() {return clickNum;}
    public String getPhoneNum() {return phoneNum;}
    public RecruitListItem(int idRecruit, String userId ,String missionName, String vilageName, String recruitContent, String termStart, String termEnd, int recruitNum, int joinedNum, String reward, String ImageURL, int clickNum, String phoneNum){
        this.idRecruit = idRecruit;
        this.userId = userId;
        this.missionName = missionName;
        this.vilageName = vilageName;
        this.recruitContent = recruitContent;
        this.termStart = termStart;
        this.termEnd = termEnd;
        this.recruitNum = recruitNum;
        this.joinedNum = joinedNum;
        this.reward = reward;
        this.ImageURL = ImageURL;
        this.clickNum = clickNum;
        this.phoneNum = phoneNum;
    }

    public String toString() {

        return "idrecruit=" + idRecruit + "&UserId=" + userId + "&missionName=" + missionName + "&vilageName" + vilageName + "&recruitContent=" +recruitContent + "&termStart=" + termStart + "&termEnd=" + termEnd + "&" + "recruitNum=" + Integer.toString(recruitNum) + "&joinedNum=" + Integer.toString(joinedNum) + "&reward=" + reward + "&ImageURL=" + ImageURL + "&clickNum=" + clickNum + "&phoneNum=" + phoneNum;
    }

}