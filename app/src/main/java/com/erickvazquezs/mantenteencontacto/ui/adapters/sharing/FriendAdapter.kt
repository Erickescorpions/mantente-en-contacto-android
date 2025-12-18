package com.erickvazquezs.mantenteencontacto.ui.adapters.sharing

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erickvazquezs.mantenteencontacto.models.UserDto

class FriendAdapter (
    private val friends: List<UserDto>
): RecyclerView.Adapter<FriendViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendViewHolder {
        return FriendViewHolder.create(parent)
    }

    override fun onBindViewHolder(
        holder: FriendViewHolder,
        position: Int
    ) {
        holder.bind(friends[position])
    }

    override fun getItemCount(): Int {
        return friends.size
    }
}