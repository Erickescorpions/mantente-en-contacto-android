package com.erickvazquezs.mantenteencontacto.utils.permissions

class BackgroundLocationPermissionExplanationProvider: PermissionExplanationProvider {
    override fun getPermissionText(): String = "Permiso de Ubicación en Segundo Plano"

    override fun getExplanation(isNotPermanentlyDeclined: Boolean): String {
        return if(isNotPermanentlyDeclined){
            "El permiso de ubicación 'Permitir siempre' es crucial. Esto permite a la aplicación enviar el aviso automático a tus contactos, incluso cuando la aplicación está cerrada."
        }else{
            "El permiso de ubicación en segundo plano ha sido denegado de forma permanente. Para que los avisos funcionen automáticamente, ve a Configuración y selecciona 'Permitir siempre' para la ubicación."
        }
    }
}