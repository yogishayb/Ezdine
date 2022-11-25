package com.insajet.ezdine.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.insajet.ezdine.Attri;
import com.insajet.ezdine.ProfileActivity;
import com.insajet.ezdine.R;
import com.insajet.ezdine.SendOTPActivity;
import com.insajet.ezdine.model.ProfileModel;

import java.util.ArrayList;
import java.util.Locale;

public class ProfileAdapter extends ArrayAdapter<ProfileModel> implements Attri {

    private ArrayList<ProfileModel> profileModels;
    private Context context;
    private SharedPreferences.Editor restInfoEditor, restEditor, categoryCountEditor;
    private SharedPreferences restInfoPreferences,sharedPreferences,categoryCountPref;
    ArrayList<String> keyNames;
    DatabaseReference reference,databaseReference;
    RelativeLayout relativeLayout;
    String value;
    Dialog dialog;

    public static final int viewTypeNormal = 0;
    public static final int viewTypeSignout = 1;


    public ProfileAdapter(@NonNull Context context, ArrayList<ProfileModel> profileModels, SharedPreferences.Editor restInfoEditor, SharedPreferences.Editor restEditor, SharedPreferences.Editor categoryCountEditor, SharedPreferences restInfoPreferences, SharedPreferences sharedPreferences, SharedPreferences categoryCountPref, RelativeLayout relativeLayout,DatabaseReference reference) {
        super(context, R.layout.profile_list_item);
        this.context = context;
        this.profileModels = profileModels;
        this.restInfoEditor = restInfoEditor;
        this.restEditor = restEditor;
        this.categoryCountEditor = categoryCountEditor;
        this.restInfoPreferences = restInfoPreferences;
        this.sharedPreferences = sharedPreferences;
        this.categoryCountPref = categoryCountPref;
        this.relativeLayout = relativeLayout;
        this.reference = reference;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return profileModels.get(position).getType();
    }

