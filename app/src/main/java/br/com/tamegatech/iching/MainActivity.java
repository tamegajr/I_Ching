package br.com.tamegatech.iching;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView imgCoin1, imgCoin2, imgCoin3;
    private ImageView imgResult, imgMutation;
    private int num_line=1, sum;
    private int ypositionResult, ypositionMutation;
    private float angle = 7200;
    private Boolean[] Coins = new Boolean [3];
    private TextView txtResult, txtMutation;
    private Canvas canvasResult, canvasMutation;
    private final int displacement = 30;
    private final int snddisplacment =5;
    private int radius, xDisplacement;
    private Boolean hasMutation= false, music_off;
    private StringBuilder strResult = new StringBuilder();
    private StringBuilder strMutation = new StringBuilder();
//    private String strResult , strMutation;
    private String hex_result, hex_mutation, markX;
    private final static String AD_UNIT_ID = "ca-app-pub-3940256099942544~3347511713";
    private long lastClick = 0;
    private MediaPlayer backgroundMP = new MediaPlayer();
//    private MediaPlayer coinMP = new MediaPlayer();
    private SoundPool soundeffect;
    private int coinFliping;
    private ImageButton imbtnMusicOff;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("iching_preferences", Context.MODE_PRIVATE);

        if (preferences.contains("music")){
            music_off = preferences.getBoolean("music",false);
        }else{
            music_off = false;
        }

        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
/*
        Bundle bundleretrun = new Bundle();

        bundleretrun.getBundle("return");*/
      /*  if (bundleretrun!=null){
            music_off = bundleretrun.getBoolean("music_mode");
        }else{
            music_off = false;
        }*/
//        Toast.makeText(getApplicationContext(),"Music Off: " + String.valueOf(music_off),Toast.LENGTH_LONG).show();

        /*backgroundMP.stop();
        backgroundMP = MediaPlayer.create(getApplicationContext(),R.raw.bensound_relaxing);
        backgroundMP.start();*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundeffect = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
         /*backgroundMP = MediaPlayer.create(getApplicationContext(),R.raw.bensound_relaxing);
            backgroundMP.start();
            coinFliping = soundeffect.load(getApplicationContext(), R.raw.coin_flipping,1);*/

        } else {
            soundeffect = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        backgroundMP = MediaPlayer.create(getApplicationContext(),R.raw.bensound_relaxing);


        coinFliping = soundeffect.load(getApplicationContext(), R.raw.coin_flipping,1);

        imbtnMusicOff = (ImageButton) findViewById(R.id.imbtnMusicOff);

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
                    backgroundMP.stop();
                }

            }
        });

        DialogWelcome Welcome = new DialogWelcome();

        Welcome.show(getSupportFragmentManager(),"Welcome");

        AdView adView_main = (AdView) findViewById(R.id.adView_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener(){

            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                adView_main.post(new Runnable() {
                    @Override
                    public void run() {
                        loadBanner();
                    }

                    private void loadBanner() {
                        String testDeviceId = AdRequest.DEVICE_ID_EMULATOR.toString();

                        final RequestConfiguration.Builder requestConfigurationBuilder = new RequestConfiguration.Builder();

                        requestConfigurationBuilder.setTestDeviceIds(Collections.singletonList(testDeviceId)).build();

                        final RequestConfiguration requestConfiguration = requestConfigurationBuilder.build();

                        MobileAds.setRequestConfiguration(requestConfiguration);
                        MobileAds.initialize(getApplicationContext());

                        //                        adView_main.setAdUnitId(AD_UNIT_ID);
                        AdRequest adRequest_main = new AdRequest.Builder().build();
                        adView_main.loadAd(adRequest_main);
                    }
                });
            }
        });


        imgCoin1 = (ImageView) findViewById(R.id.imgCoin1);
        imgCoin2 = (ImageView) findViewById(R.id.imgCoin2);
        imgCoin3 = (ImageView) findViewById(R.id.imgCoin3);

        imgResult = (ImageView) findViewById(R.id.imgResult);
        imgMutation = (ImageView) findViewById(R.id.imgMutation);

