package com.erickvazquezs.mantenteencontacto.models

import java.util.Date

data class UserDto(
    var id: String? = null,

    val username: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val createdAt: Date = Date()
) {
    constructor() : this(id = null)
}