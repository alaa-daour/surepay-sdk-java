package com.surepay.library.tcpip;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class TcpClient
{
    public static String SERVER_IP;
    public static int SERVER_PORT;
    private String mServerMessage;
    private OnMessageReceived mMessageListener = null;

    private boolean mRun = false;

    private PrintWriter mBufferOut;

    private BufferedReader mBufferIn;
    private ByteArrayOutputStream buffer = null;

    private byte[] readBufferArray;

    private boolean isConnected = false;


    public TcpClient(OnMessageReceived listener, String serverIP, int serverPort) {
        this.mMessageListener = listener;
        SERVER_IP = serverIP;
        SERVER_PORT = serverPort;
    }

    public void run() {
        this.readBufferArray = new byte[32768];
        this.mRun = true;



        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.e("TCP Client", "C: Connecting...");


            Socket socket = new Socket(serverAddr, SERVER_PORT);


            try {
                this.mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                this.mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                InputStream mInputStream = socket.getInputStream();

                sendConnection();

                while (this.mRun) {
                    if (socket.isConnected() != this.isConnected) {
                        Log.e("TAG", "connection changed to : " + socket.isConnected());
                        this.isConnected = socket.isConnected();
                    }

                    if (this.buffer == null)
                        this.buffer = new ByteArrayOutputStream();
                    if (mInputStream != null) {
                        int getLen = mInputStream.available();
                        if (getLen > 0) {

                            int numBytes = mInputStream.read(this.readBufferArray);
                            this.buffer.write(this.readBufferArray, 0, numBytes);
                            this.mServerMessage = new String(this.readBufferArray, 0, numBytes);
                        }
                    }
                    if (this.mServerMessage != null && this.mMessageListener != null)
                    {
                        this.mMessageListener.messageReceived(this.mServerMessage);
                    }
                }
                Log.e("RESPONSE FROM SERVER", "Saad: Received Message: '" + this.mServerMessage + "'");
            } catch (Exception e) {
                Log.e("TCP", "Saad: Error: " + e.getMessage() + " | " + e.getCause());
            } finally {
                Log.e("TCP", "run: finally");


                socket.close();
            }
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }


    public void sendConnection() {
        if (this.mBufferOut != null && !this.mBufferOut.checkError()) {
            this.mBufferOut.print(setUpSendConnection());
            this.mBufferOut.flush();
        }
    }

    public static String setUpSendConnection() {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0001";
        byte[] operation = operationID.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        return ss;
    }








    public void sendAmount(String amount) {
        Log.e("SENDSEND", "sendAmount: ");
        if (this.mBufferOut != null && !this.mBufferOut.checkError()) {
            String formattedAmount = getFormattedAmount(amount);
            String sendValue = setUpFinancialTransaction(formattedAmount);
            Log.e("SENDSEND", "sendAmount: " + sendValue);
            this.mBufferOut.print(sendValue);
            this.mBufferOut.flush();
        }
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
        Log.e("SENDSEND", "setUpFinancialTransaction: " + ss);

        return ss;
    }



    public void sendReconciliation() {
        if (this.mBufferOut != null && !this.mBufferOut.checkError()) {
            this.mBufferOut.print(setUpReconciliationTransaction());
            this.mBufferOut.flush();
        }
    }

    public static String setUpReconciliationTransaction() {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0008";

        byte[] operation = operationID.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        return ss;
    }



    public void getLastReceipt() {
        if (this.mBufferOut != null && !this.mBufferOut.checkError()) {
            this.mBufferOut.println(setUpLastReceiptTransaction());
            this.mBufferOut.flush();
        }
    }

    public static String setUpLastReceiptTransaction() {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0002";

        byte[] operation = operationID.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        return ss;
    }



    public void sendReversal() {
        if (this.mBufferOut != null && !this.mBufferOut.checkError()) {
            this.mBufferOut.println(setUpReversalTransaction());
            this.mBufferOut.flush();
        }
    }

    public static String setUpReversalTransaction() {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0003";

        byte[] operation = operationID.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        return ss;
    }



    public void closeConnection() {
        if (this.mBufferOut != null && !this.mBufferOut.checkError()) {
            this.mBufferOut.println(setUpCloseConnection());
            this.mBufferOut.flush();
        }
    }

    public static String setUpCloseConnection() {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();

        String operationID = "0009";
        byte[] operation = operationID.getBytes();

        bOutput.write(2);
        bOutput.write(operation, 0, operation.length);
        bOutput.write(3);

        String ss = bOutput.toString();
        return ss;
    }








    public void stopClient() {
        closeConnection();
        clearConnection();
    }

    public void clearConnection() {
        this.mRun = false;

        if (this.mBufferOut != null) {
            this.mBufferOut.flush();
            this.mBufferOut.close();
        }
        this.mMessageListener = null;
        this.mBufferIn = null;
        this.mBufferOut = null;
        this.mServerMessage = null;
    }

    public static interface OnMessageReceived {
        void messageReceived(String param1String);
    }
}