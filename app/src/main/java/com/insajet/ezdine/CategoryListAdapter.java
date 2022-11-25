package com.insajet.ezdine;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.insajet.ezdine.model.CategoryModel;

import java.util.ArrayList;
import java.util.Locale;

public class CategoryListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<CategoryModel> categoryList;
    private final DatabaseReference categoryReference,itemReference;
    private final ViewGroup viewGroup;



    public CategoryListAdapter(Context context,ArrayList<CategoryModel> categoryList,ViewGroup viewGroup,DatabaseReference categoryReference, DatabaseReference itemReference){
        super(context,R.layout.listitem_category);
        this.context = context;
        this.categoryList = categoryList;
        this.categoryReference = categoryReference;
        this.viewGroup = viewGroup;
        this.itemReference = itemReference;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.listitem_category,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.tvCategoryItem);
            viewHolder.tvValue = convertView.findViewById(R.id.tvValue);
            viewHolder.viewLine = convertView.findViewById(R.id.viewLine);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String count = "0";
        if(position==categoryList.size()-1){
            viewHolder.viewLine.setVisibility(View.GONE);
        }
        viewHolder.textView.setText(String.format("%s. %s",position+1,categoryList.get(position).getCategoryName()));
        viewHolder.tvValue.setText(categoryList.get(position).getItemCount());


        return convertView;
    }

    static class ViewHolder{
        TextView textView, tvValue;

        View viewLine;
    }
}
