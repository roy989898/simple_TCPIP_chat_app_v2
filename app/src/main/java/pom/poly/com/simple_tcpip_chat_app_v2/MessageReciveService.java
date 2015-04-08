package pom.poly.com.simple_tcpip_chat_app_v2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageReciveService extends Service {
    private ReceiveMessageThread rmt;
    private Socket socket;
    private BufferedReader is;
    private PrintWriter os;
    private int threadNumber = 0;


    public MessageReciveService() {
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link android.content.Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p/>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p/>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p/>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p/>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link android.os.AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link android.content.Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        rmt = new ReceiveMessageThread();
        if (threadNumber < 1) {// prevent to create more than one thread at a Client to connect to the server
            rmt.start();
            threadNumber++;
            Log.d("onHandleIntent", "Thread create");
        }

        return START_REDELIVER_INTENT;
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
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
        threadNumber = threadNumber - 1;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class ReceiveMessageThread extends Thread {

        public void run() {
            try {
                socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os = new PrintWriter(socket.getOutputStream());
                while (true) {
                    //os.println("get time");//for test
                    //os.flush();//for test
                    Log.d("onHandleIntent", "in loop");
                    String str = is.readLine();//从系统标准输入读入一字符串
                    Log.d("onHandleIntent", "From server: " + str);
                    //TODO to classif the different typr of message adn save them to the Sqlite server use 正規表示式 ,asyn task
                    //TODO  Notify the message and show the to the UI

                }
            } catch (Exception e) {
                e.printStackTrace();


            }

        }
    }
}
