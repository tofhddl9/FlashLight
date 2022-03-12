package com.lgtm.flashlight

import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel(
    private val cameraManager: CameraManager
) : ViewModel() {

    private val _isFlashOn: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFlashOn: LiveData<Boolean>
        get() = _isFlashOn

    fun onPowerButtonClicked() {
        val isFlashOn = _isFlashOn.value!!
        _isFlashOn.value = !isFlashOn
    }

    fun toggleFlashLight() {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, _isFlashOn.value!!)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}

class ViewModelFactory(
    private val cameraManager: CameraManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(cameraManager) as T
        }
        throw IllegalArgumentException("cannot create MainViewModel")
    }
}
