/*
 *
 *   Created by Sina Dalvand on 1/21/20 8:23 AM
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */
package ir.sinadalvand.videorecoreder.utils.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CamHelper(private var surfaceHolder: SurfaceHolder,val context:Context) : SurfaceHolder.Callback {

    private var camera: Camera? = null
    private var recorder: MediaRecorder? = null
    private var flagRecord: Boolean = false
    private var camProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
    private var path: String = ""

    // set details to surface
    init {
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this)
    }

    fun onPause() {
        try {
            if (flagRecord) {
                endRecord()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        surfaceHolder = holder!!
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        endRecord()
        releaseCamera()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        initCamera(getFrontCam())
    }


    private fun initCamera(type: Int) {

        if (camera != null)
            releaseCamera()

        try {
            camera = Camera.open(type)
            camera?.lock()
            if (type == 1)
                camera?.setDisplayOrientation(90)
            startPreview()
            camera?.unlock()
        } catch (e: Exception) {
            e.printStackTrace()
            releaseCamera()
        }

    }

    fun startRecord(): Boolean {

        path = getVideoFilePath()

        if (recorder == null)
            recorder = MediaRecorder()

        try {
            recorder!!.setCamera(camera)
            recorder!!.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            recorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
            recorder!!.setProfile(camProfile)
            recorder!!.setOrientationHint(270)
            recorder!!.setPreviewDisplay(surfaceHolder.surface)
            recorder?.setOutputFile(path)
            recorder?.prepare()
            recorder?.start()
            flagRecord = true
        } catch (e: Exception) {
            e.printStackTrace()
            recorder!!.reset()
            recorder!!.release()
            recorder = null
            flagRecord = true
            deleteFile(path)
            return false
        }
        return true
    }

    fun endRecord() {
        if (!flagRecord) {
            return
        }
        flagRecord = false
        try {
            if (recorder != null) {
                recorder!!.stop()
                recorder!!.reset()
                recorder!!.release()
                recorder = null
                camera?.unlock()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelRecord() {
        endRecord()
        deleteFile(path)
    }


    // release camera and make it null
    private fun releaseCamera() {
        try {
            if (camera != null) {
                camera?.setPreviewCallback(null)
                camera?.stopPreview()
                camera?.lock()
                camera?.release()
                camera = null
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    // use this method for get front cam id
    private fun getFrontCam(): Int {
        val cameraInfo = CameraInfo()
        val cameraCount: Int = Camera.getNumberOfCameras()
        try {
            for (i in 0 until cameraCount) {
                Camera.getCameraInfo(i, cameraInfo)
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    return i
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return 1
    }


    // start preview
    private fun startPreview() {
        camera?.setPreviewDisplay(surfaceHolder)
        camera?.startPreview()
    }


    // set front to cam
    private fun getCamParams(type: Int): Camera.Parameters {
        val parameters = camera!!.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        parameters.set("cam_mode", type)
        return parameters
    }


    private fun frontCameraRotate(type: Int): Int {
        val info = CameraInfo()
        Camera.getCameraInfo(type, info)
        val degrees: Int = 0
        var result: Int = 0
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        }
        return result
    }


    @SuppressLint("SimpleDateFormat")
    private fun getVideoFilePath(): String {
        val rawPath = ContextCompat.getExternalFilesDirs(context,null).get(0).absolutePath + "/VideoRecorder/"
        val file = File(rawPath)
        file.mkdir()
        return file.absolutePath + "/" + SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date()) + "cameraRecorder.mp4"
    }

    fun deleteFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            if (file.isFile()) {
                file.delete()
            } else {
                val filePaths: Array<String> = file.list()
                for (path in filePaths) {
                    deleteFile(filePath + File.separator.toString() + path)
                }
                file.delete()
            }
        }
    }

    fun getPath(): String {
        return path
    }
}