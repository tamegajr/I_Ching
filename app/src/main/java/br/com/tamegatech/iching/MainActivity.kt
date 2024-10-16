package br.com.tamegatech.iching

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.io.InputStream
import java.util.Random

class MainActivity : AppCompatActivity() {
    private var imgCoin1: ImageView? = null
    private var imgCoin2: ImageView? = null
    private var imgCoin3: ImageView? = null
    private var imgResult: ImageView? = null
    private var imgMutation: ImageView? = null
    private var num_line = 1
    private var sum = 0
    private var ypositionResult = 0
    private var ypositionMutation = 0
    private var angle = 7200f
    private val Coins = arrayOfNulls<Boolean>(3)
    private val txtResult: TextView? = null
    private val txtMutation: TextView? = null
    private var canvasResult: Canvas? = null
    private var canvasMutation: Canvas? = null
    private val displacement = 30
    private val snddisplacment = 5
    private var radius = 0
    private var xDisplacement = 0
    private var hasMutation = false
//    private var music_off: Boolean? = null
    private val strResult = StringBuilder()
    private val strMutation = StringBuilder()

    //    private String strResult , strMutation;
    private val hex_result: String? = null
    private val hex_mutation: String? = null
    private var markX: String? = null
    private var lastClick: Long = 0
    private lateinit var backgroundMP: MediaPlayer

    //    private MediaPlayer coinMP = new MediaPlayer();

    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private var soundEffect: SoundPool= SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(audioAttributes)
        .build()
//    private var coinFliping = 0
    private var imbtnMusicOff: ImageButton? = null
    private lateinit var preferences: SharedPreferences


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("iching_preferences", Context.MODE_PRIVATE)

//        preferences.edit().putBoolean("music", true).apply()
        var music_off = preferences.getBoolean("music",false)
        music_off = false

        setContentView(R.layout.activity_main)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


//        soundEffect

        /*backgroundMP = MediaPlayer.create(getApplicationContext(),R.raw.bensound_relaxing);
            backgroundMP.start();
            coinFliping = soundeffect.load(getApplicationContext(), R.raw.coin_flipping,1);*/
        val backGroundMusic = resources.getIdentifier("bensound_relaxing", "raw", packageName)
        val backgroundMP = MediaPlayer.create(applicationContext, backGroundMusic)


//        soundEffect.play(R.raw.bensound_relaxing, 1.0f, 1.0f, 0, -1, 1.0f)

        imbtnMusicOff = findViewById<View>(R.id.imbtnMusicOff) as ImageButton

        if (music_off) {
            if (backgroundMP != null) {
                backgroundMP.stop()
                imbtnMusicOff!!.setImageResource(R.drawable.music_note)
            }
        } else {
            imbtnMusicOff!!.setImageResource(R.drawable.music_note_off)
            backgroundMP.start()
        }

        imbtnMusicOff!!.setOnClickListener {
            if (music_off) {
                music_off = false
//                preferences.edit().putBoolean("music", false).apply()
                imbtnMusicOff!!.setImageResource(R.drawable.music_note_off)
//                backgroundMP = MediaPlayer.create(applicationContext, R.raw.bensound_relaxing)
                backgroundMP.start()
            } else {
                music_off = true
                preferences.edit().putBoolean("music", true).apply()
                imbtnMusicOff!!.setImageResource(R.drawable.music_note)
                backgroundMP.stop()
            }
        }

        val welcome = DialogWelcome()

        welcome.show(supportFragmentManager, "Welcome")

        val adView_main = findViewById<View>(R.id.adView_main) as AdView

        MobileAds.initialize(this) {
            adView_main.post(object : Runnable {
                override fun run() {
                    loadBanner()
                }

                fun loadBanner() {
                    //                        String testDeviceId = AdRequest.DEVICE_ID_EMULATOR.toString();

                    val requestConfigurationBuilder = RequestConfiguration.Builder()

                    //                        requestConfigurationBuilder.setTestDeviceIds(Collections.singletonList(testDeviceId)).build();
                    val requestConfiguration = requestConfigurationBuilder.build()

                    MobileAds.setRequestConfiguration(requestConfiguration)
                    MobileAds.initialize(applicationContext)

                    //                        adView_main.setAdUnitId(AD_UNIT_ID);
                    val adRequest_main = AdRequest.Builder().build()
                    adView_main.loadAd(adRequest_main)
                }
            })
        }


