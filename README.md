# WeatherApp

## Descripción
WeatherApp es una aplicación que proporciona información meteorológica precisa y actualizada para cualquier ubicación. Con una interfaz intuitiva y fácil de usar, WeatherApp te permite obtener el pronóstico del tiempo actual y futuro en cualquier lugar del mundo.

## Características
- **Pronóstico Actual:** Obtén la información meteorológica actualizada en tiempo real para tu ubicación actual.
- **Pronóstico Extendido:** Accede al pronóstico del tiempo extendido para los próximos días.
- **Detalles Meteorológicos:** Información detallada sobre la temperatura y estado del tiempo.
- **Interfaz Intuitiva:** Diseño limpio y fácil de navegar para una experiencia de usuario fluida.
- **Fondo Personalizado:** El fondo de la aplicación cambia según la hora del día, proporcionando una experiencia visual única y agradable.

## Requerimientos

Para ejecutar esta aplicación, necesitarás tener instalado lo siguiente:

- **Java Development Kit (JDK):** Versión 8 o superior.
- **Android Studio:** Para compilar y ejecutar la aplicación en un emulador o dispositivo físico.
- **Conexión a Internet:** Para obtener los datos meteorológicos en tiempo real.

## Permisos Necesarios

Para garantizar el correcto funcionamiento de la aplicación, se requieren los siguientes permisos en tu dispositivo Android:

- **Acceso a Estado de la Red (`android.permission.ACCESS_NETWORK_STATE`):**
  - Este permiso permite a la aplicación verificar si hay una conexión de red disponible en el dispositivo.
  - Es necesario para determinar si el dispositivo está conectado a Internet y así poder obtener los datos meteorológicos en tiempo real.

- **Acceso a Internet (`android.permission.INTERNET`):**
  - Este permiso permite que la aplicación acceda a la red para realizar solicitudes de red y obtener datos de servicios en línea.
  - Es esencial para que la aplicación pueda descargar y mostrar los datos meteorológicos actualizados desde un servidor remoto.

- **Acceso a Ubicación Exacta (`android.permission.ACCESS_FINE_LOCATION`):**
  - Este permiso permite que la aplicación acceda a la ubicación precisa del dispositivo utilizando GPS u otras tecnologías de localización.
  - Se utiliza para proporcionar pronósticos meteorológicos específicos para la ubicación exacta del usuario.

- **Acceso a Ubicación Aproximada (`android.permission.ACCESS_COARSE_LOCATION`):**
  - Este permiso permite que la aplicación acceda a la ubicación aproximada del dispositivo utilizando métodos menos precisos, como la triangulación de torres de telefonía móvil.
  - Se utiliza para mejorar la precisión de los pronósticos meteorológicos al obtener una ubicación más general del usuario en caso de que el acceso a la ubicación exacta no esté disponible o sea impreciso.

Asegúrate de otorgar estos permisos a la aplicación cuando se te solicite. Puedes administrar los permisos en la configuración de la aplicación en tu dispositivo.


## Capturas de Pantalla
![Primera pantalla](https://github.com/EdgarSuarezMota/ClimateTime/raw/master/Imagen1.png)
![Segunda pantalla](https://github.com/EdgarSuarezMota/ClimateTime/raw/master/Imagen2.png)
![Tercera pantalla](https://github.com/EdgarSuarezMota/ClimateTime/raw/master/Imagen3.png)

## Uso
1. Abre la aplicación.
2. Permite el acceso a tu ubicación actual.
3. Explora el pronóstico del tiempo actual y futuro para esa ubicación.
4. ¡Disfruta de la información meteorológica precisa y detallada!

## Tecnologías Utilizadas
- **Java:** Utilizado para la programación del backend de la aplicación.
- **Retrofit:** Biblioteca de cliente HTTP para Java y Android, utilizada para realizar solicitudes de red de forma sencilla y eficiente.
- **Play Services Location:** Servicio de Google Play utilizado para acceder a la ubicación del dispositivo en aplicaciones de Android.

## Autor
Edgar Suárez Mota - edgarsuarez2004@gmail.com
