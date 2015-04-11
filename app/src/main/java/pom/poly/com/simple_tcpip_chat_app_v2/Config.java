package pom.poly.com.simple_tcpip_chat_app_v2;

/**
 * Created by User on 2/4/2015.
 */
public class Config {
    public final static String SP_PHONE_KEY="phonenumber";
    public final static String SP_NAME="userphone";
    public final static String INTENT_PHONENUMBER = "phonenumber";
    public final static String SERVER_IP = "192.168.56.1";
    public final static int MY_NOTIFICATION_ID = 101;
    public final static long[] VIBRATE_PATTERN = {0, 100, 200, 300};//fist number is the vibrate stoptime ,second is the vibrate start time, third is the vibrate stoptime.....
    //192.168.31.170  ad
    //192.168.56.1   g
    public final static int SERVER_PORT = 100;
    public static final String BROADCAST_ACTION =
            "pom.poly.com.simple_tcpip_chat_app_v2.BROADCAST";

    public static final String EXTENDED_DATA_STATUS =
            "pom.poly.com.simple_tcpip_chat_app_v2.STATUS";
}