/*        txtResult = (TextView) findViewById(R.id.txtResult);
        txtMutation = (TextView) findViewById(R.id.txtMutation);*/
        int btmapResultWidth = (int) getResources().getDimension(R.dimen.imgResult_width);
        int btmapResultHeight = (int) getResources().getDimension(R.dimen.imgResult_height);
        int btmapMutationWidth = (int) getResources().getDimension(R.dimen.imgMutation_width);
        int btmapMutationHeight = (int) getResources().getDimension(R.dimen.imgMutation_height);

        Bitmap bitmapResult = Bitmap.createBitmap(btmapResultWidth,btmapResultHeight,
                Bitmap.Config.ARGB_8888);
       /* BitmapFactory.Options optionsResult = new BitmapFactory.Options();
        optionsResult.inBitmap = bitmapResult;
        optionsResult.inMutable = true;
        optionsResult.inSampleSize =4;*/

        Bitmap bitmapMutation = Bitmap.createBitmap(btmapMutationWidth,btmapMutationHeight,
                Bitmap.Config.ARGB_8888);
        /*BitmapFactory.Options optionsMutation = new BitmapFactory.Options();
        optionsMutation.inBitmap = bitmapMutation;
        optionsMutation.inMutable = true;
        optionsMutation.inSampleSize = 4;*/

        canvasResult = new Canvas(bitmapResult);
        canvasMutation = new Canvas(bitmapMutation);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Paint paintResultYang = new Paint();
        Paint paintResultYin = new Paint();
        Paint paintX = new Paint();
        Paint paintCircle = new Paint();
        radius = 15;
        markX = "X";
        if (metrics.densityDpi <= 120){

//            ldpi screens

            markX = "x";
            xDisplacement = 1;
            paintResultYang.setARGB(255,255,0,0);
            paintResultYang.setStrokeWidth(3);
            paintResultYang.setStyle(Paint.Style.FILL);


            paintResultYin.setARGB(255,255,0,0);
            paintResultYin.setStrokeWidth(3);
            paintResultYin.setStyle(Paint.Style.FILL);
            DashPathEffect dashPathEffect = new DashPathEffect(new float[] {(btmapResultWidth/2)-18f,18f},0);
            paintResultYin.setPathEffect(dashPathEffect);


            paintX.setARGB(255,255,0,0);
            paintX.setStrokeWidth(1);
            paintX.setStyle(Paint.Style.STROKE);

            radius = 5;
            paintCircle.setARGB(255,255,0,0);
            paintCircle.setStrokeWidth(2);

        } else if (metrics.densityDpi > 120 && metrics.densityDpi <= 160){

//            mdpi

            xDisplacement = 1;
            markX = "x";
            paintResultYang.setARGB(255,255,0,0);
            paintResultYang.setStrokeWidth(4);
            paintResultYang.setStyle(Paint.Style.FILL);


            paintResultYin.setARGB(255,255,0,0);
            paintResultYin.setStrokeWidth(4);
            paintResultYin.setStyle(Paint.Style.FILL);
            DashPathEffect dashPathEffect = new DashPathEffect(new float[] {(btmapResultWidth/2)-17.5f,20f},0);
            paintResultYin.setPathEffect(dashPathEffect);


            paintX.setARGB(255,255,0,0);
            paintX.setStrokeWidth(2);
            paintX.setStyle(Paint.Style.STROKE);

            radius = 6;
            paintCircle.setARGB(255,255,0,0);
            paintCircle.setStrokeWidth(2);


        } else if (metrics.densityDpi >160 && metrics.densityDpi<=240){

//            hdpi

            xDisplacement =3;
            paintResultYang.setARGB(255,255,0,0);
            paintResultYang.setStrokeWidth(5);
            paintResultYang.setStyle(Paint.Style.FILL);


            paintResultYin.setARGB(255,255,0,0);
            paintResultYin.setStrokeWidth(5);
            paintResultYin.setStyle(Paint.Style.FILL);
            DashPathEffect dashPathEffect = new DashPathEffect(new float[] {(btmapResultWidth/2)-30f,40f},0);
            paintResultYin.setPathEffect(dashPathEffect);


            paintX.setARGB(255,255,0,0);
            paintX.setStrokeWidth(4);
            paintX.setStyle(Paint.Style.STROKE);

            radius = 10;
            paintCircle.setARGB(255,255,0,0);
            paintCircle.setStrokeWidth(5);

        }
        else {

//            xhdpi, xxhdpi, xxxhdpi

            xDisplacement =0;
            paintResultYang.setARGB(255,255,0,0);
            paintResultYang.setStrokeWidth(10);
            paintResultYang.setStyle(Paint.Style.FILL);


            paintResultYin.setARGB(255,255,0,0);
            paintResultYin.setStrokeWidth(10);
            paintResultYin.setStyle(Paint.Style.FILL);
            DashPathEffect dashPathEffect = new DashPathEffect(new float[] {(btmapResultWidth/2)-30f,50f},0);
            paintResultYin.setPathEffect(dashPathEffect);


            paintX.setARGB(255,255,0,0);
            paintX.setStrokeWidth(7);
            paintX.setStyle(Paint.Style.STROKE);


            paintCircle.setARGB(255,255,0,0);
            paintCircle.setStrokeWidth(5);

        }
        paintCircle.setStyle(Paint.Style.STROKE);


        Intent intent = new Intent(MainActivity.this, ResultActivity.class);

        Button btnThrow = (Button) findViewById(R.id.btnThrow);
        Bundle bundle = new Bundle();

        btnThrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*coinMP.stop();
                coinMP = MediaPlayer.create(getApplicationContext(),R.raw.coin_flipping);
                coinMP.start();*/

                /*if (num_line !=1){
                    num_line+=1;
                    angle*=num_line;
                }*/
                lastClick = SystemClock.elapsedRealtime();
                if (btnThrow.getText() == getString(R.string.i_ching_prediction)){
                    btnThrow.setContentDescription(getString(R.string.i_ching_prediction));
                    startActivity(intent);
                    finish();

                }else {
                    soundeffect.play(coinFliping,0.5f,0.5f,1,0,1);
                    angle+=angle;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ObjectAnimator flip1 = ObjectAnimator.ofFloat(imgCoin1,"rotationX", angle);
                            ObjectAnimator flip2 = ObjectAnimator.ofFloat(imgCoin2,"rotationX", angle);
                            ObjectAnimator flip3 = ObjectAnimator.ofFloat(imgCoin3,"rotationX", angle);

                            flip1.setDuration(500);
                            flip2.setDuration(500);
                            flip3.setDuration(500);

                            flip1.start();
                            flip2.start();
                            flip3.start();

                            Coins();
                            for (int i=0; i<3; i++){
                                // true = on = yang = 3 = tail
                                // false = off = yin = 2 = head
                                switch (i){
                                    case 0:
                                        if (Coins[i]){
                                            sum+=3;
                                            imgCoin1.setImageResource(R.drawable.i_ching_coin_tail);
                                        }else {
                                            sum +=2;
                                            imgCoin1.setImageResource(R.drawable.i_ching_coin_head);
                                        }
                                        break;
                                    case 1:
                                        if (Coins[i]){
                                            sum +=3;
                                            imgCoin2.setImageResource(R.drawable.i_ching_coin_tail);
                                        }else {
                                            sum +=2;
                                            imgCoin2.setImageResource(R.drawable.i_ching_coin_head);
                                        }
                                        break;
                                    case 2:
                                        if (Coins[i]){
                                            sum +=3;
                                            imgCoin3.setImageResource(R.drawable.i_ching_coin_tail);
                                        }else {
                                            sum +=2;
                                            imgCoin3.setImageResource(R.drawable.i_ching_coin_head);
                                        }
                                        break;
                                }
                            }
//                        sum = 6;

                            int recuo = (int) btmapResultHeight/7;
                            switch (num_line){
                                case 1:
                                    num_line+=1;
                                    btnThrow.setText(getResources().getText(R.string.Line2nd));
                                    /*txtResult.setText(String.valueOf(sum));
                                    txtMutation.setText(String.valueOf(sum));*/
                                /*float a = (btmapResultHeight-6) *12;
                                a/=14;*/
                                    ypositionResult = btmapResultHeight - 15;
                                    ypositionMutation = btmapMutationHeight -15;
                                    if (sum %2 ==0){
                                        if (sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            canvasResult.drawText(markX,btmapResultWidth/2 -  xDisplacement
                                                    ,ypositionResult, paintX);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionMutation,
                                                    btmapResultWidth-10, ypositionMutation,
                                                    paintResultYang);
                                            strMutation.append("1");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionMutation,
                                                    btmapResultWidth-10, ypositionMutation,
                                                    paintResultYin);
                                            strMutation.append("0");
                                        }
                                    }else{
                                        if (sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasResult.drawCircle(btmapResultWidth/2,ypositionResult,
                                                    radius,paintCircle);
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strMutation.append("0");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strMutation.append("1");

                                        }
                                    }
                                    sum =0;
