package com.lgtm.flashlight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lgtm.flashlight.databinding.ActivityMainBinding
import android.hardware.camera2.CameraManager
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider

// Flash API를 사용하면 권한 요청이 필요 없지만,
// 런타임 권한 요청에 대해 공부하기 위해, Camera API를 사용하자.
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val powerButtonAlphaAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.anim_alpah_repeat)
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory(getSystemService(CAMERA_SERVICE) as CameraManager)
        ).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()

        observeViewModel()
    }

    private fun setClickListeners() {
        binding.powerButton.setOnClickListener {
            viewModel.onPowerButtonClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.isFlashOn.observe(this, { isFlashOn ->
            if (!hasRequiredPermissions()) {
                requestRequiredPermissions()
                return@observe
            }

            binding.powerButtonAnimationView.setVisible(isFlashOn)
            updateAnimation(isFlashOn)
            viewModel.toggleFlashLight()
        })
    }

    private fun updateAnimation(isFlashOn: Boolean) {
        if (isFlashOn) {
            binding.powerButtonAnimationView.startAnimation(powerButtonAlphaAnimation)
        } else {
            binding.powerButtonAnimationView.clearAnimation()
            powerButtonAlphaAnimation.cancel()
            powerButtonAlphaAnimation.reset()
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        REQUIRED_PERMISSIONS.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestRequiredPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.getOrNull(0) != PackageManager.PERMISSION_GRANTED) {
                    val alertDialog = AlertDialog.Builder(this).apply {
                        setTitle("앱 권한 설정 안내")
                        setMessage("앱을 사용하기 위해서 애플리케이션 정보 > 권한의 모든 권한이 필요합니다")
                        setPositiveButton("권한설정") { dialog, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                                Uri.parse("package:" + applicationContext.packageName)
                            )
                            startActivity(intent)
                            dialog.cancel()
                        }
                        setNegativeButton("취소") { dialog, _ ->
                            dialog.cancel()
                        }
                    }
                    alertDialog.show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 1024
        val REQUIRED_PERMISSIONS : Array<String> = arrayOf(
            android.Manifest.permission.CAMERA,
        )
    }
}
