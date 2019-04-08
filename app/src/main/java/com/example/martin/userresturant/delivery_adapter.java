package com.example.martin.userresturant;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class delivery_adapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList <String> resturants;
    private final ArrayList<String> food_name;
    private final ArrayList<Double> price;
    private final ArrayList<Integer> count;

    public delivery_adapter(Activity context, ArrayList<String> food_name, ArrayList<Double> price , ArrayList<Integer> count, ArrayList <String> resturants) {
        super(context, R.layout.delivery_check_show, food_name);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.food_name=food_name;
        this.price=price;
        this.count=count;
        this.resturants =resturants;
    }

    public View getView(int position, View view, ViewGroup parent) {

        handler hand ;
       // LayoutInflater inflater=context.getLayoutInflater();
        if(view==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.delivery_check_show, null);
            hand =new handler(view);
            view.setTag(hand);
        }else
            hand = (handler) view.getTag();

        hand.getfood_name().setText(food_name.get(position));
        hand.getPrice().setText(""+price.get(position));
        hand.getCount().setText(""+count.get(position));
        hand.getresturant_name().setText(resturants.get(position));

        return view;
    }


    public class handler {

        private View v;
        private TextView resturant_name;
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



        public TextView getresturant_name() {
            if(resturant_name==null)
                resturant_name=v.findViewById(R.id.resturant_name);
            return resturant_name;
        }

        public TextView getfood_name() {
            if(food_name==null)
                food_name=(TextView)v.findViewById(R.id.food);
            return food_name;
        }

        public void setResturant_name(TextView tv) {
            this.resturant_name = tv;
        }

        public void setFood_name(TextView iv) {
            this.food_name = iv;
        }
    }
}