//                        angle *=num_line;
                                    break;
                                case 2:
                                    num_line+=1;
                               /* a = (btmapResultHeight-6)* 10;
                                a/=14;*/
                                    ypositionResult -= recuo;
//                                ypositionResult -= 10;
                                    btnThrow.setText(getResources().getText(R.string.Line3rd));
//                                    txtResult.setText(String.valueOf(sum));
                                    if (sum %2 ==0){
                                        if ( sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasResult.drawText(markX,btmapResultWidth/2 -xDisplacement
                                                    ,ypositionResult, paintX);
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            hasMutation = true;
                                            strMutation.append("1");
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strMutation.append("0");
                                        }
                                    }else{
                                        if (sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasResult.drawCircle(btmapResultWidth/2,ypositionResult,
                                                    radius,paintCircle);
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            hasMutation = true;
                                            strMutation.append("0");
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strMutation.append("1");
                                        }
                                    }
                                    sum =0;
//                        angle *=num_line;
                                    break;
                                case 3:
                                    num_line+=1;
                                /*a = (btmapResultHeight-6)* 8;
                                a/=14;*/
                                    ypositionResult -= recuo;
//                                ypositionResult -= 10;
                                    btnThrow.setText(getResources().getText(R.string.Line4th));
//                                    txtResult.setText(String.valueOf(sum));
                                    if (sum %2 ==0){
                                        if ( sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strMutation.append("1");
                                            canvasResult.drawText(markX,btmapResultWidth/2 -xDisplacement,
                                                    ypositionResult,paintX);
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strMutation.append("0");
                                        }
                                    }else{
                                        if (sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasResult.drawCircle(btmapResultWidth/2,ypositionResult,
                                                    radius,paintCircle);
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strMutation.append("0");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strMutation.append("1");
                                        }
                                    }
                                    sum =0;
