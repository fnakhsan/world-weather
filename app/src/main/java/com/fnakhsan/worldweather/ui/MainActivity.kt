package com.fnakhsan.worldweather.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fnakhsan.core.data.base.DataResource
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.worldweather.R
import com.fnakhsan.worldweather.databinding.ActivityMainBinding
import com.fnakhsan.worldweather.ui.detail.DetailWeatherActivity
import com.fnakhsan.worldweather.ui.utils.BaseSnackBar
import com.fnakhsan.worldweather.ui.utils.EXTRA_WEATHER
import com.fnakhsan.worldweather.ui.utils.UiState
import com.fnakhsan.worldweather.ui.weather.ListWeatherAdapter
import com.fnakhsan.worldweather.ui.weather.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvWeather.layoutManager = layoutManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()

        viewModel.data.observe(this) {
            when (it) {
                is UiState.Loading -> showLoading(true)
                is UiState.Error -> {
                    showLoading(false)
                    BaseSnackBar.errorSnackBar(
                        binding.root,
                        this,
                        it.errorMessage ?: "",
                        it.errorCode ?: ""
                    )
                }

                is UiState.Empty -> {
                    showLoading(false)
                    binding.rvWeather.visibility = View.INVISIBLE
                }

                is UiState.Success -> {
                    showLoading(false)
                    Log.d("data", "${it.data}")
                    runOnUiThread {
                        showData(it.data.toList())
                    }
                }
            }
        }

        binding.apply {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { v, actionId, event ->
                    Log.d("search", "$v : $actionId : $event")
                    searchBar.setText(searchView.text)
                    val text = searchView.text.toString()
                    if (text.isNotBlank()) {
                        viewModel.searchWeather(searchView.text.toString())
                            .observe(this@MainActivity) {
                                when (it) {
                                    is DataResource.Loading -> {
                                        showLoading(true)
                                    }

                                    is DataResource.Success -> {
                                        showLoading(false)
                                        runOnUiThread() {
                                            val intent = Intent(this@MainActivity, DetailWeatherActivity::class.java)
                                            intent.putExtra(EXTRA_WEATHER, it.data)
                                            startActivity(intent)
                                        }
                                    }

                                    is DataResource.Error -> {
                                        showLoading(false)
                                        BaseSnackBar.errorSnackBar(
                                            binding.root,
                                            this@MainActivity,
                                            it.exception.message ?: "",
                                            it.errorCode ?: ""
                                        )
                                    }
                                }
                            }
                    }
                    searchView.setText("")
                    searchView.hide()
                    true
                }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.searchWeather(
                        location.latitude,
                        location.longitude
                    )
                    viewModel.getFavLocationWeather()
                    this.location = location
                } else {
                    Toast.makeText(
                        this,
                        R.string.error_no_location,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showData(data: List<WeatherModel>) {
        val adapter = ListWeatherAdapter()
        adapter.setOnItemClickCallback(object : ListWeatherAdapter.OnItemClickCallback {
            override fun onItemClicked(data: WeatherModel) {
                val intent = Intent(this@MainActivity, DetailWeatherActivity::class.java)
                intent.putExtra(EXTRA_WEATHER, data)
                startActivity(intent)
            }
        })
        adapter.differ.submitList(data.toMutableList())
        binding.rvWeather.adapter = adapter
    }

    private fun showLoading(it: Boolean) {
        binding.apply {
            progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
            rvWeather.visibility = if (it) View.INVISIBLE else View.VISIBLE
        }
    }

}