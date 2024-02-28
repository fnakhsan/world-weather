package com.fnakhsan.worldweather.ui.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.worldweather.R
import com.fnakhsan.worldweather.databinding.FragmentListWeatherBinding
import com.fnakhsan.worldweather.ui.detail.DetailWeatherActivity
import com.fnakhsan.worldweather.ui.utils.BaseSnackBar.errorSnackBar
import com.fnakhsan.worldweather.ui.utils.EXTRA_WEATHER
import com.fnakhsan.worldweather.ui.utils.UiState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListWeatherFragment : Fragment() {
    private var _binding: FragmentListWeatherBinding? = null
    private val binding get() = _binding
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListWeatherBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        binding?.rvWeather?.layoutManager = layoutManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getMyLastLocation()
        viewModel.data.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> showLoading(true)
                is UiState.Error -> {
                    showLoading(false)
                    errorSnackBar(
                        binding?.root!!,
                        requireContext(),
                        it.errorMessage ?: "",
                        it.errorCode ?: ""
                    )
                }

                is UiState.Empty -> {
                    showLoading(false)
                    binding?.rvWeather?.visibility = View.INVISIBLE
                }

                is UiState.Success -> {
                    showLoading(false)
                    showData(it.data.toList())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.searchWeather(location.latitude, location.longitude)
                    this.location = location
                } else {
                    Toast.makeText(
                        requireContext(),
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
//        else if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            fusedLocationClient.lastLocation.addOnSuccessListener {
//                if (location != null) {
//                    Log.d("location", "masuk jelek")
//                    this.location = location
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        R.string.error_no_location,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }

    }

    private fun showData(data: List<WeatherModel>) {
        val adapter = ListWeatherAdapter()
        adapter.setOnItemClickCallback(object : ListWeatherAdapter.OnItemClickCallback {
            override fun onItemClicked(data: WeatherModel) {
                val intent = Intent(requireContext(), DetailWeatherActivity::class.java)
                intent.putExtra(EXTRA_WEATHER, data)
                startActivity(intent)
            }
        })
        adapter.differ.submitList(data.toMutableList())
        binding?.rvWeather?.adapter = adapter
    }

    private fun showLoading(it: Boolean) {
        binding?.apply {
            progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
            rvWeather.visibility = if (it) View.INVISIBLE else View.VISIBLE
        }
    }
}