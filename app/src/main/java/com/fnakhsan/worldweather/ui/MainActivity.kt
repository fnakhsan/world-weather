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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.worldweather.R
import com.fnakhsan.worldweather.databinding.ActivityMainBinding
import com.fnakhsan.worldweather.ui.detail.DetailWeatherActivity
import com.fnakhsan.worldweather.ui.utils.BaseSnackBar
import com.fnakhsan.worldweather.ui.utils.EXTRA_LOCATION
import com.fnakhsan.worldweather.ui.utils.EXTRA_WEATHER
import com.fnakhsan.worldweather.ui.utils.UiState
import com.fnakhsan.worldweather.ui.weather.ListWeatherAdapter
import com.fnakhsan.worldweather.ui.weather.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
        viewModel.getFavLocationWeather()
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
                    runOnUiThread {
                        showData(it.data)
                    }
                }
            }
        }

        binding.apply {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { v, actionId, event ->
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    Log.d("search", "sekali sih")
                    if (searchView.text.isNotEmpty()) {
                        val intent = Intent(this@MainActivity, DetailWeatherActivity::class.java)
                        intent.putExtra(EXTRA_LOCATION, searchView.text.toString())
                        startActivity(intent)
                    }
                    searchView.setText("")
                    Log.d("search", "sini")
                    false
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
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.location = location
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.searchWeather(
                            location.latitude,
                            location.longitude
                        ).flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect()
                    }
                }
            }
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
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
        runOnUiThread {
            binding.apply {
                progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
                rvWeather.visibility = if (it) View.INVISIBLE else View.VISIBLE
            }
        }
    }

}