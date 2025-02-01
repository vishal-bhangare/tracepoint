package com.example.tracepoint.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tracepoint.R
import com.example.tracepoint.databinding.FragmentHomeBinding
import com.example.tracepoint.ui.postDetails.PostAdapter
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val lostItemsAdapter = PostAdapter()
    private val foundItemsAdapter = PostAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(context, "Developed by Vishal Bhangare", Toast.LENGTH_LONG).show()
        setupRecyclerViews()
        setupTabLayout()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        binding.rvLostItems.apply {
            adapter = lostItemsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.rvFoundItems.apply {
            adapter = foundItemsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.rvLostItems.visibility = View.VISIBLE
                        binding.rvFoundItems.visibility = View.GONE
                    }
                    1 -> {
                        binding.rvLostItems.visibility = View.GONE
                        binding.rvFoundItems.visibility = View.VISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupFab() {
        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.lostItems.observe(viewLifecycleOwner) { posts ->
            lostItemsAdapter.submitList(posts)
        }

        viewModel.foundItems.observe(viewLifecycleOwner) { posts ->
            foundItemsAdapter.submitList(posts)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
