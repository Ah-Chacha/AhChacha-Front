package com.example.ahchacha.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.example.ahchacha.MainActivity
import com.example.ahchacha.R
import com.example.ahchacha.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestBinding
    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    val questions = arrayOf("첫번째 문제입니다.", "두번째 문제입니다.", "세번째 문제입니다.")
    val answers = arrayOf("정답1", "정답2", "정답3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        generateQuestion()

        initLayout()
    }

    private fun generateQuestion() {

        if(currentQuestionIndex == questions.size-1){
            binding.btnNext.text = "제출하기"
        }
        binding.questionText.text = questions[currentQuestionIndex]

        binding.btnNext.setOnClickListener {
            checkAnswer()
        }
    }

    private fun checkAnswer() {
        var currentAnswer = binding.answerText.text.toString()
        if(currentAnswer!=null && currentAnswer.equals(answers[currentQuestionIndex])){
            ++correctAnswers
        }
        currentQuestionIndex++
        binding.answerText.text!!.clear()

        if (currentQuestionIndex < questions.size) {
            generateQuestion()
        } else {
            showReport()
        }
        binding.testProgress.text = "$currentQuestionIndex / 3"
    }

    private fun showReport() {
        binding.questionText.text = "Quiz completed!\nYour Score: $correctAnswers / 10"
        binding.questionImg.setImageResource(R.drawable.test_confetti)
        binding.layoutTestingAnswer.visibility = GONE
        binding.btnNext.text = "홈으로"

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initLayout() {

    }
}