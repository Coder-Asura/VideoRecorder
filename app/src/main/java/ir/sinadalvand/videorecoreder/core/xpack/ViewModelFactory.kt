/*
 *
 *   Created by Sina Dalvand on 8/7/2019
 *   Copyright (c) 2019 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.core.xpack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T

}