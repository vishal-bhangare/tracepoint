package com.example.tracepoint.ui.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tracepoint.databinding.FragmentCreatePostBinding
import com.example.tracepoint.models.Location
import com.example.tracepoint.utils.Resource
import com.example.tracepoint.utils.SharedPrefsManager
import com.google.android.gms.maps.model.LatLng

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }
    private var selectedImages = mutableListOf<Uri>()
    private var selectedLocation: LatLng? = null
    private var currentTitle: String = ""
    private var currentDescription: String = ""

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImages.add(it)
            updateImagePreview()
        }
    }

    private fun launchImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(context, "Select Location First!!", Toast.LENGTH_LONG).show()
        setFragmentResultListener("location_request") { _, bundle ->
            val latitude = bundle.getDouble("latitude")
            val longitude = bundle.getDouble("longitude")
            selectedLocation = LatLng(latitude, longitude)
            updateLocationDisplay()
        }
        setupListeners()
        observeViewModel()
        // Restore saved values if any
        binding.etTitle.setText(currentTitle)
        binding.etDescription.setText(currentDescription)
    }

    private fun setupListeners() {
        binding.btnAddImage.setOnClickListener {
            launchImagePicker()
        }

        binding.btnSelectLocation.setOnClickListener {
            currentTitle = binding.etTitle.text.toString()
            currentDescription = binding.etDescription.text.toString()
            findNavController().navigate(
                CreatePostFragmentDirections.actionCreatePostFragmentToMapFragment()
            )
        }

        binding.btnSubmit.setOnClickListener {
            submitPost()
        }
    }

//    private fun launchImagePicker() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        imagePickerLauncher.launch(intent)
//    }

    private fun updateImagePreview() {
        // Update image preview UI
        binding.tvImageCount.text = "${selectedImages.size} images selected"
    }

    private fun submitPost() {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()
        val author = getCurrentUserId()
        val isFound = binding.switchType.isChecked

        if (title.isEmpty() || description.isEmpty() || selectedLocation == null) {
            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.createPost(
            title = title,
            description = description,
            author = author,
            location = Location("Point", listOf(selectedLocation!!.longitude, selectedLocation!!.latitude)),
            type = isFound,
            images = selectedImages
        )
    }
    private fun updateLocationDisplay() {
        selectedLocation?.let { location ->
            binding.tvSelectedLocation.apply {
                visibility = View.VISIBLE
                text = "Selected Location: Lat: ${location.latitude}, Long: ${location.longitude}"
            }
        }
    }
    private fun observeViewModel() {
        viewModel.postCreationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Show loading state
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        binding.etTitle.setText(currentTitle)
        binding.etDescription.setText(currentDescription)
        Log.e("Loc", "Loc ${selectedLocation}")
        updateLocationDisplay()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getCurrentUserId(): String {
        return SharedPrefsManager.getUserId()!!
    }
}
