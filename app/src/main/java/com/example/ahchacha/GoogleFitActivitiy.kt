package com.example.ahchacha

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ahchacha.databinding.ActivityGoogleFitActivitiyBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Task
import java.time.LocalDateTime
import java.time.ZoneId
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.SessionReadRequest
import java.util.concurrent.TimeUnit


class GoogleFitActivitiy : AppCompatActivity() {
    lateinit var binding: ActivityGoogleFitActivitiyBinding
    val TAG = "StepCounter"
    private val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
    private val BODY_SENSOR_PERMISSIONS_REQUEST_CODE = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleFitActivitiyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
        checkFitPermission()
    }

    private fun initLayout() {
        binding.btnLaunchFit.setOnClickListener {
            launchFit()
//            getGoogleToken()
        }

        binding.btnHome.setOnClickListener {
//            if(checkPermission == 2){
                accessGoogleFit()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
//            }
        }
    }

    //---functions for get google fit data---
    //지금은 fit data 연동 test 위해서 step_count data 가져옴.
    //안드로이드용 fit API 사용한 걸 -> REST API 를 사용해서, 백 단에서 처리한 데이터를 get 하는 작업 필요.

    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .addDataType(DataType.TYPE_HEART_RATE_BPM)
        .build()

    var checkPermission = 0
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // 권한이 부여된 경우 Google Fit 데이터에 액세스
                Log.w("AccessConfirmed", "Google Fit 권한이 허용되었습니다.")
                ++checkPermission
            } else {
                // 권한이 거부된 경우 처리
                Log.w("AccessRejected", "Google Fit 권한이 거부되었습니다.")
            }
        }

        if (requestCode == BODY_SENSOR_PERMISSIONS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // 권한이 부여된 경우 Google Fit 데이터에 액세스
                Log.w("AccessConfirmed", "Google Fit 권한이 허용되었습니다.!!!1")
                ++checkPermission
            } else {
                // 권한이 거부된 경우 처리
                Log.w("AccessRejected", "Google Fit 권한이 거부되었습니다.!!!")
            }
        }
    }
    private fun checkFitPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.BODY_SENSORS),
                BODY_SENSOR_PERMISSIONS_REQUEST_CODE);
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
            Log.i("accessToken!!!!!!!", "accessToken: ${account.id}")

        } else {
            //권한이 이미 있는 경우
//            accessGoogleFit()
        }
    }
    private fun accessGoogleFit() {
        val end = LocalDateTime.now()
        val start = end.minusYears(1)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()
        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusMonths(1)

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .aggregate(DataType.TYPE_HEART_RATE_BPM)
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()



        val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

        Fitness.getHistoryClient(this, account)
            .readData(readRequest)
            .addOnSuccessListener({ response ->
                // Use response data here
//                Log.i("accessToken", "accessToken: ${account.idToken}")
                Log.i(TAG, "OnSuccess()")
            })
            .addOnFailureListener({ e -> Log.d(TAG, "OnFailure()", e) })
    }
}