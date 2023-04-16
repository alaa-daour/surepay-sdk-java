package com.surepay.library.usbIntegration;

public abstract class UsbConst
{
    public static final String LibVerison = "V0001";
    public static final Integer d_NONE = Integer.valueOf(0);
    public static final Integer d_SOCKET_CLIENT = Integer.valueOf(1);
    public static final Integer d_SOCKET_SERVER = Integer.valueOf(2);
    public static final Integer d_USB = Integer.valueOf(3);


    public static final Integer d_NONE_CONNECT = Integer.valueOf(1);
    public static final Integer d_ORIGINAL_TXN_CANCEL = Integer.valueOf(2);

    public static final Integer d_USB_CONNECT_INIT = Integer.valueOf(101);
    public static final Integer d_USB_CONNECT_CONNECTING = Integer.valueOf(102);
    public static final Integer d_USB_CONNECT_SUCCESSS = Integer.valueOf(103);
    public static final Integer d_USB_CONNECT_FAIL = Integer.valueOf(104);
    public static final Integer d_USB_CONNECT_DEVICE_NOT_FOUND = Integer.valueOf(105);
    public static final Integer d_USB_CONNECT_NONE_INTERFACE = Integer.valueOf(106);
    public static final Integer d_USB_CONNECT_NOT_PREMITED = Integer.valueOf(107);
    public static final Integer d_USB_CONNECT_SEND_FAIL = Integer.valueOf(108);
    public static final Integer d_USB_CONNECT_SEND_SUCCESS = Integer.valueOf(109);
    public static final Integer d_USB_CONNECT_SEND_TIMEOUT = Integer.valueOf(110);
    public static final Integer d_USB_CONNECT_RECV_TIMEOUT = Integer.valueOf(111);
    public static final Integer d_USB_CONNECT_RECV_FAIL = Integer.valueOf(112);
    public static final Integer d_USB_CONNECT_RECV_SUCCESS = Integer.valueOf(113);
}

