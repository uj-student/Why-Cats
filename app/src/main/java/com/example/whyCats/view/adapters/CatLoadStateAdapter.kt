package com.example.whyCats.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.whyCats.databinding.LoadingStateBinding

class CatLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<CatLoadStateAdapter.CatLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: CatLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        CatLoadStateViewHolder(
            LoadingStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), retry
        )

    inner class CatLoadStateViewHolder(
        private val binding: LoadingStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.textViewError.text = loadState.error.localizedMessage
            }
            binding.progressbar.isVisible = loadState is LoadState.Loading
            binding.buttonRetry.isVisible = loadState is LoadState.Error
            binding.textViewError.isVisible = loadState is LoadState.Error
            binding.buttonRetry.setOnClickListener {
                retry()
            }
        }
    }
}