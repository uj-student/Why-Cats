package com.example.whyCats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.whyCats.R
import com.example.whyCats.databinding.FragmentCatDetailBinding
import com.example.whyCats.model.Cat
import com.example.whyCats.util.NOT_AVAILABLE
import com.example.whyCats.util.loadImage
import com.example.whyCats.util.showElement

class CatDetailFragment : Fragment() {
    private var catDetailFragmentBinding: FragmentCatDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        catDetailFragmentBinding = FragmentCatDetailBinding.inflate(inflater, container, false)
        return catDetailFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val catDetails = CatDetailFragmentArgs.fromBundle(requireArguments())
        showDetails(catDetails.catData)
    }

    private fun showDetails(feline: Cat) {
        if (feline.breeds?.isNotEmpty() == true) {
            catDetailFragmentBinding?.let { fragmentCatDetailBinding ->
                fragmentCatDetailBinding.imgCat.loadImage(feline.url)

                fragmentCatDetailBinding.tvCatName.text =
                    getString(R.string.name, feline.breeds?.first()?.name ?: NOT_AVAILABLE)
                fragmentCatDetailBinding.tvCatName.showElement(feline.breeds?.first()?.name)

                fragmentCatDetailBinding.tvOrigin.text =
                    getString(R.string.origin, feline.breeds?.first()?.origin ?: NOT_AVAILABLE)
                fragmentCatDetailBinding.tvOrigin.showElement(feline.breeds?.first()?.origin)

                fragmentCatDetailBinding.tvDescription.text =
                    getString(
                        R.string.description,
                        feline.breeds?.first()?.description ?: NOT_AVAILABLE
                    )
                fragmentCatDetailBinding.tvDescription.showElement(feline.breeds?.first()?.description)


                fragmentCatDetailBinding.tvUrl.text = getString(
                    R.string.wiki_page_url,
                    feline.breeds?.first()?.wikiUrl ?: NOT_AVAILABLE
                )
                fragmentCatDetailBinding.tvUrl.showElement(feline.breeds?.first()?.wikiUrl)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        catDetailFragmentBinding = null
    }
}