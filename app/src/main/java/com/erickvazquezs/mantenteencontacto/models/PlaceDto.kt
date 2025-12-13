package com.erickvazquezs.mantenteencontacto.models

import java.util.Date


data class PlaceDto(
    var id: String? = null,

    val name: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,

    val radius: Int = 0,
    val address: String? = null,

    val createdAt: Date = Date(),
    val userId: String? = null
) {
    constructor() : this(id = null)
}