package org.andriodtown.jelly;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2019-03-22.
 */

class MainHorizonAdapter extends RecyclerView.Adapter<MainHorizonAdapter.Holder>{

    Context context;
    ArrayList<String> data = new ArrayList<>();

    public MainHorizonAdapter(Context context, ArrayList<String> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public MainHorizonAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item,null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(MainHorizonAdapter.Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView text1;
        TextView text2;
        TextView text3;
        TextView text4;
        TextView text5;

        public Holder(View view) {
            super(view);
            text1 = view.findViewById(R.id.text1);
            text1.setTextColor(Color.WHITE);
            text2 = view.findViewById(R.id.text2);
            text3 = view.findViewById(R.id.text3);
            text4 = view.findViewById(R.id.text4);
            text5 = view.findViewById(R.id.text5);

            text1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    text1.setTextColor(Color.WHITE);
                    text2.setTextColor(Color.BLACK);
                    text3.setTextColor(Color.BLACK);
                    text4.setTextColor(Color.BLACK);
                    text5.setTextColor(Color.BLACK);
                }
            });
            text2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    text1.setTextColor(Color.BLACK);
                    text2.setTextColor(Color.WHITE);
                    text3.setTextColor(Color.BLACK);
                    text4.setTextColor(Color.BLACK);
                    text5.setTextColor(Color.BLACK);
                }
            });
            text3.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    text1.setTextColor(Color.BLACK);
                    text2.setTextColor(Color.BLACK);
                    text3.setTextColor(Color.WHITE);
                    text4.setTextColor(Color.BLACK);
                    text5.setTextColor(Color.BLACK);
                }
            });
            text4.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    text1.setTextColor(Color.BLACK);
                    text2.setTextColor(Color.BLACK);
                    text3.setTextColor(Color.BLACK);
                    text4.setTextColor(Color.WHITE);
                    text5.setTextColor(Color.BLACK);
                }
            });
            text5.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    text1.setTextColor(Color.BLACK);
                    text2.setTextColor(Color.BLACK);
                    text3.setTextColor(Color.BLACK);
                    text4.setTextColor(Color.BLACK);
                    text5.setTextColor(Color.WHITE);
                }
            });
        }
    }
}