        imgCoin1 = findViewById<View>(R.id.imgCoin1) as ImageView
        imgCoin2 = findViewById<View>(R.id.imgCoin2) as ImageView
        imgCoin3 = findViewById<View>(R.id.imgCoin3) as ImageView

        imgResult = findViewById<View>(R.id.imgResult) as ImageView
        imgMutation = findViewById<View>(R.id.imgMutation) as ImageView

        /*        txtResult = (TextView) findViewById(R.id.txtResult);
        txtMutation = (TextView) findViewById(R.id.txtMutation);*/
        val btmapResultWidth = resources.getDimension(R.dimen.imgResult_width).toInt()
        val btmapResultHeight = resources.getDimension(R.dimen.imgResult_height).toInt()
        val btmapMutationWidth = resources.getDimension(R.dimen.imgMutation_width).toInt()
        val btmapMutationHeight = resources.getDimension(R.dimen.imgMutation_height).toInt()

        val bitmapResult = Bitmap.createBitmap(
            btmapResultWidth, btmapResultHeight,
            Bitmap.Config.ARGB_8888
        )

        /* BitmapFactory.Options optionsResult = new BitmapFactory.Options();
        optionsResult.inBitmap = bitmapResult;
        optionsResult.inMutable = true;
        optionsResult.inSampleSize =4;*/
        val bitmapMutation = Bitmap.createBitmap(
            btmapMutationWidth, btmapMutationHeight,
            Bitmap.Config.ARGB_8888
        )

        /*BitmapFactory.Options optionsMutation = new BitmapFactory.Options();
        optionsMutation.inBitmap = bitmapMutation;
        optionsMutation.inMutable = true;
        optionsMutation.inSampleSize = 4;*/
        canvasResult = Canvas(bitmapResult)
        canvasMutation = Canvas(bitmapMutation)
        val metrics = resources.displayMetrics
        val paintResultYang = Paint()
        val paintResultYin = Paint()
        val paintX = Paint()
        val paintCircle = Paint()
        radius = 15
        markX = "X"
        if (metrics.densityDpi <= 120) {
            //            ldpi screens

            markX = "x"
            xDisplacement = 1
            paintResultYang.setARGB(255, 255, 0, 0)
            paintResultYang.strokeWidth = 3f
            paintResultYang.style = Paint.Style.FILL


            paintResultYin.setARGB(255, 255, 0, 0)
            paintResultYin.strokeWidth = 3f
            paintResultYin.style = Paint.Style.FILL
            val dashPathEffect = DashPathEffect(floatArrayOf((btmapResultWidth / 2) - 18f, 18f), 0f)
            paintResultYin.setPathEffect(dashPathEffect)


            paintX.setARGB(255, 255, 0, 0)
            paintX.strokeWidth = 1f
            paintX.style = Paint.Style.STROKE

            radius = 5
            paintCircle.setARGB(255, 255, 0, 0)
            paintCircle.strokeWidth = 2f
        } else if (metrics.densityDpi > 120 && metrics.densityDpi <= 160) {
            //            mdpi

            xDisplacement = 1
            markX = "x"
            paintResultYang.setARGB(255, 255, 0, 0)
            paintResultYang.strokeWidth = 4f
            paintResultYang.style = Paint.Style.FILL


            paintResultYin.setARGB(255, 255, 0, 0)
            paintResultYin.strokeWidth = 4f
            paintResultYin.style = Paint.Style.FILL
            val dashPathEffect =
                DashPathEffect(floatArrayOf((btmapResultWidth / 2) - 17.5f, 20f), 0f)
            paintResultYin.setPathEffect(dashPathEffect)


            paintX.setARGB(255, 255, 0, 0)
            paintX.strokeWidth = 2f
            paintX.style = Paint.Style.STROKE

            radius = 6
            paintCircle.setARGB(255, 255, 0, 0)
            paintCircle.strokeWidth = 2f
        } else if (metrics.densityDpi > 160 && metrics.densityDpi <= 240) {
            //            hdpi

            xDisplacement = 3
            paintResultYang.setARGB(255, 255, 0, 0)
            paintResultYang.strokeWidth = 5f
            paintResultYang.style = Paint.Style.FILL


            paintResultYin.setARGB(255, 255, 0, 0)
            paintResultYin.strokeWidth = 5f
            paintResultYin.style = Paint.Style.FILL
            val dashPathEffect = DashPathEffect(floatArrayOf((btmapResultWidth / 2) - 30f, 40f), 0f)
            paintResultYin.setPathEffect(dashPathEffect)


            paintX.setARGB(255, 255, 0, 0)
            paintX.strokeWidth = 4f
            paintX.style = Paint.Style.STROKE

            radius = 10
            paintCircle.setARGB(255, 255, 0, 0)
            paintCircle.strokeWidth = 5f
        } else {
            //            xhdpi, xxhdpi, xxxhdpi

            xDisplacement = 0
            paintResultYang.setARGB(255, 255, 0, 0)
            paintResultYang.strokeWidth = 10f
            paintResultYang.style = Paint.Style.FILL


            paintResultYin.setARGB(255, 255, 0, 0)
            paintResultYin.strokeWidth = 10f
            paintResultYin.style = Paint.Style.FILL
            val dashPathEffect = DashPathEffect(floatArrayOf((btmapResultWidth / 2) - 30f, 50f), 0f)
            paintResultYin.setPathEffect(dashPathEffect)


            paintX.setARGB(255, 255, 0, 0)
            paintX.strokeWidth = 7f
            paintX.style = Paint.Style.STROKE


            paintCircle.setARGB(255, 255, 0, 0)
            paintCircle.strokeWidth = 5f
        }
        paintCircle.style = Paint.Style.STROKE


