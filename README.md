# Emergencias Médicas

Aplicación móvil nativa para Android, desarrollada en Java, que ayuda al usuario a ubicar y contactar centros de salud (hospitales, clínicas y farmacias) y a consultar guías básicas de primeros auxilios.

## Características

- **Directorio local de centros de salud:** registro, listado, búsqueda por texto, filtro por tipo y edición de registros (nombre, tipo, dirección y teléfono).
- **Contactos de emergencia:** botones que abren el marcador del teléfono con el número ya cargado. La llamada la confirma el usuario.
- **Centro de salud más cercano:** obtiene la ubicación GPS del usuario, la muestra en un mapa de OpenStreetMap y marca el lugar de salud más próximo, con la distancia en metros.
- **Primeros auxilios:** descarga una lista de emergencias desde un JSON en línea y muestra la descripción, los pasos, las advertencias y el nivel de urgencia de cada una.

## Pantallas

| Clase | Descripción |
|---|---|
| `MainActivity` | Menú principal y formulario de registro de centros. Impide registrar dos centros con el mismo nombre. |
| `Listado` | Lista los centros guardados, con búsqueda y filtro por tipo (`Hospital`, `Clínica`, `Farmacia`). |
| `EditarActivity` | Edita un centro ya registrado. |
| `EmergenciaActivity` | Contactos de emergencia con números definidos en el código. |
| `Mapa` | Mapa con la ubicación del usuario y el lugar de salud más cercano. |
| `PrimerosAuxilios` | Guías de primeros auxilios obtenidas de un JSON remoto. |

## Tecnologías

| Componente | Detalle |
|---|---|
| Lenguaje | Java (compatibilidad 11) |
| Plataforma | Android, `minSdk 24`, `targetSdk 36`, `compileSdk 36` |
| Build | Gradle (Kotlin DSL) con Android Gradle Plugin 8.12.2 |
| Base de datos | SQLite (`SQLiteOpenHelper`) |
| Mapas | [osmdroid](https://github.com/osmdroid/osmdroid) 6.1.16 |
| Ubicación | Google Play Services Location 21.3.0 |
| Red | OkHttp 4.11.0 y Volley 1.2.1 |
| JSON | `org.json` (incluido en Android); Gson 2.10.1 declarado como dependencia |
| Interfaz | AppCompat, Material Components, ConstraintLayout |

## Fuentes de datos externas

- **Lugares de salud cercanos:** [Overpass API](https://overpass-api.de/) (datos de OpenStreetMap). La consulta actual busca únicamente dentro del municipio de **Anserma**.
- **Guías de primeros auxilios:** archivo `primeros_auxilios.json` del repositorio [`primeros-auxilios-api`](https://github.com/JohnDeivid12/primeros-auxilios-api.), publicado en `raw.githubusercontent.com`. Cada elemento del arreglo tiene esta estructura:

```json
{
  "id": 1,
  "nombre": "Corte leve",
  "descripcion": "Herida superficial en la piel.",
  "pasos": ["Lavar las manos.", "..."],
  "advertencias": ["No aplicar alcohol directamente en la herida.", "..."],
  "urgencia": "baja"
}
```

## Modelo de datos

Tabla `CentrosMedicos` en la base de datos `AdministradorBD` (versión 3):

| Columna | Tipo | Notas |
|---|---|---|
| `id` | `INTEGER` | Clave primaria autoincremental |
| `nombre` | `TEXT` | Único |
| `tipo` | `TEXT` | `Hospital`, `Clínica` o `Farmacia` |
| `direccion` | `TEXT` | |
| `telefono` | `TEXT` | |

## Permisos

| Permiso | Uso |
|---|---|
| `INTERNET` | Consulta a Overpass y descarga del JSON de primeros auxilios |
| `ACCESS_NETWORK_STATE` | Estado de la red |
| `ACCESS_FINE_LOCATION` | Ubicación GPS en la pantalla del mapa |
| `ACCESS_COARSE_LOCATION` | Declarado en el manifiesto |
| `CALL_PHONE` | Declarado en el manifiesto (la app usa `ACTION_DIAL`, que no lo requiere) |

## Requisitos

- Android Studio reciente, compatible con Android Gradle Plugin 8.12.2.
- JDK 17 o superior para ejecutar Gradle.
- Dispositivo o emulador con Android 7.0 (API 24) o superior.
- Conexión a internet para el mapa y las guías de primeros auxilios.

## Instalación y ejecución

```bash
git clone https://github.com/JohnDeivid12/App-M-vil-Emergencias-M-dicas-.git
cd App-M-vil-Emergencias-M-dicas-
```

1. Abre la carpeta del proyecto en Android Studio.
2. Espera a que Gradle sincronice las dependencias.
3. Selecciona un dispositivo o emulador y ejecuta el módulo `app`.

También se puede compilar desde la terminal:

```bash
./gradlew assembleDebug
```

## Estructura del proyecto

```text
app/src/main/
├── java/com/example/appemergenciasmedicas/
│   ├── MainActivity.java
│   ├── Listado.java
│   ├── EditarActivity.java
│   ├── EmergenciaActivity.java
│   ├── Mapa.java
│   ├── PrimerosAuxilios.java
│   └── AdminSQLliteOpenHelper.java
├── res/
│   ├── layout/        # Una vista por pantalla
│   └── values/        # Textos, colores y temas
└── AndroidManifest.xml
```

## Limitaciones conocidas

- La búsqueda del centro más cercano solo cubre Anserma y no incluye los centros registrados en la base de datos local.
- Cambiar la versión de la base de datos elimina todos los registros, porque `onUpgrade` ejecuta `DROP TABLE`.
- Los números de emergencia están escritos directamente en el código fuente.
- Las guías de primeros auxilios requieren internet; no existe copia local.
- Las pruebas del proyecto son las plantillas generadas por Android Studio.
- Las guías son orientativas y no sustituyen la atención médica profesional. En una emergencia real, contacta a los servicios de emergencia de tu localidad.
