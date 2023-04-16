package com.surepay.library.usbIntegration;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLContext;


public class Usb
{
    private static final String TAG = "USB_SURE";

    private static Integer iConnectType = UsbConst.d_USB;

    private static Integer iConnectStatue;

    private static boolean bCanceltxn;

    private static boolean bRunning = false;

    private static boolean bEND = true;

    private Integer iSocketTimeOut = Integer.valueOf(300000);

    private Integer tempTimeOut = Integer.valueOf(300000);

    private static String szRecvMsg = "init";

    static LinkedBlockingQueue<Integer> queueClientSocket = new LinkedBlockingQueue<>(10);

    private static Thread thUSB = null;

    private static UsbManager USBManager;

    private static UsbDevice USBConnDevice;

    private static UsbInterface USBConnDeviceInf;

    private static UsbDeviceConnection USBConnection;

    private static UsbEndpoint USBInput;

    private static UsbEndpoint USBOutput;

    private static HashMap<String, UsbDevice> deviceList;

    private static Iterator<UsbDevice> deviceIterator;

    private static ArrayList<String> USBDeviceList;

    private static BroadcastReceiver USBReceiver;

    private OnConnectStatusChanges statusCallBack;

    private OnReceivingData receivingDataCallback;

    private Context DisplayContext;

    // Custom property added by [Alaa Wael] to help get
    // the response data easier in Native/Kotlin/Flutter Apps
    public static TransactionInformation Response;

    public static class TransactionInformation
    {
        private String flag;

        private String cardNumber;

        private String schemaName;

        private String date;

        private String authorizeCode;

        private String responseCode;

        private String rrn;

        private String merchantId;

        private String amount;

        private String terminalId;

        public TransactionInformation(String flag, String cardNumber, String schemaName, String date, String authorizeCode, String responseCode, String rrn, String merchantId, String amount, String terminalId) {
            this.flag = flag;
            this.cardNumber = cardNumber;
            this.schemaName = schemaName;
            this.date = date;
            this.authorizeCode = authorizeCode;
            this.responseCode = responseCode;
            this.rrn = rrn;
            this.merchantId = merchantId;
            this.amount = amount;
            this.terminalId = terminalId;
        }

