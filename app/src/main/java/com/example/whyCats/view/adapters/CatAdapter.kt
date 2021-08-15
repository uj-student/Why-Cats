package com.example.whyCats.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.whyCats.databinding.CatPreviewBinding
import com.example.whyCats.model.Cat
import com.example.whyCats.util.loadImage

class CatAdapter : PagingDataAdapter<Cat, CatAdapter.CatViewHolder>(CatComparator) {

    private lateinit var listener: OnItemClickListener

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val cat = getCat(position)
        cat?.let { _cat ->
            holder.bindCat(_cat)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        return CatViewHolder(
            CatPreviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class CatViewHolder(private val binding: CatPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val catPreviewCard = binding.cvCat

        init {
            catPreviewCard.setOnClickListener { view ->
                listener.onImageClick(bindingAdapterPosition, view)
            }
        }

        fun bindCat(catData: Cat) {
            with(binding) {
                imgCat.loadImage(catData.url)
                catName.text = catData.breeds?.first()?.name
            }
        }
    }

     fun getCat(position: Int): Cat? {
        return getItem(position)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    object CatComparator : DiffUtil.ItemCallback<Cat>() {
        override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onImageClick(position: Int, cardView: View)
    }
}