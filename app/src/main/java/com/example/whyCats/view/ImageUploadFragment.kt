package com.example.whyCats.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
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
            photoUri = result
            afterImageCapture(result)

            val capturedImage = getCapturedImage(result)
            val selectedFile = bitmapToFile(capturedImage, getFilename())

            currentPhotoPath = selectedFile?.absolutePath
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
        verifyStoragePermissions(requireActivity())
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
            currentPhotoPath?.let { photoPath ->
                showProgressDialog()
                imageUploadViewModel.uploadCatImage(photoPath,
                    { onUploadResponse.onSuccessfulImageUploadComplete() },
                    { onUploadResponse.onImageUploadFailure(null) })
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
            println("Hello1 $photoUri")
            takePicture.launch(photoUri)
        }
    }

    @SuppressLint("NewApi")
    private fun getCapturedImage(selectedPhotoUri: Uri): Bitmap {
        val bitmap = when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                requireContext().contentResolver,
                selectedPhotoUri
            )
            else -> {
                val source = ImageDecoder.createSource(
                    requireContext().contentResolver,
                    selectedPhotoUri
                )
                ImageDecoder.decodeBitmap(source)
            }
        }

        return Bitmap.createScaledBitmap(bitmap, 80, 80, true)
    }

    private fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? {
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (!file?.exists()!!)
                file.mkdirs()

            val tempFile = File.createTempFile(
                fileNameToSave,
                ".jpg",
                file
            ).apply {
                currentPhotoPath = absolutePath
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            file
        }
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun verifyStoragePermissions(activity: Activity) {
        val permission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir?.exists() == true)
            storageDir.mkdirs()
        return File.createTempFile(
            getFilename(),
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private val getFilename: () -> String = { "JPEG_${getDateTimeStamp()}_" }

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
            deleteImageUploadAttempt()
        }

        override fun onImageUploadFailure(uploadResponse: Throwable?) {
            fragmentUploadImageBinding?.pbNetworkCall?.visibility = View.GONE
            view?.let { _view ->
                showSnackBarMessage(
                    _view,
                    uploadResponse?.message ?: getString(R.string.error_message)
                )
            }
            deleteImageUploadAttempt()
            resetViews()
        }
    }

    fun deleteImageUploadAttempt() {
        currentPhotoPath?.let { path ->
            imageUploadViewModel.deleteTempFile(path)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentUploadImageBinding = null
    }
}