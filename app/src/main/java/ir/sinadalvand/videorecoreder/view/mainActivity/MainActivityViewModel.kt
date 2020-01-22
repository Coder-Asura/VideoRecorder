/*
 *
 *   Created by Sina Dalvand on 1/21/2020
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.view.mainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.sinadalvand.videorecoreder.core.xpack.XviewModel
import ir.sinadalvand.videorecoreder.protocol.ConnectionInterface
import ir.sinadalvand.videorecoreder.utils.uploader.UploadState
import ir.sinadalvand.videorecoreder.utils.uploader.Uploader
import ir.sinadalvand.videorecoreder.utils.uploader.UploaderCallback
import java.io.File
import javax.inject.Inject

class MainActivityViewModel @Inject constructor() : XviewModel(), UploaderCallback {

    val uploadPercent = MutableLiveData<Float>()
    val uploadState = MutableLiveData<UploadState>()

    private var uploader: Uploader? = null

    @Inject
    lateinit var api: ConnectionInterface

    fun startUpload(path: String) {
        uploader = Uploader(File(path),this,api)
        uploader?.startUpload()
    }


    fun cancellUpload() {
        uploader?.cancelUpload()
    }


    override fun onError() {
        uploadState.postValue(UploadState.FAILED)
    }

    override fun onStart() {
        uploadState.postValue(UploadState.STARTED)
    }

    override fun onFinish() {
        uploadState.postValue(UploadState.FINISHED)
    }

    override fun onCancell() {
        uploadState.postValue(UploadState.FAILED)
    }

    override fun onProgressUpdate(currentpercent: Float) {
        uploadPercent.postValue(currentpercent)
    }


}