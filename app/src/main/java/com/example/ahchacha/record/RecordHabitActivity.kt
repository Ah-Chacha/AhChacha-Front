package com.example.ahchacha.record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ahchacha.R
import com.example.ahchacha.databinding.ActivityRecordHabitBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecordHabitActivity : AppCompatActivity() {

    lateinit var binding: ActivityRecordHabitBinding
    var habitData = HabitData(false, false, 0, 0, getCurrentDate())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordHabitBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_record_habit)
        initLayout()
    }

    private fun initLayout() {
        var mindScore = 0
        binding.radiogroupMind.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId){
                R.id.radiobtn_mind1 -> mindScore = 1
                R.id.radiobtn_mind2 -> mindScore = 2
                R.id.radiobtn_mind3 -> mindScore = 3
                R.id.radiobtn_mind4 -> mindScore = 4
                R.id.radiobtn_mind5 -> mindScore = 5
            }
        }
        binding.btnSubmitHabitRecord.setOnClickListener {
            saveHabitData(mindScore)
        }
    }

    private fun saveHabitData(mindScore: Int) {
        val currentDate = getCurrentDate()
        var isDrinked = false
        if(binding.btnDrink.isChecked){
            isDrinked = true
        }

        var isSmoked = false
        if(binding.btnSmoke.isChecked){
            isSmoked = true
        }

        var readingTime = binding.inputReadTime.text.toString().toInt()
        habitData = HabitData(isDrinked, isSmoked, readingTime, mindScore, currentDate)
    }
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

}