        val intent = Intent(this@MainActivity, ResultActivity::class.java)

        val btnThrow = findViewById<View>(R.id.btnThrow) as Button
        val bundle = Bundle()

        btnThrow.setOnClickListener {


            lastClick = SystemClock.elapsedRealtime()
            if (btnThrow.text === getString(R.string.i_ching_prediction)) {
                btnThrow.contentDescription = getString(R.string.i_ching_prediction)
                startActivity(intent)
                finish()
            } else {
                soundEffect.play(R.raw.coin_flipping, 0.5f, 0.5f, 1, 0, 1f)
                angle += angle
                val handler = Handler()
                handler.postDelayed({
                    val flip1 = ObjectAnimator.ofFloat(imgCoin1, "rotationX", angle)
                    val flip2 = ObjectAnimator.ofFloat(imgCoin2, "rotationX", angle)
                    val flip3 = ObjectAnimator.ofFloat(imgCoin3, "rotationX", angle)

                    flip1.setDuration(500)
                    flip2.setDuration(500)
                    flip3.setDuration(500)

                    flip1.start()
                    flip2.start()
                    flip3.start()

                    coins()
                    for (i in 0..2) {
                        // true = on = yang = 3 = tail
                        // false = off = yin = 2 = head
                        when (i) {
                            0 -> if (Coins[i]!!) {
                                sum += 3
                                imgCoin1!!.setImageResource(R.drawable.i_ching_coin_tail)
                            } else {
                                sum += 2
                                imgCoin1!!.setImageResource(R.drawable.i_ching_coin_head)
                            }

                            1 -> if (Coins[i]!!) {
                                sum += 3
                                imgCoin2!!.setImageResource(R.drawable.i_ching_coin_tail)
                            } else {
                                sum += 2
                                imgCoin2!!.setImageResource(R.drawable.i_ching_coin_head)
                            }

                            2 -> if (Coins[i]!!) {
                                sum += 3
                                imgCoin3!!.setImageResource(R.drawable.i_ching_coin_tail)
                            } else {
                                sum += 2
                                imgCoin3!!.setImageResource(R.drawable.i_ching_coin_head)
                            }
                        }
                    }

                    //                        sum = 6;
                    val recuo = btmapResultHeight / 7
                    when (num_line) {
                        1 -> {
                            num_line += 1
                            btnThrow.text = resources.getText(R.string.Line2nd)
                            /*txtResult.setText(String.valueOf(sum));
                                                            txtMutation.setText(String.valueOf(sum));*/
                            /*float a = (btmapResultHeight-6) *12;
                                                        a/=14;*/
                            ypositionResult = btmapResultHeight - 15
                            ypositionMutation = btmapMutationHeight - 15
                            if (sum % 2 == 0) {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    canvasResult!!.drawText(
                                        markX!!,
                                        (btmapResultWidth / 2 - xDisplacement).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintX
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionMutation.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionMutation.toFloat(),
                                        paintResultYang
                                    )
                                    strMutation.append("1")
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionMutation.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionMutation.toFloat(),
                                        paintResultYin
                                    )
                                    strMutation.append("0")
                                }
                            } else {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasResult!!.drawCircle(
                                        (btmapResultWidth / 2).toFloat(),
                                        ypositionResult.toFloat(),
                                        radius.toFloat(),
                                        paintCircle
                                    )
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strMutation.append("0")
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strMutation.append("1")
                                }
                            }
                            sum = 0
                        }

                        2 -> {
                            num_line += 1
                            /* a = (btmapResultHeight-6)* 10;
                                                    a/=14;*/
                            ypositionResult -= recuo
                            //                                ypositionResult -= 10;
                            btnThrow.text = resources.getText(R.string.Line3rd)
                            //                                    txtResult.setText(String.valueOf(sum));
                            if (sum % 2 == 0) {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasResult!!.drawText(
                                        markX!!,
                                        (btmapResultWidth / 2 - xDisplacement).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintX
                                    )
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    hasMutation = true
                                    strMutation.append("1")
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strMutation.append("0")
                                }
                            } else {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasResult!!.drawCircle(
                                        (btmapResultWidth / 2).toFloat(),
                                        ypositionResult.toFloat(),
                                        radius.toFloat(),
                                        paintCircle
                                    )
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    hasMutation = true
                                    strMutation.append("0")
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strMutation.append("1")
                                }
                            }
                            sum = 0
                        }

