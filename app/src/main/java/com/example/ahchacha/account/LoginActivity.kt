package com.example.ahchacha.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ahchacha.GoogleFitActivitiy
import com.example.ahchacha.MainActivity
import com.example.ahchacha.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
    }

    private fun initLayout() {
        binding.btnGoogle.setOnClickListener{
            val intent = Intent(this, GoogleFitActivitiy::class.java)
            startActivity(intent)
        }
    }
}