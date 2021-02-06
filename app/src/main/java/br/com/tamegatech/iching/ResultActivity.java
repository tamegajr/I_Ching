package br.com.tamegatech.iching;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.io.InputStream;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Button btnNewPrediction = (Button) findViewById(R.id.btnNewPrediction);

//        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView adView_result = (AdView) findViewById(R.id.adView_result);
        AdRequest adRequest_result = new AdRequest.Builder().build();
        adView_result.loadAd(adRequest_result);

        ImageView img_Prediction = (ImageView) findViewById(R.id.imgPrediction_Prediction);
        ImageView img_Mutation = (ImageView) findViewById(R.id.imgMutation_Prediction);

        TextView txt_Result = (TextView) findViewById(R.id.txtResult_Prediction);
        TextView txt_Mutation = (TextView) findViewById(R.id.txtMutation_Prediction);

        Bundle bundle = getIntent().getBundleExtra("iching");

        if (bundle != null){
            Bundle prediction = bundle.getBundle("prediction");
            Bundle mutation  = bundle.getBundle("mutation");
            long predic_Number = prediction.getLong("number");
            String predic_Description = prediction.getString("description");
            if (predic_Description!=null){
                String predic_Definition = prediction.getString("definition");
                String file_Prediction;
                String predic_final_Result = predic_Definition + "\n" + predic_Description;
                if (predic_Number <10){
                    file_Prediction = "iching_pictures/"+"0"+String.valueOf(predic_Number)+".png";
                }else {
                    file_Prediction = "iching_pictures/"+String.valueOf(predic_Number)+".png";
                }


                InputStream inputStream = null;
                try {
                    inputStream = getAssets().open(file_Prediction);
                /*Drawable d = Drawable.createFromStream(inputStream,null);
                img_Prediction.setImageDrawable(d);*/
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    img_Prediction.setImageBitmap(bitmap);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                txt_Result.setText(predic_final_Result);
                txt_Result.setMovementMethod(new ScrollingMovementMethod());

                long mutation_Number = mutation.getLong("number");
                String mutation_Description = mutation.getString("description");
                String mutation_Definition = mutation.getString("definition");
                String mutation_final_Result = mutation_Definition + "\n" + mutation_Description;
                String file_Mutation;
                if (mutation_Number < 10) {
                    file_Mutation = "iching_pictures/"+ "0" + String.valueOf(mutation_Number)+".png";
                }else{
                    file_Mutation = "iching_pictures/"+ String.valueOf(mutation_Number)+".png";
                }

                /*file_Mutation = "iching_pictures/"+ String.valueOf(mutation_Number)+".png";*/

                try {
                    inputStream = getAssets().open(file_Mutation);
                    Drawable d = Drawable.createFromStream(inputStream,null);
                    if (d!= null){
                        img_Mutation.setImageDrawable(d);
                    }else{
                        img_Mutation.setVisibility(View.GONE);
                    }

                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (txt_Mutation != null){
                    txt_Mutation.setText(mutation_final_Result);
                    txt_Mutation.setMovementMethod(new ScrollingMovementMethod());
                }else{
                    txt_Mutation.setVisibility(View.GONE);
                }
            } else{
                img_Mutation.setVisibility(View.GONE);
                img_Prediction.setVisibility(View.GONE);
                txt_Mutation.setVisibility(View.GONE);
                txt_Result.setText(R.string.quick_pressing_warning);
//                txt_Result.setVisibility(View.GONE);
            }




        }else {
            if (img_Mutation == null || img_Prediction == null){
                img_Mutation.setVisibility(View.GONE);
                img_Prediction.setVisibility(View.GONE);
                txt_Mutation.setVisibility(View.GONE);
                txt_Result.setVisibility(View.GONE);
            }

        }

        btnNewPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}