package com.example.ahchacha.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ahchacha.R
import com.example.ahchacha.databinding.ActivityUserInfoBinding

class UserInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityUserInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_user_info)

        initLayout()
    }

    private fun initLayout() {
        binding.btnSubmitFitnessRecord.setOnClickListener {
            saveUserInfo()
        }
    }

    private fun saveUserInfo() {
        val userAge = binding.inputUserAge.text.toString().toInt()
        val userWeight = binding.inputUserWeight.text.toString().toInt()
        val userHeight = binding.inputUserHeight.text.toString().toInt()
        val userGenderChecked = binding.genderGroup.checkedRadioButtonId
        val userGender = "none"
        if (userGenderChecked == R.id.btn_male){
            val userGender = "male"
        }else{
            val userGender = "female" }
    }
}