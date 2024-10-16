package br.com.tamegatech.iching

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.tamegatech.iching.MainActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.io.IOException
import java.io.InputStream

class ResultActivity : AppCompatActivity() {
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var imbtnMusicOff: ImageButton
//    private var music_off = false
    private lateinit var backgroundMP: MediaPlayer
    private lateinit var preferences: SharedPreferences

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*DisplayMetrics metrics = getResources().getDisplayMetrics();
        Toast.makeText(getApplicationContext(),String.valueOf(metrics.densityDpi),Toast.LENGTH_LONG).show();*/
        setContentView(R.layout.activity_result)
        this.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // Força somente a orientação retrato
        val btnNewPrediction = findViewById<View>(R.id.btnNewPrediction) as Button


        val img_Prediction = findViewById<View>(R.id.imgPrediction_Prediction) as ImageView
        val img_Mutation = findViewById<View>(R.id.imgMutation_Prediction) as ImageView

        val txt_Result = findViewById<View>(R.id.txtResult_Prediction) as TextView
        val txt_Mutation = findViewById<View>(R.id.txtMutation_Prediction) as TextView

        val bundle = intent.getBundleExtra("iching")
        val adRequest = AdRequest.Builder().build()
        myInterstitial(adRequest)
        preferences = getSharedPreferences("iching_preferences", MODE_PRIVATE)

       var music_off = preferences.getBoolean("music", false)

        //        music_off  = bundle.getBoolean("music_mode");

//        Toast.makeText(getApplicationContext(),"Music Off: " +String.valueOf(music_off),Toast.LENGTH_LONG).show();
        imbtnMusicOff = findViewById<View>(R.id.imbtnMusicOff) as ImageButton
        backgroundMP = MediaPlayer.create(applicationContext, R.raw.bensound_relaxing)
        //        backgroundMP.stop();
        if (music_off) {
            if (backgroundMP != null) {
                backgroundMP!!.stop()
                imbtnMusicOff!!.setImageResource(R.drawable.music_note)
            }
        } else {
            imbtnMusicOff!!.setImageResource(R.drawable.music_note_off)
            backgroundMP.start()
        }

        imbtnMusicOff!!.setOnClickListener {
            if (music_off) {
                music_off = false
                preferences.edit().putBoolean("music", false).apply()
                imbtnMusicOff!!.setImageResource(R.drawable.music_note_off)
                backgroundMP = MediaPlayer.create(applicationContext, R.raw.bensound_relaxing)
                backgroundMP.start()
            } else {
                music_off = true
                preferences.edit().putBoolean("music", true).apply()
                imbtnMusicOff!!.setImageResource(R.drawable.music_note)
                if (backgroundMP != null) {
                    backgroundMP!!.stop()
                }
            }
        }

        if (bundle != null) {
            val prediction = bundle.getBundle("prediction")
            val mutation = bundle.getBundle("mutation")
            val predic_Number = prediction!!.getLong("number")
            val predic_Description = prediction.getString("description")
            if (predic_Description != null) {
                val predic_Definition = prediction.getString("definition")
                val predic_final_Result = """
                    $predic_Definition
                    $predic_Description
                    """.trimIndent()
                val file_Prediction = if (predic_Number < 10) {
                    "iching_pictures/0$predic_Number.png"
                } else {
                    "iching_pictures/$predic_Number.png"
                }


                var inputStream: InputStream?
                try {
                    inputStream = assets.open(file_Prediction)
                    /*Drawable d = Drawable.createFromStream(inputStream,null);
                img_Prediction.setImageDrawable(d);*/
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    img_Prediction.setImageBitmap(bitmap)
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }


                txt_Result.text = predic_final_Result
                txt_Result.isScrollbarFadingEnabled = false
                txt_Result.movementMethod = ScrollingMovementMethod()

                val mutation_Number = mutation!!.getLong("number")
                val mutation_Description = mutation.getString("description")
                val mutation_Definition = mutation.getString("definition")
                val mutation_final_Result = """
                    $mutation_Definition
                    $mutation_Description
                    """.trimIndent()
                val file_Mutation = if (mutation_Number < 10) {
                    "iching_pictures/0$mutation_Number.png"
                } else {
                    "iching_pictures/$mutation_Number.png"
                }

                /*file_Mutation = "iching_pictures/"+ String.valueOf(mutation_Number)+".png";*/
                try {
                    inputStream = assets.open(file_Mutation)
                    val d = Drawable.createFromStream(inputStream, null)
                    if (d != null) {
                        img_Mutation.setImageDrawable(d)
                    } else {
                        img_Mutation.visibility = View.GONE
                    }

                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                txt_Mutation.text = mutation_final_Result
                txt_Mutation.isScrollbarFadingEnabled = false
                txt_Mutation.movementMethod = ScrollingMovementMethod()
            } else {
                img_Mutation.visibility = View.GONE
                img_Prediction.visibility = View.GONE
                txt_Mutation.visibility = View.GONE
                txt_Result.setText(R.string.quick_pressing_warning)
                //                txt_Result.setVisibility(View.GONE);
            }
        } else {
            if (img_Mutation == null || img_Prediction == null) {
                img_Mutation.visibility = View.GONE
                img_Prediction.visibility = View.GONE
                txt_Mutation.visibility = View.GONE
                txt_Result.visibility = View.GONE
            }
        }

        btnNewPrediction.setOnClickListener {
            MobileAds.initialize(
                applicationContext
            ) { }
            if (mInterstitialAd != null) {
                mInterstitialAd!!.show(this@ResultActivity)
            } else {
                val bundle = Bundle()
                bundle.putBoolean("music_mode", music_off)
                val intent = Intent(this@ResultActivity, MainActivity::class.java)
                intent.putExtra("return", bundle)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun myInterstitial(adRequest: AdRequest) {
        InterstitialAd.load(
            this, getString(R.string.interstitial_id), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd!!.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                super.onAdFailedToShowFullScreenContent(adError)
                            }

                            override fun onAdShowedFullScreenContent() {
                                //                                super.onAdShowedFullScreenContent();
                                mInterstitialAd = null
                            }

                            override fun onAdDismissedFullScreenContent() {
                                val intent =
                                    Intent(this@ResultActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                //                                super.onAdDismissedFullScreenContent();
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                }
            })
    }


    override fun onStop() {
        backgroundMP.stop()
        super.onStop()
    }

    override fun onDestroy() {
        backgroundMP.stop()
        super.onDestroy()
    }
}