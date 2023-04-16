package com.surepay.library.callback;

public interface ConnectionInterface {
    void OnNewReceipt(String paramString);

    void OnConnectionLost();

    void OnConnectionEstablished();

    void OnError(int paramInt);

    void OnNewReconciliation(String paramString);
}
