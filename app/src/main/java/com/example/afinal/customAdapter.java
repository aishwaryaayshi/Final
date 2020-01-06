package com.example.afinal;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class customAdapter extends BaseAdapter {
    Context context;
    //String[]id;
   // String[]text;
    ArrayList<String>id=new ArrayList<>();
    ArrayList<String>text=new ArrayList<>();
    ArrayList<Integer>image=new ArrayList<>();
    //String[]time;
    LayoutInflater inflater;
    customAdapter(Context context,ArrayList<Integer>image, ArrayList<String>id, ArrayList<String>text){
        this.context=context;
        this.id=id;
        this.text=text;
        this.image=image;

    }

    @Override
    public int getCount() {
        return id.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.sample_layout,parent,false);
        }
        //ImageView imageView=convertView.findViewById(R.id.imgView);
        ImageView imageView=convertView.findViewById(R.id.image);
        TextView nameText=convertView.findViewById(R.id.name);
        TextView postText=convertView.findViewById(R.id.post);
        //TextView timeText=convertView.findViewById(R.id.time);
        imageView.setImageResource(image.get(position));
        nameText.setText(id.get(position));
        postText.setText(text.get(position));
       //timeText.setText(time[position]);

        return convertView;
    }
}
