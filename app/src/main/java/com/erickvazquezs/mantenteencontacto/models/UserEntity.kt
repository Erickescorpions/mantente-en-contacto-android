package com.erickvazquezs.mantenteencontacto.models

import java.io.Serializable

data class UserEntity(
    val username: String,
    val email: String,
    val avatarUrl: String,
): Serializable