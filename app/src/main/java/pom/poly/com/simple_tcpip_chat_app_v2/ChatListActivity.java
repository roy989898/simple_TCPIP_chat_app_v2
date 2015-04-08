package pom.poly.com.simple_tcpip_chat_app_v2;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class ChatListActivity extends ActionBarActivity {
    private EditText edPhonenumber;
    private ListView buddy_listView;
    private ArrayAdapter adapter;
    private ArrayList BuddyArray;
    private ContentResolver mContRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContRes = getContentResolver();

        //start the Receive Service
        Intent mServiceIntent = new Intent(getApplicationContext(), MessageReciveService.class);
        startService(mServiceIntent);
        //start the Receive Service

        setContentView(R.layout.activity_chat_list);
        BuddyArray=buddydata_fromsqltoArray();
        initate_the_buddyListview();
        reflash_Budylist();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.adding) {
            //post a dialog box to input the phone number
            edPhonenumber=new EditText(this);
            new AlertDialog.Builder(this).setTitle("Please input user phone number").setIcon(android.R.drawable.ic_dialog_info).setView(edPhonenumber).setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //perpare the value that insert to the SQL databases
                    ContentValues values=new ContentValues();
                    values.put(BuddyListSQLiteHelper.COLUMN_PHONENUMNER,edPhonenumber.getText().toString());
                    //insert
                    // database.insert(BuddyListSQLiteHelper.TABLE_BUDDYS,null,values);
                    mContRes.insert(BuddyAndChatListContentProvider.CONTENT_URI_BUDDY, values);

                    //it is because add newdata,need reflash the ListView
                    BuddyArray=buddydata_fromsqltoArray();
                    initate_the_buddyListview();
                    reflash_Budylist();

                }
            }).setNegativeButton("Cancel", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Get the phone number from sqlite
     * @return string Arraylist
     */
    private ArrayList buddydata_fromsqltoArray(){
        ArrayList BuddyArray=new ArrayList();

        Cursor cursor = mContRes.query(BuddyAndChatListContentProvider.CONTENT_URI_BUDDY, null, null, null, null);
        //move the data from ccursor to arraylist
        if(cursor.moveToFirst()){
            do{
                BuddyArray.add(cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_PHONENUMNER)));
            }while(cursor.moveToNext());

        }

        return BuddyArray;


        }
    private void initate_the_buddyListview(){
        buddy_listView=(ListView)findViewById(R.id.lv_buddy);
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,BuddyArray);
        buddy_listView.setAdapter(adapter);
        //set List iteam long click
        buddy_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int c, long id) {

                //delete data in SQL by ContentProvider

                mContRes.delete(BuddyAndChatListContentProvider.CONTENT_URI_BUDDY, BuddyListSQLiteHelper.COLUMN_PHONENUMNER, new String[]{(String) BuddyArray.get(c)});
                //delete data in ArrayList
                BuddyArray.remove(c);
                reflash_Budylist();
                return true;
            }
        });

        //set Lis iteam click
        buddy_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Config.INTENT_PHONENUMBER, (String) BuddyArray.get(position));
                startActivity(intent);
            }
        });




    }
    private void reflash_Budylist(){
        adapter.notifyDataSetChanged();
    }

    }


