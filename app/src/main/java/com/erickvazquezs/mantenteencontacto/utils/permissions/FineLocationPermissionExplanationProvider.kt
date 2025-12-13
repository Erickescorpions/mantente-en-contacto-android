package com.erickvazquezs.mantenteencontacto.utils.permissions

class FineLocationPermissionExplanationProvider: PermissionExplanationProvider {
    override fun getPermissionText(): String = "Permiso de Ubicación Precisa (En Uso)"

    override fun getExplanation(isNotPermanentlyDeclined: Boolean): String {
        return if(isNotPermanentlyDeclined){
            "Necesitamos este permiso para mostrar tu ubicación en el mapa, crear destinos y calcular tu posición dentro de las zonas de aviso."
        }else{
            "El acceso a tu ubicación precisa ha sido negado permanentemente. Por favor, ve a la Configuración de la aplicación y habilita el permiso de ubicación."
        }
    }
}