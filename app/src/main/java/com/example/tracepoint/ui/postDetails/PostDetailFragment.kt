package com.example.tracepoint.ui.postDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.tracepoint.R
import com.example.tracepoint.databinding.FragmentPostDetailBinding
import com.example.tracepoint.models.Post
import com.example.tracepoint.models.User
import com.example.tracepoint.utils.Resource
import com.example.tracepoint.utils.SharedPrefsManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostDetailViewModel by viewModels()
    private val args: PostDetailFragmentArgs by navArgs()
    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        setupMap()
        observePostDetails()
        setupContactButton()
        viewModel.loadPostDetails(args.postId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
        }
    }

    private fun observePostDetails() {
        viewModel.postDetails.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { post ->
                        binding.apply {
                            progressBar.visibility = View.GONE
                            tvTitle.text = post.title
                            tvDescription.text = post.description
                            tvType.text = if (post.type) "Found Item" else "Lost Item"
                            if (post.images.isNotEmpty()) {
                                Glide.with(requireContext())
                                    .load(post.images[0])
                                    .into(ivImage)
                            }
                            showLocationOnMap(post)
                        }
                    }
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLocationOnMap(post: Post) {
        googleMap?.let { map ->
            // Check if coordinates exist
            if (post.location.coordinates.size >= 2) {
                val location = LatLng(
                    post.location.coordinates[1],
                    post.location.coordinates[0]
                )
                map.clear()
                map.addMarker(MarkerOptions().position(location))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            } else {
                Log.d("Map", "Invalid coordinates: ${post.location.coordinates}")
            }
        }
    }
    private fun setupContactButton() {
        binding.btnContact.setOnClickListener { view ->
            Toast.makeText(context, "Loading contact details...", Toast.LENGTH_SHORT).show()

            viewModel.postDetails.value?.let { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { viewModel.loadUserDetails(it.author) }
                    }
                    else -> {
                        Log.d("Contact", "Post details not available")
                    }
                }
            }
        }

        viewModel.userDetails.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { showContactDialog(it) }
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Failed to load contact details: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    Log.d("Contact", "Loading user details...")
                }
            }
        }
    }



    private fun showContactDialog(user: User) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Contact Information")
            .setMessage("Email: ${user.email}\nPhone: ${user.contact ?: "Not provided"}")
            .setPositiveButton("Close", null)
            .show()
    }
}
