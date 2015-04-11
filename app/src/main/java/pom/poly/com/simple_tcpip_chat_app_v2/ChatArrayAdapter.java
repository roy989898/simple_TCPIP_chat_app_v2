package pom.poly.com.simple_tcpip_chat_app_v2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatArrayAdapter extends ArrayAdapter {

    private TextView chatText;
    private ArrayList chatMessageList;
    private LinearLayout singleMessageContainer;


    public ChatArrayAdapter(Context context, int textViewResourceId, ArrayList chtaHistoryArray) {
        super(context, textViewResourceId);
        chatMessageList = chtaHistoryArray;

    }

    public void add(String object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public String getItem(int index) {
        return (String) this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        String chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(chatMessageObj.toString());
        //chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a : R.drawable.bubble_b);
        chatText.setBackgroundResource(R.drawable.bubble_a);
        singleMessageContainer.setGravity(Gravity.LEFT);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}