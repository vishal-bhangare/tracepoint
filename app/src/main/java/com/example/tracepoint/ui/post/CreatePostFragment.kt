package com.example.tracepoint.ui.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tracepoint.databinding.FragmentCreatePostBinding
import com.example.tracepoint.models.Location
import com.example.tracepoint.utils.Resource
import com.google.android.gms.maps.model.LatLng

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels()
    private var selectedImages = mutableListOf<Uri>()
    private var selectedLocation: LatLng? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImages.add(uri)
                updateImagePreview()
            }
        }
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

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnAddImage.setOnClickListener {
            launchImagePicker()
        }

        binding.btnSelectLocation.setOnClickListener {
            findNavController().navigate(
                CreatePostFragmentDirections.actionCreatePostFragmentToMapFragment()
            )
        }

        binding.btnSubmit.setOnClickListener {
            submitPost()
        }
    }

    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun updateImagePreview() {
        // Update image preview UI
        binding.tvImageCount.text = "${selectedImages.size} images selected"
    }

    private fun submitPost() {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()
        val isFound = binding.switchType.isChecked

        if (title.isEmpty() || description.isEmpty() || selectedLocation == null) {
            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.createPost(
            title = title,
            description = description,
            location = Location("Point", listOf(selectedLocation!!.longitude, selectedLocation!!.latitude)),
            type = isFound,
            images = selectedImages
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
