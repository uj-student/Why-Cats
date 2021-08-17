package com.example.whyCats.view

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.whyCats.BuildConfig
import com.example.whyCats.R
import com.example.whyCats.databinding.FragmentImageUploadBinding
import com.example.whyCats.util.getDateTimeStamp
import com.example.whyCats.util.showSnackBarMessage
import com.example.whyCats.viewModel.ImageUploadViewModel
import java.io.File
import java.io.IOException


class ImageUploadFragment : Fragment() {

    private val imageUploadViewModel: ImageUploadViewModel by viewModels()
    private var fragmentUploadImageBinding: FragmentImageUploadBinding? = null
    private var currentPhotoPath: String? = null
    private var photoUri: Uri? = null

    private val imageFromGallery =
        // need to come back here and clean up
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            afterImageCapture(result)
            createImageFile().also { file ->
                photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    file
                )
            }
//            photoUri = result
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                photoUri?.let { photoURI ->
                    afterImageCapture(photoURI)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUploadImageBinding = FragmentImageUploadBinding.inflate(inflater, container, false)
        return fragmentUploadImageBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
    }

    private fun setUpListeners() {
        fragmentUploadImageBinding?.takePictureButton?.setOnClickListener {
            dispatchTakePictureIntent()
        }

        fragmentUploadImageBinding?.getFromGalleryImageButton?.setOnClickListener {
            imageFromGallery.launch("image/*")
        }

        fragmentUploadImageBinding?.uploadImageButton?.setOnClickListener {
            photoUri?.let { _ ->
                currentPhotoPath?.let { photoPath ->
                    showProgressDialog()
                    imageUploadViewModel.uploadCatImage(photoPath,
                        { onUploadResponse.onSuccessfulImageUploadComplete() },
                        { onUploadResponse.onImageUploadFailure(null) })
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
        photoFile?.also { file ->
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
            takePicture.launch(photoUri)
        }
    }

    private fun setUriForGalleryImage() {

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val filename = "JPEG_${getDateTimeStamp()}_"
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir?.exists() == true)
            storageDir.mkdirs()
        return File.createTempFile(
            filename,
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun afterImageCapture(imageUri: Uri) {
        fragmentUploadImageBinding?.imageCaptureContainer?.visibility = View.GONE
        fragmentUploadImageBinding?.imagePreviewContainer?.visibility = View.VISIBLE
        fragmentUploadImageBinding?.pictureImage?.setImageURI(imageUri)
    }

    private fun showProgressDialog() {
        fragmentUploadImageBinding?.imageCaptureContainer?.visibility = View.GONE
        fragmentUploadImageBinding?.imagePreviewContainer?.visibility = View.GONE
        fragmentUploadImageBinding?.pbNetworkCall?.visibility = View.VISIBLE
    }

    private fun resetViews() {
        fragmentUploadImageBinding?.imageCaptureContainer?.visibility = View.VISIBLE
        fragmentUploadImageBinding?.pbNetworkCall?.visibility = View.GONE
    }

    interface ImageUploadCallback {
        fun onSuccessfulImageUploadComplete()
        fun onImageUploadFailure(uploadResponse: Throwable?)
    }

    private val onUploadResponse = object : ImageUploadCallback {
        override fun onSuccessfulImageUploadComplete() {
            fragmentUploadImageBinding?.pbNetworkCall?.visibility = View.GONE
            view?.findNavController()
                ?.navigate(ImageUploadFragmentDirections.actionUploadImageFragmentToHomeFragment())
            view?.let { _view ->
                showSnackBarMessage(_view, getString(R.string.upload_successful))
            }
        }

        override fun onImageUploadFailure(uploadResponse: Throwable?) {
            fragmentUploadImageBinding?.pbNetworkCall?.visibility = View.GONE
            view?.let { _view ->
                showSnackBarMessage(
                    _view,
                    uploadResponse?.message ?: getString(R.string.error_message)
                )
            }
            resetViews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentUploadImageBinding = null
    }
}