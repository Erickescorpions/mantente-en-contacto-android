package com.erickvazquezs.mantenteencontacto.ui.adapters.sharing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erickvazquezs.mantenteencontacto.R
import com.erickvazquezs.mantenteencontacto.databinding.ItemUserSearchBinding
import com.erickvazquezs.mantenteencontacto.models.UserDto

class UserSearchViewHolder(
    private val binding: ItemUserSearchBinding,
    private val onClickAddUser: (UserDto) -> Unit
): RecyclerView.ViewHolder(binding.root) {
    private var currentItem: UserDto? = null
    init {
        binding.btnAddContact.setOnClickListener {
            currentItem?.let(onClickAddUser)
        }
    }

    fun bind(user: UserDto) {
        currentItem = user

        binding.tvUsername.text = user.username
        Glide.with(binding.root)
            .load(user.avatarUrl)
            .error(R.drawable.placeholder_user)
            .into(binding.ivUserAvatar)

        binding.btnAddContact.visibility = View.VISIBLE
        binding.tvFriends.visibility = View.GONE

        if (user.isFriend) {
            binding.btnAddContact.visibility = View.GONE
            binding.tvFriends.visibility = View.VISIBLE
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onClick: (UserDto) -> Unit
        ): UserSearchViewHolder {
            val binding = ItemUserSearchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return UserSearchViewHolder(binding, onClick)
        }
    }
}