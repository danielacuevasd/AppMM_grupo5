# App Master Martini - Grupo 5

**Integrantes:** Diego Reyes y Daniela Cuevas
**Asignatura:** Desarrollo de Aplicaciones Móviles (DSY1105) · Ingeniería en Informática · Duoc UC

Aplicación móvil en **Kotlin** y **Jetpack Compose** para el caso de **Master Martini Chile SPA**: una plataforma de contenidos técnicos (recetas, videos y cursos) para emprendedores y profesionales de pastelería, chocolatería, panadería y heladería.

Es un **MVP académico**: todos los datos y contenidos son ficticios.

---

## Funcionalidades

1. **Sesión de usuario:** registro e ingreso con validación por campo en tiempo real (nombre, correo, celular y clave). La clave se guarda como hash SHA-256 y la sesión persiste con DataStore. Si la sesión guardada apunta a un usuario que ya no existe, se cierra.
2. **Perfil del negocio:** nombre, RUT de la empresa (con dígito verificador), tipo de negocio, cantidad de personas y dirección. Foto de perfil desde la cámara o la galería, guardada en el dispositivo.
3. **Encuesta de intereses:** intereses, objetivo y preferencia de recibir novedades.
4. **Catálogo (pantalla Aprender):** recetas, cursos y videos con filtros por tipo y por categoría, y un filtro "Guardados".
5. **Detalle de contenido:** tipo, categoría, duración, nivel y descripción, con opción de guardar o quitar de favoritos (se guardan por usuario en Room).
6. **Notificaciones locales:** al guardar la encuesta con "Recibir novedades" activo, la app pide el permiso y avisa de un contenido según los intereses elegidos.
7. **Diseño adaptable y animaciones:** tres disposiciones de la Home (teléfono, tablet mediana y expandida) con `WindowSizeClass`, barra de navegación inferior en las pantallas principales (Inicio, Aprender y Perfil), y animaciones en el splash, las transiciones, los errores de los campos, el botón principal y la confirmación del registro.

## Recursos nativos

- **Cámara** (permiso `CAMERA`, con `FileProvider`) y **galería** de fotos.
- **Notificaciones locales** (permiso `POST_NOTIFICATIONS` en Android 13 o superior).

---

## Tecnologías

- Kotlin 2.0.21 y Jetpack Compose con Material 3 (BOM 2024.09.00)
- Arquitectura MVVM con repositorios
- Navigation Compose 2.8.5, con transiciones animadas
- Room 2.6.1 (con KSP) y DataStore Preferences 1.1.1
- Corrutinas y StateFlow
- Pruebas: JUnit 4 (más de 70 pruebas unitarias) y una prueba instrumentada de la base de datos

## Flujo de datos

Pantalla → ViewModel → Repositorio → DAO → Room. Las pantallas no contienen reglas: piden al ViewModel que valide y reaccionan al estado. Las reglas de validación están en `validation/`.

## Estructura del código

```text
app/src/main/java/com/example/appmm_grupo5/
├── data/           # Room (DAOs, AppDatabase, DatosEjemplo), SesionDataStore, Seguridad
├── model/          # Entidades y estados de interfaz
├── navigation/     # NavGraph, Screen, NavigationEvent
├── notificaciones/ # Canal y envío de notificaciones
├── repository/     # Repositorios
├── ui/
│   ├── components/ # Componentes reutilizables (campos, botones, barra de navegación, etc.)
│   ├── screens/    # Splash, Login, Registro, Home, Aprender, Detalle, Perfil, Encuesta
│   ├── theme/      # Colores, tipografía y tema
│   └── utils/      # Utilidades de tamaño de pantalla
├── validation/     # Reglas de validación (correo, celular, RUT, clave, etc.)
└── viewmodel/      # ViewModels
```

---

## Cómo ejecutar

**Requisitos:** Android Studio reciente (compatible con AGP 8.13), JDK 17 o superior (sirve el JDK integrado de Android Studio), `compileSdk` 36, `minSdk` 24 y `targetSdk` 36. Las notificaciones se prueban mejor en Android 13 o superior.

1. Abre la carpeta raíz del proyecto con **Open** en Android Studio.
2. Espera la sincronización de Gradle (**Sync Project with Gradle Files**).
3. Elige un emulador o un dispositivo y pulsa **Run** (▶) con la configuración `app`.

No hay usuarios precargados: en la pantalla de Login usa **Regístrate** para crear una cuenta.

**Recorrido sugerido:** crear cuenta → completar el perfil y la encuesta (activar "Recibir novedades" para ver la notificación) → explorar el catálogo y usar los filtros → abrir un contenido y guardarlo como favorito → ver "Guardados" → cambiar la foto de perfil → cerrar sesión.

## Pruebas

```bash
./gradlew :app:testDebugUnitTest          # pruebas unitarias
./gradlew :app:connectedDebugAndroidTest  # prueba de la base de datos (con emulador encendido)
```

---

## Decisiones y límites del MVP

- Los datos y contenidos son ficticios y no hay servidor.
- La clave se guarda con SHA-256 sin sal; en un producto real se usaría un algoritmo para claves (bcrypt o Argon2).
- La notificación es local y de demostración: en un producto real saldría de un servidor o de una tarea programada.
- Mientras se desarrolla, si cambia el esquema de Room la base se recrea (`fallbackToDestructiveMigration`).
- **Mejoras futuras:** eventos e inscripción, panel del equipo, accesibilidad ajustable, GPS y "Mis creaciones".

## Flujo de trabajo

GitHub con `main` estable, `develop` de integración y ramas `feature/*` con Pull Requests revisados por el otro integrante. Tareas y responsables en un tablero de Trello.