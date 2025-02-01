package com.example.tracepoint.ui.postDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tracepoint.R
import com.example.tracepoint.databinding.ItemPostBinding
import com.example.tracepoint.models.Post

class PostAdapter : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) { fun bind(post: Post) {
            binding.apply {
                tvTitle.text = post.title
                tvDescription.text = post.description
                tvType.text = if (post.type) "Found" else "Lost"

                if (post.images.isNotEmpty()) {
                    Glide.with(ivImage)
                        .load(post.images[0])
                        .into(ivImage)
                }

                root.setOnClickListener {
                    val navController = it.findNavController()
                    val currentDestination = navController.currentDestination?.id

                    when (currentDestination) {
                        R.id.homeFragment -> navController.navigate(
                            R.id.action_homeFragment_to_postDetailFragment,
                            Bundle().apply { putString("postId", post._id) }
                        )
                        R.id.profileFragment -> navController.navigate(
                            R.id.action_profileFragment_to_postDetailFragment,
                            Bundle().apply { putString("postId", post._id) }
                        )
                    }
                }

            }
        }

    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
