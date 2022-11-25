package com.insajet.ezdine;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.insajet.ezdine.adapters.ItemListAdapter;
import com.insajet.ezdine.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemlistActivity extends AppCompatActivity {
    Intent intent;

    DatabaseReference reference,catCountReference;
    private List<Item> itemList;


    private RecyclerView listView;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private TextView tvEmpty;

    public static ItemlistActivity instance;

    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemlist);
        instance = this;
        intent  = getIntent();
        String categoryId = intent.getStringExtra("id");
        String categoryName = intent.getStringExtra("categoryName");
        String uid = intent.getStringExtra("uid");
        itemList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("restaurants").child(uid).child("items").child(categoryId);
        catCountReference = FirebaseDatabase.getInstance().getReference().child("restaurants").child(uid).child("category").child(categoryId).child("itemCount");



        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty  = findViewById(R.id.tvEmpty);
        Button buttonAdd = findViewById(R.id.btnAdd);
        relativeLayout = findViewById(R.id.relativateLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setTitle(categoryName);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);



        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);






        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new Thread(() ->{

                    }
                ).start();
                dialog = new Dialog(ItemlistActivity.this);
                LayoutInflater layoutInflater   = LayoutInflater.from(getApplicationContext());

                View view = layoutInflater.inflate(R.layout.add_alert,relativeLayout,false);
                EditText etItemName = view.findViewById(R.id.etItemName),etItemPrice = view.findViewById(R.id.etItemPrice),etDescri = view.findViewById(R.id.tvDescri);

                Button buttonAddToMenu  = view.findViewById(R.id.btnAdd);
                buttonAddToMenu.setOnClickListener(v1 -> {

                        String itemname = etItemName.getText().toString().trim();
                        String itemprice = etItemPrice.getText().toString().trim();
                        String descri = etDescri.getText().toString().trim();
                        if(descri.isEmpty()){
                            descri="0";
                        }
                        if(itemname.isEmpty()){
                            Toast.makeText(ItemlistActivity.this, "Enter item name", Toast.LENGTH_SHORT).show();
                        }else if(itemprice.isEmpty()){
                            Toast.makeText(ItemlistActivity.this, "Enter item price", Toast.LENGTH_SHORT).show();
                        }else {
                            buttonAddToMenu.setText("Wait");
                            buttonAddToMenu.setEnabled(false);
                            String finalDescri = descri;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String itemid = reference.push().getKey();
                                    Item item = new Item(itemid,itemname,itemprice, finalDescri);
                                    assert itemid != null;
                                    reference.child(itemid).setValue(item).addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            runOnUiThread(() -> {
                                                Toast.makeText(ItemlistActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                                buttonAddToMenu.setText("Add");
                                                buttonAddToMenu.setEnabled(true);
                                                etItemName.setText("");
                                                etItemPrice.setText("");
                                                etDescri.setText("");
                                                etItemName.requestFocus();
                                            });

                                        }
                                    });
                                }
                            }).start();

                        }

                });
                dialog.setContentView(view);

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00ffffff));
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.dia;
                        dialog.getWindow().setDimAmount(0.5f);


                dialog.show();
            }
        });

    }
    public static ItemlistActivity getInstance(){
        return instance;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChildEventListener eventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        itemList.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Item item = dataSnapshot.getValue(Item.class);
                             itemList.add(item);
                        }


                        ItemListAdapter myAdapter = new ItemListAdapter(ItemlistActivity.this,itemList, reference);
                        listView.setHasFixedSize(true);
                        listView.setLayoutManager(new LinearLayoutManager(ItemlistActivity.this));
                        listView.setAdapter(myAdapter);
                        catCountReference.setValue(String.valueOf(itemList.size()));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                if(itemList.isEmpty()){
                                    tvEmpty.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.GONE);
                                }else {
                                    tvEmpty.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);


                                }
                            }
                        });

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemList.clear();
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Item item = dataSnapshot.getValue(Item.class);
                            itemList.add(item);
                        }


                        ItemListAdapter myAdapter = new ItemListAdapter(ItemlistActivity.this,itemList, reference);
                        listView.setHasFixedSize(true);
                        listView.setLayoutManager(new LinearLayoutManager(ItemlistActivity.this));
                        listView.setAdapter(myAdapter);
                        catCountReference.setValue(String.valueOf(itemList.size()));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                if(itemList.isEmpty()){
                                    tvEmpty.setVisibility(View.VISIBLE);
                                    listView.setVisibility(View.GONE);
                                }else {
                                    tvEmpty.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);


                                }
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                reference.addValueEventListener(valueEventListener);
              // reference.removeEventListener(valueEventListener);

                //reference.addChildEventListener(eventListener);
            }
        }).start();

    }


}