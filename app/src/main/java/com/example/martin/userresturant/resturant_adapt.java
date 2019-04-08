package com.example.martin.userresturant;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class resturant_adapt extends ArrayAdapter {
    Activity conext;
    ArrayList <String> name;
    ArrayList <Uri> image;

    public resturant_adapt( Activity context, ArrayList <String> name,ArrayList <Uri> image) {
        super(context, R.layout.resturant_show, image);

        this.image=image;
        this.name =name;
        this.conext=context;
    }


    public View getView(int position, View v, ViewGroup parent) {
        View view =v;
        handler hand ;

        if(view ==null){
            LayoutInflater inflater = (LayoutInflater) conext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view =inflater.inflate(R.layout.resturant_show,parent,false);
            hand =new handler(view);
            view.setTag(hand);
        }else
            hand = (handler) view.getTag();

        hand.getTv().setText(name.get(position));
        Picasso.with(conext).load(image.get(position)).into(hand.getIv());
        return view;

    }
}

