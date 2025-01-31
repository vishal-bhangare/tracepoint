package com.example.tracepoint.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tracepoint.R
import com.example.tracepoint.databinding.FragmentProfileBinding
import com.example.tracepoint.ui.post.PostAdapter
import com.google.android.material.tabs.TabLayout

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private val lostItemsAdapter = PostAdapter()
    private val foundItemsAdapter = PostAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupTabLayout()
        loadUserProfile()
        observeViewModel()

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

    }

    private fun setupRecyclerViews() {
        binding.rvUserPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lostItemsAdapter
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.rvUserPosts.adapter = lostItemsAdapter
                        viewModel.loadUserLostItems()
                    }
                    1 -> {
                        binding.rvUserPosts.adapter = foundItemsAdapter
                        viewModel.loadUserFoundItems()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadUserProfile() {
        viewModel.loadUserProfile()
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            binding.apply {
                tvName.text = user.name
                tvEmail.text = user.email
                tvContact.text = user.contact ?: "No contact provided"
            }
        }

        viewModel.userLostItems.observe(viewLifecycleOwner) { posts ->
            lostItemsAdapter.submitList(posts)
        }

        viewModel.userFoundItems.observe(viewLifecycleOwner) { posts ->
            foundItemsAdapter.submitList(posts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
