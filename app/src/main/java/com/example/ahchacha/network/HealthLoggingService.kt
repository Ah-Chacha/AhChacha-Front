package com.example.ahchacha.network

import com.example.ahchacha.LifeLogData
import com.example.ahchacha.ResponseDataClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HealthLoggingService {
    @POST("/user/login")
    fun postData(
        @Body requestData: LifeLogData
    ): Call<ResponseDataClass>
}