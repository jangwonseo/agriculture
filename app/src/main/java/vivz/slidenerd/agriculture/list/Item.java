package vivz.slidenerd.agriculture.list;

import android.util.Log;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by geon on 2015-09-30.
 */
public class Item implements Serializable {
    private String thumbUrl;            // 이미지 경로
    private String name;                // 마을 이름
    private String addr;                // 주소
    private String prcafsManMoblphon;   // 실무자 전화번호
    private String vilageHmpgEnnc;      // 마을 홈피 유무
    private String vilageHmpgUrl;       // 마을 홈피 주소
    private String vilageSlgn;          // 마을 간단 소개
    private String tableName;           // 테마
    private String vilageId;

    public Item() {
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }

    public String getPrcafsManMoblphon() {
        return prcafsManMoblphon;
    }

    public String getVilageHmpgEnnc() {
        return vilageHmpgEnnc;
    }

    public String getVilageHmpgUrl() {
        return vilageHmpgUrl;
    }

    public String getVilageSlgn() {
        return vilageSlgn;
    }

    public String getTableName() {
        return tableName;
    }

    public String getVilageId() {
        return vilageId;
    }

    public String toString() {

        String addrEncoded = null;
        String vilageSlgnEncoded = null;
//        String vilageHmpgEnncEncoded = null;
//        String vilageHmpgUrlEncoded = null;
//        String vilageKndNmEncoded = null;
        try {
            addrEncoded = URLEncoder.encode(addr, "UTF-8");
            vilageSlgnEncoded = URLEncoder.encode(vilageSlgn,"UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("regist", "thumbUrl=" + thumbUrl + "&name=" + name + "&addr=" + addrEncoded + "&prcafsManMoblphon=" + prcafsManMoblphon +
                "&vilageHmpgEnnc=" + vilageHmpgEnnc + "&vilageHmpgUrl=" + vilageHmpgUrl +
                "&vilageSlgn=" + vilageSlgnEncoded + "&tableName=" + tableName + "&vilageId=" + vilageId);
        return ("thumbUrl=" + thumbUrl + "&name=" + name + "&addr=" + addrEncoded + "&prcafsManMoblphon=" + prcafsManMoblphon +
                "&vilageHmpgEnnc=" + vilageHmpgEnnc + "&vilageHmpgUrl=" + vilageHmpgUrl +
                "&vilageSlgn=" + vilageSlgnEncoded +  "&tableName=" + tableName +  "&vilageId=" + vilageId);

    }

    public Item(String thumbUrl, String name, String addr, String prcafsManMoblphon, String vilageHmpgEnnc, String vilageHmpgUrl,
                String vilageSlgn, String tableName, String vilageId) {

        this.thumbUrl = thumbUrl;
        this.name = name;
        this.addr = addr;
        this.prcafsManMoblphon = prcafsManMoblphon;
        this.vilageHmpgEnnc = vilageHmpgEnnc;
        this.vilageHmpgUrl = vilageHmpgUrl;
        this.vilageSlgn = vilageSlgn;
        this.tableName = tableName;
        this.vilageId = vilageId;
    }
}