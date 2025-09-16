package com.erickvazquezs.mantenteencontacto.avatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.erickvazquezs.mantenteencontacto.models.AvatarEntity
import com.erickvazquezs.mantenteencontacto.R

class AvatarListAdapter(val list: List<AvatarEntity>) : RecyclerView.Adapter<AvatarListAdapter.AvatarListViewHolder>() {

    var onItemSelected: ((AvatarEntity) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvatarListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.avatar_item, parent, false)
        return AvatarListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: AvatarListViewHolder,
        position: Int
    ) {
        holder.render(list[position], onItemSelected)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class AvatarListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val img: ImageView? = view.findViewById(R.id.imgAvatar)

        fun render(avatar: AvatarEntity, onItemSelected: ((AvatarEntity) -> Unit)?) {
            img?.setImageResource(avatar.avatarId)
            img?.setOnClickListener {
                onItemSelected?.invoke(avatar)
            }
        }
    }
}