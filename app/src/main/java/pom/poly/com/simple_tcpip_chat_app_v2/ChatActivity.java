package pom.poly.com.simple_tcpip_chat_app_v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
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
    private ArrayList<String> chtaHistoryArray = new ArrayList<String>();
    private ResponseReceiver rr;

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

        //temp for test the Chatarray
        chtaHistoryArray.add("test1");
        chtaHistoryArray.add("test2");
        chtaHistoryArray.add("test3");
        chtaHistoryArray.add("test3");
        chtaHistoryArray.add("test3");
        chtaHistoryArray.add("test3");
        chtaHistoryArray.add("test3");
        chtaHistoryArray.add("test3");
        //temp for test the Chatarray

        btSend.setOnClickListener(this);
        RegisterBrodcastReciver();
        reflashAndShowAlltheChatHistory();

    }

    private void RegisterBrodcastReciver() {
        // IntentFilter mStatusIntentFilter = new IntentFilter(Config.BROADCAST_ACTION+phoneNumber);//use + phonenumber to reg
        IntentFilter mStatusIntentFilter = new IntentFilter(Config.BROADCAST_ACTION);//use + phonenumber to reg //TODO for Test
        rr = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                rr,
                mStatusIntentFilter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
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
    }

    private void reflashAndShowAlltheChatHistory() {
        ArrayList chtaHistoryArray = new ArrayList();
        //get the BUDDY Sql helper
        BuddyListSQLiteHelper budySQLHelp = new BuddyListSQLiteHelper(getApplicationContext());
        //get the databases
        SQLiteDatabase database = budySQLHelp.getReadableDatabase();
        String[] d_column = {BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MTYPE, BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MESSAGE};
        //query to the database,need all the phone number
        String[] where = {phoneNumber};
        Cursor cursor = database.query(BuddyListSQLiteHelper.TABLE_CHAT_HISTORY, d_column, BuddyListSQLiteHelper.COLUMN_PHONENUMNER + " =?", where, null, null, null);
        //move the data from ccursor to arraylist
        //nee to determine the message type, receive from other, or send to other
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MTYPE)) == BuddyListSQLiteHelper.MESSAGE_TYPE_RECEIVE) {
                    chtaHistoryArray.add("SEND" + cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MESSAGE)));
                }
                if (cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MTYPE)) == BuddyListSQLiteHelper.MESSAGE_TYPE_SEND) {
                    chtaHistoryArray.add("RECEIVE" + cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_CHAT_HISTORY_MESSAGE)));
                }


            } while (cursor.moveToNext());

        }
        //close
        budySQLHelp.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        //this code just for try the TCPIP
        // Intent mServiceIntent = new Intent(getApplicationContext(), MessageReciveIntentService.class);// old method, cnt use IntentService
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
                //Success
                edSend.setText("");
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
                os.println(edSend.getText().toString());
                os.flush();
                Thread.sleep(400);
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

        }
    }
}
