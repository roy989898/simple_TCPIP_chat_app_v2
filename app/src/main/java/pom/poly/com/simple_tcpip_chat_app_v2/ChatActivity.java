package pom.poly.com.simple_tcpip_chat_app_v2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener {
    private ListView chatListView;
    private Button btSend;
    private EditText edSend;
    private String phoneNumber;
    private ArrayAdapter adapter;
    private ArrayList<String> chtaHistoryArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        phoneNumber = getIntent().getStringExtra(Config.INTENT_PHONENUMBER);
        ab.setTitle(phoneNumber);
        //set the array adapter and chatListView
        chatListView = (ListView) findViewById(R.id.lvChatHistory);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chtaHistoryArray);
        chatListView.setAdapter(adapter);


        btSend = (Button) findViewById(R.id.btSEnd);
        edSend = (EditText) findViewById(R.id.etSed);

        //temp for test the Chatarray
        chtaHistoryArray.add("test1");
        chtaHistoryArray.add("test2");
        chtaHistoryArray.add("test3");
        //temp for test the Chatarray

        btSend.setOnClickListener(this);
        reflashAndShowAlltheChatHistory();


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
        Intent mServiceIntent = new Intent(getApplicationContext(), MessageReciveIntentService.class);
        startService(mServiceIntent);
    }


}
