package vivz.slidenerd.agriculture.navigate;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vivz.slidenerd.agriculture.R;

/**
 * Created by makejin on 2015-09-20.
 */
class List_Adapter_Marker extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Item> data;
    private int layout;
    private Typeface yunGothicFont;
    public List_Adapter_Marker(Context context, int layout, ArrayList<Item> data){
        //윤고딕 폰트
        yunGothicFont = Typeface.createFromAsset(context.getAssets(), "fonts/yungothic330.ttf");
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
    }
    public void updateResults(ArrayList<Item> data){
        this.data = data;
        notifyDataSetChanged();
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public Item getItem(int position){return data.get(position);}
    @Override
    public long getItemId(int position){return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }
        Item listviewitem=data.get(position);

        TextView Title=(TextView)convertView.findViewById(R.id.poiName);
        Title.setTypeface(yunGothicFont);
        Title.setText(listviewitem.getName());
        Title.setTextColor(Color.BLACK);

        TextView subTitle=(TextView)convertView.findViewById(R.id.poiAddr);
        subTitle.setTypeface(yunGothicFont);
        subTitle.setText(listviewitem.getAddr()+ " " + String.format("%.3f", listviewitem.getDistance()/1000.0) + "km");
        subTitle.setTextColor(Color.BLACK);


        return convertView;
    }
}