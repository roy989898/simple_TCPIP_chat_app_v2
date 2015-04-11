package pom.poly.com.simple_tcpip_chat_app_v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyStartServiceReceiver extends BroadcastReceiver {
    public MyStartServiceReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //start the Receive Service
        Intent mServiceIntent = new Intent(context, MessageReciveService.class);
        context.startService(mServiceIntent);
        //start the Receive Service
    }
}
