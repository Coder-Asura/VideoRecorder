/*
 *
 *   Created by Sina Dalvand on 1/21/2020
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */

package ir.sinadalvand.videorecoreder.utils.uploader

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream


class FileRequestBody(val file: File,val callback: UploaderCallback) : RequestBody() {

    private val DEFAULT_BUFFER_SIZE = 2048

    override fun contentType(): MediaType? {
        return MediaType.parse("*/*")
    }


    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val fileLength: Long = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val inp = FileInputStream(file)
        var uploaded: Long = 0
        try {
            var read: Int = 0
            val handler = Handler(Looper.getMainLooper())
            while (inp.read(buffer).also({ read = it }) != -1) {
                handler.post{
                    callback.onProgressUpdate((100 * uploaded / fileLength).toFloat())
                }
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        }catch (e:Exception){
            e.printStackTrace()
        } finally {
            inp.close()
        }
    }

}