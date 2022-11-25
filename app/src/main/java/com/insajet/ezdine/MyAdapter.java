package com.insajet.ezdine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.insajet.ezdine.model.Item;
import java.util.List;

public class MyAdapter extends ArrayAdapter<Item> {

    Context context;
    List<Item> itemList;

    MyAdapter(Context context, List<Item> itemList){
        super(context, R.layout.listitem_layout,itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.listitem_layout,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.etName = convertView.findViewById(R.id.tvItemName);
            viewHolder.etPrice = convertView.findViewById(R.id.tvItemPrice);
            viewHolder.tvDescri = convertView.findViewById(R.id.tvdescri);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Item item = itemList.get(position);

        viewHolder.etName.setText(item.getItemName());
        viewHolder.etPrice.setText("Rs. "+item.getItemPrice());
        if(item.getItemDescri().equals("0")){
            viewHolder.tvDescri.setVisibility(View.INVISIBLE)  ;
        }else{
            viewHolder.tvDescri.setText(item.getItemDescri());
        }
        return convertView;
    }



    static class ViewHolder{
        AppCompatTextView etName;
        TextView etPrice, tvDescri;
    }



}

