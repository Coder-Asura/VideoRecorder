/*
 *
 *   Created by Sina Dalvand on 1/21/2020
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ir.sinadalvand.videorecoreder.view.mainActivity.MainActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun provideMainActivity():MainActivity

}