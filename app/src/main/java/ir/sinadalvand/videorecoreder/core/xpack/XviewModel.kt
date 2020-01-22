/*
 *
 *   Created by Sina Dalvand on 8/7/2019
 *   Copyright (c) 2019 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.core.xpack

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


abstract class XviewModel : ViewModel() {


    private var firstFetch = true

    fun isFirstFetch(): Boolean = firstFetch

    fun consumeFetch() {
        firstFetch = false
    }

    protected fun <T> hasValue(data: MutableLiveData<T>): Boolean {
        return data.value != null
    }

    protected fun <T> valueEqualment(newData: T, oldData: T): Boolean {
        return newData == oldData
    }
}