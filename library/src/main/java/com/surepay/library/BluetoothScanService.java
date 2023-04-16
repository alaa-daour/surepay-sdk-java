package com.surepay.library;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


public class BluetoothScanService
        extends Service {
    private String TAG = "TAG";

    private Thread acceptThread;
    public static Context c;
    public static String RESTART_LISTEN_THREAD = "RESTART_LISTEN_THREAD";
    public static String SEND_AMOUNT_ACTION = "SEND_AMOUNT_ACTION";
    public static String SEND_AMOUNT_VALUE = "SEND_AMOUNT_VALUE";
    public static final String POS_CONNECTION_ACTION = "POS_CONNECTION_ACTION";
    public static final String POS_CONNECTION_STATUS = "POS_CONNECTION_STATUS";
    public static String SEND_RECONCILIATION_ACTION = "SEND_RECONCILIATION_ACTION";
    public static String REVERSE_ACTION = "REVERSE_ACTION";
    public static String GET_LAST_TRANSACTION_ACTION = "GET_LAST_TRANSACTION_ACTION";

    private LocalBroadcastManager localBroadcastManager;

    private boolean transactionInProgress;

    private BluetoothServerSocket mmServerSocket;

    private BluetoothSocket bluetoothSocket = null;
    public static boolean POSisConnected = false;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(BluetoothScanService.RESTART_LISTEN_THREAD)) {
                BluetoothScanService.this.startAcceptThread();
            } else if (intent.getAction().equalsIgnoreCase(BluetoothScanService.SEND_AMOUNT_ACTION)) {

                String amount = intent.getExtras().getString(BluetoothScanService.SEND_AMOUNT_VALUE);
                if (BluetoothScanService.this.bluetoothSocket != null && BluetoothScanService.this.bluetoothSocket.isConnected()) {
                    (new BluetoothScanService.SendStartTransactionRequest(amount)).start();
                } else {
                    BluetoothScanService.this.handleFinancialRequestResponse(" -1 ".getBytes());
                }
            } else if (intent.getAction().equalsIgnoreCase(BluetoothScanService.SEND_RECONCILIATION_ACTION)) {

                if (BluetoothScanService.this.bluetoothSocket != null && BluetoothScanService.this.bluetoothSocket.isConnected()) {
                    (new BluetoothScanService.SendReconciliationRequest()).start();
                } else {
                    BluetoothScanService.this.handleFinancialRequestResponse(" -1 ".getBytes());
                }
            } else if (intent.getAction().equalsIgnoreCase(BluetoothScanService.GET_LAST_TRANSACTION_ACTION)) {

                if (BluetoothScanService.this.bluetoothSocket != null && BluetoothScanService.this.bluetoothSocket.isConnected()) {
                    (new BluetoothScanService.GetLastTransactionRequest()).start();
                } else {
                    BluetoothScanService.this.handleFinancialRequestResponse(" -1 ".getBytes());
                }
            } else if (intent.getAction().equalsIgnoreCase(BluetoothScanService.REVERSE_ACTION)) {

                if (BluetoothScanService.this.bluetoothSocket != null && BluetoothScanService.this.bluetoothSocket.isConnected()) {
                    (new BluetoothScanService.ReverseTransactionRequest()).start();
                } else {
                    BluetoothScanService.this.handleFinancialRequestResponse(" -1 ".getBytes());
                }
            }
        }
    };
    private final StringBuilder fullMessage = new StringBuilder("");

    private void startAcceptThread() {
        if (this.acceptThread != null) {
            try {
                this.acceptThread.interrupt();
            } catch (Exception ex) {
                ex.printStackTrace();
                POSService.cInterface.OnError(-800);
            }
        }
        this.acceptThread = new AcceptConnectionsThread();
        this.acceptThread.start();
    }


    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void onCreate() {
        super.onCreate();
        c = getApplicationContext();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        startAcceptThread();
        unregisterReceivers();

        IntentFilter filter = new IntentFilter(RESTART_LISTEN_THREAD);
        filter.addAction(SEND_AMOUNT_ACTION);
        filter.addAction(SEND_RECONCILIATION_ACTION);
        filter.addAction(GET_LAST_TRANSACTION_ACTION);
        filter.addAction(REVERSE_ACTION);

        this.localBroadcastManager.registerReceiver(this.receiver, filter);
        startForeground();


        if (this.mmServerSocket != null) {
            sendServerStatus("Server Start Listening", true);
        }

        if (this.bluetoothSocket != null && this.bluetoothSocket.isConnected()) {
            sendPOSConnectionStatus(true);
        }

        return Service.START_STICKY;
    }

    private void unregisterReceivers() {
        try {
            if (this.receiver != null)
                this.localBroadcastManager.unregisterReceiver(this.receiver);
        } catch (Exception e) {
            e.printStackTrace();
            POSService.cInterface.OnError(-801);
        }
    }


    private void startForeground() {
        NotificationCompat.Builder mBuilder = (new NotificationCompat.Builder((Context) this, createNotificationChannel())).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(getText(R.string.app_name)).setPriority(-2);

        Notification notification = mBuilder.build();
        startForeground(R.string.app_name, notification);
    }

    private String createNotificationChannel() {
        String channelId = getString(R.string.app_name);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_NONE);

            channel.setLightColor(-16776961);
            channel.setLockscreenVisibility(-1);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(channel);
            return channelId;
        }
        return "";
    }


    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }

    private class AcceptConnectionsThread
            extends Thread {
        @SuppressLint("MissingPermission")
        AcceptConnectionsThread() {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            BluetoothServerSocket tmp = null;
            try {
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("ABT_SPP_Service_POS", Constants.UUID_NAME);
//                }
            } catch (IOException e) {
                Log.e(BluetoothScanService.this.TAG, "Socket's listen() method failed", e);
                POSService.cInterface.OnError(-802);
            }
            BluetoothScanService.this.mmServerSocket = tmp;
            BluetoothScanService.this.sendServerStatus("Server Established", true);
        }

        @SuppressLint("MissingPermission")
        public void run() {
            BluetoothScanService.this.sendServerStatus("Server Start Listening", true);


            while (true) {
                try {
                    BluetoothScanService.this.bluetoothSocket = BluetoothScanService.this.mmServerSocket.accept();
                    if (BluetoothScanService.this.bluetoothSocket != null) {
//                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
                        BluetoothScanService.this.sendServerStatus("Socket Connection Success, " + BluetoothScanService.this.bluetoothSocket.getRemoteDevice().getName(), true);
                        BluetoothScanService.this.sendPOSConnectionStatus(true);
                    }
                } catch (IOException e) {
                    BluetoothScanService.this.sendServerStatus("Socket's accept() method failed, " + e.getMessage(), false);
                    BluetoothScanService.this.sendPOSConnectionStatus(false);
                    POSService.cInterface.OnError(-802);

                    break;
                }
                BluetoothScanService.this.deviceConnected(BluetoothScanService.this.bluetoothSocket);
            }
        }


        public void cancel() {
            try {
                BluetoothScanService.this.mmServerSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothScanService.this.TAG, "Could not close the connect bluetoothSocket", e);
                POSService.cInterface.OnError(-803);
            }
        }
    }

    private void deviceConnected(BluetoothSocket socket) {
        (new ReadDataThread(socket)).run();
    }

    private class ReadDataThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private byte[] readBufferArray;
        private String endTag = "</E_Receipt>";
        private String endTag1 = "</E_Receipt>\n";
        private String endTag2 = "</E_Receipt>\r";
        private String endTag3 = "</E_Receipt>\r\n";
        private ByteArrayOutputStream buffer = null;



        ReadDataThread(BluetoothSocket socket) {
            Log.e("SAADSAAD", "ReadDataThread: ");
            this.mmSocket = socket;
            InputStream tmpIn = null;
            BluetoothScanService.this.sendServerStatus("Socket reading thread started", true);

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                BluetoothScanService.this.sendServerStatus("Error occurred when creating input stream<<" + e, false);
                POSService.cInterface.OnError(-804);
            }

            this.mmInStream = tmpIn;
        }

        public void run() {
            Log.e("SAADSAAD", "ReadDataThread: run ");

            this.readBufferArray = new byte[32768];

            boolean readingReport = false;

            try {
                Log.e("SAADSAAD", "run: try 1");
                while (true) {
                    Log.e("SAADSAAD", "run: while ");

                    try {
                        Log.e("SAADSAAD", "run: try 2");

                        if (this.buffer == null) {
                            this.buffer = new ByteArrayOutputStream();
                        }
                        int numBytes = this.mmInStream.read(this.readBufferArray);
                        this.buffer.write(this.readBufferArray, 0, numBytes);

                        BluetoothScanService.this.sendServerStatus("New Message Part", true);
                        BluetoothScanService.this.forwardReceivedData(new String(this.readBufferArray, 0, numBytes));

                        BluetoothScanService.this.sendServerStatus("Is ETX? " + ((this.readBufferArray[numBytes - 1] == 3) ? 1 : 0), true);

                        if (this.readBufferArray[0] == 2) {

                            BluetoothScanService.this.sendServerStatus("Is STX? TRUE", true);

                            if (this.readBufferArray[numBytes - 1] == 3) {
                                if (!readingReport) {

                                    BluetoothScanService.this.handleFinancialRequestResponse(this.buffer.toByteArray());
                                    clearBuffer(); continue;
                                }
                                BluetoothScanService.this.sendReport(new String(this.readBufferArray, 0, numBytes), 4);
                                clearBuffer();
                                readingReport = false;
                                continue;
                            }
                            readingReport = true;
                            BluetoothScanService.this.sendReport(new String(this.readBufferArray, 0, numBytes), 1);
                            clearBuffer();
                            continue;
                        }
                        if (readingReport) {
                            if (this.readBufferArray[numBytes - 1] == 3) {

                                readingReport = false;
                                BluetoothScanService.this.sendReport(new String(this.readBufferArray, 0, numBytes), 2);
                            } else {
                                BluetoothScanService.this.sendReport(new String(this.readBufferArray, 0, numBytes), 3);
                            }
                            clearBuffer();
                            continue;
                        }
                        String readString = new String(this.buffer.toByteArray(), 0, this.buffer.size());

                        if (readString.endsWith(this.endTag) || readString
                                .endsWith(this.endTag1) || readString
                                .endsWith(this.endTag2) || readString
                                .endsWith(this.endTag3)) {
                            BluetoothScanService.this.sendServerStatus("We have End receipt", true);

                            BluetoothScanService.this.saveRecord((new String(this.buffer.toByteArray(), 0, this.buffer.size())).trim());
                            clearBuffer();

                            if (BluetoothScanService.this.isTransactionInProgress()) {
                                BluetoothScanService.this.setTransactionInProgress(false);
                                BluetoothScanService.this.handleSuccessFinancialRequestResponse();
                            }  continue;
                        }
                        Log.w("*Uncompleted message*", "-----------\n\r" + (new String(this.buffer.toByteArray(), 0, this.buffer.size())).trim() + "-----------\n\r");
                        BluetoothScanService.this.sendServerStatus("Receipt is not full receipt", true);

                    }
                    catch (IOException e) {
                        cancel();
                        POSService.cInterface.OnError(-806);
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("SAADSAAD", "run: catch");
                e.printStackTrace();
                POSService.cInterface.OnError(-806);
            }
        }

        private void clearBuffer() {
            Log.e("SAAADSAAD", "ReadDataThread: clear buffer ");
            Log.e("SAAADSAAD", "full message: " + BluetoothScanService.this.fullMessage.toString());
            Arrays.fill(this.readBufferArray, (byte)0);
            this.buffer.reset();
            this.buffer = null;
        }

        void cancel() {
            Log.e("SAADSAAD", "cancel: ");
            try {
                this.mmSocket.close();
                BluetoothScanService.this.sendPOSConnectionStatus(false);
            } catch (IOException e) {
                BluetoothScanService.this.sendServerStatus("Could not close the connect bluetoothSocket " + e, false);
                POSService.cInterface.OnError(-803);
            }
        }
    }

    private void handleFinancialRequestResponse(byte[] response) {
        Log.e("SAADSAAD", "handleFinancialRequestResponse: ");
        setTransactionInProgress(false);
        String res = new String(response, 1, response.length - 2);


        try {
            int errorCode = Integer.parseInt(res);
            switch (errorCode) {
                case -2:
                    POSService.cInterface.OnError(-2);
                    return;
                case -25:
                    POSService.cInterface.OnError(-25);
                    return;
                case -26:
                    POSService.cInterface.OnError(-26);
                    return;
                case -42:
                    POSService.cInterface.OnError(-42);
                    return;
                case -43:
                    POSService.cInterface.OnError(-43);
                    return;
                case -44:
                    POSService.cInterface.OnError(-44);
                    return;
                case -45:
                    POSService.cInterface.OnError(-45);
                    return;
                case -46:
                    POSService.cInterface.OnError(-46);
                    return;
                case -48:
                    POSService.cInterface.OnError(-48);
                    return;
                case -49:
                    POSService.cInterface.OnError(-49);
                    return;
                case -50:
                    POSService.cInterface.OnError(-50);
                    return;
                case -52:
                    POSService.cInterface.OnError(-52);
                    return;
                case -51:
                    POSService.cInterface.OnError(-51);
                    return;
                case -1:
                    POSService.cInterface.OnError(-1);
                    return;
            }
            POSService.cInterface.OnError(-55);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            POSService.cInterface.OnError(-55);
        }
    }




    private void handleSuccessFinancialRequestResponse() {
        setTransactionInProgress(false);

        TransactionResult transactionResult = new TransactionResult();
        transactionResult.setSuccessfulTransaction(true);
    }




    private void forwardReceivedData(byte[] data) {
        String ss = new String(data);
    }





    private void forwardReceivedData(String data) {
        Log.e("SAADSAAD", "MSG PART ==> " + data);
        if (isNumeric(data)) {

            sendError(data);
        } else {
            this.fullMessage.append(data);
        }
    }



    private void sendError(String data) {
        POSService.cInterface.OnError(Integer.parseInt(data));
    }

    private void sendServerStatus(String status, boolean isOn) {
        Log.e("SAADSAAD", "sendServerStatus: ");
    }




    private void sendReport(String readData, int reportPartType) {}



    private void sendPOSConnectionStatus(boolean status) {
        if (status) {
            if (!POSisConnected) POSService.cInterface.OnConnectionEstablished();

        } else if (POSisConnected) {
            POSService.cInterface.OnConnectionLost();
        }
        POSisConnected = status;

        Intent intent = new Intent("POS_CONNECTION_ACTION");
        intent.putExtra("POS_CONNECTION_STATUS", status);
        this.localBroadcastManager.sendBroadcast(intent);
    }

    private class SendStartTransactionRequest extends Thread {
        private OutputStream mmOutStream;
        private String amountValue;
        private String operationID;

        SendStartTransactionRequest(String amountValue) {
            OutputStream tmpOut = null;
            this.amountValue = amountValue;
            this.operationID = "0005";

            try {
                tmpOut = BluetoothScanService.this.bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(BluetoothScanService.this.TAG, "Error occurred when creating output stream", e);
                POSService.cInterface.OnError(-805);
            }
            this.mmOutStream = tmpOut;
        }


        public void run() {
            super.run();
            writeMessage();
        }

        private void writeMessage() {
            try {
                this.mmOutStream.write(getTestTransAction(this.amountValue));
                this.mmOutStream.flush();
                BluetoothScanService.this.setTransactionInProgress(true);
            } catch (Exception e) {
                Log.e(BluetoothScanService.this.TAG, "Error occurred when sending data", e);
                POSService.cInterface.OnError(-807);
            } finally {}
        }







        private byte[] getTestTransAction(String amountValue) {
            ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

            String operationID = "0005";
            String ref = "123456";
            String amountPlusRef = amountValue + "|" + ref;

            byte[] operation = operationID.getBytes();
            byte[] amount = amountPlusRef.getBytes();

            bOutput.write(2);
            bOutput.write(operation, 0, operation.length);
            bOutput.write(amount, 0, amount.length);
            bOutput.write(3);

            String ss = bOutput.toString();

            Log.w("VA", "");
            return bOutput.toByteArray();
        }
    }

    private class SendReconciliationRequest extends Thread {
        private OutputStream mmOutStream;
        private String operationID;

        SendReconciliationRequest() {
            OutputStream tmpOut = null;
            this.operationID = "0008";

            try {
                tmpOut = BluetoothScanService.this.bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(BluetoothScanService.this.TAG, "Error occurred when creating output stream", e);
                POSService.cInterface.OnError(-805);
            }
            this.mmOutStream = tmpOut;
        }


        public void run() {
            super.run();
            writeMessage();
        }


        private void writeMessage() {
            try {
                this.mmOutStream.write(getTestReconciliation());
                this.mmOutStream.flush();
                BluetoothScanService.this.setTransactionInProgress(true);
            } catch (Exception e) {
                Log.e(BluetoothScanService.this.TAG, "Error occurred when sending data", e);
                POSService.cInterface.OnError(-807);
            } finally {}
        }







        private byte[] getTestReconciliation() {
            ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

            String operationID = "0008";

            byte[] operation = operationID.getBytes();

            bOutput.write(2);
            bOutput.write(operation, 0, operation.length);
            bOutput.write(3);


            return bOutput.toByteArray();
        }
    }

    private class GetLastTransactionRequest extends Thread {
        private OutputStream mmOutStream;
        private String operationID;

        GetLastTransactionRequest() {
            OutputStream tmpOut = null;
            this.operationID = "0002";

            try {
                tmpOut = BluetoothScanService.this.bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(BluetoothScanService.this.TAG, "Error occurred when creating output stream", e);
                POSService.cInterface.OnError(-805);
            }
            this.mmOutStream = tmpOut;
        }


        public void run() {
            super.run();
            writeMessage();
        }

        private void writeMessage() {
            try {
                this.mmOutStream.write(getTestLastTRx());
                this.mmOutStream.flush();
                BluetoothScanService.this.setTransactionInProgress(true);
            } catch (Exception e) {
                Log.e(BluetoothScanService.this.TAG, "Error occurred when sending data", e);
                POSService.cInterface.OnError(-807);
            } finally {}
        }







        private byte[] getTestLastTRx() {
            ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

            String operationID = "0002";

            byte[] operation = operationID.getBytes();

            bOutput.write(2);
            bOutput.write(operation, 0, operation.length);
            bOutput.write(3);

            String ss = bOutput.toString();

            Log.w("VA", "");
            return bOutput.toByteArray();
        }
    }

    private class ReverseTransactionRequest extends Thread {
        private OutputStream mmOutStream;
        private String operationID;

        ReverseTransactionRequest() {
            OutputStream tmpOut = null;
            this.operationID = "0003";

            try {
                tmpOut = BluetoothScanService.this.bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(BluetoothScanService.this.TAG, "Error occurred when creating output stream", e);
                POSService.cInterface.OnError(-805);
            }
            this.mmOutStream = tmpOut;
        }


        public void run() {
            super.run();
            writeMessage();
        }

        private void writeMessage() {
            try {
                this.mmOutStream.write(getTestReverseTRx());
                this.mmOutStream.flush();
                BluetoothScanService.this.setTransactionInProgress(true);
            } catch (Exception e) {
                Log.e(BluetoothScanService.this.TAG, "Error occurred when sending data", e);
                POSService.cInterface.OnError(-807);
            } finally {}
        }







        private byte[] getTestReverseTRx() {
            ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

            String operationID = "0003";

            byte[] operation = operationID.getBytes();

            bOutput.write(2);
            bOutput.write(operation, 0, operation.length);
            bOutput.write(3);

            String ss = bOutput.toString();

            Log.w("VA", "");
            return bOutput.toByteArray();
        }
    }

    private boolean isTransactionInProgress() {
        return this.transactionInProgress;
    }

    private void setTransactionInProgress(boolean value) {
        this.transactionInProgress = value;
    }

    private void saveRecord(String data) {
        Log.e("FULL_ITEM", "==> " + data);
        String separator = "(<\\?xml version=\"1.0\"\\?>)";
        String[] xmls = data.split(separator);

        sendServerStatus("Message part length " + xmls.length, true);


        for (String s : xmls) {
            sendServerStatus("------------------------\r\nMessage part Body<< " + s + "-------------------\r\n", true);
        }

        for (String xml : xmls) {
            if (xml.indexOf("ID=\"01\"") > 0) {
                sendServerStatus("New Receipt Received", true);

                POSService.storeFinTransSharedPref(xml, getApplicationContext());
                POSService.cInterface.OnNewReceipt(xml);
            } else if (xml.indexOf("ID=\"02\"") > 0) {


                POSService.cInterface.OnNewReconciliation(xml);
            }
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
}
