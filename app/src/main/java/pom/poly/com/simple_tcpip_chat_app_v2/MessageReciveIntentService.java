package pom.poly.com.simple_tcpip_chat_app_v2;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * <p/>
 * helper methods.
 */
public class MessageReciveIntentService extends IntentService {
    private Socket socket;
    private BufferedReader is;
    private PrintWriter os;
    private String message = "";


    public MessageReciveIntentService() {
        super("MessageReciveIntentService");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            is.close();//关闭Socket输入流
            os.close();//关闭Socket输出流
            socket.close();//关闭Socket
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("onHandleIntent", "hihihihhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        try {
            //192.168.56.1
            //192.168.31.170
            socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintWriter(socket.getOutputStream());

            while (true) {


                os.println("get time");
                os.flush(); //刷新输出流，使Server马上收到该字符串
                //Log.d("onHandleIntent", "after flush");


                String str = is.readLine();//从系统标准输入读入一字符串
                Log.d("onHandleIntent", "From server: " + str);
                Thread.sleep(10000);
            }
            /*do {
                os.println("get time");
                try {
                    if (!is.ready()) {
                        if (message != null) {
                            message = "";
                        }
                    }
                    int num = is.read();
                    message += Character.toString((char) num);
                    Log.d("onHandleIntent", "From server: " + message);
                } catch (Exception classNot) {
                }

            } while (!message.equals("bye"));*/


        } catch (Exception e) {
            Log.d("onHandleIntent", e.toString());
            try {
                is.close();//关闭Socket输入流
                os.close();//关闭Socket输出流
                socket.close();//关闭Socket
            } catch (IOException e1) {
                e1.printStackTrace();
            }


        }

    }


}
