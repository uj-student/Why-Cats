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
import androidx.navigation.fragment.findNavController
import com.example.whyCats.BuildConfig
import com.example.whyCats.R
import com.example.whyCats.databinding.FragmentImageUploadBinding
import com.example.whyCats.model.UploadResponse
import com.example.whyCats.util.showSnackBarMessage
import com.example.whyCats.viewModel.ImageUploadViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ImageUploadFragment : Fragment() {

    private val imageUploadViewModel: ImageUploadViewModel by viewModels()
    private var fragmentUploadImageBinding: FragmentImageUploadBinding? = null
    private lateinit var currentPhotoPath: String
    private var photoUri: Uri? = null

    private val imageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            afterImageCapture(result)
            createImageFile()
            photoUri = result
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
        activity?.let { activity ->
            activity.title = getString(R.string.upload_image)
        }
        fragmentUploadImageBinding?.takePictureButton?.setOnClickListener {
            dispatchTakePictureIntent()
        }

        fragmentUploadImageBinding?.getFromGalleryImageButton?.setOnClickListener {
            imageFromGallery.launch("image/*")
        }

        fragmentUploadImageBinding?.uploadImageButton?.setOnClickListener {
            photoUri?.let {
                showProgressDialog()
                imageUploadViewModel.uploadCatImage(currentPhotoPath,
                    { onUploadResponse.onSuccessfulImageUploadComplete() },
                    { onUploadResponse.onImageUploadFailure(null)})
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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir?.exists() == true)
            storageDir.mkdirs()
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
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
        }

        override fun onImageUploadFailure(uploadResponse: Throwable?) {
            fragmentUploadImageBinding?.pbNetworkCall?.visibility = View.GONE
            view?.let {view->
                showSnackBarMessage(view, uploadResponse?.message ?: getString(R.string.error_message))
            }
            resetViews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentUploadImageBinding = null
    }
}