package com.insajet.ezdine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;

public class QrActivity extends AppCompatActivity {

    ImageView imageView;
    private TextView textView;
    private String restName;

    Bitmap bitmap;
    Intent intent;
    public final static int QRcodeWidth = 500 ;



    private Button btnShareQr;

    private String url,greet,url2;

    private ProgressBar progressBar;
    private Toolbar toolbar;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_qr);

        intent = getIntent();

        String id = intent.getStringExtra("uid");
        restName = intent.getStringExtra("restName");
        url = "https://ezdine.in/pages/menu.html/?uid="+id;
        url2 = "https://yogitej.herokuapp.com/?uid="+id;

        greet = restName+"\nScan the above QR code to access the menu! OR Click the below link\n"+url+"\n\n\n\n TestLink =>" +url2;

        textView = findViewById(R.id.tvrestName);
        imageView = findViewById(R.id.imageview);
        btnShareQr = findViewById(R.id.btnShareQr);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        linearLayout = findViewById(R.id.ll);

        toolbar.setTitle("QR Code");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnShareQr.setOnClickListener(v -> imageShare());
        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
        new PrepareData().execute();




    }

    private Bitmap TextToImageEncodeBitmap(String Value) throws WriterException {

        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black
                        ):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);


        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(restName,canvas.getWidth()/2,20,paint);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }


    private class PrepareData extends AsyncTask<Void, Void, Void> {


        protected Void doInBackground(Void... param) {

            try {
                bitmap = TextToImageEncodeBitmap(url);
                runOnUiThread(()-> imageView.setImageBitmap(bitmap));
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void param) {
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            btnShareQr.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            textView.setText(restName);
        }
    }

    public void imageShare(){
        Uri uri = getBitmapUri(imageView);


        if (uri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT,greet  );
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            Toast.makeText(QrActivity.this, "uri null", Toast.LENGTH_SHORT).show();
        }
    }
    public Uri getBitmapUri(ImageView imageView){
        Bitmap bitmap1 = Bitmap.createBitmap(linearLayout.getWidth(),linearLayout.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap1);
        linearLayout.draw(canvas);

//        Drawable drawable = imageView.getDrawable();
//        Bitmap bitmap = null;
//        if(drawable instanceof BitmapDrawable){
//            bitmap =((BitmapDrawable) imageView.getDrawable()).getBitmap();
//
//        }else{
//            return null;
//        }
        Uri uri  = null;
        try {
            File file = new File(getExternalCacheDir()+"/share.png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap1.compress(Bitmap.CompressFormat.PNG,90,fileOutputStream);
            fileOutputStream.close();
            uri = Uri.fromFile(file);
            Log.d("erorr",uri.toString());
        }catch (Exception e){
            Log.d("errorr",e.toString());
        }
        return uri;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}