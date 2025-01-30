package com.example.tracepoint.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.tracepoint.R
import com.example.tracepoint.databinding.FragmentPostDetailBinding
import com.example.tracepoint.models.Post
import com.example.tracepoint.models.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by viewModels()
    private val args: PostDetailFragmentArgs by navArgs()
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        loadPostDetails()
        setupContactButton()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment

        mapFragment.getMapAsync { map ->
            googleMap = map
            viewModel.postDetails.value?.let { post ->
                showLocationOnMap(post)
            }
        }
    }

    private fun loadPostDetails() {
        viewModel.getPostDetails(args.postId)
        viewModel.postDetails.observe(viewLifecycleOwner) { post ->
            binding.apply {
                tvTitle.text = post.title
                tvDescription.text = post.description
                tvType.text = if (post.type) "Found Item" else "Lost Item"
                showLocationOnMap(post)
            }
        }
    }

    private fun showLocationOnMap(post: Post) {
        googleMap?.let { map ->
            val location = LatLng(
                post.location.coordinates[1],
                post.location.coordinates[0]
            )
            map.addMarker(MarkerOptions().position(location))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private fun setupContactButton() {
        binding.btnContact.setOnClickListener {
            viewModel.getAuthorDetails(viewModel.postDetails.value?.author ?: return@setOnClickListener)
        }

        viewModel.authorDetails.observe(viewLifecycleOwner) { user ->
            // Show contact dialog with user.email and user.contact
            showContactDialog(user)
        }
    }

    private fun showContactDialog(user: User) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Contact Information")
            .setMessage("Email: ${user.email}\nPhone: ${user.contact ?: "Not provided"}")
            .setPositiveButton("Close", null)
            .create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
