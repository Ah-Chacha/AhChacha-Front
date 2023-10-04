package com.example.ahchacha.record

//일 단위로 logging이 이뤄져야하는 Data들 구조 모아뒀습니다.
data class HabitData(
    val drink: Boolean,
    val smoke: Boolean,
    val readingTime: Int,
    val mindScore: Int,
    val currentDate: String    // 현재 날짜 (예: "2023-10-05")
)
data class FitnessData(
    val calorie: Int,
    val step: Int,
    val fitnessTime: Int,
    val currentDate: String    // 현재 날짜 (예: "2023-10-05")
)
data class SleepData(
    val totalSleepTime: Long,   // 총 수면 시간 (분 단위 등)
    val remSleepTime: Long,     // 램 수면 시간 (분 단위 등)
    val selectedSleepDisorder: List<String>, // 수면 장애 요인 리스트
    val currentDate: String    // 현재 날짜 (예: "2023-10-05")
)