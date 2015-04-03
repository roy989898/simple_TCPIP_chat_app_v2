package pom.poly.com.simple_tcpip_chat_app_v2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ChatListActivity extends ActionBarActivity {
    private EditText edPhonenumber;
    private ListView buddy_listView;
    private ArrayAdapter adapter;
    private ArrayList BuddyArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    //get the BUDDY Sql helper
                    BuddyListSQLiteHelper budySQLHelp=new BuddyListSQLiteHelper(getApplicationContext());
                    //get the databases
                    SQLiteDatabase database=budySQLHelp.getWritableDatabase();
                    //perpare the value that insert to the SQL databases
                    ContentValues values=new ContentValues();
                    values.put(BuddyListSQLiteHelper.COLUMN_PHONENUMNER,edPhonenumber.getText().toString());
                    //insert
                    database.insert(BuddyListSQLiteHelper.TABLE_BUDDYS,null,values);
                    //close
                    budySQLHelp.close();
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
        //get the BUDDY Sql helper
        BuddyListSQLiteHelper budySQLHelp=new BuddyListSQLiteHelper(getApplicationContext());
        //get the databases
        SQLiteDatabase database=budySQLHelp.getReadableDatabase();
        String[] d_column={BuddyListSQLiteHelper.COLUMN_PHONENUMNER};
        //query to the database,need all the phone number
        Cursor cursor=database.query(BuddyListSQLiteHelper.TABLE_BUDDYS, d_column, null, null, null, null, null);
        //move the data from ccursor to arraylist
        if(cursor.moveToFirst()){
            do{
                BuddyArray.add(cursor.getString(cursor.getColumnIndex(BuddyListSQLiteHelper.COLUMN_PHONENUMNER)));
            }while(cursor.moveToNext());

        }
        //close
        budySQLHelp.close();
        return BuddyArray;


        }
    private void initate_the_buddyListview(){
        buddy_listView=(ListView)findViewById(R.id.lv_buddy);
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,BuddyArray);
        buddy_listView.setAdapter(adapter);
        buddy_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int c, long id) {
                //delete the data in Sqlite
                //get the BUDDY Sql helper
                BuddyListSQLiteHelper budySQLHelp=new BuddyListSQLiteHelper(getApplicationContext());
                //get the databases
                SQLiteDatabase database=budySQLHelp.getWritableDatabase();
                //delete data in SQL
                database.delete(BuddyListSQLiteHelper.TABLE_BUDDYS,BuddyListSQLiteHelper.COLUMN_PHONENUMNER+"=?",new String[]{(String)BuddyArray.get(c)});
                //delete data in ArrayList
                BuddyArray.remove(c);
                reflash_Budylist();
                return true;
            }
        });
    }
    private void reflash_Budylist(){
        adapter.notifyDataSetChanged();
    }

    }


