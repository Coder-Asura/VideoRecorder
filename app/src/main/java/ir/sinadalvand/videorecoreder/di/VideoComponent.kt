/*
 *
 *   Created by Sina Dalvand on 1/21/2020
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ir.sinadalvand.videorecoreder.VidApplication
import ir.sinadalvand.videorecoreder.di.modules.ActivityModule
import ir.sinadalvand.videorecoreder.di.modules.NetworkModule
import ir.sinadalvand.videorecoreder.di.modules.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,NetworkModule::class, ActivityModule::class,ViewModelModule::class])
interface VideoComponent :AndroidInjector<VidApplication>{

}