//                        angle *=num_line;
                                    break;
                                case 4:
                                    num_line+=1;
                                /*a = (btmapResultHeight-6) * 6;
                                a/=14;*/
                                    ypositionResult -= recuo;
//                                ypositionResult -= 10;
                                    btnThrow.setText(getResources().getText(R.string.Line5th));
//                                    txtResult.setText(String.valueOf(sum));
//                                sum =0;
                                    if (sum %2 ==0){
                                        if (sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            canvasResult.drawText(markX,btmapResultWidth/2 -xDisplacement
                                                    ,ypositionResult, paintX);
                                            strMutation.append("1");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strMutation.append("0");
                                        }
                                    }else{
                                        if (sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            canvasResult.drawCircle(btmapResultWidth/2,ypositionResult,
                                                    radius,paintCircle);
                                            strMutation.append("0");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strMutation.append("1");
                                        }
                                    }
//                                angle *=num_line;
                                    sum =0;
                                    break;
                                case 5:
                                    num_line+=1;
                                /*a = (btmapResultHeight-6) *4;
                                a/=14;*/
                                    ypositionResult -= recuo;
//                                ypositionResult -= 10;
                                    btnThrow.setText(getResources().getText(R.string.Line6th));
//                                    txtResult.setText(String.valueOf(sum));
//                                sum =0;
                                    if (sum %2 ==0){
                                        if (sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            canvasResult.drawText(markX,btmapResultWidth/2 -xDisplacement,
                                                    ypositionResult, paintX);
                                            strMutation.append("1");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strMutation.append("0");
                                        }
                                    }else{
                                        if (sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasResult.drawCircle(btmapResultWidth/2,ypositionResult,
                                                    radius,paintCircle);
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);

                                            strMutation.append("0");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strMutation.append("1");
                                        }
                                    }
