package com.lgtm.flashlight

import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.lgtm.flashlight.databinding.ActivityColorPickerBinding

class ColorPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityColorPickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColorPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListeners()

        initBrightnessSeekBar()

        initColorPickerView()
    }

    private fun setOnClickListeners() {
        binding.colorPickerToggleButton.setOnClickListener {
            binding.colorPickerContainer.toggleVisibleInvisible()
        }

        binding.rootLayout.setOnClickListener {
            binding.colorPickerContainer.toggleVisibleInvisible()
        }
    }

    private fun initBrightnessSeekBar() {
        val curBrightnessValue = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS).toFloat()
        binding.brightSeekbar.progress = (curBrightnessValue * 100).toInt()
        binding.brightSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val params = window.attributes
                params.screenBrightness = 1.0f * progress / 100
                window.attributes = params
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                // do nothing
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                // do nothing
            }

        })
    }

    private fun initColorPickerView() {
        binding.colorPickerView.addOnColorChangedListener {
            binding.rootLayout.setBackgroundColor(it)
        }
    }

}