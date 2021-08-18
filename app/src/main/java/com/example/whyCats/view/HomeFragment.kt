package com.example.whyCats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whyCats.databinding.FragmentHomeBinding
import com.example.whyCats.view.adapters.CatAdapter
import com.example.whyCats.view.adapters.CatLoadStateAdapter
import com.example.whyCats.viewModel.CatDisplayViewModel
import kotlinx.coroutines.flow.collectLatest

class HomeFragment : Fragment() {

    private val catViewModel: CatDisplayViewModel by viewModels()
    private var fragmentHomeBinding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return fragmentHomeBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val catAdapter = CatAdapter()
        GridLayoutManager(requireContext(), 1, RecyclerView.VERTICAL, false).apply {
            fragmentHomeBinding?.rvCat?.layoutManager = this
        }
        val footerAdapter = CatLoadStateAdapter {
            catAdapter.retry()
        }
        fragmentHomeBinding?.rvCat?.adapter = catAdapter.withLoadStateFooter(footerAdapter)

        submitData(catAdapter)
        setUpListeners(catAdapter)
    }

    private fun setUpListeners(adapter: CatAdapter) {
        adapter.setOnItemClickListener(object : CatAdapter.OnItemClickListener {
            override fun onImageClick(position: Int, cardView: View) {
                val cat = adapter.getCat(position)
                cat?.let { _cat ->
                    cardView.findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToCatDetailFragment(_cat)
                    )
                }
            }
        })

        adapter.addLoadStateListener { loadState ->
            fragmentHomeBinding?.pbNetworkCall?.isVisible =
                loadState.source.refresh is LoadState.Loading
            fragmentHomeBinding?.errorLayout?.isVisible =
                loadState.source.refresh is LoadState.Error

        }

        fragmentHomeBinding?.btnRetry?.setOnClickListener { adapter.retry() }
    }

    private fun submitData(adapter: CatAdapter) {
        lifecycleScope.launchWhenStarted {
            catViewModel.cat.collectLatest { pagedData ->
                adapter.submitData(pagedData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentHomeBinding = null
    }
}