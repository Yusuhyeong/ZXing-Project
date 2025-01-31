package com.suhyeong.zxingapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import com.suhyeong.zxingapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // QR 인식 화면으로 이동 하기 전 permission check
        binding.imgCameraOpen.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() { // CAMERA permission check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            val cameraIntent = Intent(this, CameraActivity::class.java)
            startActivity(cameraIntent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // permission 확인 후 CameraActivity로 이동
        if (requestCode == CAMERA_PERMISSION_CODE) { // CAMERA permission 응답 코드 (100)
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val cameraIntent = Intent(this, CameraActivity::class.java)
                startActivity(cameraIntent)
            } else {
                Toast.makeText(this, "rejected permission.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}