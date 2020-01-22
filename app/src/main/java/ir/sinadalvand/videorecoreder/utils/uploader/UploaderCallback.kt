/*
 *
 *   Created by Sina Dalvand on 1/21/2020
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.utils.uploader

interface UploaderCallback{
    fun onError()
    fun onStart()
    fun onFinish()
    fun onCancell()
    fun onProgressUpdate(currentpercent: Float)
}