package com.fnakhsan.worldweather.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.worldweather.R
import com.fnakhsan.worldweather.databinding.ActivityDetailWeatherBinding
import com.fnakhsan.worldweather.ui.utils.BaseSnackBar
import com.fnakhsan.worldweather.ui.utils.EXTRA_LOCATION
import com.fnakhsan.worldweather.ui.utils.EXTRA_WEATHER
import com.fnakhsan.worldweather.ui.utils.iconUrlMapper
import com.fnakhsan.worldweather.ui.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailWeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailWeatherBinding
    private val viewModel: DetailWeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val weather = intent.parcelable<WeatherModel>(EXTRA_WEATHER)
        val location = intent.getStringExtra(EXTRA_LOCATION)

        if (weather == null) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (location == null) finishAndRemoveTask()
                else viewModel.searchQueryWeather(location)
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collectLatest {
                        Log.d("search", "banyak")
                        when (it) {
                            is DataResource.Loading -> {
                            }

                            is DataResource.Error -> {
                                runOnUiThread {
                                    BaseSnackBar.errorSnackBar(
                                        binding.root,
                                        this@DetailWeatherActivity,
                                        it.exception.message ?: "",
                                        it.errorCode ?: ""
                                    )
                                }
                            }

                            is DataResource.Success -> {
                                Log.d("search", "sukses")
                                runOnUiThread { showData(it.data!!) }
                            }
                        }
                    }
            }
        } else showData(weather)
    }

    private fun showData(weather: WeatherModel) {
        viewModel.isFavorite(weather.id)

        binding.apply {
            tvLocation.text = weather.location
            Glide.with(this@DetailWeatherActivity).load(iconUrlMapper(weather.iconUrl))
                .fitCenter()
                .into(ivWeather)
            tvWeather.text = weather.description
            tvDateTime.text = weather.datetime
            tvTemperature.text = getString(R.string.temperature, weather.temperature)
            tvFeelsLike.text = getString(R.string.feels_like, weather.feelsLike)
            tvHumidity.text = getString(R.string.humidity, weather.humidity)
            tvWindSpeed.text = getString(R.string.wind_speed, weather.windSpeed)
            tvVisibility.text = getString(R.string.visibility, weather.visibility)
            tvCloudiness.text = getString(R.string.cloudiness, weather.cloudiness)
        }

        viewModel.isFavorite.observe(this) {
            binding.btnFavorite.apply {
                if (it) {
                    setImageResource(R.drawable.ic_favorite_active_48)
                    setOnClickListener {
                        viewModel.setFavorite(weather, false)
                    }
                } else {
                    setImageResource(R.drawable.ic_favorite_default_48)
                    setOnClickListener {
                        viewModel.setFavorite(weather, true)
                    }
                }
            }
        }
    }
}