package br.com.tamegatech.iching;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class ResultActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    private ImageButton imbtnMusicOff;
    private boolean music_off;
    private MediaPlayer backgroundMP;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*DisplayMetrics metrics = getResources().getDisplayMetrics();
        Toast.makeText(getApplicationContext(),String.valueOf(metrics.densityDpi),Toast.LENGTH_LONG).show();*/
        setContentView(R.layout.activity_result);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// Força somente a orientação retrato
        Button btnNewPrediction = (Button) findViewById(R.id.btnNewPrediction);


        ImageView img_Prediction = (ImageView) findViewById(R.id.imgPrediction_Prediction);
        ImageView img_Mutation = (ImageView) findViewById(R.id.imgMutation_Prediction);

        TextView txt_Result = (TextView) findViewById(R.id.txtResult_Prediction);
        TextView txt_Mutation = (TextView) findViewById(R.id.txtMutation_Prediction);

        Bundle bundle = getIntent().getBundleExtra("iching");
        AdRequest adRequest = new AdRequest.Builder().build();
        myInterstitial(adRequest);
        preferences = getSharedPreferences("iching_preferences", Context.MODE_PRIVATE);

        music_off = preferences.getBoolean("music",false);

//        music_off  = bundle.getBoolean("music_mode");

//        Toast.makeText(getApplicationContext(),"Music Off: " +String.valueOf(music_off),Toast.LENGTH_LONG).show();

        imbtnMusicOff = (ImageButton) findViewById(R.id.imbtnMusicOff);
        backgroundMP = MediaPlayer.create(getApplicationContext(),R.raw.bensound_relaxing);
//        backgroundMP.stop();
        if (music_off){
            if (backgroundMP!=null){
                backgroundMP.stop();
                imbtnMusicOff.setImageResource(R.drawable.music_note);
            }
        }else {
            imbtnMusicOff.setImageResource(R.drawable.music_note_off);
            backgroundMP.start();
        }

        imbtnMusicOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (music_off){
                    music_off = false;
                    preferences.edit().putBoolean("music",false).apply();
                    imbtnMusicOff.setImageResource(R.drawable.music_note_off);
                    backgroundMP = MediaPlayer.create(getApplicationContext(),R.raw.bensound_relaxing);
                    backgroundMP.start();
                }else{
                    music_off = true;
                    preferences.edit().putBoolean("music",true).apply();
                    imbtnMusicOff.setImageResource(R.drawable.music_note);
                    if (backgroundMP !=null){
                        backgroundMP.stop();
                    }
                }

            }
        });

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
                txt_Result.setScrollbarFadingEnabled(false);
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
                    txt_Mutation.setScrollbarFadingEnabled(false);
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
                MobileAds.initialize(getApplicationContext(), new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

                    }
                });

                if (mInterstitialAd!=null){
                    mInterstitialAd.show(ResultActivity.this);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("music_mode",music_off);
                    Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                    intent.putExtra("return",bundle);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void myInterstitial(AdRequest adRequest) {


        InterstitialAd.load(this, getString(R.string.interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
//                                super.onAdShowedFullScreenContent();
                                mInterstitialAd =null;
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
//                                super.onAdDismissedFullScreenContent();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }


    @Override
    protected void onStop() {
        backgroundMP.stop();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        backgroundMP.stop();
        super.onDestroy();
    }
}