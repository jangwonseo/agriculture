package vivz.slidenerd.agriculture.list;

import android.util.Log;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by geon on 2015-09-30.
 */
/*
thumbUrlCours1, exprnDstncId, chargerMoblphonNo, exprnProgrmNm, exprnLiverStgDc, adres1, vilageHmpgUrl,
vilageNm, tableName, operEraBegin, operEraEnd, nmprCoMumm, nmprCoMxmm, operTimeMnt, pc, onlineResvePosblAt
*/
public class Item implements Serializable {
    private String thumbUrlCours1, exprnDstncId, chargerMoblphonNo, exprnProgrmNm, exprnLiverStgDc, adres1, vilageHmpgUrl,
    vilageNm, tableName, operEraBegin, operEraEnd, nmprCoMumm, nmprCoMxmm, operTimeMnt, pc, onlineResvePosblAt;

    public Item() {
    }

    public String getThumbUrlCours1() {
        return thumbUrlCours1;
    }

    public String getExprnProgrmNm() {
        return exprnProgrmNm;
    }

    public String getAdres1() {
        return adres1;
    }

    public String getChargerMoblphonNo() {
        return chargerMoblphonNo;
    }

    public String getExprnLiverStgDc() {
        return exprnLiverStgDc;
    }

    public String getTableName() {
        return tableName;
    }

    public String getExprnDstncId() {
        return exprnDstncId;
    }

    public String getVilageHmpgUrl() {
        return vilageHmpgUrl;
    }

    public String getVilageNm() {
        return vilageNm;
    }

    public String getOperEraBegin() {
        return operEraBegin;
    }

    public String getOperEraEnd() {
        return operEraEnd;
    }

    public String getNmprCoMumm() {
        return nmprCoMumm;
    }

    public String getNmprCoMxmm() {
        return nmprCoMxmm;
    }

    public String getOperTimeMnt() {
        return operTimeMnt;
    }

    public String getPc() {
        return pc;
    }

    public String getOnlineResvePosblAt() {
        return onlineResvePosblAt;
    }

    public String toString() {

        String addrEncoded = null;
        String vilageSlgnEncoded = null;
        String ExpgName = null;
//        String vilageHmpgEnncEncoded = null;
//        String vilageHmpgUrlEncoded = null;
//        String vilageKndNmEncoded = null;
        try {
            addrEncoded = URLEncoder.encode(adres1, "UTF-8");
            vilageSlgnEncoded = URLEncoder.encode(exprnLiverStgDc,"UTF-8");
            ExpgName = URLEncoder.encode(exprnProgrmNm, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.e("regist", "thumbUrlCours1=" + thumbUrlCours1 +"&exprnDstncId=" + exprnDstncId + "&chargerMoblphonNo=" + chargerMoblphonNo +
        "&exprnProgrmNm=" + exprnProgrmNm + "&exprnLiverStgDc=" + vilageSlgnEncoded + "&adres1=" + addrEncoded + "&vilageHmpgUrl=" + vilageHmpgUrl +
        "&vilageNm=" + vilageNm + "&tableName=" + tableName + "&operEraBegin=" + operEraBegin + "&operEraEnd=" + operEraEnd +
        "&nmprCoMumm=" + nmprCoMumm + "&nmprCoMxmm=" + nmprCoMxmm + "&operTimeMnt=" + operTimeMnt + "&pc=" + pc + "&onlineResvePosblAt=" +
                onlineResvePosblAt);
        return ("thumbUrlCours1=" + thumbUrlCours1 +"&exprnDstncId=" + exprnDstncId + "&chargerMoblphonNo=" + chargerMoblphonNo +
                "&exprnProgrmNm=" + exprnProgrmNm + "&exprnLiverStgDc=" + vilageSlgnEncoded + "&adres1=" + addrEncoded + "&vilageHmpgUrl=" + vilageHmpgUrl +
                "&vilageNm=" + vilageNm + "&tableName=" + tableName + "&operEraBegin=" + operEraBegin + "&operEraEnd=" + operEraEnd +
                "&nmprCoMumm=" + nmprCoMumm + "&nmprCoMxmm=" + nmprCoMxmm + "&operTimeMnt=" + operTimeMnt + "&pc=" + pc + "&onlineResvePosblAt=" +
                onlineResvePosblAt);

    }

    public Item(String thumbUrlCours1, String exprnDstncId, String chargerMoblphonNo, String exprnProgrmNm,
                String exprnLiverStgDc, String adres1, String vilageHmpgUrl, String vilageNm, String tableName,
                String operEraBegin, String operEraEnd, String nmprCoMumm, String nmprCoMxmm, String operTimeMnt,
                String pc, String onlineResvePosblAt) {
        this.thumbUrlCours1 = thumbUrlCours1;
        this.exprnDstncId = exprnDstncId;
        this.chargerMoblphonNo = chargerMoblphonNo;
        this.exprnProgrmNm = exprnProgrmNm;
        this.exprnLiverStgDc = exprnLiverStgDc;
        this.adres1 = adres1;
        this.vilageHmpgUrl = vilageHmpgUrl;
        this.vilageNm = vilageNm;
        this.tableName = tableName;
        this.operEraBegin = operEraBegin;
        this.operEraEnd = operEraEnd;
        this.nmprCoMumm = nmprCoMumm;
        this.nmprCoMxmm = nmprCoMxmm;
        this.operTimeMnt = operTimeMnt;
        this.pc = pc;
        this.onlineResvePosblAt = onlineResvePosblAt;
    }
}