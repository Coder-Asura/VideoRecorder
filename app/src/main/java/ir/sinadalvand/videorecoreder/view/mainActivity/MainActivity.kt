package ir.sinadalvand.videorecoreder.view.mainActivity


import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader.Builder
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import ir.sinadalvand.videorecoreder.BuildConfig
import ir.sinadalvand.videorecoreder.R
import ir.sinadalvand.videorecoreder.core.xpack.XappCompatActivity
import ir.sinadalvand.videorecoreder.utils.camera.CamHelper
import ir.sinadalvand.videorecoreder.utils.customView.VideoRecordingButton
import ir.sinadalvand.videorecoreder.utils.uploader.UploadState
import ir.sinadalvand.videorecoreder.utils.uploader.Uploader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : XappCompatActivity<MainActivityViewModel>(),
    ValueAnimator.AnimatorUpdateListener,
    VideoRecordingButton.OnRecordListener {

    private var currentNativeAd: UnifiedNativeAd? = null

    /* Camera Helper*/
    private lateinit var cam: CamHelper

    /* Animation Const */
    private var width = 0
    private var height = 0
    private var marginBottom = 0
    private var marginEnd = 0
    private var animator: ValueAnimator? = null


    private val REQUEST_CODE = 1010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // set camera helper
        cam = CamHelper(cameraContainer.holder, this)

        // request and check permissions
        hasPersmission()

        // show Ads
        initADs()


        RecordButton.setOnRecordListener(this)


        // animator fot resizing cam preview
        animator = ValueAnimator.ofFloat(0f, 1f)
        animator?.addUpdateListener(this)
        animator?.duration = 500




        vm.uploadState.observe(this, Observer {
            when (it) {
                UploadState.STARTED -> {
                    RecordButton.setProgressColor(Color.GREEN)
                    RecordButton.loadMode(true)
                    Toast.makeText(this,"Upload Started ...",Toast.LENGTH_LONG).show()

                }
                UploadState.FINISHED -> {
                    RecordButton.loadMode(false)
                    RecordButton.setProgressColor(Color.GREEN)
                    RecordButton.setPercent(100f)
                    Toast.makeText(this,"Upload Successful",Toast.LENGTH_LONG).show()
                }
                UploadState.FAILED -> {
                    RecordButton.loadMode(false)
                    RecordButton.setProgressColor(Color.RED)
                    RecordButton.setPercent(100f)
                    Toast.makeText(this,"Upload Failed",Toast.LENGTH_LONG).show()
                }
            }
        })


        vm.uploadPercent.observe(this, Observer {
            RecordButton.setPercent(it)
        })

    }

    private fun uploadToServer() {
        vm.startUpload(cam.getPath())
    }

    override fun onFinish() {
        collapseCam()
        cam.endRecord()
        uploadToServer()
    }

    override fun onCancel() {
        collapseCam()
        cam.cancelRecord()
        Toast.makeText(this,"Record Canceled !",Toast.LENGTH_LONG).show()
    }

    override fun onZoomIn(value: Float) {

    }

    override fun onStarting(): Boolean {
        RecordButton.setProgressColor(Color.YELLOW)
        val perm = hasPersmission()
        return if (perm) {
            expandCam()
            cam.startRecord()
            true
        } else
            false
    }

    private fun expandCam() {
        if (width == 0) {
            width = cameraContainer.width
            height = cameraContainer.height
            marginBottom =
                (cameraContainer.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
            marginEnd = (cameraContainer.layoutParams as ConstraintLayout.LayoutParams).marginEnd
        }
        animator?.start()
    }

    private fun collapseCam() {
        animator?.reverse()
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        val animated = animation?.animatedValue as Float
        val reverse = 1 - animated
        val params = cameraContainer.layoutParams as ConstraintLayout.LayoutParams
        params.marginEnd = (marginEnd * reverse).toInt()
        params.bottomMargin = (marginBottom * reverse).toInt()
        params.width = (width + ((mainContainer.width - width) * animated)).toInt()
        params.height = (height + ((mainContainer.height - height) * animated)).toInt()
        cameraContainer.layoutParams = params
    }


    // init Google ads
    private fun initADs() {
        val builder = Builder(this, BuildConfig.ad_banner_id)
            .forUnifiedNativeAd { unifiedNativeAd ->
                populateUnifiedNativeAdView(unifiedNativeAd, ad_unified)
            }


        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .setClickToExpandRequested(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)


        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                Toast.makeText(
                    this@MainActivity,
                    "Ad banner load Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                ad_unified.visibility = View.VISIBLE
            }
        }).build()


        adLoader.loadAd(AdRequest.Builder().build())
    }

    // set view for ads
    private fun populateUnifiedNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {


        // You must call destroy on old ads when you are done with them,
        // otherwise you will have a memory leak.
        currentNativeAd?.destroy()
        currentNativeAd = nativeAd
        // Set the media view.
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)


        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)


        adView.mediaView.setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)
        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }


        adView.setNativeAd(nativeAd)
        val vc = nativeAd.videoController
        if (vc.hasVideoContent()) {
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
            }
        }

    }


    override fun onPause() {
        super.onPause()
        cam.onPause()
    }

    override fun onDestroy() {
        currentNativeAd?.destroy()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            showCamera()
        }

    }

    // check for permissions and return true if all GRANTED
    private fun hasPersmission(): Boolean {
        val WRPerm = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val AUPerm = ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        val CAMPerm = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        showCamera()

        return if (!WRPerm || !AUPerm || !CAMPerm) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
                ),
                REQUEST_CODE
            )
            false
        } else
            true


    }

    // check camera permission and then show camera preview
    private fun showCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraContainer.visibility = View.VISIBLE
        }
    }
}
