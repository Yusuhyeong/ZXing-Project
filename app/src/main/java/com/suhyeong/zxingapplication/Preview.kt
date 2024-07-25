package com.suhyeong.zxingapplication

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

class Preview(context: Context, private val mCameraId: Int, private val mCallback: OnCameraOpenedListener) : SurfaceView(context), SurfaceHolder.Callback {
    @Suppress("deprecation")
    private var mCamera: Camera? = Camera.open(mCameraId)

    interface OnCameraOpenedListener {
        fun onCameraOpen()
    }

    init {
        setOnTouchListener { _, event ->
            handleTouch(event)
            true
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            if (mCamera == null) {
                mCamera = Camera.open(mCameraId)
            }

            mCallback.onCameraOpen()

            mCamera?.apply {
                setPreviewDisplay(holder)
                startPreview()
            }
        } catch (ex: IOException) {
            mCamera?.release()
            mCamera = null
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mCamera?.apply {
            stopPreview()
            release()
        }
        mCamera = null
    }

    fun getCamera(): Camera? {
        return mCamera
    }

    private fun handleTouch(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val camera = mCamera ?: return
            val parameters = camera.parameters
            val focusMode = parameters.supportedFocusModes
            if (focusMode.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                camera.parameters = parameters
                camera.autoFocus { success, _ ->
                    if (success) {
                        Log.d("zxing", "AutoFocus succeeded")
                    } else {
                        Log.d("zxing", "AutoFocus failed")
                    }
                }
            }
        }
    }
}
