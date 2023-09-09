package com.example.ahchacha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ahchacha.databinding.ActivityLoginBinding
import com.example.ahchacha.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {

    }

}