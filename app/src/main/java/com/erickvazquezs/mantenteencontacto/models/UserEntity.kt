package com.erickvazquezs.mantenteencontacto.models

import java.io.Serializable

data class UserEntity(
    val username: String,
    val phoneNumber: String? = null,
    val email: String,
    val avatar: AvatarEntity,
    val password: String
): Serializable