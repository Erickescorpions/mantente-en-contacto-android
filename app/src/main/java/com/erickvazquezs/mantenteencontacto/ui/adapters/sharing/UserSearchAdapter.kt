package com.erickvazquezs.mantenteencontacto.ui.adapters.sharing

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erickvazquezs.mantenteencontacto.models.UserDto

class UserSearchAdapter(
    private val users: List<UserDto>,
    private val onClickAddUser: (UserDto) -> Unit
): RecyclerView.Adapter<UserSearchViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserSearchViewHolder {
        return UserSearchViewHolder.create(parent, onClickAddUser)
    }

    override fun onBindViewHolder(
        holder: UserSearchViewHolder,
        position: Int
    ) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }
}