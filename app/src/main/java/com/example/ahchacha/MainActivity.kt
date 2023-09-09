package com.example.ahchacha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ahchacha.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.run {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, HomeFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_report -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_content, ReportFragment())
                            .commitAllowingStateLoss()
                    }
                    R.id.menu_info -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, InfoFragment())
                        .commitAllowingStateLoss()
                }
                }
                true
            }
        }
    }
}