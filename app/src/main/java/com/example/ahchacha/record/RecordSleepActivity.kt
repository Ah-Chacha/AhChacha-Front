package com.example.ahchacha.record

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ahchacha.LifeLogData
import com.example.ahchacha.R
import com.example.ahchacha.ResponseDataClass
import com.example.ahchacha.databinding.ActivityRecordSleepBinding
import com.example.ahchacha.network.HealthLoggingService
import com.example.ahchacha.network.RetrofitHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.SessionReadRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class RecordSleepActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecordSleepBinding
    var sleepTimeMillis:Long = 0
    var remTimeMillis:Long =0
    var sleepData = SleepData(sleepTimeMillis, remTimeMillis, emptyList(), getCurrentDate())

    val RC_SIGN_IN = 2

    private var retrofit: Retrofit = RetrofitHelper.getRetrofitInstance()
    var api : HealthLoggingService = retrofit.create(HealthLoggingService::class.java)

    private lateinit var selectedSleepStartTime: List<Int>
    private lateinit var selectedSleepEndTime: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordSleepBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
//        checkFitPermission()
        postToServer()
        requestDataToGoogle()
    }
    private fun initLayout() {
        binding.inputSleepStart.isFocusable = false
        binding.inputSleepStart.isFocusableInTouchMode = false

        binding.inputSleepStart.setOnClickListener {
            showTimePicker(binding.inputSleepStart)
        }

        binding.inputSleepEnd.setOnClickListener {
            showTimePicker(binding.inputSleepEnd)
        }

        binding.btnLaunchFit.setOnClickListener {
//            launchFit()
        }

        binding.btnSubmitSleepRecord.setOnClickListener {
            saveSleepData()
        }
    }
    private fun saveSleepData() {
        val currentDate = getCurrentDate()

        // 각 Chip의 상태를 확인하고, 선택한 경우에만 해당 항목을 리스트에 추가
        val selectedSleepDisorder = mutableListOf<String>()
        if (binding.nightmaresChip.isChecked) {
            selectedSleepDisorder.add("Nightmares")
        }
        if (binding.bathroomChip.isChecked) {
            selectedSleepDisorder.add("Waking up for bathroom trips")
        }
        if (binding.noReasonChip.isChecked) {
            selectedSleepDisorder.add("Waking up for no reason")
        }
        if (binding.breathingChip.isChecked) {
            selectedSleepDisorder.add("Difficulty breathing")
        }
        if (binding.snoringChip.isChecked) {
            selectedSleepDisorder.add("Loud snoring")
        }

        sleepData = SleepData(sleepTimeMillis, remTimeMillis, selectedSleepDisorder, currentDate)
    }

    //현재 서버 통신은 성공, 404 뜨는 상황 (엔드포인트 연결 문제 or 데이터 구조 안맞아서)
    private fun postToServer() {

        val logDataSample = LifeLogData(
            email= "test@test.net",
            password="1q2w3e4r"
        )

        api.postData(logDataSample).enqueue(object :
            Callback<ResponseDataClass> {

            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>
            ) {
                Log.d("api post 성공",response.toString())
                Log.d("api post 성공", response.body().toString())
            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {
                Log.d("api post 실패",t.message.toString())
                Log.d("api post 실패","fail")
            }
        })
    }

    val RC_REQUEST_SLEEP_AND_CONTINUE_SUBSCRIPTION = 77
    val fitnessOptions = FitnessOptions.builder()
        .accessSleepSessions(FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
        .build()
    private fun requestDataToGoogle() {


        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusMonths(1)
        val sessionsClient = Fitness.getSessionsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))

        val SLEEP_STAGE_NAMES = arrayOf(
            "Unused",
            "Awake (during sleep)",
            "Sleep",
            "Out-of-bed",
            "Light sleep",
            "Deep sleep",
            "REM sleep"
        )

        val request = SessionReadRequest.Builder()
            .readSessionsFromAllApps()
            // By default, only activity sessions are included, so it is necessary to explicitly
            // request sleep sessions. This will cause activity sessions to be *excluded*.
            .includeSleepSessions()
            // Sleep segment data is required for details of the fine-granularity sleep, if it is present.
            .read(DataType.TYPE_SLEEP_SEGMENT)
            .setTimeInterval(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
            .build()

        sessionsClient
            .readSession(request)
            .addOnSuccessListener { response ->
                Log.i("SLEEEP DATA", "Sleep success!!!!!")

                for (session in response.sessions) {
//                    val sessionStart = convertEpochTimeToHourMinute(session.getStartTime(TimeUnit.MILLISECONDS))
//                    val sessionEnd = convertEpochTimeToHourMinute(session.getEndTime(TimeUnit.MILLISECONDS))
//                    Log.i("SLEEEP DATA", "Sleep between $sessionStart and $sessionEnd")

                    // If the sleep session has finer granularity sub-components, extract them:
                    val dataSets = response.getDataSet(session)
                    for (dataSet in dataSets) {
                        for (point in dataSet.dataPoints) {
                            val sleepStageVal = point.getValue(Field.FIELD_SLEEP_SEGMENT_TYPE).asInt()
                            val sleepStage = SLEEP_STAGE_NAMES[sleepStageVal]

                            val sessionStartMillis = session.getStartTime(TimeUnit.MILLISECONDS)
                            val sessionEndMillis = session.getEndTime(TimeUnit.MILLISECONDS)
                            val sessionStartDate = convertEpochTimeToHourMinute(sessionStartMillis)
                            val sessionEndDate = convertEpochTimeToHourMinute(sessionEndMillis)
                            Log.i("SLEEEP DATA", "Sleep between $sessionStartDate and $sessionEndDate")

//                            val segmentStart = point.getStartTime(TimeUnit.MILLISECONDS)
//                            val segmentEnd = point.getEndTime(TimeUnit.MILLISECONDS)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.i("SLEEEP DATA", "Failure Response: ", e)
            }
    }

    private fun convertEpochTimeToHourMinute(epochMillis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = Date(epochMillis)
        return dateFormat.format(date)
    }


    //---functions for sub-feature---
    private fun showTimePicker(inputTime: TextInputEditText){
        //time picker 창 열고 값 반환

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

            if (inputTime == binding.inputSleepStart) {
                selectedSleepStartTime = listOf(selectedHour, selectedMinute)
            } else if (inputTime == binding.inputSleepEnd) {
                selectedSleepEndTime = listOf(selectedHour, selectedMinute)
            }

            // 두 시간을 모두 선택했을 때 계산을 수행하거나 다른 작업을 수행할 수 있습니다.
            if (::selectedSleepStartTime.isInitialized && ::selectedSleepEndTime.isInitialized) {
                calculateSleepTime(selectedSleepStartTime, selectedSleepEndTime)
            }
        }

        picker.show(supportFragmentManager, "timePicker")
    }
    private fun calculateSleepTime(sleepStartTime: List<Int>, sleepEndTime: List<Int>) {
        //입력 시간 토대로 총 수면 시간 계산
        val startHour = sleepStartTime[0]
        val startMinute = sleepStartTime[1]
        val endHour = sleepEndTime[0]
        val endMinute = sleepEndTime[1]
        val startCalendar = Calendar.getInstance()
        startCalendar.set(Calendar.HOUR_OF_DAY, startHour)
        startCalendar.set(Calendar.MINUTE, startMinute)

        // 취침 종료 시간의 Calendar 객체 생성
        val endCalendar = Calendar.getInstance()
        endCalendar.set(Calendar.HOUR_OF_DAY, endHour)
        endCalendar.set(Calendar.MINUTE, endMinute)

        // 취침 시간이 전날 밤일 경우
        if (startHour > endHour || (startHour == endHour && startMinute > endMinute)) {
            endCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        sleepTimeMillis = endCalendar.timeInMillis - startCalendar.timeInMillis

        val hours = sleepTimeMillis / (60 * 60 * 1000)
        val minutes = (sleepTimeMillis % (60 * 60 * 1000)) / (60 * 1000)

        binding.textTotalSleepTime.text = "$hours 시간 $minutes 분"
    }
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    //---functions for getting Google Token---

}