package com.example.whyCats.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.whyCats.databinding.CatPreviewBinding
import com.example.whyCats.model.UploadHistoryResponse
import com.example.whyCats.util.loadImage

class UploadHistoryAdapter :
    PagingDataAdapter<UploadHistoryResponse, UploadHistoryAdapter.CatViewHolder>(Comparator) {

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

        fun bindCat(catData: UploadHistoryResponse) {
            var imageName = catData.uploadedFileName
            imageName = imageName.substring(imageName.lastIndexOf("/") + 1)

            var imageUploadTimeStamp = catData.createdAt
            imageUploadTimeStamp = imageUploadTimeStamp.replaceAfter(".", "")
            with(binding) {
                imgCat.loadImage(catData.url)
                catName.text = imageName
                catDateUploaded.visibility = View.VISIBLE
                catDateUploaded.text = imageUploadTimeStamp
            }
        }
    }

    fun getCat(position: Int): UploadHistoryResponse? {
        return getItem(position)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    object Comparator : DiffUtil.ItemCallback<UploadHistoryResponse>() {
        override fun areItemsTheSame(
            oldItem: UploadHistoryResponse,
            newItem: UploadHistoryResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UploadHistoryResponse,
            newItem: UploadHistoryResponse
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onImageClick(position: Int, cardView: View)
    }
}