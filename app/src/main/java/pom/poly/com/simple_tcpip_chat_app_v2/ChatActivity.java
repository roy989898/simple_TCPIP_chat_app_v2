package pom.poly.com.simple_tcpip_chat_app_v2;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener {
    private ListView chatListView;
    private Button btSend;
    private EditText edSend;
    private String phoneNumber;
    private ChatArrayAdapter adapter;
    private ArrayList<Message> chtaHistoryArray = new ArrayList<Message>();
    private ResponseReceiver rr;
    private ContentResolver mContRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        phoneNumber = getIntent().getStringExtra(Config.INTENT_PHONENUMBER);
        ab.setTitle(phoneNumber);
        //set the array adapter and chatListView
        chatListView = (ListView) findViewById(R.id.lvChatHistory);
        adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage, chtaHistoryArray);
        chatListView.setAdapter(adapter);


        btSend = (Button) findViewById(R.id.btSEnd);
        edSend = (EditText) findViewById(R.id.etSed);

        mContRes = getContentResolver();
        btSend.setOnClickListener(this);
        RegisterBrodcastReciver();
        reflashAndShowAlltheChatHistory();

    }

    private void RegisterBrodcastReciver() {
        // IntentFilter mStatusIntentFilter = new IntentFilter(Config.BROADCAST_ACTION+phoneNumber);//use + phonenumber to reg
        IntentFilter mStatusIntentFilter = new IntentFilter(Config.BROADCAST_ACTION + phoneNumber);//use + phonenumber to reg //TODO for Test
        rr = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                rr,
                mStatusIntentFilter);
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void reflashAndShowAlltheChatHistory() {
        //get the BUDDY Sql helper
        BuddyListSQLiteHelper budySQLHelp = new BuddyListSQLiteHelper(getApplicationContext());
        //get the databases
        SQLiteDatabase database = budySQLHelp.getReadableDatabase();
        String[] d_column = {BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MTYPE, BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MESSAGE};
        //query to the database,need all the phone number
        String[] where = {phoneNumber};
        //Cursor cursor = database.query(BuddyListSQLiteHelper.TABLE_CHAT_HISTORY, d_column, BuddyListSQLiteHelper.COLUMN_PHONENUMNER + " =?", where, null, null, null);
        //Cursor cursor=mContRes.query(BuddyAndChatListContentProvider.CONTENT_URI_CHAT,null,BuddyListSQLiteHelper.COLUMN_PHONENUMNER + "=?",where,null);
        Cursor cursor = mContRes.query(BuddyAndChatListContentProvider.CONTENT_URI_CHAT, null, null, null, null);
        Log.d("from", cursor.getCount() + "");
        Log.d("from", cursor.getColumnName(0) + "");
        Log.d("from", cursor.getColumnName(1) + "");
        Log.d("from", cursor.getColumnName(2) + "");
        //move the data from ccursor to arraylist
        //nee to determine the message type, receive from other, or send to other
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MTYPE)).equals(BuddyListSQLiteHelper.MESSAGE_TYPE_RECEIVE)) {
                    Log.d("from", "L");
                    chtaHistoryArray.add(new Message(cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MESSAGE)), true));
                }
                if (cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MTYPE)).equals(BuddyListSQLiteHelper.MESSAGE_TYPE_SEND)) {
                    Log.d("from", "R");
                    chtaHistoryArray.add(new Message(cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MESSAGE)), false));
                }


            } while (cursor.moveToNext());

        }
        //close
        budySQLHelp.close();
        adapter.notifyDataSetChanged();
        chatListView.setSelection(chatListView.getCount() - 1);
    }

    @Override
    public void onClick(View v) {
        //this code just for try the TCPIP
        new SendMessageTask().execute();
        //TODO save message


    }

    class SendMessageTask extends AsyncTask {
        /**
         * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
         */
        private static final int SUCCESS_SEND = 1;
        private static final int FAIL_SEND = -1;

        public SendMessageTask() {
            super();
        }

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param o The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if ((Integer) o == FAIL_SEND) {
                //send not success
                Toast.makeText(getApplicationContext(), "can't,try later", Toast.LENGTH_LONG).show();
            } else {
                //Success,save the Mesage
                SaveMessageToSqlAsyncTask task = new SaveMessageToSqlAsyncTask(BuddyListSQLiteHelper.MESSAGE_TYPE_SEND, phoneNumber, edSend.getText().toString());//MessageType T M
                task.execute();//start to save to SQL by ContentProvider
                edSend.setText("");
                reflashAndShowAlltheChatHistory();
            }
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */

        @Override
        protected Object doInBackground(Object[] params) {
            Socket socket = null;
            try {
                socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
                PrintWriter os = new PrintWriter(socket.getOutputStream());
                // sen the Message
                os.println(Config.createSendMessage(getSharedPreferences(Config.SP_NAME, MODE_PRIVATE).getString(Config.SP_PHONE_KEY, "00"), phoneNumber, edSend.getText().toString()));
                os.flush();
                Thread.sleep(Config.THREAD_SLEEP_TIME);
                os.close();//关闭Socket输出流
                socket.close();//关闭Socket
                return SUCCESS_SEND;//
            } catch (IOException e) {
                e.printStackTrace();
                return FAIL_SEND;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return FAIL_SEND;
            }


        }


    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO on receive do a here
            //load from sql to array
            //refresh
            reflashAndShowAlltheChatHistory();

        }
    }

    class SaveMessageToSqlAsyncTask extends AsyncTask {
        String MessageType, T, M;

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
        public SaveMessageToSqlAsyncTask(String MessageType, String T, String M) {
            this.MessageType = MessageType;
            this.T = T;
            this.M = M;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            ContentResolver mContRes = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_PHONENUMNER, T);
            values.put(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MESSAGE, M);
            values.put(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MTYPE, MessageType);
            mContRes.insert(BuddyAndChatListContentProvider.CONTENT_URI_CHAT, values);
            return null;
        }

    }
}
