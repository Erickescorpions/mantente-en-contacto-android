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

- Autenticación segura de usuarios mediante Firebase Authentication.
- Persistencia de información de usuarios y lugares utilizando Firebase Firestore.
- Mapa interactivo con Google Maps SDK, que permite seleccionar ubicaciones directamente mediante gestos (tap).
- Registro de lugares personalizados desde el mapa, incluyendo nombre, coordenadas y radio.
- Creación y gestión de geocercas para detectar automáticamente la llegada del usuario a los lugares registrados.
- Solicitud y manejo de permisos de ubicación en primer plano y en segundo plano (Background Location).
- Envío de notificaciones en segundo plano mediante Firebase Cloud Messaging y Cloud Functions.
- Navegación principal mediante Bottom Navigation Bar para acceder a las secciones de Mapa, Lugares y el perfil del usuario.
- Uso de Preferences DataStore para controlar la visualización del onboarding solo en el primer inicio de la aplicación.
- Pantalla de detección de conectividad, que informa al usuario cuando no hay conexión a internet y reacciona automáticamente al restablecerla.
---
## Proximos pasos
1. Opción para activar o desactivar la detección de llegada en destinos específicos, permitiendo un mayor control sobre el funcionamiento de las geocercas.
2. Configuración de la frecuencia de notificaciones, permitiendo definir cuántas veces al día se desea notificar la llegada a un lugar determinado.
3. Creación y administración de grupos de contactos, con el objetivo de decidir con quién compartir la información de llegada a ciertos lugares.
4. Posibilidad de eliminar y administrar lugares y amigos previamente registrados desde la aplicación.
5. Gestión del perfil del usuario, permitiendo modificar la imagen de perfil y actualizar el correo electrónico asociado a la cuenta.
6. Mejora continua de la interfaz de usuario (UI) y la experiencia de usuario (UX), con el objetivo de que la aplicación sea intuitiva, clara y fácil de usar, incluso para usuarios con poca experiencia técnica.
