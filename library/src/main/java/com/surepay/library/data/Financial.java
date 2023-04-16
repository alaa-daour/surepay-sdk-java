package com.surepay.library.data;

import androidx.lifecycle.ViewModel;
import com.surepay.library.POSService;
import java.io.IOException;
import java.io.StringReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


public class Financial
        extends ViewModel
{
    public static String PAN_TAG = "PAN";
    public static String EXP_DATE_TAG = "EXP_DATE";
    public static String AMOUNT_TAG = "AMOUNT";
    public static String SCHEME_N_A_TAG = "SCHEME_N_A";
    public static String SCHEME_N_E_TAG = "SCHEME_N_E";
    public static String TRANS_DT_ST_TAG = "TRANS_DT_ST";
    public static String AUTH_TAG = "AUTH";
    public static String RRN_TAG = "RRN";
    public static String MID_TAG = "MID";
    public static String TRANS_TYPE_TAG = "TRANS_TYPE";
    public static String MER_NAME_E_TAG = "MER_NAME_E";
    public static String MER_NAME_A_TAG = "MER_NAME_A";
    public static String MER_ADDRS_E_TAG = "MER_ADDRS_E";
    public static String MER_ADDRS_A_TAG = "MER_ADDRS_A";
    public static String AID_TAG = "AID";
    public static String STAN_TAG = "STAN";
    public static String MadaSpec_TAG = "MadaSpec";
    public static String APP_VER_TAG = "APP_VER";
    public static String TID_TAG = "TID";
    public static String AQU_ID_TAG = "AQU_ID";
    public static String TRANS_DT_END_TAG = "TRANS_DT_END";
    public static String CVM_MSG_TAG = "CVM_MSG";
    public static String BANK_ID_TAG = "BANK_ID";
    public static String LEAPICCTagsInfo_TAG = "LEAPICCTagsInfo";
    public static String MER_PHONE_TAG = "MER_PHONE";
    public static String OFFLINE_TRX_TAG = "OFFLINE_TRX";
    public static String TX_RSLT_TAG = "TX_RSLT";

    private String PAN;
    private String EXP_DATE;
    private String AMOUNT;
    private String SCHEME_N_A;
    private String SCHEME_N_E;
    private String TRANS_DT_ST;
    private String AUTH;
    private String RRN;
    private String MID;
    private String TRANS_TYPE;
    private String MER_NAME_E;
    private String MER_NAME_A;
    private String MER_ADDRS_E;
    private String MER_ADDRS_A;
    private String AID;
    private String STAN;
    private String MadaSpec;
    private String APP_VER;
    private String TID;
    private String AQU_ID;
    private String TRANS_DT_END;
    private String CVM_MSG;
    private String BANK_ID;
    private String LEAPICCTagsInfo;
    private String MER_PHONE;
    private String OFFLINE_TRX;
    private String TX_RSLT;
    private boolean isMerchant;

    public void parseXml(String xml) {
        try {
            String updatedXML = xml.replace("'", "");

            POSService.financial = new Financial();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(updatedXML));
            int event = xpp.getEventType();
            while (event != 1) {
                String name = xpp.getName();
                switch (event) {
                    case 2:
                        if (name.equals(PAN_TAG)) {
                            POSService.financial.setPAN(xpp.nextText()); break;
                        }  if (name.equals(EXP_DATE_TAG)) {
                        POSService.financial.setEXP_DATE(xpp.nextText()); break;
                    }  if (name.equals(AMOUNT_TAG)) {
                        POSService.financial.setAMOUNT(xpp.nextText()); break;
                    }  if (name.equals(SCHEME_N_A_TAG)) {
                        POSService.financial.setSCHEME_N_A(xpp.nextText()); break;
                    }  if (name.equals(SCHEME_N_E_TAG)) {
                        POSService.financial.setSCHEME_N_E(xpp.nextText()); break;
                    }  if (name.equals(TRANS_DT_ST_TAG)) {
                        POSService.financial.setTRANS_DT_ST(xpp.nextText()); break;
                    }  if (name.equals(AUTH_TAG)) {
                        POSService.financial.setAUTH(xpp.nextText()); break;
                    }  if (name.equals(RRN_TAG)) {
                        POSService.financial.setRRN(xpp.nextText()); break;
                    }  if (name.equals(MID_TAG)) {
                        POSService.financial.setMID(xpp.nextText()); break;
                    }  if (name.equals(TRANS_TYPE_TAG)) {
                        POSService.financial.setTRANS_TYPE(xpp.nextText()); break;
                    }  if (name.equals(MER_NAME_E_TAG)) {
                        POSService.financial.setMER_NAME_E(xpp.nextText()); break;
                    }  if (name.equals(MER_NAME_A_TAG)) {
                        POSService.financial.setMER_NAME_A(xpp.nextText()); break;
                    }  if (name.equals(MER_ADDRS_E_TAG)) {
                        POSService.financial.setMER_ADDRS_E(xpp.nextText()); break;
                    }  if (name.equals(MER_ADDRS_A_TAG)) {
                        POSService.financial.setMER_ADDRS_A(xpp.nextText()); break;
                    }  if (name.equals(AID_TAG)) {
                        POSService.financial.setAID(xpp.nextText()); break;
                    }  if (name.equals(STAN_TAG)) {
                        POSService.financial.setSTAN(xpp.nextText()); break;
                    }  if (name.equals(MadaSpec_TAG)) {
                        POSService.financial.setMadaSpec(xpp.nextText()); break;
                    }  if (name.equals(APP_VER_TAG)) {
                        POSService.financial.setAPP_VER(xpp.nextText()); break;
                    }  if (name.equals(TID_TAG)) {
                        POSService.financial.setTID(xpp.nextText()); break;
                    }  if (name.equals(AQU_ID_TAG)) {
                        POSService.financial.setAQU_ID(xpp.nextText()); break;
                    }  if (name.equals(TRANS_DT_END_TAG)) {
                        POSService.financial.setTRANS_DT_END(xpp.nextText()); break;
                    }  if (name.equals(BANK_ID_TAG)) {
                        POSService.financial.setBANK_ID(xpp.nextText()); break;
                    }  if (name.equals(LEAPICCTagsInfo_TAG)) {
                        POSService.financial.setLEAPICCTagsInfo(xpp.nextText()); break;
                    }  if (name.equals(MER_PHONE_TAG)) {
                        POSService.financial.setMER_PHONE(xpp.nextText()); break;
                    }  if (name.equals(CVM_MSG_TAG)) {
                        POSService.financial.setCVM_MSG(xpp.nextText()); break;
                    }  if (name.equals(OFFLINE_TRX_TAG)) {
                        POSService.financial.setOFFLINE_TRX(xpp.nextText()); break;
                    }  if (name.equals(TX_RSLT_TAG)) {
                        POSService.financial.setTX_RSLT(xpp.nextText());
                    }
                        break;
                }


                event = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPAN() {
        return this.PAN;
    }

    public void setPAN(String PAN) {
        this.PAN = PAN;
    }

    public String getEXP_DATE() {
        return this.EXP_DATE;
    }

    public void setEXP_DATE(String EXP_DATE) {
        this.EXP_DATE = EXP_DATE;
    }

    public String getAMOUNT() {
        return this.AMOUNT;
    }

    public void setAMOUNT(String AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getSCHEME_N_A() {
        return this.SCHEME_N_A;
    }

    public void setSCHEME_N_A(String SCHEME_N_A) {
        this.SCHEME_N_A = SCHEME_N_A;
    }

    public String getSCHEME_N_E() {
        return this.SCHEME_N_E;
    }

    public void setSCHEME_N_E(String SCHEME_N_E) {
        this.SCHEME_N_E = SCHEME_N_E;
    }

    public String getTRANS_DT_ST() {
        return this.TRANS_DT_ST;
    }

    public void setTRANS_DT_ST(String TRANS_DT_ST) {
        this.TRANS_DT_ST = TRANS_DT_ST;
    }

    public String getRRN() {
        return this.RRN;
    }

    public void setRRN(String RRN) {
        this.RRN = RRN;
    }

    public String getMID() {
        return this.MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }

    public String getTRANS_TYPE() {
        return this.TRANS_TYPE;
    }

    public void setTRANS_TYPE(String TRANS_TYPE) {
        this.TRANS_TYPE = TRANS_TYPE;
    }

    public String getMER_NAME_E() {
        return this.MER_NAME_E;
    }

    public void setMER_NAME_E(String MER_NAME_E) {
        this.MER_NAME_E = MER_NAME_E;
    }

    public String getMER_NAME_A() {
        return this.MER_NAME_A;
    }

    public void setMER_NAME_A(String MER_NAME_A) {
        this.MER_NAME_A = MER_NAME_A;
    }

    public String getMER_ADDRS_E() {
        return this.MER_ADDRS_E;
    }

    public void setMER_ADDRS_E(String MER_ADDRS_E) {
        this.MER_ADDRS_E = MER_ADDRS_E;
    }

    public String getMER_ADDRS_A() {
        return this.MER_ADDRS_A;
    }

    public void setMER_ADDRS_A(String MER_ADDRS_A) {
        this.MER_ADDRS_A = MER_ADDRS_A;
    }

    public String getAID() {
        return this.AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getSTAN() {
        return this.STAN;
    }

    public void setSTAN(String STAN) {
        this.STAN = STAN;
    }

    public String getMadaSpec() {
        return this.MadaSpec;
    }

    public void setMadaSpec(String madaSpec) {
        this.MadaSpec = madaSpec;
    }

    public String getAPP_VER() {
        return this.APP_VER;
    }

    public void setAPP_VER(String APP_VER) {
        this.APP_VER = APP_VER;
    }

    public String getTID() {
        return this.TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getAQU_ID() {
        return this.AQU_ID;
    }

    public void setAQU_ID(String AQU_ID) {
        this.AQU_ID = AQU_ID;
    }

    public String getTRANS_DT_END() {
        return this.TRANS_DT_END;
    }

    public void setTRANS_DT_END(String TRANS_DT_END) {
        this.TRANS_DT_END = TRANS_DT_END;
    }

    public String getCVM_MSG() {
        return this.CVM_MSG;
    }

    public void setCVM_MSG(String CVM_MSG) {
        this.CVM_MSG = CVM_MSG;
    }

    public String getBANK_ID() {
        return this.BANK_ID;
    }

    public void setBANK_ID(String BANK_ID) {
        this.BANK_ID = BANK_ID;
    }

    public String getLEAPICCTagsInfo() {
        return this.LEAPICCTagsInfo;
    }

    public void setLEAPICCTagsInfo(String LEAPICCTagsInfo) {
        this.LEAPICCTagsInfo = LEAPICCTagsInfo;
    }

    public String getMER_PHONE() {
        return this.MER_PHONE;
    }

    public void setMER_PHONE(String MER_PHONE) {
        this.MER_PHONE = MER_PHONE;
    }

    public String getOFFLINE_TRX() {
        return this.OFFLINE_TRX;
    }

    public void setOFFLINE_TRX(String OFFLINE_TRX) {
        this.OFFLINE_TRX = OFFLINE_TRX;
    }

    public String getAUTH() {
        return this.AUTH;
    }

    public void setAUTH(String AUTH) {
        this.AUTH = AUTH;
    }

    public String getTX_RSLT() {
        return this.TX_RSLT;
    }

    public void setTX_RSLT(String TX_RSLT) {
        this.TX_RSLT = TX_RSLT;
    }

    public boolean isMerchant() {
        return this.isMerchant;
    }

    public void setMerchant(boolean merchant) {
        this.isMerchant = merchant;
    }
}