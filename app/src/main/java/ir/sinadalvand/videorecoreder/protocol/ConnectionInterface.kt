/*
 *
 *   Created by Sina Dalvand on 21/1/2020
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.protocol

import ir.sinadalvand.videorecoreder.model.Upload
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url


interface ConnectionInterface {

    @Multipart
    @POST("/uploader.php")
    fun uploadFile(@Part file: MultipartBody.Part?): Call<Upload>

}