    @Override
    public int getCount() {
        return profileModels.size();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder = null;
        ProfileModel profileModel = profileModels.get(position);
        int listViewItemType = getItemViewType(position);

        if(convertView==null){
            viewHolder = new ViewHolder();
            if(listViewItemType ==viewTypeNormal ){
                convertView = LayoutInflater.from(context).inflate(R.layout.profile_list_item,parent, false);
                if(viewHolder.tvValue == null){
                    viewHolder.tvLabel  = convertView.findViewById(R.id.tvLabel);
                    viewHolder.tvValue = convertView.findViewById(R.id.tvValue);
                    viewHolder.imageView = convertView.findViewById(R.id.iv);
                    viewHolder.viewLine = convertView.findViewById(R.id.viewLine);
                    viewHolder.ll = convertView.findViewById(R.id.ll);
                }

            }else if (listViewItemType == viewTypeSignout){
                convertView = LayoutInflater.from(context).inflate(R.layout.sign_out_item,parent, false);
                if (viewHolder.tvSignOut == null){
                    viewHolder.tvSignOut = convertView.findViewById(R.id.btnSignOut);
                }

            }
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }


        if(position!=7){
            if(position==profileModels.size()-1){
                viewHolder.viewLine.setVisibility(View.GONE);
            }
            viewHolder.tvLabel.setText(profileModels.get(position).getLabel());
            if(profileModels.get(position).getValue().equals("0")){
                viewHolder.tvValue.setText("");
            }else {
                viewHolder.tvValue.setText(profileModels.get(position).getValue());
            }

            if(profileModels.get(position).getImageId()==1){
                viewHolder.imageView.setBackgroundResource(R.drawable.ic_launcher_foreground);
            }else {
                viewHolder.imageView.setBackgroundResource(profileModels.get(position).getImageId());
            }

            ViewHolder finalViewHolder = viewHolder;
            convertView.setOnClickListener(v -> {
                if(position!=7){
                    if(position==0){
                        Toast.makeText(context, "ID cant be changed", Toast.LENGTH_SHORT).show();
                    }else if(position==1){
                        dialogv(position,finalViewHolder,"ownerName",reference.child("ownerName"));
                    }else if(position==2){
                        dialogv(position,finalViewHolder,"restName",reference.child("restName"));
                    }else if (position==3){
                        Toast.makeText(context, "Number cant be changed", Toast.LENGTH_SHORT).show();
                    }else if(position==4){
                        dialogv(position,finalViewHolder,"email",reference.child("email"));
                    }else if (position==5){
                        dialogv(position,finalViewHolder,"website",reference.child("website"));
                    }else if (position==6){
                        dialogv(position,finalViewHolder,"address",reference.child("address"));
                    }
                }


            });
        }


        if(position==7){

            viewHolder.tvSignOut.setOnClickListener(v -> {
                Dialog dialog = new Dialog(context);
                this.dialog = dialog;
                View dView = LayoutInflater.from(context).inflate(R.layout.dialog_user_sign_out,relativeLayout,false);
                Button btnYes = dView.findViewById(R.id.btnYes);
                Button btnNo = dView.findViewById(R.id.btnNo);
                TextView textView = dView.findViewById(R.id.tvrusure);
                dialog.setContentView(dView);
                dialog.getWindow().getAttributes().windowAnimations = R.style.dia;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();

                textView.setText(String.format(Locale.ENGLISH,"%s %s","Are you sure ? ",profileModels.get(1).getValue()));

                btnYes.setOnClickListener(v12 -> {
                    FirebaseAuth.getInstance().signOut();
                    restEditor.clear();
                    restEditor.apply();
                    restInfoEditor.clear();
                    restInfoEditor.apply();
                    categoryCountEditor.clear();
                    categoryCountEditor.apply();
                    Intent intent = new Intent(context, SendOTPActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    dialog.dismiss();
                    context.startActivity(intent);
                });
                btnNo.setOnClickListener(v1 -> dialog.dismiss());
            });
        }

        return convertView;



    }


    static class ViewHolder{
        TextView tvLabel=null, tvValue=null;
        ImageView imageView;
        View viewLine;
        LinearLayout ll;
        Button tvSignOut=null;
    }

    void dialogv(int position, ViewHolder finalViewHolder, String labelname, DatabaseReference databaseReference){
        this.databaseReference = databaseReference;
        Dialog dialog = new Dialog(context);
        this.dialog = dialog;
        View dView = LayoutInflater.from(context).inflate(R.layout.dialog_update_profile_item,relativeLayout,false);
        TextView tvLabel = dView.findViewById(R.id.tvLabel);
        EditText etValue = dView.findViewById(R.id.etValue);
        Button btnUpdate = dView.findViewById(R.id.btnUpdate);
        dialog.setContentView(dView);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dia;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
        if(profileModels.get(position).getValue().equals("0")){
            etValue.setText("");
            etValue.setHint("");
        }else {
            etValue.setText(profileModels.get(position).getValue());
            etValue.setHint(profileModels.get(position).getValue());
        }
        tvLabel.setText(profileModels.get(position).getLabel());
        btnUpdate.setOnClickListener(v -> {
            String value = etValue.getText().toString().trim();{
                if(value.isEmpty()){
                    Toast.makeText(context, "Input cant be Empty", Toast.LENGTH_SHORT).show();
                }else{
                    btnUpdate.setText("Wait");
                    value = String.format(Locale.ENGLISH,"%s",value);
                    finalViewHolder.tvValue.setText(value);
                    new Loaddata().execute(value,labelname);
                }
            }
        });
    }


    public class Loaddata extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {
            
            restInfoEditor.putString(strings[1],strings[0]);
            restEditor.putString(strings[1],strings[0]);
            restInfoEditor.apply();
            restEditor.apply();
            databaseReference.setValue(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            ProfileActivity.getInstance().listUpdate();

        }
    }


}