                        3 -> {
                            num_line += 1
                            /*a = (btmapResultHeight-6)* 8;
                                                    a/=14;*/
                            ypositionResult -= recuo
                            //                                ypositionResult -= 10;
                            btnThrow.text = resources.getText(R.string.Line4th)
                            //                                    txtResult.setText(String.valueOf(sum));
                            if (sum % 2 == 0) {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strMutation.append("1")
                                    canvasResult!!.drawText(
                                        markX!!,
                                        (btmapResultWidth / 2 - xDisplacement).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintX
                                    )
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strMutation.append("0")
                                }
                            } else {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasResult!!.drawCircle(
                                        (btmapResultWidth / 2).toFloat(),
                                        ypositionResult.toFloat(),
                                        radius.toFloat(),
                                        paintCircle
                                    )
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strMutation.append("0")
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strMutation.append("1")
                                }
                            }
                            sum = 0
                        }

                        4 -> {
                            num_line += 1
                            /*a = (btmapResultHeight-6) * 6;
                                                    a/=14;*/
                            ypositionResult -= recuo
                            //                                ypositionResult -= 10;
                            btnThrow.text = resources.getText(R.string.Line5th)
                            //                                    txtResult.setText(String.valueOf(sum));
                            //                                sum =0;
                            if (sum % 2 == 0) {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    canvasResult!!.drawText(
                                        markX!!,
                                        (btmapResultWidth / 2 - xDisplacement).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintX
                                    )
                                    strMutation.append("1")
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strMutation.append("0")
                                }
                            } else {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    canvasResult!!.drawCircle(
                                        (btmapResultWidth / 2).toFloat(),
                                        ypositionResult.toFloat(),
                                        radius.toFloat(),
                                        paintCircle
                                    )
                                    strMutation.append("0")
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strMutation.append("1")
                                }
                            }
                            //                                angle *=num_line;
                            sum = 0
                        }

                        5 -> {
                            num_line += 1
                            /*a = (btmapResultHeight-6) *4;
                                                    a/=14;*/
                            ypositionResult -= recuo
                            //                                ypositionResult -= 10;
                            btnThrow.text = resources.getText(R.string.Line6th)
                            //                                    txtResult.setText(String.valueOf(sum));
                            //                                sum =0;
                            if (sum % 2 == 0) {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    canvasResult!!.drawText(
                                        markX!!,
                                        (btmapResultWidth / 2 - xDisplacement).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintX
                                    )
                                    strMutation.append("1")
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strMutation.append("0")
                                }
                            } else {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasResult!!.drawCircle(
                                        (btmapResultWidth / 2).toFloat(),
                                        ypositionResult.toFloat(),
                                        radius.toFloat(),
                                        paintCircle
                                    )
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )

                                    strMutation.append("0")
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strMutation.append("1")
                                }
                            }
                            //                        angle *=num_line;
                            sum = 0
                        }

                        6 -> {
                            /*a = (btmapResultHeight-6)*2;
                                                    a/=14;*/
                            ypositionResult -= recuo
                            num_line = 1
                            btnThrow.text = resources.getText(R.string.Line1st)
                            if (sum % 2 == 0) {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    canvasResult!!.drawText(
                                        markX!!,
                                        (btmapResultWidth / 2 - xDisplacement).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintX
                                    )
                                    strMutation.append("1")
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strResult.append("0")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    strMutation.append("0")
                                }
                            } else {
                                if (sum % 3 == 0) {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYin
                                    )
                                    canvasResult!!.drawCircle(
                                        (btmapResultWidth / 2).toFloat(),
                                        ypositionResult.toFloat(),
                                        radius.toFloat(),
                                        paintCircle
                                    )
                                    strMutation.append("0")
                                    hasMutation = true
                                } else {
                                    canvasResult!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strResult.append("1")
                                    canvasMutation!!.drawLine(
                                        10f,
                                        ypositionResult.toFloat(),
                                        (btmapResultWidth - 10).toFloat(),
                                        ypositionResult.toFloat(),
                                        paintResultYang
                                    )
                                    strMutation.append("1")
                                }
                            }
                            //                                    txtResult.setText(String.valueOf(sum));
                            //                                strResult.append("\n" + "sum = " + txtResult.getText());
                            //                                    txtResult.setText(strResult);
                            sum = 0

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
                            bundle.putBundle("prediction", hexagram(strResult))
                            bundle.putBundle("mutation", hexagram(strMutation))
                            bundle.putBoolean("music_mode", music_off!!)
                            intent.putExtra("iching", bundle)
                            btnThrow.setText(R.string.i_ching_prediction)
                        }
                    }
                    /*strResult.append("\n" + "sum = " + txtResult.getText());
                                            txtResult.setText(strResult);
                                            strMutation.append("\n" + "sum = " + txtMutation.getText());
                                            txtMutation.setText(strMutation);
                    */
                }, 500)

                //                Bitmap reducedResult = BitmapFactory.d
                imgResult!!.setImageBitmap(bitmapResult)
                imgMutation!!.setImageBitmap(bitmapMutation)
            }
            //                coinMP.stop();
        }
    }

    private fun hexagram(symbol: StringBuilder): Bundle {
        val hexagram = Bundle()
        val pre_hexagram = ""
        try {
            val file_Name = getString(R.string.hexagram_file)
            val `is` = applicationContext.assets.open(file_Name)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val jsonData = String(buffer, charset("UTF-8"))
            val JA = JSONArray(jsonData)
            val JO = JA.getJSONObject(0)
            val JO1 = JO.getJSONObject(symbol.toString())
            /*pre_hexagram = JO.getString(symbol.toString());
            int frst_position = pre_hexagram.indexOf(":");
            int snd_position = pre_hexagram.indexOf(",");
            hexagram = pre_hexagram.substring(frst_position,snd_position);*/
            hexagram.putLong("number", JO1.getLong("number"))
            hexagram.putString("definition", JO1.getString("definition"))
            hexagram.putString("description", JO1.getString("description"))
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return hexagram
    }

    private fun coins(): Array<Boolean?> {
        for (i in 0..2) {
            val random = Random()
            Coins[i] = random.nextBoolean()
        }

        return Coins
    }

    private fun loadBitmapFromAssets(context: Context, path: String): Bitmap? {
        var stream: InputStream? = null
        try {
            stream = context.assets.open(path)
            return BitmapFactory.decodeStream(stream)
        } catch (ignored: Exception) {
        } finally {
            try {
                stream?.close()
            } catch (ignored: Exception) {
            }
        }
        return null
    }

    override fun onDestroy() {
        backgroundMP.stop()
        super.onDestroy()
    }

    override fun onStop() {
        backgroundMP.stop()
        super.onStop()
    }

    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544~3347511713"
    }
}