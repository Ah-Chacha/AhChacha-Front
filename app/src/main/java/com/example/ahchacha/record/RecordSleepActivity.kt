package com.example.ahchacha.record

import android.Manifest
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
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Task
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
import java.util.Locale
import java.util.concurrent.TimeUnit

class RecordSleepActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecordSleepBinding
    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    var sleepTimeMillis:Long = 0
    var remTimeMillis:Long =0
    var sleepData = SleepData(sleepTimeMillis, remTimeMillis, emptyList(), getCurrentDate())

    val TAG = "StepCounter"
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
        checkFitPermission()
        postToServer()
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
            launchFit()
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


    //---functions for get google fit data---
    //지금은 fit data 연동 test 위해서 step_count data 가져옴.
    //안드로이드용 fit API 사용한 걸 -> REST API 를 사용해서, 백 단에서 처리한 데이터를 get 하는 작업 필요.

    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .build()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> accessGoogleFit()
                else -> {
                    // Result wasn't from Google Fit
                }
            }
            else -> {
                // Permission not granted
            }
        }
    }
    private fun checkFitPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                1);
        }
    }

    private fun launchFit() {
        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                account,
                fitnessOptions)
        } else {
            accessGoogleFit()
        }

        getGoogleToken()
    }
    private fun accessGoogleFit() {
        val end = LocalDateTime.now()
        val start = end.minusYears(1)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()

        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        Fitness.getHistoryClient(this, account)
            .readData(readRequest)
            .addOnSuccessListener({ response ->
                // Use response data here
                Log.i(TAG, "OnSuccess()")
            })
            .addOnFailureListener({ e -> Log.d(TAG, "OnFailure()", e) })

        getFitData()


    }
    private fun getFitData() {
    // Read the data that's been collected throughout the past week.
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)
        Log.i(TAG, "Range Start: $startTime")
        Log.i(TAG, "Range End: $endTime")

        val readRequest =
            DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build()

        Fitness.getRecordingClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Successfully subscribed!")
                    readData()
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
    }
    private fun readData() {
        //read fit data
        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .readDailyTotal(DataType.TYPE_SLEEP_SEGMENT)
            .addOnSuccessListener { dataSet ->
                var userInputSteps = 0

                for (dp in dataSet.dataPoints) {
                    for (field in dp.dataType.fields) {
                        Log.d("Stream Name : ", dp.originalDataSource.streamName)
                        if (!"user_input".equals(dp.originalDataSource.streamName)) {
                            val steps = dp.getValue(field).asInt()
                            userInputSteps += steps
                        }
                    }
                }

//                binding.textStepCnt.text = userInputSteps.toString()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem getting the step count.", e)
            }

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
    private fun getGoogleToken() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()


        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.silentSignIn()
            .addOnCompleteListener(this) { task ->
                handleSignInResult(task)
            }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken
            Log.i(TAG, "idToken: $idToken")

//            updateUI(account)
        } catch (e: ApiException) {
            Log.w(TAG, "handleSignInResult:error", e)
//            updateUI(null)
        }
    }
}