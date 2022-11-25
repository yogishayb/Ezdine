package com.insajet.ezdine.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.insajet.ezdine.ItemlistActivity;
import com.insajet.ezdine.R;
import com.insajet.ezdine.model.Item;

import java.util.List;
import java.util.Locale;

public class ItemListAdapter  extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    Context context;
    List<Item> itemList;
    DatabaseReference reference;

    public ItemListAdapter(Context context, List<Item> itemList, DatabaseReference reference){
        this.context = context;
        this.itemList = itemList;
        this.reference = reference;
    }


    @NonNull
    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View convertView = layoutInflater.inflate(R.layout.itemlist_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ViewHolder holder, int position) {
        final Item item = itemList.get(position);
        holder.etName.setText(item.getItemName());
        holder.etPrice.setText(String.format("Rs. %s", item.getItemPrice()));
        if(item.getItemDescri().equals("0")){
            holder.tvDescri.setVisibility(View.GONE)  ;
        }else{
            holder.tvDescri.setText(item.getItemDescri());
        }

        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(context);
                View dView = LayoutInflater.from(context).inflate(R.layout.dialog_category_options, (ViewGroup) v.getParent(),false);
                Button btnEdit = dView.findViewById(R.id.btnEdit),btnDelete = dView.findViewById(R.id.btnDelete);
                TextView tvItemName = dView.findViewById(R.id.tvItemName);
                tvItemName.setText(itemList.get(position).getItemName());
                dialog.setContentView(dView);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
                dialog.getWindow().getAttributes().windowAnimations = R.style.dia;
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.child(itemList.get(position).itemId).removeValue();

                        dialog.dismiss();
                    }
                });

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Dialog dialog1 = new Dialog(context);
                        View dview1 = LayoutInflater.from(context).inflate(R.layout.add_alert, (ViewGroup) v.getParent(),false);
                        EditText etItemName = dview1.findViewById(R.id.etItemName), etItemPrice = dview1.findViewById(R.id.etItemPrice), etDescri = dview1.findViewById(R.id.tvDescri);
                        Button btnSave = dview1.findViewById(R.id.btnAdd);
                        dialog1.setContentView(dview1);
                        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
                        dialog1.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        dialog1.getWindow().setGravity(Gravity.BOTTOM);
                        dialog1.getWindow().getAttributes().windowAnimations = R.style.dia;
                        dialog1.show();
                        etItemName.setText(itemList.get(position).getItemName());
                        etItemPrice.setText(itemList.get(position).getItemPrice());
                        etItemName.setHint(itemList.get(position).getItemName());
                        etItemPrice.setHint(itemList.get(position).getItemPrice());
                        if(!itemList.get(position).getItemDescri().equals("0")){
                            etDescri.setText(itemList.get(position).getItemDescri());
                            etDescri.setHint(itemList.get(position).getItemDescri());
                        }

                        btnSave.setText(String.format(Locale.ENGLISH, "%s","Save"));
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String itemName = etItemName.getText().toString().trim(), itemPrice = etItemPrice.getText().toString().trim(), descr = etDescri.getText().toString().trim();
                                if(itemName.isEmpty() || itemPrice.isEmpty()){
                                    Toast.makeText(context, "Input cant be Empty", Toast.LENGTH_SHORT).show();
                                }else {
                                    if(descr.isEmpty()){
                                        descr = "0";
                                    }
                                    btnSave.setText("Wait");
                                    Item item = new Item(itemList.get(position).getItemId(),itemName,itemPrice,descr);
                                    reference.child(itemList.get(position).getItemId()).setValue(item);
                                    btnSave.setText("Save");
                                    dialog1.dismiss();
                                }
                            }
                        });

                    }
                });
                dialog.show();

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        AppCompatTextView etName;
        TextView etPrice, tvDescri;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etName = itemView.findViewById(R.id.tvItemName);
            etPrice = itemView.findViewById(R.id.tvItemPrice);
            tvDescri = itemView.findViewById(R.id.tvdescri);
            relativeLayout = itemView.findViewById(R.id.rl);
        }
    }
}
