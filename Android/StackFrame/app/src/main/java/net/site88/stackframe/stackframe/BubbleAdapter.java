package net.site88.stackframe.stackframe;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Desone on 11/12/2015.
 */
public class BubbleAdapter extends BaseAdapter {

    ArrayList<Message> list = new ArrayList<>();
    Context context;
    private static LayoutInflater inflater = null;

    public BubbleAdapter(Activity mainActivity, ArrayList<Message> messages)
    {
        list = messages;
        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Bubble bubble = new Bubble();
        View row;
        row = inflater.inflate(R.layout.chat_bubble, null);
        bubble.text = (TextView) row.findViewById(R.id.text);
        bubble.date = (TextView) row.findViewById(R.id.date);
        bubble.avatar = (ImageView) row.findViewById(R.id.imageView);
        bubble.text.setText(list.get(position).getText());
        bubble.date.setText(list.get(position).getDate()); //Do date cleaning here
        //bubble.avatar.setImageResource(); //Set image here!
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked a message by: " + list.get(position).getUsername(), Toast.LENGTH_SHORT).show();
            }
        });
        return row;
    }

    public class Bubble
    {
        TextView text;
        TextView date;
        ImageView avatar;
    }
}
