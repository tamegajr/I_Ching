package br.com.tamegatech.iching;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;
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
    private final int displacement = 30, snddisplacment =5, radius =15;
    private Boolean hasMutation= false;
    private StringBuilder strResult = new StringBuilder();
    private StringBuilder strMutation = new StringBuilder();
//    private String strResult , strMutation;
    private String hex_result, hex_mutation;
    private long lastClick = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);

        RequestConfiguration requestConfiguration
                = new RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        MobileAds.setRequestConfiguration(requestConfiguration);
        AdView adView_main = (AdView) findViewById(R.id.adView_main);
        /*AdRequest adRequest_main = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build();*/
        adView_main.loadAd(new AdRequest.Builder().build());


        imgCoin1 = (ImageView) findViewById(R.id.imgCoin1);
        imgCoin2 = (ImageView) findViewById(R.id.imgCoin2);
        imgCoin3 = (ImageView) findViewById(R.id.imgCoin3);

        imgResult = (ImageView) findViewById(R.id.imgResult);
        imgMutation = (ImageView) findViewById(R.id.imgMutation);

        txtResult = (TextView) findViewById(R.id.txtResult);
        txtMutation = (TextView) findViewById(R.id.txtMutation);
        int btmapResultWidth = (int) getResources().getDimension(R.dimen.imgResult_width);
        int btmapResultHeight = (int) getResources().getDimension(R.dimen.imgResult_height);
        int btmapMutationWidth = (int) getResources().getDimension(R.dimen.imgMutation_width);
        int btmapMutationHeight = (int) getResources().getDimension(R.dimen.imgMutation_height);

//        BitmapFactory.Options

        /*strResult = "";
        strMutation = "";*/

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

        Paint paintResultYang = new Paint();
        paintResultYang.setARGB(255,255,0,0);
        paintResultYang.setStrokeWidth(10);
        paintResultYang.setStyle(Paint.Style.FILL);

        Paint paintResultYin = new Paint();
        paintResultYin.setARGB(255,255,0,0);
        paintResultYin.setStrokeWidth(10);
        paintResultYin.setStyle(Paint.Style.FILL);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[] {(btmapResultWidth/2)-30f,50f},0);
        paintResultYin.setPathEffect(dashPathEffect);

        Paint paintX = new Paint();
        paintX.setARGB(255,255,0,0);
        paintX.setStrokeWidth(10);
        paintX.setStyle(Paint.Style.STROKE);

        Paint paintCircle = new Paint();
        paintCircle.setARGB(255,255,0,0);
        paintCircle.setStrokeWidth(5);
        paintCircle.setStyle(Paint.Style.STROKE);
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);

        Button btnThrow = (Button) findViewById(R.id.btnThrow);
        Bundle bundle = new Bundle();

        btnThrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (num_line !=1){
                    num_line+=1;
                    angle*=num_line;
                }*/
                lastClick = SystemClock.elapsedRealtime();
                if (btnThrow.getText() == getString(R.string.i_ching_prediction)){

                        startActivity(intent);
                        finish();

                }else {
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
//                        sum = 8;

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
                                            canvasResult.drawText("X",btmapResultWidth/2,ypositionResult,
                                                    paintX);
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
                                                    15,paintCircle);
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
                                            canvasResult.drawText("X",btmapResultWidth/2,ypositionResult,
                                                    paintX);
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYang);
                                            hasMutation = true;
                                            strMutation.append("1");
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    imgResult.getWidth()-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    imgResult.getWidth()-10, ypositionResult,
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
                                                    15,paintCircle);
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
                                            canvasResult.drawText("X",btmapResultWidth/2,ypositionResult,
                                                    paintX);
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    imgResult.getWidth()-10, ypositionResult,
                                                    paintResultYin);
                                            strResult.append("0");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    imgResult.getWidth()-10, ypositionResult,
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
                                                    15,paintCircle);
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    btmapResultWidth-10, ypositionResult,
                                                    paintResultYin);
                                            strMutation.append("0");
                                            hasMutation = true;
                                        }else{
                                            canvasResult.drawLine(10, ypositionResult,
                                                    imgResult.getWidth()-10, ypositionResult,
                                                    paintResultYang);
                                            strResult.append("1");
                                            canvasMutation.drawLine(10, ypositionResult,
                                                    imgResult.getWidth()-10, ypositionResult,
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
                                            canvasResult.drawText("X",btmapResultWidth/2,ypositionResult,
                                                    paintX);
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
                                                    15,paintCircle);
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
                                            canvasResult.drawText("X",btmapResultWidth/2,ypositionResult,
                                                    paintX);
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
                                                    15,paintCircle);
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
                                            canvasResult.drawText("X",btmapResultWidth/2,ypositionResult,
                                                    paintX);
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
                                                    15,paintCircle);
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
                    },1000);

//                Bitmap reducedResult = BitmapFactory.d
                    imgResult.setImageBitmap(bitmapResult);
                    imgMutation.setImageBitmap(bitmapMutation);

                }


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
}