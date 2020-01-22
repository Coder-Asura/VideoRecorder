/*
 *
 *   Created by Sina Dalvand on 1/21/2020
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.utils.uploader

import ir.sinadalvand.videorecoreder.model.Upload
import ir.sinadalvand.videorecoreder.protocol.ConnectionInterface
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject


class Uploader(private val file: File,private val callback: UploaderCallback,api: ConnectionInterface) : Callback<Upload> {

    var call: Call<Upload>?

    init {
        val fileBody = FileRequestBody(file, callback)
        val filePart = MultipartBody.Part.createFormData("file", file.name, fileBody)
        call = api.uploadFile(filePart)
    }

    fun startUpload() {
        call?.enqueue(this)
        callback.onStart()
    }


    fun cancelUpload() {
        call?.cancel()
        callback.onCancell()
    }

    override fun onFailure(call: Call<Upload>, t: Throwable) {
        callback.onError()
    }

    override fun onResponse(call: Call<Upload>, response: Response<Upload>) {
        callback.onFinish()
    }

}