        public String getFlag() {
            return this.flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getCardNumber() {
            return this.cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getSchemaName() {
            return this.schemaName;
        }

        public void setSchemaName(String schemaName) {
            this.schemaName = schemaName;
        }

        public String getDate() {
            return this.date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAuthorizeCode() {
            return this.authorizeCode;
        }

        public void setAuthorizeCode(String authorizeCode) {
            this.authorizeCode = authorizeCode;
        }

        public String getResponseCode() {
            return this.responseCode;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }

        public String getRrn() {
            return this.rrn;
        }

        public void setRrn(String rrn) {
            this.rrn = rrn;
        }

        public String getMerchantId() {
            return this.merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public String getAmount() {
            return this.amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTerminalId() {
            return this.terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }

        public String toString() {
            return "TransactionInformation{flag='" + this.flag + '\'' + ", cardNumber='" + this.cardNumber + '\'' + ", schemaName='" + this.schemaName + '\'' + ", date='" + this.date + '\'' + ", authorizeCode='" + this.authorizeCode + '\'' + ", responseCode='" + this.responseCode + '\'' + ", rrn='" + this.rrn + '\'' + ", merchantId='" + this.merchantId + '\'' + ", amount='" + this.amount + '\'' + ", terminalId='" + this.terminalId + '\'' + '}';
        }
    }

    public static class ReconciliationInformation
    {
        private String flag;


        private String result;


        private String message;


        public ReconciliationInformation() {}


        public ReconciliationInformation(String flag, String result, String message) {
            this.flag = flag;
            this.result = result;
            this.message = message;
        }


        public String getFlag() {
            return this.flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getResult() {
            return this.result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public String toString() {
            return "ReconciliationInformation{flag=" + this.flag + ", result=" + this.result + ", message=" + this.message + '}';
        }
    }

    public enum TerminalError
    {
        PIN_CANCELED,
        AMOUNT_NOT_FOUND,
        INSERT_TIMEOUT,
        INSERT_CANCELED,
        CANCEL_KRY_PRESSED,
        CANCEL_CONFIRM_AMOUNT,
        ICC_TRANSACTION_FAILED,
        TRANSACTION_NOT_ALLOWED,
        MANUAL_INTER_NOT_ALLOWED,
        FUNCTION_NOT_SUPPORTED,
        NO_INCOME_REQUEST,
        NO_RECORDS,
        NOT_SUPPORTED,
        UNKNOWN_ERROR;

        public static TerminalError fromInteger(int value) {
            switch (value) {
                case -46:
                    return PIN_CANCELED;

                case -43:
                    return AMOUNT_NOT_FOUND;

                case -26:
                    return INSERT_TIMEOUT;

                case -25:
                    return INSERT_CANCELED;

                case -2:
                    return CANCEL_KRY_PRESSED;

                case -45:
                    return CANCEL_CONFIRM_AMOUNT;

                case -48:
                    return ICC_TRANSACTION_FAILED;

                case -49:
                    return TRANSACTION_NOT_ALLOWED;

                case -50:
                    return MANUAL_INTER_NOT_ALLOWED;

                case -42:
                    return FUNCTION_NOT_SUPPORTED;

                case -44:
                    return NO_INCOME_REQUEST;

                case -52:
                    return NO_RECORDS;

                case -51:
                    return NOT_SUPPORTED;
            }

            Log.d("USB_SURE", "TerminalError: UNKNOWN_ERROR " + value);
            return UNKNOWN_ERROR;
        }
    }


    public void setStatusCallBack(OnConnectStatusChanges statusCallBack) {
        this.statusCallBack = statusCallBack;
    }

    public void setReceivingDataCallback(OnReceivingData receivingDataCallback) {
        this.receivingDataCallback = receivingDataCallback;
    }

    public OnConnectStatusChanges getStatusCallBack() {
        return this.statusCallBack;
    }

    public OnReceivingData getReceivingDataCallback() {
        return this.receivingDataCallback;
    }

    public Integer getTimeout() {
        return this.iSocketTimeOut;
    }

    public void setTimeout(Integer timeOut_ms) {
        this.iSocketTimeOut = timeOut_ms;
    }

    public String getRecvMsg() {
        return szRecvMsg;
    }

    public void emptyRecvMsg() {
        szRecvMsg = "";
    }

    public void setConnectType(int type) {
        iConnectType = Integer.valueOf(type);
    }

    public Integer initParam() {
        szRecvMsg = "";
        iConnectStatue = UsbConst.d_NONE_CONNECT;
        if (this.statusCallBack != null) {
            this.statusCallBack.connectionStatusUsbChanges(UsbConst.d_NONE_CONNECT.intValue());
        }
        bCanceltxn = false;
        bEND = false;
        queueClientSocket.clear();
        return Integer.valueOf(0);
    }

    public Integer sendAmount(Context context, String amount) {
        this.tempTimeOut = this.iSocketTimeOut;
        this.DisplayContext = context;
        String formattedAmount = getFormattedAmount(amount);
        Log.e("USB_SURE", "sendAmount: " + amount + " " + formattedAmount);

        final String szSendMsg = setUpFinancialTransaction(formattedAmount);
        initParam();

        if (iConnectType == UsbConst.d_USB) {
            Log.d("USB_SURE", "post: in and type is d_USB");
            (new Thread(new Runnable() {
                public void run() {
                    Usb.this.usbConnection(szSendMsg);
                }
            })).start();
        }
        return Integer.valueOf(0);
    }

    public TransactionInformation sendAmountWithResponse(Context context, String amount) {
        this.tempTimeOut = this.iSocketTimeOut;
        this.DisplayContext = context;
        String formattedAmount = getFormattedAmount(amount);
        Log.e("USB_SURE", "sendAmount: " + amount + " " + formattedAmount);

        final String szSendMsg = setUpFinancialTransaction(formattedAmount);
        initParam();

        if (iConnectType == UsbConst.d_USB) {
            Log.d("USB_SURE", "post: in and type is d_USB");
            (new Thread(new Runnable() {
                public void run() {
                    Usb.this.usbConnection(szSendMsg);
                }
            })).start();
        }
        return Response;
    }

    public static String getFormattedAmount(String amount) {
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

    public static String setUpFinancialTransaction(String amountValue) {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0005";

        byte[] operation = operationID.getBytes();
        byte[] amount = amountValue.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(amount, 0, amount.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        return ss;
    }

    public Integer getLastTransactionFromTerminal(Context context) {
        this.tempTimeOut = this.iSocketTimeOut;
        this.DisplayContext = context;
        final String szSendMsg = setUpGetLastTransaction();
        initParam();
        if (iConnectType == UsbConst.d_USB) {
            Log.d("USB_SURE", "post: in and type is d_USB");
            (new Thread(new Runnable() {
                public void run() {
                    Usb.this.usbConnection(szSendMsg);
                }
            })).start();
        }
        return Integer.valueOf(0);
    }

    public static String setUpGetLastTransaction() {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0002";

        byte[] operation = operationID.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        return ss;
    }

    public Integer checkTerminalReady(Context context) {
        this.tempTimeOut = Integer.valueOf(2000);
        this.DisplayContext = context;
        final String szSendMsg = setUpCheckTerminalReady();
        initParam();
        if (iConnectType == UsbConst.d_USB) {
            Log.d("USB_SURE", "post: in and type is d_USB");
            (new Thread(new Runnable() {
                public void run() {
                    Usb.this.usbConnection(szSendMsg);
                }
            })).start();
        }

        return Integer.valueOf(0);
    }

    public static String setUpCheckTerminalReady() {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0001";

        byte[] operation = operationID.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        return ss;
    }

    public Integer sendReconciliation(Context context) {
        this.tempTimeOut = this.iSocketTimeOut;
        this.DisplayContext = context;
        final String szSendMsg = setupSendReconciliation();
        initParam();
        if (iConnectType.equals(UsbConst.d_USB)) {
            Log.d("USB_SURE", "post: in and type is d_USB");
            (new Thread(new Runnable() {
                public void run() {
                    Usb.this.usbConnection(szSendMsg);
                }
            })).start();
        }
        return Integer.valueOf(0);
    }

    private static String setupSendReconciliation() {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0008";

        byte[] operation = operationID.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        Log.d("USB_SURE", "sendReconciliation: " + ss);
        return ss;
    }

    public Integer setReverseTransaction(Context context) {
        this.tempTimeOut = this.iSocketTimeOut;
        this.DisplayContext = context;
        final String szSendMsg = setupReverseTransaction();
        initParam();
        if (iConnectType.equals(UsbConst.d_USB)) {
            Log.d("USB_SURE", "post: in and type is d_USB");
            (new Thread(new Runnable() {
                public void run() {
                    Usb.this.usbConnection(szSendMsg);
                }
            })).start();
        }
        return Integer.valueOf(0);
    }

    private static String setupReverseTransaction() {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0003";

        byte[] operation = operationID.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        Log.d("USB_SURE", "reversal: " + ss);

        return ss;
    }

    public void usbConnectionInitialize(Activity activity, Context context) {
        final String actionString = context.getPackageName() + ".USB_PERMISSION";

        USBReceiver = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
                    synchronized (this) {
                        UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                        if (device != null) {
                            Toast.makeText(context, "REMOVE DEVICE", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action)) {
                    synchronized (this) {
                        UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                        if (intent.getBooleanExtra("permission", false)) {
                            if (device != null);
                        }
                        else {
                            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(actionString), PendingIntent.FLAG_ONE_SHOT);
                            Usb.USBManager.requestPermission(device, mPermissionIntent);
                        }
                    }
                }

                if (actionString.equals(action)) {
                    synchronized (this) {
                        UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                        if (intent.getBooleanExtra("permission", false) &&
                                device != null)
                        {
                            Usb.USBConnDevice = device;
                        }
                    }
                }
            }
        };


        try {
            if (USBManager == null) {
                USBManager = (UsbManager)activity.getSystemService(Context.USB_SERVICE);
                IntentFilter filterAttached_and_Detached = new IntentFilter("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
                filterAttached_and_Detached.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
                filterAttached_and_Detached.addAction(actionString);
                context.registerReceiver(USBReceiver, filterAttached_and_Detached);

                deviceList = USBManager.getDeviceList();

                deviceIterator = deviceList.values().iterator();
                USBDeviceList = new ArrayList<>();
                while (deviceIterator.hasNext()) {
                    UsbDevice device = deviceIterator.next();

                    USBDeviceList.add(String.valueOf(device.getVendorId()));
                    USBDeviceList.add(String.valueOf(device.getProductId()));

                    if (device.getVendorId() == 3238 && device.getProductId() == 41040)
                    {
                        if (!USBManager.hasPermission(device)) {
                            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(actionString), PendingIntent.FLAG_ONE_SHOT);

                            USBManager.requestPermission(device, mPermissionIntent);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String str = e.getMessage().toString();
        }
    }

    public int usbConnectionOpen(Context context) {
        this.DisplayContext = context;
        boolean bDeviceFound = false;
        String actionString = context.getPackageName() + ".USB_PERMISSION";

        initParam();

        if (USBManager == null) {
            settingProgress(context, new Integer[] { UsbConst.d_USB_CONNECT_FAIL });
            return -1;
        }

        deviceList = USBManager.getDeviceList();

        deviceIterator = deviceList.values().iterator();
        USBDeviceList = new ArrayList<>();

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

            USBDeviceList.add(String.valueOf(device.getVendorId()));
            USBDeviceList.add(String.valueOf(device.getProductId()));

            if (device.getVendorId() == 3238 && device.getProductId() == 41040) {
                if (!USBManager.hasPermission(device)) {
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(actionString), PendingIntent.FLAG_ONE_SHOT);

                    USBManager.requestPermission(device, mPermissionIntent);
                    settingProgress(context, new Integer[] { UsbConst.d_USB_CONNECT_NOT_PREMITED }); break;
                }
                USBConnDevice = device;

                if (USBConnDevice == null) {
                    settingProgress(context, new Integer[] { UsbConst.d_USB_CONNECT_DEVICE_NOT_FOUND });
                    return -1;
                }  int i;

                for (i = 0; i < USBConnDevice.getInterfaceCount(); i++) {
                    UsbInterface intf = USBConnDevice.getInterface(i);

                    if (intf.getEndpointCount() >= 2) {
                        USBConnDeviceInf = intf;
                        break;
                    }
                }

                if (USBConnDeviceInf != null) {
                    try {
                        for (i = 0; i < USBConnDeviceInf.getEndpointCount(); i++) {
                            UsbEndpoint ep = USBConnDeviceInf.getEndpoint(i);
                            if (ep.getType() == 2) {
                                if (ep.getDirection() == 0) {
                                    USBOutput = ep;
                                } else {
                                    USBInput = ep;
                                }
                            }
                        }
                    } catch (Exception e) {
                        String str = e.getMessage().toString();
                    }

                    UsbDeviceConnection connection = null;

                    if (USBManager.hasPermission(USBConnDevice)) {
                        connection = USBManager.openDevice(USBConnDevice);
                        if (connection == null) {
                            settingProgress(context, new Integer[] { UsbConst.d_USB_CONNECT_FAIL });
                            return -1;
                        }
                        if (connection.claimInterface(USBConnDeviceInf, true)) {

                            USBConnection = connection;
                            bDeviceFound = true;
                            settingProgress(context, new Integer[] { UsbConst.d_USB_CONNECT_SUCCESSS }); break;
                        }
                        connection.close();

                        break;
                    }
                    settingProgress(context, new Integer[] { UsbConst.d_USB_CONNECT_NOT_PREMITED });
                    break;
                }
                settingProgress(context, new Integer[] { UsbConst.d_USB_CONNECT_NONE_INTERFACE });
                break;
            }
        }

        if (!bDeviceFound) {
            settingProgress(context, new Integer[] { UsbConst.d_USB_CONNECT_FAIL });
        }
        return 0;
    }

    private void usbConnection(String query) {
        String szOutput = null;
        connectionProgress(new Integer[] { Integer.valueOf(0) });
        Log.d("USB_SURE", "usbConnection: start-ONE");

        try {
            int isSent = USBConnection.bulkTransfer(USBOutput, query.getBytes(), query.length(), this.tempTimeOut.intValue());
            if (isSent < 0) {
                connectionProgress(new Integer[] { UsbConst.d_USB_CONNECT_SEND_FAIL });
                return;
            }
            connectionProgress(new Integer[] { UsbConst.d_USB_CONNECT_SEND_SUCCESS });
        }
        catch (Exception e) {
            connectionProgress(new Integer[] { UsbConst.d_USB_CONNECT_SEND_FAIL });

            return;
        }

        long lStartTime = System.currentTimeMillis();
        StringBuilder b = new StringBuilder();
        byte[] szBuff = new byte[8096];
        boolean iDataEnd = false;
        boolean iHasCancel = false;

        try {
            int isReceived = USBConnection.bulkTransfer(USBInput, szBuff, szBuff.length, this.tempTimeOut.intValue());
            if (isReceived < 0) {
                connectionProgress(new Integer[] { UsbConst.d_USB_CONNECT_RECV_TIMEOUT });
            } else {
                for (int i = 0; i < 8096; i++) {
                    byte bChar = szBuff[i];
                    if (bChar >= 32 || bChar == 10) {
                        b.append((char)bChar);
                    }
                }
                szOutput = b.toString();
                if (szOutput.length() > 0) {
                    szRecvMsg = szOutput;
                    handelOutputResponse(szRecvMsg);
                    connectionProgress(new Integer[] { UsbConst.d_USB_CONNECT_RECV_SUCCESS });
                } else {
                    connectionProgress(new Integer[] { UsbConst.d_USB_CONNECT_RECV_TIMEOUT });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            connectionProgress(new Integer[] { UsbConst.d_USB_CONNECT_RECV_TIMEOUT });
        }
    }

    private void handelOutputResponse(String szRecvMsg) {
        Log.d("USB_SURE", "handelOutputResponse: Start");
        if (this.receivingDataCallback != null) {
            Log.d("USB_SURE", "handelOutputResponse: Callback NOT null");
            boolean isError = isNumeric(szRecvMsg);
            if (isError) {
                Log.d("USB_SURE", "handelOutputResponse: May Be Error");
                int code = Integer.parseInt(szRecvMsg);
                if (code == 0) {
                    Log.d("USB_SURE", "handelOutputResponse: onTerminalReadyForTransaction");
                    this.receivingDataCallback.onTerminalReadyForTransaction();
                    return;
                }
                Log.d("USB_SURE", "handelOutputResponse: Error With Code " + code);
                this.receivingDataCallback.onUsbErrorFromTerminal(TerminalError.fromInteger(Integer.parseInt(szRecvMsg)), code);
            } else {
                Log.d("USB_SURE", "handelOutputResponse: Got Transaction");
                String[] split = szRecvMsg.split("\\|");
                if (split.length == 10) {
                    Log.d("USB_SURE", "handelOutputResponse: Send Transaction");
                    this.receivingDataCallback.onUsbGetTransaction(new TransactionInformation(split[0], split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9]));
                    Response = new TransactionInformation(split[0], split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9]);
                }
                else {
                    Log.d("USB_SURE", "handelOutputResponse: Spilt length" + szRecvMsg);
                }
            }
        } else {
            Log.d("USB_SURE", "handelOutputResponse: CallBack is Null");
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void connectionProgress(Integer... progress) {
        if (bCanceltxn) {
            iConnectStatue = UsbConst.d_ORIGINAL_TXN_CANCEL;
            if (this.statusCallBack != null) {
                this.statusCallBack.connectionStatusUsbChanges(UsbConst.d_ORIGINAL_TXN_CANCEL.intValue());
            } else {
                Log.d("USB_SURE", "connectionProgress:  statusCallBack Is Null");
            }
        } else {

            iConnectStatue = progress[0];
            if (this.statusCallBack != null) {
                this.statusCallBack.connectionStatusUsbChanges(progress[0].intValue());
            } else {
                Log.d("USB_SURE", "connectionProgress:  statusCallBack Is Null");
            }
            Log.d("USB_SURE", "connectionProgress: " + iConnectStatue);
        }
    }

    private void settingProgress(Context context, Integer... progress) {
        iConnectStatue = progress[0];
        if (this.statusCallBack != null) {
            this.statusCallBack.connectionStatusUsbChanges(progress[0].intValue());
        }
    }

    private static void resultSettingDisplay(Context context) {
        try {
            if (iConnectStatue == UsbConst.d_USB_CONNECT_SUCCESSS) {
                Toast.makeText(context, StatusCodeToString_CHT(iConnectStatue.intValue()), Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(context, StatusCodeToString_CHT(iConnectStatue.intValue()), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            szRecvMsg = "implement fail";
        }
    }

    public static String StatusCodeToString_CHT(int StatusCode) {
        if (StatusCode == UsbConst.d_NONE_CONNECT.intValue()) {
            return "NONE_CONNECT";
        }
        if (StatusCode == UsbConst.d_ORIGINAL_TXN_CANCEL.intValue()) {
            return "TXN_CANCEL";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_INIT.intValue()) {
            return "CONNECT_INIT";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_CONNECTING.intValue()) {
            return "CONNECT_CONNECTING";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_SUCCESSS.intValue()) {
            return "USB CONNECT_SUCCESSS";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_FAIL.intValue()) {
            return "USB CONNECT_FAIL";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_DEVICE_NOT_FOUND.intValue()) {
            return "DEVICE_NOT_FOUND";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_NONE_INTERFACE.intValue()) {
            return "NONE_INTERFACE";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_NOT_PREMITED.intValue()) {
            return "NOT_PREMITED";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_SEND_FAIL.intValue()) {
            return "SEND_FAIL";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_SEND_SUCCESS.intValue()) {
            return "SEND_SUCCESS";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_SEND_TIMEOUT.intValue()) {
            return "SEND_TIMEOUT";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_RECV_TIMEOUT.intValue()) {
            return "RECV_TIMEOUT";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_RECV_FAIL.intValue()) {
            return "RECV_FAIL";
        }
        if (StatusCode == UsbConst.d_USB_CONNECT_RECV_SUCCESS.intValue()) {
            return "RECV_SUCCESS";
        }
        Log.d("USB_SURE", "StatusCodeToString_CHT: UNKNOWN_ERROR " + StatusCode);
        return "Unknow:" + StatusCode;
    }

    public static interface OnReceivingData {
        void onUsbGetTransaction(Usb.TransactionInformation param1TransactionInformation);

        void onUsbGetReconciliation(Usb.ReconciliationInformation param1ReconciliationInformation);

        void onTerminalReadyForTransaction();

        void onUsbErrorFromTerminal(Usb.TerminalError param1TerminalError, int param1Int);
    }

    public static interface OnConnectStatusChanges {
        void connectionStatusUsbChanges(int param1Int);
    }
}