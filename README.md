# App Master Martini - Grupo 5
Integrantes: Diego Reyes y Daniela Cuevas

Aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose** orientada a la capacitación, aprendizaje y consulta de recetas, cursos y videos para profesionales del sector de pastelería, chocolatería, panadería y heladería (Master Martini).

Este proyecto forma parte de la asignatura **Desarrollo de Aplicaciones Móviles** (4º semestre, Ingeniería en Informática, DUOC UC).

---

## Descripción del Proyecto

**App MasterMartini** es una plataforma interactiva diseñada para emprendedores y profesionales del rubro gastronómico. Permite explorar un catálogo diverso de contenido educativo, gestionar un perfil comercial, guardar recetas y cursos favoritos, realizar encuestas sobre hábitos del negocio y recibir notificaciones informativas.

La interfaz está construida completamente con **Jetpack Compose (Material 3)** e incorpora diseño adaptable (Responsive UI) para garantizar una experiencia de usuario fluida en teléfonos, plegables y tablets.

---

## Características Principales

1. **Gestión de Usuarios y Sesiones:**
   - Registro e Inicio de Sesión con validaciones en tiempo real (correo, RUT, contraseña).
   - Encriptación/hashing seguro de contraseñas.
   - Persistencia de sesión mediante **Preferences DataStore** para mantener al usuario autenticado.

2. **Catálogo de Contenidos:**
   - Exploración de Recetas, Cursos y Videos educativos.
   - Filtrado dinámico por categorías (Galletas, Chocolatería, Masas de hoja, Pastelería, Heladería, Horneo, Empanadas, Bollería).
   - Buscador e indicadores de nivel de dificultad y duración.

3. **Detalle e Interacción:**
   - Vista en detalle con información paso a paso de cada preparación o curso.
   - Marcar/desmarcar elementos como **Favoritos**, sincronizados con la base de datos local.

4. **Perfil de Negocio y Encuesta:**
   - Configuración de datos comerciales (Nombre del negocio, rubro, dirección, etc.).
   - Encuesta de hábitos y preferencias del local.
   - Selección de foto de perfil desde la Galería o Cámara usando `FileProvider`.

5. **Diseño Adaptable (Responsive Layout):**
   - Interfaz responsive según el tamaño de pantalla (Compacta, Mediana y Expandida) utilizando `WindowSizeClass`.

6. **Notificaciones Locales:**
   - Canales de notificaciones para interacciones y avisos dentro de la aplicación.

---

## Tecnologías Utilizadas

- **Lenguaje:** [Kotlin](https://kotlinlang.org/) (JVM 11)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) con **Material Design 3**
- **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture orientada a componentes.
- **Navegación:** Jetpack Navigation Compose con transiciones animadas.
- **Base de Datos Local:** [Room Database](https://developer.android.com/training/data-storage/room) con **KSP** (Kotlin Symbol Processing).
- **Almacenamiento Clave-Valor:** [Jetpack DataStore (Preferences)](https://developer.android.com/topic/libraries/architecture/datastore)
- **Manejo Asíncrono:** Kotlin Coroutines & StateFlow
- **Pruebas (Testing):** JUnit 4, Espresso y Compose Testing framework.

---

## Estructura del Código

```text
app/src/main/java/com/example/appmm_grupo5/
├── data/           # Base de datos Room (DAOs, AppDatabase, SesionDataStore, DatosEjemplo)
├── model/          # Modelos de datos (Usuario, Contenido, Favorito, PerfilNegocio, etc.)
├── navigation/     # Grafo de navegación (NavGraph, Screen, NavigationEvent)
├── notificaciones/ # Helper y canales para notificaciones del sistema
├── repository/     # Repositorios que unifican las fuentes de datos (Room / DataStore)
├── ui/
│   ├── components/ # Componentes reutilizables de UI (Botones, Campos de Texto, Diálogos, etc.)
│   ├── screens/    # Pantallas principales (Splash, Login, Registro, Home, Aprender, Detalle, Perfil, Encuesta)
│   ├── theme/      # Configuración del tema Material 3 (Colores, Tipografía, Tema)
│   └── utils/      # Utilidades de UI (WindowSizeUtils)
├── validation/     # Lógica de validación de formularios (RUT, Email, Contraseñas)
└── viewmodel/      # ViewModels (MainViewModel, SesionViewModel, UsuarioViewModel, CatalogoViewModel, PerfilViewModel)
```

---

## Requisitos de Entorno y Ejecución

### Requisitos Previos
- **Android Studio** (Ladybug 2024.2.1 o superior recomendado)
- **JDK:** Java 11
- **Android SDK:**
  - `compileSdk`: 36
  - `minSdk`: 24 (Android 7.0 Nougat)
  - `targetSdk`: 36

### Pasos para Ejecutar

1. **Clonar o abrir el proyecto:**
   Abre Android Studio y selecciona `Open` eligiendo la carpeta raíz del proyecto.

2. **Sincronizar Gradle:**
   Espera a que Android Studio descargue las dependencias y sincronice el proyecto (`Sync Project with Gradle Files`).

3. **Ejecutar la aplicación:**
   - Selecciona un emulador o un dispositivo físico conectado vía ADB.
   - Haz clic en el botón **Run** ($\triangleright$) en la barra superior o presiona `Shift + F10`.



