package com.dicoding.picodiploma.loginwithanimation.view.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dicoding.picodiploma.loginwithanimation.databinding.CardCellBinding
import com.dicoding.picodiploma.loginwithanimation.local.LocalStories
import com.dicoding.picodiploma.loginwithanimation.utility.CardViewHolder

class StoryPagingAdapter: PagingDataAdapter<LocalStories, CardViewHolder>(DIFF_CALLBACK) {
    private var onItemClickCallBack: OnItemClickCallBack? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = CardCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val data = getItem(position)
        Log.d("Data Paging", data.toString())
        if (data != null) {
            holder.bind(data)
        }
        holder.itemView.setOnClickListener {
            if (data != null)
                onItemClickCallBack?.onItemClicked(data)
            else Log.d("Data Clicked Paging 3", "Null")
        }
    }

    interface OnItemClickCallBack{
        fun onItemClicked(data: LocalStories)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LocalStories>() {
            override fun areItemsTheSame(oldItem: LocalStories, newItem: LocalStories): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: LocalStories, newItem: LocalStories): Boolean =
                oldItem == newItem

        }
    }
}