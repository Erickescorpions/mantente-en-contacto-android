package com.erickvazquezs.mantenteencontacto.utils.permissions

interface PermissionExplanationProvider {
    fun getPermissionText(): String
    fun getExplanation(isNotPermanentlyDeclined: Boolean): String
}