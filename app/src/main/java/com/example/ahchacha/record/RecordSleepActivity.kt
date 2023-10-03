package com.example.ahchacha.record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ahchacha.databinding.ActivityRecordSleepBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class RecordSleepActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecordSleepBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordSleepBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

//    private fun initLayout() {
//        binding.text1.setOnClickListener {
////            MaterialTimePicker.Builder().setInputMode(INPUT_MODE_CLOCK)
//            val picker =
//                MaterialTimePicker.Builder()
//                    .setTimeFormat(TimeFormat.CLOCK_12H)
//                    .setHour(12)
//                    .setMinute(10)
//                    .setTitleText("Select Appointment time")
//                    .build()
//        }
//    }

    private fun initLayout() {

        binding.inputSleepStart.isFocusable = false
        binding.inputSleepStart.isFocusableInTouchMode = false
        binding.inputSleepStart.setOnClickListener {
            showTimePicker(binding.inputSleepStart)
        }
        binding.inputSleepEnd.setOnClickListener {
            showTimePicker(binding.inputSleepEnd)
        }

}

    private fun showTimePicker(inputTime: TextInputEditText) {

        inputTime.isFocusable = false
        inputTime.isFocusableInTouchMode = false

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val builder = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTitleText("Select Appointment time")
            .setInputMode(INPUT_MODE_KEYBOARD)

        val picker = builder.build()

        picker.addOnPositiveButtonClickListener {
            val selectedHour = picker.hour
            val selectedMinute = picker.minute
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            inputTime.setText(formattedTime)
        }

        picker.show(supportFragmentManager, "timePicker")
    }
}
