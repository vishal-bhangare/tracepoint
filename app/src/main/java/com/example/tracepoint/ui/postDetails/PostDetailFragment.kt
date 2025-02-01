package com.example.tracepoint.ui.postDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        setupMap()
        observePostDetails()
        setupContactButton()

        // Load post details using ID
        viewModel.loadPostDetails(args.postId)
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
                    binding.apply {
                        progressBar.visibility = View.GONE
                        tvTitle.text = result.data?.title
                        tvDescription.text = result.data?.description
                        tvType.text = if (result.data?.type == true) "Found Item" else "Lost Item"

                        // Load first image if available
                        if (result.data?.images?.isNotEmpty() == true) {
                            Glide.with(requireContext())
                                .load(result.data.images[0])
                                .into(ivImage)
                        }

                        // Show location on map
                        result.data?.let { showLocationOnMap(it) }
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
            val location = LatLng(
                post.location.coordinates[1],
                post.location.coordinates[0]
            )
            map.clear()
            map.addMarker(MarkerOptions().position(location))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private fun setupContactButton() {
        binding.btnContact.setOnClickListener {
            viewModel.postDetails.value?.let { result ->
                if (result is Resource.Success) {
                    result.data?.author?.let { it1 -> viewModel.loadUserDetails(it1) }
                }
            }
        }

        // Observe user details
        viewModel.userDetails.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { showContactDialog(it) }
                }
                is Resource.Error -> {
                    Toast.makeText(context, "Failed to load contact details", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Handle loading state if needed
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
