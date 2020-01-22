/*
 *
 *   Created by Sina Dalvand on 8/7/2019
 *   Copyright (c) 2019 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.core.xpack

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjection
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import javax.inject.Inject


abstract class XappCompatActivity<T : XviewModel>  : AppCompatActivity() {

    @Inject
    lateinit var vmFactory: ViewModelFactory


    lateinit var vm: T


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        val time = System.currentTimeMillis()
        AndroidInjection.inject(this)
        val passTime = System.currentTimeMillis()
        Timber.e("Time1: ${passTime-time}")

        super.onCreate(savedInstanceState)

        val time3 = System.currentTimeMillis()
        vm = ViewModelProviders.of(this,vmFactory)[getGenericEasy()]
        val passTime3 = System.currentTimeMillis()
        Timber.e("Time2: ${passTime3-time3}")


    }


    /**
     * unfortunately after write "abstract fun setViewModel()" i realize ,
     * can get generic type whit out this func , so fuck that
     * lets use this fucking shit
     * @return Class<T>
     */
    private fun getGenericEasy(): Class<T> {
        val ops = javaClass.genericSuperclass
        val tType = (ops as ParameterizedType).actualTypeArguments[0]
        val className = tType.toString().split(" ")[1]
        return Class.forName(className) as Class<T>
    }


    /**
     * this function should override in fragment that
     * need get parameter from every where before onCreate()
     */
    open fun getArgs() {

    }




}