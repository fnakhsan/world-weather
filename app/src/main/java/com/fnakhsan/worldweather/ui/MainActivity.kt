package com.fnakhsan.worldweather.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.fnakhsan.worldweather.R
import com.fnakhsan.worldweather.databinding.ActivityMainBinding
import com.fnakhsan.worldweather.ui.weather.ListWeatherFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager = supportFragmentManager
        val weatherFragment = ListWeatherFragment()
        val fragment = fragmentManager.findFragmentByTag(ListWeatherFragment::class.java.simpleName)

        @SuppressLint("CommitTransaction")
        if (fragment !is ListWeatherFragment) {
            Log.d("Main", "Fragment Name :" + ListWeatherFragment::class.java.simpleName)
            fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, weatherFragment, ListWeatherFragment::class.java.simpleName)
                .commit()
        }
    }
}