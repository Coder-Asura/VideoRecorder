package ir.sinadalvand.videorecoreder

import com.google.android.gms.ads.MobileAds
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ir.sinadalvand.videorecoreder.di.DaggerVideoComponent

class VidApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this, getString(R.string.ad_application_id))
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerVideoComponent.create()
    }


}