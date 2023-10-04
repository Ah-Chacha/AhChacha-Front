package com.example.ahchacha.record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.ahchacha.R
import com.example.ahchacha.databinding.ActivityRecordFitnessBinding
import com.example.ahchacha.databinding.ActivityRecordSleepBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecordFitnessActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecordFitnessBinding

    var fitnessData = FitnessData(0,0,0,getCurrentDate())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordFitnessBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_record_fitness)
        initLayout()
    }

    private fun initLayout() {
        binding.btnSubmitFitnessRecord.setOnClickListener {
            saveFitnessData()
        }

        binding.inputTotalFit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 입력하기 전에 호출됩니다.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출됩니다.
                val newText = s.toString()
                // newText에 사용자의 입력이 포함됩니다. 여기서 원하는 처리를 수행하세요.
            }

            override fun afterTextChanged(s: Editable?) {
                // 입력이 완료된 후에 호출됩니다.)
                val timePattern = Regex("""^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$""")

                if (s!!.matches(timePattern)) {
                    // 올바른 시간 형식인 경우, 시간을 저장하거나 처리할 수 있습니다.
                    // 여기에 저장 또는 처리 로직 추가
                } else {
                    // 오류 메시지를 사용자에게 표시
                    binding.inputTotalFit.setText("00:00")
                    Toast.makeText(this@RecordFitnessActivity, "올바른 시간 형식을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun saveFitnessData() {
        val currentDate = getCurrentDate()
        val totalCalroies = binding.inputTotalCal.text.toString().toInt()
        val totalSteps = binding.inputTotalStep.text.toString().toInt()
        val fitnessTime = binding.inputTotalFit.text.toString().toInt()

        fitnessData = FitnessData(totalCalroies, totalSteps, fitnessTime, currentDate)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }
}