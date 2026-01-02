package com.thundernet.web

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var splashLayout: View
    private lateinit var preferences: SharedPreferences
    private var currentUrl: String = "http://172.16.1.1"
    private var loadingAnimationHandler: Handler? = null
    private var loadingAnimationRunnable: Runnable? = null
    
    companion object {
        private const val TAG = "ThunderNetApp"
        private const val PREF_SERVER_URL = "server_url"
        private const val DEFAULT_URL = "http://172.16.1.1"
        private const val PREF_DARK_MODE = "dark_mode"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate iniciado")
        
        try {
            // Inicializar SharedPreferences
            preferences = PreferenceManager.getDefaultSharedPreferences(this)
            
            // Obtener la URL guardada
            currentUrl = preferences.getString(PREF_SERVER_URL, DEFAULT_URL) ?: DEFAULT_URL
            Log.d(TAG, "URL a cargar: $currentUrl")
            
            // Configurar modo oscuro desde preferencias
            val darkModeEnabled = preferences.getBoolean(PREF_DARK_MODE, false)
            AppCompatDelegate.setDefaultNightMode(
                if (darkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES 
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            
            // Mostrar pantalla de carga con animaciones
            try {
                setContentView(R.layout.splash_layout)
                splashLayout = findViewById(R.id.splashLayout)
                
                // Inicializar animaciones del splash
                setupSplashAnimations()
                
                Log.d(TAG, "Splash layout cargado con animaciones")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando splash: ${e.message}")
                // Intentar cargar layout alternativo
                setContentView(android.R.layout.simple_list_item_1)
            }
            
            // Esperar 5 segundos y cargar WebView
            Handler(Looper.getMainLooper()).postDelayed({
                loadWebView()
            }, 5000) // 5 segundos
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreate: ${e.message}")
            e.printStackTrace()
            // Mostrar mensaje de error al usuario
            showErrorCrash(e.message ?: "Error desconocido")
        }
    }

    private fun setupSplashAnimations() {
        try {
            val loadingCircle = findViewById<ImageView>(R.id.loadingCircle)
            val wowLogo = findViewById<ImageView>(R.id.wowLogo)
            val loadingText = findViewById<TextView>(R.id.loadingText)
            
            // ANIMACIÓN 1: Rotación del círculo de carga
            val rotateAnimation = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1200 // 1.2 segundos por vuelta
                repeatCount = Animation.INFINITE
                interpolator = android.view.animation.LinearInterpolator()
            }
            
            loadingCircle.startAnimation(rotateAnimation)
            
            // ANIMACIÓN 2: Fade in para el logo
            wowLogo?.alpha = 0f
            wowLogo?.animate()
                ?.alpha(1f)
                ?.setDuration(1000)
                ?.setStartDelay(200)
                ?.start()
            
            // ANIMACIÓN 3: Texto "Conectando..." con puntos animados
            loadingText?.let {
                animateLoadingText(it)
            }
            
            // ANIMACIÓN 4: Parpadeo sutil del logo
            val fadeAnimation = android.view.animation.AlphaAnimation(0.7f, 1.0f).apply {
                duration = 1500
                repeatMode = android.view.animation.Animation.REVERSE
                repeatCount = android.view.animation.Animation.INFINITE
            }
            wowLogo?.startAnimation(fadeAnimation)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error configurando animaciones: ${e.message}")
        }
    }
    
    private fun animateLoadingText(textView: TextView) {
        var dotCount = 0
        val maxDots = 3
        
        loadingAnimationHandler = Handler(Looper.getMainLooper())
        loadingAnimationRunnable = object : Runnable {
            override fun run() {
                dotCount = (dotCount + 1) % (maxDots + 1)
                val dots = ".".repeat(dotCount)
                textView.text = "Conectando$dots"
                loadingAnimationHandler?.postDelayed(this, 500)
            }
        }
        
        loadingAnimationRunnable?.let {
            loadingAnimationHandler?.post(it)
        }
    }
    
    private fun stopSplashAnimations() {
        try {
            // Detener animación del círculo
            val loadingCircle = findViewById<ImageView?>(R.id.loadingCircle)
            loadingCircle?.clearAnimation()
            
            // Detener animación del logo
            val wowLogo = findViewById<ImageView?>(R.id.wowLogo)
            wowLogo?.clearAnimation()
            
            // Detener animación del texto
            loadingAnimationRunnable?.let {
                loadingAnimationHandler?.removeCallbacks(it)
            }
            loadingAnimationHandler = null
            loadingAnimationRunnable = null
            
        } catch (e: Exception) {
            Log.e(TAG, "Error deteniendo animaciones: ${e.message}")
        }
    }

    private fun loadWebView() {
        Log.d(TAG, "loadWebView iniciado")
        
        // Detener animaciones del splash
        stopSplashAnimations()
        
        try {
            setContentView(R.layout.activity_main)
            
            // Inicializar vistas con verificaciones
            webView = findViewById(R.id.webView) ?: throw IllegalStateException("WebView no encontrado")
            val progressBar: ProgressBar = findViewById(R.id.progressBar)
            val errorLayout: LinearLayout = findViewById(R.id.errorLayout)
            val retryButton: Button = findViewById(R.id.retryButton)
            val menuButton: ImageButton = findViewById(R.id.menuButton)
            
            // Configurar WebView con mejor manejo de errores
            setupWebView(progressBar, errorLayout, retryButton)
            
            // Configurar botón de menú
            menuButton.setOnClickListener {
                showOptionsMenu(it)
            }
            
            // Configurar botón de reintentar
            retryButton.setOnClickListener {
                errorLayout.visibility = View.GONE
                webView.visibility = View.VISIBLE
                // SIMPLEMENTE RECARGAR LA PÁGINA ACTUAL
                webView.reload()
            }
            
            // Cargar URL inicial solo si no hay algo cargado
            if (webView.url.isNullOrEmpty()) {
                loadUrl(currentUrl)
            }
            
            Log.d(TAG, "WebView cargado exitosamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en loadWebView: ${e.message}")
            e.printStackTrace()
            showErrorCrash("No se pudo cargar la aplicación: ${e.message}")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(progressBar: ProgressBar, errorLayout: LinearLayout, retryButton: Button) {
        try {
            val webSettings = webView.settings
            
            // Configuraciones básicas
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.databaseEnabled = true
            webSettings.setSupportZoom(true)
            webSettings.builtInZoomControls = true
            webSettings.displayZoomControls = false
            
            // CONFIGURACIONES PARA EVITAR BLOQUEOS DE SEGURIDAD
            webSettings.allowFileAccess = true
            webSettings.allowContentAccess = true
            webSettings.allowFileAccessFromFileURLs = true
            webSettings.allowUniversalAccessFromFileURLs = true
            webSettings.loadsImagesAutomatically = true
            webSettings.blockNetworkImage = false
            webSettings.blockNetworkLoads = false
            
            // Para evitar bloqueos de CORS/ORB
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            
            // DESHABILITAR SAFE BROWSING PARA EVITAR ERRORES
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                webSettings.safeBrowsingEnabled = false
            }
            
            // Mejoras de rendimiento
            webSettings.cacheMode = WebSettings.LOAD_DEFAULT
            webSettings.loadWithOverviewMode = true
            webSettings.useWideViewPort = true
            webSettings.builtInZoomControls = true
            webSettings.displayZoomControls = false
            
            // IMPORTANTE: Habilitar cookies
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
            
            // Configurar WebViewClient personalizado que IGNORA errores de seguridad
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    progressBar.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                    Log.d(TAG, "Página iniciando: $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                    Log.d(TAG, "Página cargada: $url")
                    
                    // Verificar si la página se cargó correctamente
                    if (view?.progress == 100) {
                        errorLayout.visibility = View.GONE
                        webView.visibility = View.VISIBLE
                    }
                }

                // MÉTODO MODIFICADO: IGNORAR TODOS LOS ERRORES DE SEGURIDAD
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    val errorMsg = error?.description ?: "Error desconocido"
                    val errorCode = error?.errorCode ?: -1
                    
                    Log.w(TAG, "Error WebView detectado: $errorMsg (Código: $errorCode)")
                    
                    // LISTA DE ERRORES DE SEGURIDAD QUE VAMOS A IGNORAR
                    val securityErrors = listOf(
                        WebViewClient.ERROR_UNSAFE_RESOURCE,
                        WebViewClient.ERROR_UNSUPPORTED_SCHEME
                    )
                    
                    // Verificar si es un error de seguridad
                    val isSecurityError = securityErrors.contains(errorCode) ||
                            errorMsg.contains("ORB", ignoreCase = true) ||
                            errorMsg.contains("CORS", ignoreCase = true) ||
                            errorMsg.contains("ERR_BLOCKED_BY_ORB", ignoreCase = true) ||
                            errorMsg.contains("ERR_BLOCKED_BY_CORS", ignoreCase = true) ||
                            errorMsg.contains("ERR_UNSAFE_RESOURCE", ignoreCase = true) ||
                            errorMsg.contains("unsafe", ignoreCase = true) ||
                            errorMsg.contains("blocked", ignoreCase = true) ||
                            errorMsg.contains("security", ignoreCase = true)
                    
                    if (isSecurityError) {
                        // IGNORAR COMPLETAMENTE el error - no mostrar nada al usuario
                        Log.w(TAG, "Error de seguridad ignorado: $errorMsg")
                        
                        // Si la página ya está parcialmente cargada, solo mostrar advertencia en logs
                        if (webView.progress > 50) {
                            Log.d(TAG, "Página cargada parcialmente (${webView.progress}%), ignorando error de seguridad")
                        }
                    } else if (errorMsg.contains("ERR_FAILED", ignoreCase = true)) {
                        // Para ERR_FAILED, verificar si ya cargó algo
                        if (webView.progress > 70) {
                            Log.w(TAG, "ERR_FAILED ignorado, página cargada al ${webView.progress}%")
                        } else {
                            // Si está muy cargada, mostrar error leve
                            showError("Error de conexión: $errorMsg")
                        }
                    } else if (errorMsg.contains("ERR_CONNECTION_REFUSED", ignoreCase = true) ||
                               errorMsg.contains("ERR_NAME_NOT_RESOLVED", ignoreCase = true) ||
                               errorMsg.contains("ERR_INTERNET_DISCONNECTED", ignoreCase = true)) {
                        // Mostrar solo errores de conexión reales
                        showError("No se puede conectar al servidor: $errorMsg")
                    } else {
                        // Para otros errores, solo loggear pero no mostrar al usuario
                        Log.e(TAG, "Error WebView: $errorMsg")
                    }
                }
                
                // SOBREESCRIBIR PARA INTERCEPTAR Y PERMITIR TODO
                @SuppressLint("WebViewClientOnReceivedSslError")
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    // IMPORTANTE: PERMITIR TODOS LOS ERRORES SSL
                    Log.w(TAG, "Error SSL ignorado: ${error?.toString()}")
                    handler?.proceed() // Continuar a pesar del error SSL
                }
                
                // IMPORTANTE: Para evitar bloqueos de CORS
                @SuppressLint("WebViewApiAvailability")
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    url?.let {
                        Log.d(TAG, "Intentando cargar URL: $it")
                        // Permitir todos los protocolos
                        if (it.startsWith("http://") || it.startsWith("https://") || 
                            it.startsWith("file://") || it.startsWith("content://")) {
                            return false // Dejar que WebView maneje la URL
                        }
                        // Para otros protocolos (tel:, mailto:, etc.)
                        return true
                    }
                    return false
                }
                
                // Para Android N y superior
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    Log.d(TAG, "Intentando cargar URL (nueva API): $url")
                    
                    // Permitir todos los protocolos web
                    if (url.startsWith("http://") || url.startsWith("https://") ||
                        url.startsWith("file://") || url.startsWith("content://")) {
                        return false // Dejar que WebView maneje la URL
                    }
                    return true
                }
                
                // MÉTODO NUEVO: Para interceptar solicitudes de recursos y evitar bloqueos ORB
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    // NO bloquear ninguna solicitud
                    return null
                }
                
                @Deprecated("Deprecated in Java")
                override fun shouldInterceptRequest(
                    view: WebView?,
                    url: String?
                ): WebResourceResponse? {
                    // NO bloquear ninguna solicitud (API antigua)
                    return null
                }
            }
            
            // Configurar WebChromeClient para mejor manejo de JavaScript
            webView.webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                    Log.d(TAG, "Console: ${consoleMessage.message()} -- ${consoleMessage.lineNumber()}")
                    return true
                }
                
                override fun onPermissionRequest(request: PermissionRequest?) {
                    // Otorgar todos los permisos solicitados
                    request?.grant(request.resources)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error configurando WebView: ${e.message}")
        }
    }
    
    private fun loadUrl(url: String) {
        try {
            Log.d(TAG, "Cargando URL: $url")
            
            // User-Agent personalizado para evitar bloqueos
            val userAgent = "Mozilla/5.0 (Linux; Android 10; SM-G973F) " +
                           "AppleWebKit/537.36 (KHTML, like Gecko) " +
                           "Chrome/91.0.4472.120 Mobile Safari/537.36"
            
            webView.settings.userAgentString = userAgent
            
            // Headers adicionales para evitar bloqueos
            val headers = HashMap<String, String>()
            headers["User-Agent"] = userAgent
            headers["Accept"] = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
            headers["Accept-Language"] = "es-ES,es;q=0.9,en;q=0.8"
            headers["Cache-Control"] = "no-cache"
            headers["Sec-Fetch-Dest"] = "document"
            headers["Sec-Fetch-Mode"] = "navigate"
            headers["Sec-Fetch-Site"] = "none"
            headers["Sec-Fetch-User"] = "?1"
            
            webView.loadUrl(url, headers)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error cargando URL: ${e.message}")
            
            // Intentar sin headers si falla
            try {
                webView.loadUrl(url)
            } catch (e2: Exception) {
                Log.e(TAG, "Error en segundo intento: ${e2.message}")
                showError("No se pudo cargar la página: ${e2.message}")
            }
        }
    }
    
    private fun showOptionsMenu(view: View) {
        try {
            val menuItems = listOf(
                "🔄 Actualizar",
                "⚙️ Configurar URL", 
                "🌙 Modo Oscuro",
                "🧹 Limpiar Caché",
                "ℹ️ Acerca de"
            )
            
            // Crear un ArrayAdapter personalizado
            val adapter = object : ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                menuItems
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    
                    // Personalizar el texto a BLANCO
                    textView.setTextColor(Color.WHITE)
                    textView.textSize = 16f
                    textView.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    textView.setPadding(24.dpToPx(), 16.dpToPx(), 24.dpToPx(), 16.dpToPx())
                    textView.minHeight = 48.dpToPx()
                    
                    return view
                }
            }
            
            // Crear el diálogo con .setAdapter() en lugar de .setItems()
            AlertDialog.Builder(this)
                .setTitle("⚡ ThunderNet WoW")
                .setAdapter(adapter) { dialog, which ->
                    dialog.dismiss()
                    when (which) {
                        0 -> { // Actualizar - CAMBIADO: Solo recarga la página actual
                            webView.reload()
                            showToast("✅ Página actualizada")
                        }
                        1 -> { // Configurar URL
                            showUrlConfigDialog()
                        }
                        2 -> { // Modo Oscuro
                            toggleDarkMode()
                        }
                        3 -> { // Limpiar Caché
                            clearCache()
                        }
                        4 -> { // Acerca de
                            showAboutDialog()
                        }
                    }
                }
                .show()
                .apply {
                    // Personalizar el título
                    findViewById<TextView>(android.R.id.title)?.apply {
                        setTextColor(Color.parseColor("#00B4FF"))
                        textSize = 18f
                        gravity = Gravity.CENTER
                        setPadding(0, 24.dpToPx(), 0, 16.dpToPx())
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    
                    // Personalizar fondo
                    window?.setBackgroundDrawableResource(R.drawable.dialog_background)
                    
                    // Personalizar la lista si existe
                    val listView = findViewById<ListView>(android.R.id.list)
                    listView?.setBackgroundColor(Color.parseColor("#0A1428"))
                }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando menú: ${e.message}")
            showToast("Error al mostrar menú")
        }
    }
    
    private fun showUrlConfigDialog() {
        try {
            // Crear el layout personalizado
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24.dpToPx(), 16.dpToPx(), 24.dpToPx(), 8.dpToPx())
                setBackgroundColor(Color.parseColor("#0A1428"))
            }
            
            // Título
            val title = TextView(this).apply {
                text = "⚙️ Configurar URL del servidor"
                setTextColor(Color.parseColor("#00B4FF")) // Azul eléctrico
                textSize = 18f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 16.dpToPx())
            }
            
            // EditText personalizado
            val editText = EditText(this).apply {
                setText(currentUrl)
                hint = "Ej: http://172.16.1.1"
                setTextColor(Color.WHITE)
                setHintTextColor(Color.parseColor("#CCCCCC"))
                
                // Usar el drawable si existe, sino usar color sólido
                try {
                    background = ContextCompat.getDrawable(this@MainActivity, R.drawable.edittext_background)
                } catch (e: Exception) {
                    setBackgroundColor(Color.parseColor("#0A1428"))
                }
                
                setPadding(16.dpToPx(), 12.dpToPx(), 16.dpToPx(), 12.dpToPx())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 24.dpToPx()
                }
            }
            
            layout.addView(title)
            layout.addView(editText)
            
            // Crear el diálogo
            val dialog = AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton("💾 GUARDAR") { dialog, _ ->
                    val newUrl = editText.text.toString().trim()
                    if (newUrl.isNotEmpty()) {
                        currentUrl = if (newUrl.startsWith("http")) newUrl else "http://$newUrl"
                        
                        preferences.edit()
                            .putString(PREF_SERVER_URL, currentUrl)
                            .apply()
                        
                        // CAMBIADO: Cargar la nueva URL desde el inicio
                        loadUrl(currentUrl)
                        showToast("✅ URL actualizada y guardada")
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("❌ CANCELAR") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("🔄 PREDETERMINADA") { dialog, _ ->
                    currentUrl = DEFAULT_URL
                    preferences.edit()
                        .putString(PREF_SERVER_URL, currentUrl)
                        .apply()
                    // CAMBIADO: Cargar desde el inicio
                    loadUrl(currentUrl)
                    showToast("🔄 URL restaurada a predeterminada")
                    dialog.dismiss()
                }
                .create()
            
            // Mostrar el diálogo
            dialog.show()
            
            // Personalizar fondo del diálogo
            try {
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            } catch (e: Exception) {
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0A1428")))
            }
            
            // Personalizar botones después de mostrar el diálogo
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(Color.parseColor("#0A1428"))
                setBackgroundColor(Color.parseColor("#00B4FF"))
                setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(Color.parseColor("#00B4FF"))
                setBackgroundColor(Color.parseColor("#132347"))
                setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
            }
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)?.apply {
                setTextColor(Color.parseColor("#00B4FF"))
                setBackgroundColor(Color.parseColor("#132347"))
                setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando diálogo: ${e.message}")
            showToast("Error al configurar URL")
        }
    }
    
    private fun toggleDarkMode() {
        try {
            val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            val newMode = if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            
            AppCompatDelegate.setDefaultNightMode(newMode)
            
            // Guardar la preferencia del modo oscuro
            preferences.edit()
                .putBoolean(PREF_DARK_MODE, !isDarkMode)
                .apply()
            
            showToast(if (isDarkMode) "🌞 Modo claro activado" else "🌙 Modo oscuro activado")
            
            // Recargar la actividad para aplicar cambios
            recreate()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error cambiando modo oscuro: ${e.message}")
            showToast("Error al cambiar modo")
        }
    }
    
    private fun clearCache() {
        try {
            // Limpieza completa
            webView.clearCache(true)
            webView.clearHistory()
            webView.clearFormData()
            webView.clearSslPreferences()
            
            // Cookies
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            
            // Almacenamiento Web
            WebStorage.getInstance().deleteAllData()
            
            showToast("🧹 Caché y datos limpiados")
            
            // Recargar la página después de limpiar - SOLO SI YA HAY UNA PÁGINA CARGADA
            if (!webView.url.isNullOrEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    webView.reload()
                }, 500)
            } else {
                // Si no hay página cargada, cargar la URL predeterminada
                loadUrl(currentUrl)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error limpiando caché: ${e.message}")
            showToast("Error al limpiar caché")
        }
    }
    
    private fun showAboutDialog() {
        try {
            val message = """
                ⚡ ThunderNet WoW ⚡
                
                Versión: 1.0.0
                
                Aplicación oficial de ThunderNet
                World of Warcraft
                
                Desarrollado con ❤️
                para la comunidad
                
                © 2026+ ThunderNet WoW
                Todos los derechos reservados
            """.trimIndent()
            
            val dialog = AlertDialog.Builder(this)
                .setTitle("ℹ️ Acerca de")
                .setMessage(message)
                .setPositiveButton("👌 ACEPTAR") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            
            dialog.show()
            
            // Personalizar el diálogo
            try {
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            } catch (e: Exception) {
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0A1428")))
            }
            
            // Personalizar título
            dialog.findViewById<TextView>(android.R.id.title)?.apply {
                setTextColor(Color.parseColor("#00B4FF"))
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(0, 16.dpToPx(), 0, 8.dpToPx())
            }
            
            // Personalizar mensaje
            dialog.findViewById<TextView>(android.R.id.message)?.apply {
                setTextColor(Color.WHITE)
                textSize = 14f
                gravity = Gravity.CENTER
                setLineSpacing(1.2f, 1.2f)
                setPadding(16.dpToPx(), 8.dpToPx(), 16.dpToPx(), 8.dpToPx())
            }
            
            // Personalizar botón
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(Color.parseColor("#0A1428"))
                setBackgroundColor(Color.parseColor("#00B4FF"))
                setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando about: ${e.message}")
        }
    }
    
    private fun showError(message: String) {
        try {
            val errorLayout: LinearLayout? = findViewById(R.id.errorLayout)
            val errorText: TextView? = findViewById(R.id.errorMessage)
            
            errorText?.text = "❌ $message"
            errorLayout?.visibility = View.VISIBLE
            webView.visibility = View.GONE
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando error: ${e.message}")
        }
    }
    
    private fun showErrorCrash(message: String) {
        // Mostrar error fatal en pantalla completa
        setContentView(android.R.layout.simple_list_item_1)
        val textView: TextView = findViewById(android.R.id.text1)
        textView.text = "❌ Error: $message\n\n🔧 Reinstala la aplicación."
        textView.gravity = android.view.Gravity.CENTER
        textView.setTextColor(Color.WHITE)
        textView.setBackgroundColor(Color.parseColor("#0A1428"))
        textView.setPadding(20, 20, 20, 20)
    }
    
    private fun showToast(message: String) {
        try {
            val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
            
            // Personalizar el toast si es posible
            toast.view?.apply {
                try {
                    setBackgroundResource(R.drawable.toast_background)
                } catch (e: Exception) {
                    setBackgroundColor(Color.parseColor("#CC0A1428"))
                }
                val textView = findViewById<TextView>(android.R.id.message)
                textView?.apply {
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    setPadding(24.dpToPx(), 16.dpToPx(), 24.dpToPx(), 16.dpToPx())
                }
            }
            
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100.dpToPx())
            toast.show()
            
        } catch (e: Exception) {
            // Si falla la personalización, mostrar toast normal
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
    
    // Manejo del botón atrás
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
    // Limpiar recursos cuando la actividad se destruye
    override fun onDestroy() {
        super.onDestroy()
        stopSplashAnimations()
        Log.d(TAG, "onDestroy - recursos limpiados")
    }
    
    // Métodos de ciclo de vida para debugging
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }
    
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }
    
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }
}
