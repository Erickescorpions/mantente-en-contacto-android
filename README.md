# Mantente en Contacto

## Problema
En muchas ocasiones olvidamos avisar a nuestros seres queridos que hemos llegado bien a nuestro destino — casa, trabajo, escuela, etc.  
Aunque esta es una tarea sencilla que toma apenas unos segundos, muchas veces se pasa por alto.  
Esto puede generar preocupación, especialmente cuando se debe notificar a varias personas (por ejemplo, mamá, papá, pareja o hermanos).

## Solución
**Mantente en Contacto** es una aplicación móvil que automatiza el envío de notificaciones a familiares o amigos cuando el usuario llega a un destino previamente configurado, sin necesidad de compartir la ubicación en tiempo real ni de enviar mensajes manuales.  
El sistema busca hacerlo de manera **no intrusiva** y **respetuosa con la privacidad** del usuario.

---

## Caracteristicas implementadas en esta entrega

1. **Onboarding**
   - Ahora funciona con **ViewPager2** y un **Dots Indicator** para mostrar el progreso de las pantallas.

2. **Navegación**
   - Se reemplazó la navegación con **Intents y Activities** por el **Navigation Component** con **Fragments**.

3. **Registro con Firebase**
   - Se implementó el registro de cuenta usando **Firebase Authentication**.
   - Los datos del usuario se guardan en **Firestore**.

4. **DataStore**
   - Se usa **Preferences DataStore** para mostrar el onboarding solo la primera vez que se abre la app.

---

## Alcances logrados

- **Implementación de Firebase:**  
  Permite enfocar el desarrollo en la funcionalidad principal de la aplicación, facilitando la integración del backend y el manejo de usuarios.

- **Navigation Component:**  
  Mejora la estructura interna de la app al simplificar la navegación entre pantallas y mantener un flujo más ordenado.

- **Autenticación:**  
  Es un punto clave para la aplicación, ya que la idea principal es permitir que el usuario comparta con familiares o amigos información sobre su ubicación o si ha llegado a un destino.

---

## Características previstas para la versión final

- Registrar lugares en un **mapa (Google Maps)**.  
- Crear **grupos** con otros usuarios de la app.  
- Compartir **lugares registrados** con esos grupos.  
- Enviar **notificaciones automáticas** cuando algún usuario llegue a un destino previamente registrado.
