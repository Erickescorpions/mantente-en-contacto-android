package com.erickvazquezs.mantenteencontacto.ui.adapters.sharing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        // TODO: user Glide para traer la imagen del usuario
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