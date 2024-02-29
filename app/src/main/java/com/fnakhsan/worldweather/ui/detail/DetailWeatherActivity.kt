package com.fnakhsan.worldweather.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.worldweather.R
import com.fnakhsan.worldweather.databinding.ActivityDetailWeatherBinding
import com.fnakhsan.worldweather.ui.utils.EXTRA_WEATHER
import com.fnakhsan.worldweather.ui.utils.iconUrlMapper
import com.fnakhsan.worldweather.ui.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailWeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailWeatherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val weather = intent.parcelable<WeatherModel>(EXTRA_WEATHER)

        binding.apply {
            tvLocation.text = weather?.location
            Glide.with(this@DetailWeatherActivity).load(iconUrlMapper(weather?.iconUrl ?: "")).fitCenter()
                .into(ivWeather)
            tvWeather.text = weather?.description
            tvDateTime.text = weather?.datetime
            tvTemperature.text = getString(R.string.temperature, weather?.temperature ?: 0.0)
            tvFeelsLike.text = getString(R.string.feels_like, weather?.feelsLike ?: 0.0)
            tvHumidity.text = getString(R.string.humidity, weather?.humidity ?: 0)
            tvWindSpeed.text = getString(R.string.wind_speed, weather?.windSpeed ?: 0.0)
            tvVisibility.text = getString(R.string.visibility, weather?.visibility ?: 0)
            tvCloudiness.text = getString(R.string.cloudiness, weather?.cloudiness ?: 0)
        }
    }
}