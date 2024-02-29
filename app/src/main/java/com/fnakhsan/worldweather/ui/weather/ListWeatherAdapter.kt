package com.fnakhsan.worldweather.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fnakhsan.core.domain.model.WeatherModel
import com.fnakhsan.worldweather.R
import com.fnakhsan.worldweather.databinding.ItemWeatherBinding
import com.fnakhsan.worldweather.ui.utils.iconUrlMapper

class ListWeatherAdapter : RecyclerView.Adapter<ListWeatherAdapter.ListViewHolder>() {
    private lateinit var binding: ItemWeatherBinding
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: WeatherModel)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        binding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder()
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        holder.setIsRecyclable(false)
    }

    inner class ListViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WeatherModel) {
            binding.apply {
                tvLocation.text = data.location
                Glide.with(itemView.context).load(iconUrlMapper(data.iconUrl)).fitCenter().into(ivWeather)
                tvWeather.text = data.description
                tvTemperature.text =
                    itemView.context.resources.getString(R.string.temperature, data.temperature)
                tvLastUpdate.text = itemView.context.resources.getString(R.string.last_update, data.lastUpdate)
            }
            itemView.setOnClickListener { onItemClickCallback.onItemClicked(data) }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}