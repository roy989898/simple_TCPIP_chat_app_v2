package pom.poly.com.simple_tcpip_chat_app_v2;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
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
    private NotificationManager notificationManager;


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
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
        if (threadNumber > 0)
            threadNumber = threadNumber - 1;
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
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void sendBrodcastMessage(String F, String T, String M) {
        Intent localIntent =
                new Intent(Config.BROADCAST_ACTION + F)//TODO need to change to Config.BROADCAST_ACTION+phonenumber
                        // Puts the status into the Intent
                        .putExtra(Config.INTENT_KEY_FROM_, F);
        localIntent.putExtra(Config.INTENT_KEY_TO_, F);
        localIntent.putExtra(Config.INTENT_KEY_MESSAGE_, M);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification(String message, String toPhonenumber) {
        //create the Intent to open the ChatActivity for a specific phonnumber
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Config.INTENT_PHONENUMBER, toPhonenumber);
        // put the intent into the PendingIntent

        PendingIntent pendingintent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        //set the notification message
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setAutoCancel(true);//Make this notification automatically dismissed when the user touches it
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_info);
        builder.setLargeIcon(bmp);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info);
        builder.setTicker(toPhonenumber + ": " + message);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setSound(uri);
        builder.setContentTitle(toPhonenumber);
        builder.setContentText(message);
        builder.setContentIntent(pendingintent);
        builder.setVibrate(Config.VIBRATE_PATTERN);
        //builder.setNumber(5);
        //create Message
        notificationManager.notify(Config.MY_NOTIFICATION_ID, builder.build());


    }

    private class ReceiveMessageThread extends Thread {

        public void run() {
            try {
                socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os = new PrintWriter(socket.getOutputStream());
                SharedPreferences sp = getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
                String phonenumber = sp.getString(Config.SP_PHONE_KEY, "00");
                os.println(Config.HelloMessage(phonenumber));//for test
                os.flush();//for test
                while (true) {
                    //os.println("get time");//for test
                    //os.flush();//for test
                    Log.d("onHandleIntent", "in loop");
                    String str = is.readLine();//从系统标准输入读入一字符串
                    Log.d("onHandleIntent", "From server: " + str);
                    //TODO to classif the different typr of message adn save them to the Sqlite server use 正規表示式 ,asyn task
                    String[] ftm = Config.alalysisTheReceiveMessage(str);

                    if (ftm[0] != null) {// because some time the server will get a message don't match "f[0-9]+t[0-9]+m.+"
                        showNotification(ftm[2], ftm[1]);
                        //save in to the sql,using a new thread,by asynctask
                        SaveMessageToSqlAsyncTask task = new SaveMessageToSqlAsyncTask(ftm[0], BuddyListSQLiteHelper.MESSAGE_TYPE_RECEIVE, ftm[2]);
                        task.execute();
                        //TODO  save in to the sql
                        sendBrodcastMessage(ftm[0], ftm[1], ftm[2]);
                    }


                }
            } catch (Exception e) {
                threadNumber = threadNumber - 1;
                e.printStackTrace();
                Log.d("onHandleIntent", "Quit");
                Log.d("onHandleIntent", e.toString());


            }

        }
    }

    class SaveMessageToSqlAsyncTask extends AsyncTask {
        String F, MessageType, M;

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        public SaveMessageToSqlAsyncTask(String F, String MessageType, String M) {
            this.F = F;
            this.MessageType = MessageType;
            this.M = M;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            ContentResolver mContRes = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_PHONENUMNER, F);
            values.put(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MESSAGE, M);
            values.put(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MTYPE, MessageType);
            mContRes.insert(BuddyAndChatListContentProvider.CONTENT_URI_CHAT, values);
            return null;
        }

    }

}
