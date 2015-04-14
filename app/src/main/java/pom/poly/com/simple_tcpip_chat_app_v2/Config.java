package pom.poly.com.simple_tcpip_chat_app_v2;

import android.util.Log;

/**
 * Created by User on 2/4/2015. test1504
 */
public class Config {
    public final static String SP_PHONE_KEY = "phonenumber";
    public final static String SP_NAME = "userphone";
    public final static String INTENT_PHONENUMBER = "phonenumber";
    public final static int MY_NOTIFICATION_ID = 101;
    public final static long[] VIBRATE_PATTERN = {0, 100, 200, 300};//fist number is the vibrate stoptime ,second is the vibrate start time, third is the vibrate stoptime.....
    //192.168.31.170  ad  taco try okok?
    //192.168.56.1   g
    public final static int SERVER_PORT = 5527;
    public static final String BROADCAST_ACTION =
            "pom.poly.com.simple_tcpip_chat_app_v2.BROADCAST";
    public static final String EXTENDED_DATA_STATUS =
            "pom.poly.com.simple_tcpip_chat_app_v2.STATUS";
    public static final String INTENT_KEY_FROM_ = "f";
    public static final String INTENT_KEY_TO_ = "t";
    public static final String INTENT_KEY_MESSAGE_ = "m";
    public static final int THREAD_SLEEP_TIME = 200;
    public static final String SP_KEY_SERVER_IP = "SERVERIP";
    public static String SERVER_IP = "192.168.56.1";

    public static String HelloMessage(String phoneNumber) {

        return "iam:" + phoneNumber;
    }

    public static String[] alalysisTheReceiveMessage(String message) {
        //message.matches("f[0-9]+t[0-9]+m.+");
        Log.d("onHandleIntent", "raw: message");
        String[] sa = new String[3];
        if (message.matches("f[0-9]+t[0-9]+m.+")) {
            sa[0] = message.substring(message.indexOf('f') + 1, message.indexOf('t'));
            sa[1] = message.substring(message.indexOf('t') + 1, message.indexOf('m'));
            sa[2] = message.substring(message.indexOf('m') + 1);
            return sa;
        } else {
            return new String[3];
        }
    }

    public static String createSendMessage(String fromPhonenumber, String Tophonenymber, String message) {
        return "f" + fromPhonenumber + "t" + Tophonenymber + "m" + message;
    }


}
