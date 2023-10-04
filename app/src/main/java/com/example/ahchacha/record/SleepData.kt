package com.example.ahchacha.record

data class SleepData(
    val totalSleepTime: Long,   // 총 수면 시간 (분 단위 등)
    val remSleepTime: Long,     // 램 수면 시간 (분 단위 등)
    val selectedSleepDisorder: List<String>, // 수면 장애 요인 리스트
    val currentDate: String    // 현재 날짜 (예: "2023-10-05")
)