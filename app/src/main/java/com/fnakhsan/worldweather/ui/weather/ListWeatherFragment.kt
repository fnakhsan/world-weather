package com.fnakhsan.worldweather.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.worldweather.databinding.FragmentListWeatherBinding
import com.fnakhsan.worldweather.ui.utils.BaseSnackBar.errorSnackBar
import com.fnakhsan.worldweather.ui.utils.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListWeatherFragment : Fragment() {
    private var _binding: FragmentListWeatherBinding? = null
    private val binding get() = _binding
    private val viewModel: WeatherViewModel by viewModels()
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

        viewModel.searchWeather("Kudus")
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

    private fun showData(data: List<WeatherModel>) {
        val adapter = ListWeatherAdapter()
        adapter.setOnItemClickCallback(object : ListWeatherAdapter.OnItemClickCallback {
            override fun onItemClicked(data: WeatherModel) {
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