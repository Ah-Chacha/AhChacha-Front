package com.example.ahchacha.record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ahchacha.R
import com.example.ahchacha.databinding.ActivityRecordFitnessBinding
import com.example.ahchacha.databinding.ActivityRecordSleepBinding

class RecordFitnessActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecordFitnessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordFitnessBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_record_fitness)
        initLayout()
    }

    private fun initLayout() {

    }
}