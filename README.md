# GasolineraV3

GasolineraV3 es una aplicación móvil desarrollada en Kotlin y Java, utilizando una arquitectura de tres capas (Presentación, Negocio, Datos). El sistema permite gestionar estaciones de servicio, registrar información relevante sobre bombas y combustibles, y simular filas de autos para estimar tiempos de atención y consumo de combustible. Utiliza una base de datos local SQLite y el SDK de Google Maps para Android.
Aplicacion hecha en Kotlin+Java para la materia de Arquitectura de software con la arquitectura 3 capas , tomar en cuenta que el proyecto es meramente nativo , asi que no se utiliza ningun framework ,se recomienda que tal cual este el codigo , este la documentacion.
## Funcionalidades principales

- Gestión de tipos de combustible: permite registrar, editar y eliminar tipos como gasolina y diésel.
- Gestión de estaciones: registro de nombre, dirección y ubicación mediante selección en mapa.
- Gestión de bombas: cada estación puede tener múltiples bombas según tipo de combustible y cantidad.
- Gestión de stock: se ingresan los litros disponibles para cada tipo de combustible en cada estación.
- Simulación de fila:
  - El usuario dibuja la fila sobre el mapa.
  - El sistema calcula:
    - Cantidad estimada de autos en la fila.
    - Litros necesarios.
    - Si el stock es suficiente o no.
    - Tiempo estimado de atención, considerando la cantidad de bombas disponibles.

## Estructura del proyecto

- `presentacion/`: contiene los fragments y pantallas visuales (como PTipoCombustible, PEstacion, etc.).
- `negocio/`: controladores y lógica de negocio (NTipoCombustible, NFila, etc.).
- `datos/`: acceso a la base de datos SQLite, clases de entidad y lógica de persistencia (DTipoCombustible, DFila, etc.).

## Integración con Google Maps

- Uso de SupportMapFragment.
- Selección de ubicación para estaciones.
- Dibujo libre sobre el mapa para simular filas de vehículos.

## Interfaz de usuario

- Paleta de colores basada en naranja (#FF5722) y blanco.
- Diseño limpio y funcional.
- BottomNavigationView con todos los ítems visibles y en el mismo color.
- Interacciones intuitivas para cada módulo.

## Dependencias

- Google Maps SDK for Android
- AndroidX Navigation Component
- Material Components
- SQLite (sin Room)

## Estado del proyecto

Actualmente en desarrollo. Incluye módulos funcionales para la gestión y simulación completa del flujo de atención en estaciones de servicio.

## Requisitos

- Android Studio Arctic Fox o superior
- API nivel 21 (Lollipop) o superior
- Acceso a Internet para cargar Google Maps
