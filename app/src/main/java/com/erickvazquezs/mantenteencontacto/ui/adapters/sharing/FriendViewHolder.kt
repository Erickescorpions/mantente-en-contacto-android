package com.erickvazquezs.mantenteencontacto.ui.adapters.sharing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erickvazquezs.mantenteencontacto.databinding.ItemFriendBinding
import com.erickvazquezs.mantenteencontacto.models.UserDto

class FriendViewHolder(
    private val binding: ItemFriendBinding
): RecyclerView.ViewHolder(binding.root) {
    private var currentItem: UserDto? = null

    init {

    }

    fun bind(user: UserDto) {
        currentItem = user

        binding.tvUsername.text = user.username
        Glide.with(binding.root)
            .load(user.avatarUrl)
            .into(binding.ivUserAvatar)
    }

    companion object {
        fun create(
            parent: ViewGroup
        ): FriendViewHolder {
            val binding = ItemFriendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return FriendViewHolder(binding)
        }
    }
}