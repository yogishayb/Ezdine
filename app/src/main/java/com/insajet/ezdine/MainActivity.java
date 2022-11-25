package com.insajet.ezdine;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.insajet.ezdine.model.CategoryModel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Attri {

    private DatabaseReference categoryReference,itemReference;
    private SharedPreferences sharedPreferences,restInfoPreferences  ;

    private String uid;
    private String restName;

    ArrayList<CategoryModel> categoryList;
    private ListView listView;
    private ProgressBar progressBar;


    Dialog alertDialog;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myyyy","created");

        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("restaurant",MODE_PRIVATE);

        restInfoPreferences = getSharedPreferences(restInfoPrefName,MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phoneNumber","0");
        uid = sharedPreferences.getString("uid","0");
        restName = restInfoPreferences.getString("restName","d");
        categoryList = new ArrayList<>();


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("EzDine");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_sharp_more_vert_24_white));
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.lv);
        progressBar = findViewById(R.id.progressBar);
        Button btnAddCategory = findViewById(R.id.btnAdd);


        categoryReference = FirebaseDatabase.getInstance().getReference().child("restaurants").child(uid).child("category");
        itemReference = FirebaseDatabase.getInstance().getReference().child("restaurants").child(uid).child("items");


        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);


        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent  = new Intent(MainActivity.this,ItemlistActivity.class);


            intent.putExtra("id",categoryList.get(position).getId());
            intent.putExtra("categoryName",categoryList.get(position).getCategoryName());
            intent.putExtra("uid",uid);
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Dialog dialog = new Dialog(MainActivity.this);
            View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_category_options,parent,false);
            Button btnEdit = dialogView.findViewById(R.id.btnEdit),btnDelete = dialogView.findViewById(R.id.btnDelete);
            TextView tvItemNAme = dialogView.findViewById(R.id.tvItemName);
            tvItemNAme.setText(String.format(Locale.ENGLISH,"%s",categoryList.get(position).getCategoryName()));
            dialog.setContentView(dialogView);

            btnEdit.setOnClickListener(v -> {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                Dialog dialog1 = new Dialog(MainActivity.this);
                View dialogView1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_edit,parent,false);
                dialog1.setContentView(dialogView1);
                dialog1.getWindow().getAttributes().windowAnimations = R.style.dia;
                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
                dialog1.getWindow().setGravity(Gravity.BOTTOM);
                dialog1.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                dialog1.show();
                EditText etValue = dialogView1.findViewById(R.id.etValue);
                etValue.requestFocus();
                etValue.setOnFocusChangeListener((v1, hasFocus) -> {
                    if(hasFocus){
                        etValue.setSelection(etValue.getText().length());
                    }
                });
                Button btnUpdate = dialogView1.findViewById(R.id.btnUpdate);
                etValue.setText(categoryList.get(position).getCategoryName());
                etValue.setHint(categoryList.get(position).getCategoryName());

                btnUpdate.setOnClickListener(v12 -> {
                    String value = etValue.getText().toString().trim();
                    categoryReference.child(categoryList.get(position).getId()).child("categoryName").setValue(value);
                    dialog1.dismiss();
                });

            });

            btnDelete.setOnClickListener(v -> {
                categoryReference.child(categoryList.get(position).getId()).removeValue();
                itemReference.child(categoryList.get(position).getId()).removeValue();
                dialog.dismiss();
            });
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
            dialog.getWindow().getAttributes().windowAnimations = R.style.dia;
            dialog.show();
            return true;
        });





        //add--category--btn--onclick
        btnAddCategory.setOnClickListener(v -> {
            alertDialog = new Dialog(MainActivity.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogview = layoutInflater.inflate(R.layout.dialog_add_category_item, relativeLayout,false);
            alertDialog.setContentView(dialogview);

            EditText etCategoryName = dialogview.findViewById(R.id.etCategoryName);
            Button btnAdd = dialogview.findViewById(R.id.btnAddCategory);

            etCategoryName.requestFocus();

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(etCategoryName,InputMethodManager.SHOW_IMPLICIT);


            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            alertDialog.show();
            Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.dia;
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00ffffff ));
            alertDialog.getWindow().setGravity(Gravity.BOTTOM);
            alertDialog.getWindow().setDimAmount(0.5f);
            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            alertDialog.setCanceledOnTouchOutside(true);


            btnAdd.setOnClickListener(vi -> {
                String categoryName = etCategoryName.getText().toString().trim();

                if (!TextUtils.isEmpty(categoryName)) {
                    btnAdd.setEnabled(false);
                    btnAdd.setText("Wait");
                    String id = categoryReference.push().getKey();
                    CategoryModel item = new CategoryModel(id, categoryName,"0");
                    assert id != null;
                    categoryReference.child(id).setValue(item).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            etCategoryName.setText("");
                            etCategoryName.requestFocus();
                            btnAdd.setEnabled(true);
                            btnAdd.setText("Add");
                            Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Enter category name", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.qrCode:
                new Thread(() -> {
                    Intent intent1 = new Intent(MainActivity.this,QrActivity.class);
                    intent1.putExtra("uid",uid);
                    intent1.putExtra("restName",restName);
                    startActivity(intent1);
                }).start();
                return true;
            case R.id.profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra("uid",uid));
                return true;
            case R.id.aboutus:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                return true;
            case R.id.invite:
                invite();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(() -> categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryModel item = dataSnapshot.getValue(CategoryModel.class);
                    if (item != null){
                        if(item.getCategoryName() !=null & item.getId()!=null & item.getItemCount()!=null){
                            categoryList.add(item);

                        }
                    }

                }
                CategoryListAdapter myAdapter = new CategoryListAdapter(MainActivity.this, categoryList,relativeLayout,categoryReference, itemReference);
                listView.setAdapter(myAdapter);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        })).start();
    }

    void invite(){
        String url= "https://play.google.com/store/apps/details?id=com.virtualminds.chattimes";
        String invitation = "Hey! Looking for a digital Dine-in experience for your Restaurants/Cafes?\n" +
                "Download our app &  create your own Menu QR Code for Free!\n";
        String data = String.format(Locale.ENGLISH,"%s %s",invitation,url);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,data);
        startActivity(Intent.createChooser(intent,"Share Link"));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("myyyy","restarted");

    }
}
