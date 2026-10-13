# AI Quota Widget 📊🤖

Aplicación Android moderna desarrollada con **Jetpack Compose**, **Material 3** y **AppWidgets** para monitorear en tiempo real tu consumo y saldo restante en **Google Gemini** y **ChatGPT (OpenAI)**.

---

## 🌟 Características

- **Widget para Pantalla de Inicio (AppWidget)**:
  - Indicadores visuales de porcentaje disponible en tiempo real.
  - Conteo de solicitudes/mensajes consumidos vs. límite del plan.
  - Temporizador de cuenta regresiva para el próximo reinicio automático de ciclo.
  - Botones de acceso rápido `+1` en el widget para registrar consumo sin abrir la app.
  - Botón de actualización inmediata y sincronización automática cada 30 minutos.
- **Soporte de Suscripciones y Cuotas**:
  - **ChatGPT Plus**: Ventana rotativa de 40 mensajes cada 3 horas.
  - **ChatGPT Team / Pro**: 100 mensajes cada 3 horas.
  - **OpenAI API Prepago**: Control de presupuesto mensual en USD ($).
  - **Gemini Free Tier**: Límite diario de 1,500 solicitudes/día con reinicio automático a las 00:00.
  - **Gemini Advanced (Google One AI Premium)**: Cuota de prompts diaria.
  - **Gemini API Pay-as-you-go**: Límite de gasto mensual en USD ($).
- **Verificación de API en Vivo**:
  - Prueba de conectividad directa con endpoints de Google AI Studio y OpenAI.
  - Medición de latencia de red (ms) y verificación de validez de credenciales.
- **Historial de Consumo**:
  - Registro cronológico de consultas realizadas discriminado por modelo (Gemini 2.5 Flash, GPT-4o, o3-mini, etc.).

---

## 📱 Cómo añadir el Widget en tu pantalla

1. Ve a la pantalla de inicio de tu dispositivo Android.
2. Mantén pulsado un área vacía de la pantalla.
3. Toca en el menú **Widgets**.
4. Busca **AI Quota Widget** o **AI Quota Monitor**.
5. Arrastra el widget (tamaño recomendado `4x2` o `3x2`) a tu pantalla.
6. ¡Listo! Puedes pulsar `+1` directamente en el widget cada vez que uses tus asistentes de IA.

---

## 🛠️ Estructura del Proyecto

```text
├── .github/workflows/
│   ├── android-ci.yml        # CI automatizado: compilación, tests y generación de APK
│   └── android-release.yml   # Workflow para empaquetar versiones Release / AAB
├── app/
│   ├── src/main/
│   │   ├── java/com/example/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/QuotaRepository.kt       # Persistencia y reinicios de ciclo
│   │   │   ├── model/AiQuotaModel.kt         # Modelos de datos y cálculos
│   │   │   ├── network/AiApiChecker.kt       # Verificación en vivo de APIs
│   │   │   ├── ui/                           # Interfaz Jetpack Compose (M3)
│   │   │   │   ├── QuotaMainScreen.kt
│   │   │   │   ├── QuotaViewModel.kt
│   │   │   │   └── components/
│   │   │   └── widget/
│   │   │       └── AiQuotaWidgetProvider.kt  # Receptor y render de RemoteViews
│   │   └── res/
│   │       ├── layout/widget_ai_quota.xml    # Diseño visual del widget
│   │       └── xml/ai_quota_widget_info.xml  # Configuración AppWidgetProvider
│   └── src/test/java/com/example/            # Pruebas unitarias y Robolectric
├── gradle/libs.versions.toml                 # Version Catalog centralizado
├── gradlew & gradlew.bat                     # Gradle Wrapper preconfigurado
└── .env.example                              # Plantilla de variables de entorno
```

---

## 🚀 Flujo de Trabajo y CI/CD (GitHub Actions)

El repositorio incluye flujos de trabajo de GitHub Actions preconfigurados en `.github/workflows/`:

1. **Android CI (`android-ci.yml`)**:
   - Se ejecuta automáticamente en cada `push` o `pull_request` a las ramas principales (`main`, `master`).
   - Configura JDK 21 con caché de dependencias Gradle.
   - Ejecuta las pruebas unitarias y de Robolectric:
     ```bash
     ./gradlew testDebugUnitTest
     ```
   - Compila el APK de depuración:
     ```bash
     ./gradlew assembleDebug
     ```
   - Sube automáticamente el archivo `.apk` resultante a los artefactos de la ejecución para que puedas descargarlo e instalarlo de inmediato.

2. **Android Release (`android-release.yml`)**:
   - Permite compilar versiones de producción o Android App Bundles (`.aab`) mediante tags `v*` o ejecución manual con `workflow_dispatch`.

---

## 💻 Compilación Local

Para compilar el proyecto en tu máquina local:

```bash
# Dar permisos de ejecución al wrapper (Linux/macOS)
chmod +x gradlew

# Ejecutar pruebas unitarias
./gradlew testDebugUnitTest

# Compilar e instalar en emulador o dispositivo conectado
./gradlew installDebug
```

---

## 🔒 Privacidad y Seguridad

- Las claves de API y los contadores se almacenan de forma local en el dispositivo del usuario (`SharedPreferences`).
- No se envían tokens ni contraseñas a servidores de terceros que no sean los endpoints oficiales de Google y OpenAI.
- El archivo `.gitignore` previene que claves privadas, archivos `.env` o keystores se suban accidentalmente al repositorio.
