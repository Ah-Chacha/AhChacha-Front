package com.example.ahchacha.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    val BASE_URL: String = "http://3.38.240.16"
    var gson = GsonBuilder().setLenient().create()

    fun getRetrofitInstance(): Retrofit {
        val builder: Retrofit.Builder = Retrofit.Builder()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // 서버의 기본 URL을 지정합니다.
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }
}