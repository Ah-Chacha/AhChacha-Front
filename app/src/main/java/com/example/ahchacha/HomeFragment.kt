package com.example.ahchacha

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ahchacha.account.SettingActivity
import com.example.ahchacha.databinding.FragmentHomeBinding
import com.example.ahchacha.record.RecordFitnessActivity
import com.example.ahchacha.record.RecordSleepActivity


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        binding.iconSetting.setOnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
        }

        binding.layoutRecordSleep.setOnClickListener {
            val intent = Intent(context, RecordSleepActivity::class.java)
            startActivity(intent)
        }

        binding.layoutRecordFitness.setOnClickListener {
            val intent = Intent(context, RecordFitnessActivity::class.java)
            startActivity(intent)
        }


    }
}