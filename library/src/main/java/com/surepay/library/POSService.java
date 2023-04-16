package com.surepay.library;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.surepay.library.data.Financial;
import com.surepay.library.callback.ConnectionInterface;
import com.surepay.library.view.FinancialTransActivity;
import com.surepay.library.view.TransactionAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;


public class POSService
{
    public static String SHARED_PREFERENCE_KEY = "last_submitted_fin_trans";

    public static String deviceInfoStr = "";
    public static String lastFinTransStr = "";

    public static Financial financial;

    public static ConnectionInterface cInterface;


    public POSService(ConnectionInterface cInterface) {
        this.cInterface = cInterface;
    }


    public int StartListenService(@NonNull Context context, @NonNull String strCommMedia, String strServiceName, String strServiceUUID, String iPort, boolean showVisibleDialog) {
        if (strCommMedia.equalsIgnoreCase(strCommMedia)) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                return -100;
            }

            if (!mBluetoothAdapter.isEnabled())
            {

                return -101;
            }
            startScanService(context, showVisibleDialog);
            return 0;
        }

        return -102;
    }


    private void startScanService(Context context, boolean showVisibleDialog) {
        if (showVisibleDialog)
            makeDeviceVisible(context);
        Intent intent = new Intent(context, BluetoothScanService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
    private void makeDeviceVisible(Context context) {
        Intent discoverableIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
        discoverableIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 300);
        context.startActivity(discoverableIntent);
    }

    public int ShutdownListenService() {
        if (BluetoothScanService.POSisConnected) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.disable();
            return 0;
        }
        return -103;
    }


    public int checkConnectionStatus() {
        if (BluetoothScanService.POSisConnected)
            return 0;
        return -103;
    }

    public int GetConnectedDeviceInfo() {
        if (BluetoothScanService.POSisConnected) {
            Set<BluetoothDevice> connectedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            if (connectedDevices.size() > 0) {
                Iterator<BluetoothDevice> iterator = connectedDevices.iterator(); if (iterator.hasNext()) { BluetoothDevice d = iterator.next();
                    String deviceName = d.getName();
                    String macAddress = d.getAddress();
                    deviceInfoStr = "Name: " + deviceName + " - Address: " + macAddress;
                    return 0; }

            }
        }
        deviceInfoStr = "No device is connected";
        return -103;
    }



    public int PerformFinTrx(@NonNull Context context, @NonNull int iTransactionType, @NonNull String fAmount, String strRRN, String strNAQDAmount, String strReserved) {
        if (BluetoothScanService.POSisConnected) {
            if (iTransactionType == 0) {
                try {
                    Intent intent = new Intent(BluetoothScanService.SEND_AMOUNT_ACTION);
                    intent.putExtra(BluetoothScanService.SEND_AMOUNT_VALUE, getFormattedAmount(fAmount));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    return 0;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return -104;
                }
            }
            return -102;
        }
        return -103;
    }

    public int sendReconciliationBt(@NonNull Context context) {
        if (BluetoothScanService.POSisConnected) {
            try {
                Intent intent = new Intent(BluetoothScanService.SEND_RECONCILIATION_ACTION);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return -103;
    }

    public int sendReverseBt(@NonNull Context context) {
        if (BluetoothScanService.POSisConnected) {
            try {
                Intent intent = new Intent(BluetoothScanService.REVERSE_ACTION);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return -103;
    }

    private String getFormattedAmount(String amount) {
        float floatAmount = Float.parseFloat(amount);

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat)nf;
        formatter.applyPattern("#.00");

        String formatted = formatter.format(floatAmount);
        Log.e("formatted", "formatted ==> " + formatted);
        StringBuilder sb = new StringBuilder(formatted);
        sb.deleteCharAt(sb.indexOf("."));
        String formattedValue = sb.toString();
        formattedValue = String.format(Locale.US, "%012d", new Object[] { Long.valueOf(Long.parseLong(formattedValue)) });

        return formattedValue;
    }

    public int GetLastTrxResult(Context context) {
        if (BluetoothScanService.POSisConnected) {
            try {
                Intent intent = new Intent(BluetoothScanService.GET_LAST_TRANSACTION_ACTION);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -103;
    }


    public int ShowReceipt(Context context) {
        String data = getFinTransSharedPref(context);
        if (!data.equals("")) {
            lastFinTransStr = data;
            financial = new Financial();
            financial.parseXml(lastFinTransStr);
            Intent intent = new Intent(context, FinancialTransActivity.class);
            context.startActivity(intent);
            return 0;
        }
        return -105;
    }

    public int ExportReceipt(Context context) {
        String data = getFinTransSharedPref(context);
        if (!data.equals("")) {
            lastFinTransStr = data;
            financial = new Financial();
            financial.parseXml(lastFinTransStr);
            createReceipt(context);
            return 0;
        }
        return -105;
    }


    public int ClearTransactions(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().remove(SHARED_PREFERENCE_KEY).commit();
        return 0;
    }

    public static void storeFinTransSharedPref(String data, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SHARED_PREFERENCE_KEY, data);
        editor.apply();
    }

    private String getFinTransSharedPref(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String data = preferences.getString(SHARED_PREFERENCE_KEY, "");

        return data;
    }

    private void createReceipt(final Context context) {
        AsyncTask.execute(new Runnable()
        {
            public void run() {
                int transResultCode = Integer.parseInt(POSService.financial.getTX_RSLT());
                if (transResultCode == 2) {
                    View view = POSService.this.createReceiptView(context, true);
                    Bitmap bitmap = POSService.this.getBitmapFromView(view);
                    POSService.saveToInternalStorage(bitmap, true);
                } else {
                    View merchView = POSService.this.createReceiptView(context, true);
                    Bitmap merchBitmap = POSService.this.getBitmapFromView(merchView);
                    POSService.saveToInternalStorage(merchBitmap, true);
                    View clientView = POSService.this.createReceiptView(context, false);
                    Bitmap clientBitmap = POSService.this.getBitmapFromView(clientView);
                    POSService.saveToInternalStorage(clientBitmap, false);
                }
            }
        });
    }

    private Bitmap getBitmapFromView(View view) {
        view.measure(0, 0);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }


    private static void saveToInternalStorage(Bitmap bitmapImage, boolean isMerchant) {
        File mypath, directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);


        if (isMerchant) { mypath = new File(directory, "Merchant_Receipt.jpg"); }
        else { mypath = new File(directory, "Client_Receipt.jpg"); }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            cInterface.OnError(-808);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                cInterface.OnError(-808);
            }
        }
    }

    private View createReceiptView(Context context, boolean isMerchant) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_financial_transaction, null);

        ListView listView = (ListView)view.findViewById(R.id.listView);

        ArrayList<Financial> financialArrayList = new ArrayList<>();

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
        if (isMerchant) { financial.setMerchant(true); }
        else { financial.setMerchant(false); }
        financialArrayList.add(financial);

        TransactionAdapter transactionAdapter = new TransactionAdapter(context, financialArrayList);
        listView.setAdapter((ListAdapter)transactionAdapter);

        return view;
    }
}
