package com.example.martin.userresturant;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> sizes;
    private final ArrayList<Double> prices;
    public static String size;
    public static Double price;

    public CustomListAdapter(Activity context, ArrayList<String> sizes, ArrayList<Double> prices) {
        super(context, R.layout.size_show, sizes);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.sizes = sizes;
        this.prices = prices;
    }


    handler hand ;
    public View getView(final int position, View view, final ViewGroup parent) {


        if(view==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.size_show, null);
            hand =new handler(view);
            view.setTag(hand);
        }
        hand = (handler) view.getTag();


        hand.getsize().setText(sizes.get(position));
        hand.getprice().setText(""+ prices.get(position));
        hand.getradio().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<sizes.size();i++){
                    RadioButton rb =  parent.getChildAt(i).findViewById(R.id.radioButton);
                    if(i!=position){
                        rb.setChecked(false);
                    }else {
                        rb.setChecked(true);
                        size = sizes.get(position);
                        price = prices.get(position);
                    }
                }

            }
        });
       /* if(hand.getradio().isChecked()){

        }*/
        return view;


    }

    public class handler {

        private View v;
        private TextView size;
        private TextView price;
        private RadioButton radioButton;



        public handler(View v) {
            this.v = v;
        }

        public TextView getsize() {
            if(size ==null)
                size =v.findViewById(R.id.size);
            return size;
        }

        public TextView getprice() {
            if(price ==null)
                price =v.findViewById(R.id.prices);
            return price;
        }

        public void setsize(TextView tv) {
            this.size = tv;
        }

        public void setprice(TextView iv) {
            this.price = iv;
        }

        public RadioButton getradio(){
            if(radioButton==null)
                radioButton=v.findViewById(R.id.radioButton);
                return radioButton;
        }

        public void setRadioButton(RadioButton rb){ this.radioButton = rb;}

    }
}
