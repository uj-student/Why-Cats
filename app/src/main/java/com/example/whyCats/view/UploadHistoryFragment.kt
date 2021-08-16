package com.example.whyCats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whyCats.R
import com.example.whyCats.databinding.FragmentUploadHistoryBinding
import com.example.whyCats.model.Cat
import com.example.whyCats.view.adapters.CatLoadStateAdapter
import com.example.whyCats.view.adapters.UploadHistoryAdapter
import com.example.whyCats.viewModel.UserUploadHistoryViewModel
import kotlinx.coroutines.flow.collectLatest

class UploadHistoryFragment : Fragment() {

    private val userUploadHistoryViewModel: UserUploadHistoryViewModel by viewModels()
    private var fragmentUploadHistoryBinding: FragmentUploadHistoryBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUploadHistoryBinding =
            FragmentUploadHistoryBinding.inflate(inflater, container, false)
        return fragmentUploadHistoryBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val uploadAdapter = UploadHistoryAdapter()
        GridLayoutManager(requireContext(), 1, RecyclerView.VERTICAL, false).apply {
            fragmentUploadHistoryBinding?.rvCat?.layoutManager = this
        }
        val footerAdapter = CatLoadStateAdapter {
            uploadAdapter.retry()
        }
        fragmentUploadHistoryBinding?.rvCat?.adapter =
            uploadAdapter.withLoadStateFooter(footerAdapter)

        submitData(uploadAdapter)
        setUpAdapter(uploadAdapter)
    }

    private fun setUpAdapter(adapter: UploadHistoryAdapter) {
        adapter.setOnItemClickListener(object : UploadHistoryAdapter.OnItemClickListener {
            override fun onImageClick(position: Int, cardView: View) {
                val cat = adapter.getCat(position)
                val newCatObject = Cat(
                    id = cat?.id ?: "",
                    url = cat?.url ?: "",
                    width = cat?.width ?: 0,
                    height = cat?.height ?: 0,
                    breeds = null
                )
                // thought it was a good idea to develop have a details page, bit the info isn't all that useful
//                cardView.findNavController().navigate(
//                    UploadHistoryFragmentDirections.actionUploadHistoryFragmentToCatDetailFragment(
//                        newCatObject
//                    )
//                )

            }
        })

        adapter.addLoadStateListener { loadState ->
            fragmentUploadHistoryBinding?.pbNetworkCall?.isVisible =
                loadState.source.refresh is LoadState.Loading
            fragmentUploadHistoryBinding?.errorLayout?.isVisible =
                loadState.source.refresh is LoadState.Error

        }
        fragmentUploadHistoryBinding?.btnRetry?.setOnClickListener { adapter.retry() }
    }

    private fun submitData(adapter: UploadHistoryAdapter) {
        lifecycleScope.launchWhenStarted {
            userUploadHistoryViewModel.uploadHistory.collectLatest {
                    pagedData ->
                adapter.submitData(pagedData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentUploadHistoryBinding = null
    }
}