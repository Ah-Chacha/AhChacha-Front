package com.example.ahchacha.report

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ahchacha.R
import com.example.ahchacha.databinding.ActivityLoginBinding
import com.example.ahchacha.databinding.ActivityReportSleepBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ReportSleepActivity : AppCompatActivity() {

    lateinit var binding: ActivityReportSleepBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportSleepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {
        initBarChart(binding.chart)
        setData(binding.chart)
        initPieChart(binding.pieChart)
    }

    private fun initPieChart(piechart: PieChart) {

        val centerText = "80%" // 가운데에 표시할 텍스트
        piechart.centerText = centerText

        val progressValue = 80f
        val remainingValue = 100f - progressValue

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(progressValue, ""))
        entries.add(PieEntry(remainingValue, ""))

        with(piechart) {
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 60f
            setHoleColor(Color.WHITE)
            transparentCircleRadius = 0f
            animateY(1000, Easing.EaseInOutCubic)
            legend.isEnabled = false
            setDrawRoundedSlices(true)
        }

        val dataSet: PieDataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(getColor(R.color.md_theme_light_tertiary), Color.GRAY) // 진행률과 나머지의 색상

        val pieData: PieData = PieData(dataSet)
        pieData.setValueTextSize(0f) // 값 텍스트 크기를 0으로 설정하여 모두 숨김
        pieData.setValueTextColor(Color.TRANSPARENT) // 값 텍스트 색상을 투명으로 설정하여 모두 숨김
        piechart.data = pieData
    }

    private fun initBarChart(barChart: BarChart) {
        // 차트 회색 배경 설정 (default = false)
        barChart.setDrawGridBackground(false)
        // 막대 그림자 설정 (default = false)
        barChart.setDrawBarShadow(false)
        // 차트 테두리 설정 (default = false)
        barChart.setDrawBorders(false)

        val description = Description()
        // 오른쪽 하단 모서리 설명 레이블 텍스트 표시 (default = false)
        description.isEnabled = false
        barChart.description = description
        barChart.setDrawBarShadow(false)

        // X, Y 바의 애니메이션 효과
        barChart.animateY(1000)
        barChart.animateX(1000)

        val leftAxis: YAxis = barChart.axisLeft
        // 좌측 선 설정 (default = true)
        leftAxis.setDrawAxisLine(false)
        // 좌측 텍스트 컬러 설정
        leftAxis.textColor = Color.BLACK
        leftAxis.axisMinimum = 1f // 최소 값 설정
        leftAxis.axisMaximum = 10f // 최대 값 설정
        leftAxis.granularity = 2f // 간격을 1로 설정하여 1씩 증가하도록 함
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawLabels(false)
        // 우측 레이블 숨김
        val rightAxis: YAxis = barChart.axisRight
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)

        // 바차트의 타이틀
        val legend: Legend = barChart.legend
        // 범례 모양 설정 (default = 정사각형)
        legend.form = Legend.LegendForm.LINE
        // 타이틀 텍스트 사이즈 설정
        legend.textSize = 16f
        // 타이틀 텍스트 컬러 설정
        legend.textColor = Color.BLACK
        // 범례 위치 설정
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        // 범례 방향 설정
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        // 차트 내부 범례 위치하게 함 (default = false)
        legend.setDrawInside(false)
    }

    private fun setData(barChart: BarChart) {

        // Zoom In / Out 가능 여부 설정
        barChart.setScaleEnabled(false)

        val valueList = ArrayList<BarEntry>()
        val title = "이번 주 수면시간"

        // 월요일부터 일요일까지의 데이터
        val days = arrayOf("월", "화", "수", "목", "금", "토", "일")
        val sleepHours = floatArrayOf(6f, 7f, 6.5f, 7.5f, 6.7f, 8f, 7.2f)

        for (i in days.indices) {
            valueList.add(BarEntry(i.toFloat(), sleepHours[i]))
        }

        val barDataSet = BarDataSet(valueList, title)
        // 바 색상 설정 (ColorTemplate.LIBERTY_COLORS)
        barDataSet.setColors(
            Color.rgb(207, 248, 246), Color.rgb(148, 212, 212), Color.rgb(136, 180, 187),
            Color.rgb(118, 174, 175), Color.rgb(42, 109, 130)
        )
        barDataSet.valueTextSize = 12f // 크기를 원하는 값으로 조정
        barDataSet.formSize = 15f

        val data = BarData(barDataSet)

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(days) // x축 레이블 설정
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.textColor = Color.BLACK
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)

        barChart.data = data
        barChart.invalidate()
    }
}