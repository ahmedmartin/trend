package com.example.martin.userresturant;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class table_adapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> food_name;
    private final ArrayList<Double> price;
    private final ArrayList<Integer> count;

    public table_adapter(Activity context, ArrayList<String> food_name, ArrayList<Double> price , ArrayList<Integer> count) {
        super(context, R.layout.table_check_show, food_name);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.food_name=food_name;
        this.price=price;
        this.count=count;
    }

    public View getView(int position, View view, ViewGroup parent) {

        handler hand ;
        if(view==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.table_check_show, null);
            hand = new handler(view);
            view.setTag(hand);
        }else
            hand = (handler) view.getTag();

        hand.getfood_name().setText(food_name.get(position));
        hand.getPrice().setText(""+price.get(position));
        hand.getCount().setText(""+count.get(position));


        return view;
    }


    public class handler {

        private View v;
        private TextView food_name;
        private TextView price;
        private TextView count;



        public handler(View v) {
            this.v = v;
        }

        public TextView getPrice() {
            if(price==null)
                price=v.findViewById(R.id.prices);
            return price;
        }

        public TextView getCount() {
            if(count==null)
                count=(TextView)v.findViewById(R.id.count);
            return count;
        }

        public void setPrice(TextView tv) {
            this.price = tv;
        }

        public void setCount(TextView iv) {
            this.count = iv;
        }


        public TextView getfood_name() {
            if(food_name==null)
                food_name=(TextView)v.findViewById(R.id.food);
            return food_name;
        }

        public void setFood_name(TextView iv) {
            this.food_name = iv;
        }
    }
}
