package com.example.martin.userresturant;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class handler {

    private View v;
    private TextView tv;
    private ImageView iv;



    public handler(View v) {
        this.v = v;
    }

    public TextView getTv() {
        if(tv==null)
            tv=v.findViewById(R.id.resturant_name);
        return tv;
    }

    public ImageView getIv() {
        if(iv==null)
            iv=v.findViewById(R.id.resturant_img);
        return iv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }
}