//                        angle *=num_line;
                                    sum=0;
                                    break;
                                case 6:
                                /*a = (btmapResultHeight-6)*2;
                                a/=14;*/
                                    ypositionResult -= recuo;
                                    num_line=1;
                                    btnThrow.setText(getResources().getText(R.string.Line1st));
                                    if (sum %2 ==0){
                                        if (sum %3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            canvasResult.drawText(markX,btmapResultWidth/2 -xDisplacement,
                                                    ypositionResult, paintX);
                                            strMutation.append("1");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strMutation.append("0");
                                        }
                                    }else{
                                        if (sum % 3 ==0){
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            canvasResult.drawCircle(btmapResultWidth/2,ypositionResult,
                                                    radius,paintCircle);
                                            strMutation.append("0");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            strMutation.append("1");
                                        }
                                    }
//                                    txtResult.setText(String.valueOf(sum));
//                                strResult.append("\n" + "sum = " + txtResult.getText());
//                                    txtResult.setText(strResult);
                                    sum =0;
//                        angle *=num_line;
//                                strResult.append("\n" + "sum = " + txtResult.getText());
//                                txtResult.setText(strResult);
//                                strMutation.append("\n" + "sum = " + txtMutation.getText());

                                  /*  txtMutation.setText(strMutation);
                                    hex_result =  hexagram(strResult);

                                    hex_mutation = hexagram(strMutation);

                                    strResult.append("\n" + hex_result);
                                    strMutation.append("\n" + hex_mutation);

                                    txtResult.setText(strResult);
                                    txtMutation.setText(strMutation);*/

                                    bundle.putBundle("prediction",hexagram(strResult));
                                    bundle.putBundle("mutation",hexagram(strMutation));
                                    bundle.putBoolean("music_mode",music_off);
                                    intent.putExtra("iching",bundle);
                                    btnThrow.setText(R.string.i_ching_prediction);
                                    break;

                            }
                        /*strResult.append("\n" + "sum = " + txtResult.getText());
                        txtResult.setText(strResult);
                        strMutation.append("\n" + "sum = " + txtMutation.getText());
                        txtMutation.setText(strMutation);
*/
                        }
                    },500);

//                Bitmap reducedResult = BitmapFactory.d
                    imgResult.setImageBitmap(bitmapResult);
                    imgMutation.setImageBitmap(bitmapMutation);

                }

//                coinMP.stop();
            }
        });





    }

    private Bundle hexagram(StringBuilder symbol) {
        Bundle hexagram = new Bundle();
        String pre_hexagram="";
        try{
            String file_Name = getString(R.string.hexagram_file);
            InputStream is = getApplicationContext().getAssets().open(file_Name);
            int size = is.available();
            byte [] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonData;
            jsonData = new String(buffer, "UTF-8");
            JSONArray JA = new JSONArray(jsonData);
            JSONObject JO = JA.getJSONObject(0);
            JSONObject JO1 = JO.getJSONObject(symbol.toString());
            /*pre_hexagram = JO.getString(symbol.toString());
            int frst_position = pre_hexagram.indexOf(":");
            int snd_position = pre_hexagram.indexOf(",");
            hexagram = pre_hexagram.substring(frst_position,snd_position);*/
            hexagram.putLong("number", JO1.getLong("number"));
            hexagram.putString("definition", JO1.getString("definition"));
            hexagram.putString("description", JO1.getString("description"));

        }
        catch (IOException | JSONException e){
            e.printStackTrace();
        }

        return hexagram;
    }

    private Boolean[] Coins(){

        for (int i=0; i<3; i++){
            Random random = new Random();
            Coins[i] = random.nextBoolean();
        }

        return Coins;
    }
    private Bitmap loadBitmapFromAssets(Context context, String path)
    {
        InputStream stream = null;
        try
        {
            stream = context.getAssets().open(path);
            return BitmapFactory.decodeStream(stream);
        }
        catch (Exception ignored) {} finally
        {
            try
            {
                if(stream != null)
                {
                    stream.close();
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        backgroundMP.stop();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        backgroundMP.stop();
        super.onStop();
    }
}