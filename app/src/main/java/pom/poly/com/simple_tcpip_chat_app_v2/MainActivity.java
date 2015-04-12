package pom.poly.com.simple_tcpip_chat_app_v2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private EditText etPhone;
    private Button btReg;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etPhone=(EditText)findViewById(R.id.etPhoneNumber);
        btReg=(Button)findViewById(R.id.btReg);
        btReg.setOnClickListener(this);
        sp=getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
        Config.SERVER_IP = sp.getString(Config.SP_KEY_SERVER_IP, Config.SERVER_IP);
        if(sp.contains(Config.SP_PHONE_KEY)){
            gotoChatList();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //Save the phone number of the user at the first time
        SharedPreferences.Editor ed=sp.edit();
        ed.putString(Config.SP_PHONE_KEY,etPhone.getText().toString());
        ed.commit();
        gotoChatList();
    }

    private void gotoChatList(){
        Intent intent=new Intent(this,ChatListActivity.class) ;
        startActivity(intent);

    }
}
