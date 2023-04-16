package com.surepay.library.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.surepay.library.data.Financial;
import com.surepay.library.POSService;
import com.surepay.library.R;
import java.util.ArrayList;


public class FinancialTransActivity
        extends Activity
{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_transaction);

        ListView listView = (ListView)findViewById(R.id.listView);

        ArrayList<Financial> financialArrayList = new ArrayList<>();

        try {
            int transResultCode = Integer.parseInt(POSService.financial.getTX_RSLT());
            if (transResultCode == 2) {
                Financial financial = POSService.financial;
                financial.setMerchant(true);
                financialArrayList.add(financial);
            } else {
                for (int i = 0; i < 2; i++) {
                    Financial financial = new Financial();
                    financial.setPAN(POSService.financial.getPAN());
                    financial.setEXP_DATE(POSService.financial.getEXP_DATE());
                    financial.setAMOUNT(POSService.financial.getAMOUNT());
                    financial.setSCHEME_N_A(POSService.financial.getSCHEME_N_A());
                    financial.setSCHEME_N_E(POSService.financial.getSCHEME_N_E());
                    financial.setTRANS_DT_ST(POSService.financial.getTRANS_DT_ST());
                    financial.setAUTH(POSService.financial.getAUTH());
                    financial.setRRN(POSService.financial.getRRN());
                    financial.setMID(POSService.financial.getMID());
                    financial.setTRANS_TYPE(POSService.financial.getTRANS_TYPE());
                    financial.setMER_NAME_E(POSService.financial.getMER_NAME_E());
                    financial.setMER_NAME_A(POSService.financial.getMER_NAME_A());
                    financial.setMER_ADDRS_E(POSService.financial.getMER_ADDRS_E());
                    financial.setMER_ADDRS_A(POSService.financial.getMER_ADDRS_A());
                    financial.setAID(POSService.financial.getAID());
                    financial.setSTAN(POSService.financial.getSTAN());
                    financial.setMadaSpec(POSService.financial.getMadaSpec());
                    financial.setAPP_VER(POSService.financial.getAPP_VER());
                    financial.setTID(POSService.financial.getTID());
                    financial.setAQU_ID(POSService.financial.getAQU_ID());
                    financial.setTRANS_DT_END(POSService.financial.getTRANS_DT_END());
                    financial.setBANK_ID(POSService.financial.getBANK_ID());
                    financial.setLEAPICCTagsInfo(POSService.financial.getLEAPICCTagsInfo());
                    financial.setMER_PHONE(POSService.financial.getMER_PHONE());
                    financial.setCVM_MSG(POSService.financial.getCVM_MSG());
                    financial.setOFFLINE_TRX(POSService.financial.getOFFLINE_TRX());
                    financial.setTX_RSLT(POSService.financial.getTX_RSLT());
                    if (i == 0) { financial.setMerchant(true); }
                    else { financial.setMerchant(false); }
                    financialArrayList.add(financial);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        TransactionAdapter transactionAdapter = new TransactionAdapter((Context)this, financialArrayList);
        listView.setAdapter((ListAdapter)transactionAdapter);
    }
}
