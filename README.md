# Mantente en Contacto

[Video demostrativo](https://youtube.com/shorts/aV_SIPPC9AU?feature=share)
## Problema
En muchas ocasiones olvidamos avisar a nuestros seres queridos que hemos llegado bien a nuestro destino — casa, trabajo, escuela, etc.  
Aunque esta es una tarea sencilla que toma apenas unos segundos, muchas veces se pasa por alto.  
Esto puede generar preocupación, especialmente cuando se debe notificar a varias personas (por ejemplo, mamá, papá, pareja o hermanos).

## Solución
**Mantente en Contacto** es una aplicación móvil que automatiza el envío de notificaciones a familiares o amigos cuando el usuario llega a un destino previamente configurado, sin necesidad de compartir la ubicación en tiempo real ni de enviar mensajes manuales.  
El sistema busca hacerlo de manera **no intrusiva** y **respetuosa con la privacidad** del usuario.

---

## Características Implementadas en la Versión Actual
Esta entrega integra la funcionalidad de ubicación y persistencia de datos.

1. 🗺️ **Geocercas y Ubicación**
- **Google Maps SDK**: Implementación del mapa para la visualización y selección de lugares.
- **Selección de Lugares**: Permite seleccionar y registrar lugares con un toque (Tap) en el mapa.
- **Registro de Lugares**: Los lugares seleccionados se guardan en Firestore.
- **Geofencing Core**: Se genera una Geocerca por cada lugar registrado para detectar la entrada o salida del usuario en el rango definido.
- **Permisos de Ubicación**: Se solicitan los permisos de Localización en tiempo real y Background Location para la detección continua de Geocercas.

2. 📱 **Interfaz y Navegación**  
- **Bottom Navigation**: Se agregó un Bottom Navigation Bar para facilitar el acceso a las vistas principales (e.g., Mapa, Lugares, Configuración).
- **Vistas**: Se implementó una vista específica para el registro de nuevos lugares.

3. 🛡️ **Autenticación y Persistencia**  
- **Registro/Inicio de Sesión**: Implementación completa de la autenticación de usuarios con correo electronico y contraseña usando Firebase Authentication.
- **Persistencia de datos**: Los datos de usuario y los lugares registrados se guardan en Firestore.
- **DataStore**: Se usa Preferences DataStore para controlar que el onboarding se muestre solo la primera vez.

4. 🔔 **Notificaciones**
- **Notificaciones Locales**: Implementación de un sistema de notificaciones para alertar al usuario sobre eventos (por ejemplo, al entrar o salir de una geocerca).
   - Nota: Actualmente, la habilitación de las notificaciones debe ser manual por parte del usuario.

---
## Características Previstas para la Versión Final
1. 🧑‍🤝‍🧑 **Red de Contactos y Grupos**
- **Creación de Grupos**: Permitir a los usuarios crear y nombrar grupos (ej. "Familia", "Trabajo").
- **Sistema de Amistad/Solicitudes**: Implementación de solicitudes de amistad para que los usuarios puedan pertenecer a los grupos.
- **Compartir Lugares**: Habilitar la funcionalidad para compartir los lugares registrados con los grupos creados.

2. 🔔 **Notificaciones Automatizadas e Inteligentes**
- **Envío Automático de Notificaciones**: Implementación del envío de notificaciones a los miembros de los grupos cuando el usuario principal llegue a un destino registrado.
- **Solicitud de Permisos**: Manejo automático de la solicitud de permisos de notificación al instalar la app (en lugar de la habilitación manual).

3. ⚙️ **Configuración Avanzada de Geocercas**
- **Frecuencia de Alerta**: Permitir al usuario definir la frecuencia con la que se enviará el aviso de llegada:
   - Cada vez que llegue.
   - Solo la primera vez que llegue.
   - Cuando llegue y cuando se vaya.
- **Gestión Inteligente de Geocercas**: Dependiendo de la configuración, desactivar y activar automáticamente las Geocercas para optimizar el consumo de batería y la lógica.
- **Recuperación de Cuenta**: Manejar la lógica para permitir la creación o restauración de Geocercas la primera vez que se instala la aplicación en un nuevo dispositivo, si el usuario ya tenía una cuenta existente.
