package com.suhyeong.zxingapplication

import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.suhyeong.zxingapplication.databinding.ActivityCameraBinding
import java.util.regex.Pattern

class CameraActivity : AppCompatActivity(), Preview.OnCameraOpenedListener {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var mPreview: Preview
    private lateinit var mPattern: Pattern
    private lateinit var lastResultStr: String
    private lateinit var popupHandler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mPattern = Patterns.WEB_URL
        lastResultStr = "";
        popupHandler = Handler()

        openCamera()
    }

    private fun openCamera() {
        mPreview = Preview(this, Camera.CameraInfo.CAMERA_FACING_BACK, this as Preview.OnCameraOpenedListener)

        binding.flPreview.addView(mPreview)
    }

    override fun onCameraOpen() {
        Log.d("zxing", "onCameraOpen!!")
        val camera = mPreview.getCamera()
        camera?.apply {
            setDisplayOrientation(90)
            setPreviewCallback { data, camera ->
                try {
                    val size = camera.parameters.previewSize
                    val width = size.width
                    val height = size.height

                    // 데이터를 ZXing 라이브러리가 처리할 수 있는 형태로 변환
                    val source = PlanarYUVLuminanceSource(
                        data, width, height, 0, 0, width, height, false
                    )
                    val bitmap = BinaryBitmap(HybridBinarizer(source))
                    val result = MultiFormatReader().decode(bitmap)

                    Log.d("zxing", "result = ${result.text}")

                    if (mPattern.matcher(result.text).matches()) {
                        binding.tvUrl.text = result.text
                        binding.clQr.visibility = View.VISIBLE
                        popupHandler.removeCallbacksAndMessages(null)
                        closePopUpTimer()
                    }
                } catch (e: NotFoundException) {
                    Log.e("zxing", "qr error")
                } catch (e: ArrayIndexOutOfBoundsException) {
                    Log.e("zxing", "zxing data error", e)
                }
            }
        }
    }

    private fun closePopUpTimer() {
        popupHandler.postDelayed({
            Log.d("zxing", "close Bottom Sheet")
            binding.clQr.visibility = View.GONE
            lastResultStr = ""
        }, 5000)
    }
}