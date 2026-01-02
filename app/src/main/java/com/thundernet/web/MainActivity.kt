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
import android.view.MotionEvent
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
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var splashLayout: View
    private lateinit var preferences: SharedPreferences
    private lateinit var menuButton: Button
    private var currentUrl: String = "http://tuservidor.com"
    private var loadingAnimationHandler: Handler? = null
    private var loadingAnimationRunnable: Runnable? = null
    
    // Variables para el servidor WoW
    private var wowServer: String = "127.0.0.1"
    private var wowPort: String = "8085"
    
    companion object {
        private const val TAG = "ThunderNetApp"
        private const val PREF_SERVER_URL = "server_url"
        private const val DEFAULT_URL = "http://tuservidor.com"
        private const val PREF_DARK_MODE = "dark_mode"
        
        // Nuevas preferencias para servidor WoW
        private const val PREF_WOW_SERVER = "wow_server"
        private const val PREF_WOW_PORT = "wow_port"
        
        private const val DEFAULT_WOW_SERVER = "127.0.0.1"
        private const val DEFAULT_WOW_PORT = "8085" // Puerto típico de authserver
        
        // Tiempo de timeout para ping
        private const val PING_TIMEOUT = 3000 // 3 segundos
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
            
            // Obtener configuración del servidor WoW
            wowServer = preferences.getString(PREF_WOW_SERVER, DEFAULT_WOW_SERVER) ?: DEFAULT_WOW_SERVER
            wowPort = preferences.getString(PREF_WOW_PORT, DEFAULT_WOW_PORT) ?: DEFAULT_WOW_PORT
            
            Log.d(TAG, "URL a cargar: $currentUrl")
            Log.d(TAG, "Servidor WoW: $wowServer:$wowPort")
            
            // Configurar modo oscuro
            val darkModeEnabled = preferences.getBoolean(PREF_DARK_MODE, false)
            AppCompatDelegate.setDefaultNightMode(
                if (darkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES 
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            
            // Mostrar pantalla de carga
            try {
                setContentView(R.layout.splash_layout)
                splashLayout = findViewById(R.id.splashLayout)
                setupSplashAnimations()
                Log.d(TAG, "Splash layout cargado")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando splash: ${e.message}")
                setContentView(android.R.layout.simple_list_item_1)
            }
            
            // Esperar 5 segundos y cargar WebView
            Handler(Looper.getMainLooper()).postDelayed({
                loadWebView()
            }, 5000)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error en onCreate: ${e.message}")
            e.printStackTrace()
            showErrorCrash(e.message ?: "Error desconocido")
        }
    }

    private fun setupSplashAnimations() {
        try {
            val loadingCircle = findViewById<ImageView>(R.id.loadingCircle)
            val wowLogo = findViewById<ImageView>(R.id.wowLogo)
            val loadingText = findViewById<TextView>(R.id.loadingText)
            
            val rotateAnimation = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1200
                repeatCount = Animation.INFINITE
                interpolator = android.view.animation.LinearInterpolator()
            }
            
            loadingCircle.startAnimation(rotateAnimation)
            
            wowLogo?.alpha = 0f
            wowLogo?.animate()
                ?.alpha(1f)
                ?.setDuration(1000)
                ?.setStartDelay(200)
                ?.start()
            
            loadingText?.let {
                animateLoadingText(it)
            }
            
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
            val loadingCircle = findViewById<ImageView?>(R.id.loadingCircle)
            loadingCircle?.clearAnimation()
            
            val wowLogo = findViewById<ImageView?>(R.id.wowLogo)
            wowLogo?.clearAnimation()
            
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
        stopSplashAnimations()
        
        try {
            setContentView(R.layout.activity_main)
            
            webView = findViewById(R.id.webView) ?: throw IllegalStateException("WebView no encontrado")
            val progressBar: ProgressBar = findViewById(R.id.progressBar)
            val errorLayout: LinearLayout = findViewById(R.id.errorLayout)
            val retryButton: Button = findViewById(R.id.retryButton)
            menuButton = findViewById(R.id.menuButton)
            
            setupWebView(progressBar, errorLayout, retryButton)
            
            menuButton.setOnClickListener {
                showOptionsMenu(it)
            }
            
            setupMenuButtonAnimation()
            
            retryButton.setOnClickListener {
                errorLayout.visibility = View.GONE
                webView.visibility = View.VISIBLE
                webView.reload()
            }
            
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
            
            webSettings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
                
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    safeBrowsingEnabled = false
                }
            }
            
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
            
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    progressBar.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                    injectMobileCSS(view)
                    
                    if (view?.progress == 100) {
                        errorLayout.visibility = View.GONE
                        webView.visibility = View.VISIBLE
                    }
                }
                
                private fun injectMobileCSS(view: WebView?) {
                    val css = """
                        <style>
                            html, body { width: 100% !important; max-width: 100% !important; }
                            img { max-width: 100% !important; height: auto !important; }
                        </style>
                    """.trimIndent()
                    
                    view?.evaluateJavascript("""
                        if (!document.querySelector('meta[name=\"viewport\"]')) {
                            var meta = document.createElement('meta');
                            meta.name = 'viewport';
                            meta.content = 'width=device-width, initial-scale=1.0';
                            document.head.appendChild(meta);
                        }
                    """.trimIndent(), null)
                }

                @SuppressLint("WebViewClientOnReceivedSslError")
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.proceed()
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error configurando WebView: ${e.message}")
        }
    }
    
    private fun setupMenuButtonAnimation() {
    menuButton.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Animación de presionado (muy sutil)
                v.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .alpha(0.7f)
                    .setDuration(100)
                    .start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Animación de liberación
                v.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(100)
                    .start()
                
                // Pequeña rotación
                v.animate()
                    .rotationBy(180f)
                    .setDuration(300)
                    .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
                    .start()
            }
        }
        false
    }
}
    
    private fun showOptionsMenu(view: View) {
        try {
            // MENÚ ACTUALIZADO: Añadida opción "Reino"
            val menuItems = listOf(
                "🔄 Actualizar",
                "⚙️ Configurar URL",
                "🌐 Reino",  // NUEVA OPCIÓN
                "🌙 Modo Oscuro",
                "🧹 Limpiar Caché",
                "ℹ️ Acerca de"
            )
            
            val adapter = object : ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                menuItems
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    textView.setTextColor(Color.WHITE)
                    textView.textSize = 16f
                    textView.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    textView.setPadding(24.dpToPx(), 16.dpToPx(), 24.dpToPx(), 16.dpToPx())
                    textView.minHeight = 48.dpToPx()
                    return view
                }
            }
            
            AlertDialog.Builder(this)
                .setTitle("⚡ ThunderNet APK ⚡")
                .setAdapter(adapter) { dialog, which ->
                    dialog.dismiss()
                    when (which) {
                        0 -> {
                            webView.reload()
                            showToast("✅ Página actualizada")
                        }
                        1 -> showUrlConfigDialog()
                        2 -> showRealmStatus()  // NUEVO: Mostrar estado del reino
                        3 -> toggleDarkMode()
                        4 -> clearCache()
                        5 -> showAboutDialog()
                    }
                }
                .show()
                .apply {
                    findViewById<TextView>(android.R.id.title)?.apply {
                        setTextColor(Color.parseColor("#00B4FF"))
                        textSize = 18f
                        gravity = Gravity.CENTER
                        setPadding(0, 24.dpToPx(), 0, 16.dpToPx())
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    
                    window?.setBackgroundDrawableResource(R.drawable.dialog_background)
                    
                    val listView = findViewById<ListView>(android.R.id.list)
                    listView?.setBackgroundColor(Color.parseColor("#0A1428"))
                }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error mostrando menú: ${e.message}")
            showToast("Error al mostrar menú")
        }
    }
    
    // NUEVO: Diálogo para mostrar estado del reino
    private fun showRealmStatus() {
        Log.d(TAG, "Verificando estado del servidor: $wowServer:$wowPort")
        
        // Mostrar diálogo de carga
        val loadingDialog = AlertDialog.Builder(this)
            .setTitle("🌐 Verificando Reino...")
            .setMessage("Conectando con el servidor...")
            .setCancelable(false)
            .create()
        
        loadingDialog.show()
        
        // Ejecutar ping en un hilo separado
        Executors.newSingleThreadExecutor().execute {
            var isOnline = false
            var errorMessage = ""
            
            try {
                val port = try {
                    wowPort.toInt()
                } catch (e: NumberFormatException) {
                    8085
                }
                
                Log.d(TAG, "Intentando conectar a $wowServer:$port")
                
                // Intentar conexión socket
                val socket = Socket()
                socket.connect(InetSocketAddress(wowServer, port), PING_TIMEOUT)
                socket.close()
                isOnline = true
                Log.d(TAG, "Conexión exitosa")
                
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error desconocido"
                Log.e(TAG, "Error de conexión: $errorMessage")
                isOnline = false
            }
            
            // Volver al hilo principal para mostrar resultado
            runOnUiThread {
                loadingDialog.dismiss()
                showRealmStatusResult(isOnline, errorMessage)
            }
        }
    }
    
    private fun showRealmStatusResult(isOnline: Boolean, errorMessage: String = "") {
        val statusText = if (isOnline) "🟢 ONLINE" else "🔴 OFFLINE"
        val statusColor = if (isOnline) "#00FF00" else "#FF0000"
        val serverInfo = "Servidor: $wowServer\nPuerto: $wowPort"
        val message = if (isOnline) {
            "✅ El servidor está funcionando correctamente.\n\n$serverInfo"
        } else {
            "❌ No se pudo conectar al servidor.\n\n$serverInfo\n\nError: ${if (errorMessage.isNotEmpty()) errorMessage else "Tiempo de espera agotado"}"
        }
        
        // Crear layout personalizado
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32.dpToPx(), 24.dpToPx(), 32.dpToPx(), 16.dpToPx())
            setBackgroundColor(Color.parseColor("#0A1428"))
        }
        
        // Estado
        val statusView = TextView(this).apply {
            text = statusText
            setTextColor(Color.parseColor(statusColor))
            textSize = 22f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20.dpToPx())
        }
        
        // Mensaje
        val messageView = TextView(this).apply {
            text = message
            setTextColor(Color.WHITE)
            textSize = 14f
            gravity = Gravity.CENTER
            setLineSpacing(1.2f, 1.2f)
            setPadding(0, 0, 0, 24.dpToPx())
        }
        
        // Indicador visual
        val indicator = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                4.dpToPx()
            ).apply {
                bottomMargin = 20.dpToPx()
            }
            setBackgroundColor(Color.parseColor(statusColor))
        }
        
        layout.addView(statusView)
        layout.addView(indicator)
        layout.addView(messageView)
        
        AlertDialog.Builder(this)
            .setTitle("🌐 Estado del Reino")
            .setView(layout)
            .setPositiveButton("🔄 Reintentar") { dialog, _ ->
                dialog.dismiss()
                showRealmStatus()
            }
            .setNegativeButton("⬅️ Volver") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("⚙️ Configurar") { dialog, _ ->
                dialog.dismiss()
                showUrlConfigDialog()
            }
            .show()
            .apply {
                window?.setBackgroundDrawableResource(R.drawable.dialog_background)
                
                findViewById<TextView>(android.R.id.title)?.apply {
                    setTextColor(Color.parseColor("#00B4FF"))
                    textSize = 18f
                    gravity = Gravity.CENTER
                    setPadding(0, 16.dpToPx(), 0, 8.dpToPx())
                }
                
                // Personalizar botones
                getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                    setTextColor(Color.parseColor("#0A1428"))
                    setBackgroundColor(Color.parseColor("#00B4FF"))
                    setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
                }
                getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                    setTextColor(Color.parseColor("#00B4FF"))
                    setBackgroundColor(Color.parseColor("#132347"))
                    setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
                }
                getButton(AlertDialog.BUTTON_NEUTRAL)?.apply {
                    setTextColor(Color.parseColor("#00B4FF"))
                    setBackgroundColor(Color.parseColor("#132347"))
                    setPadding(24.dpToPx(), 12.dpToPx(), 24.dpToPx(), 12.dpToPx())
                }
            }
    }
    
    // ACTUALIZADO: Diálogo de configuración de URL ahora incluye servidor WoW
    private fun showUrlConfigDialog() {
        try {
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24.dpToPx(), 16.dpToPx(), 24.dpToPx(), 8.dpToPx())
                setBackgroundColor(Color.parseColor("#0A1428"))
            }
            
            // Título
            val title = TextView(this).apply {
                text = "⚙️ Configuración del Servidor"
                setTextColor(Color.parseColor("#00B4FF"))
                textSize = 18f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 20.dpToPx())
            }
            
            // Sub-título 1: Página Web
            val subtitle1 = TextView(this).apply {
                text = "🌐 Página Web:"
                setTextColor(Color.WHITE)
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, 10.dpToPx(), 0, 8.dpToPx())
            }
            
            // URL de la página web
            val urlEditText = EditText(this).apply {
                setText(currentUrl)
                hint = "Ej: http://tuservidor.com"
                setTextColor(Color.WHITE)
                setHintTextColor(Color.parseColor("#CCCCCC"))
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
                    bottomMargin = 20.dpToPx()
                }
            }
            
            // Separador
            val separator = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.dpToPx()
                ).apply {
                    bottomMargin = 20.dpToPx()
                    topMargin = 10.dpToPx()
                }
                setBackgroundColor(Color.parseColor("#00B4FF"))
            }
            
            // Sub-título 2: Servidor WoW
            val subtitle2 = TextView(this).apply {
                text = "🌐 Servidor WoW:"
                setTextColor(Color.WHITE)
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, 10.dpToPx(), 0, 8.dpToPx())
            }
            
            // Campo para servidor WoW
            val serverEditText = EditText(this).apply {
                setText(wowServer)
                hint = "IP o dominio del servidor"
                setTextColor(Color.WHITE)
                setHintTextColor(Color.parseColor("#CCCCCC"))
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
                    bottomMargin = 12.dpToPx()
                }
            }
            
            // Campo para puerto WoW
            val portEditText = EditText(this).apply {
                setText(wowPort)
                hint = "Puerto (ej: 8085, 3724)"
                setTextColor(Color.WHITE)
                setHintTextColor(Color.parseColor("#CCCCCC"))
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
            layout.addView(subtitle1)
            layout.addView(urlEditText)
            layout.addView(separator)
            layout.addView(subtitle2)
            layout.addView(serverEditText)
            layout.addView(portEditText)
            
            val dialog = AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton("💾 GUARDAR TODO") { dialog, _ ->
                    // Guardar URL de la página web
                    val newUrl = urlEditText.text.toString().trim()
                    if (newUrl.isNotEmpty()) {
                        currentUrl = if (newUrl.startsWith("http")) newUrl else "http://$newUrl"
                        preferences.edit()
                            .putString(PREF_SERVER_URL, currentUrl)
                            .apply()
                    }
                    
                    // Guardar configuración del servidor WoW
                    val newServer = serverEditText.text.toString().trim()
                    val newPort = portEditText.text.toString().trim()
                    
                    if (newServer.isNotEmpty()) {
                        wowServer = newServer
                        preferences.edit()
                            .putString(PREF_WOW_SERVER, wowServer)
                            .apply()
                    }
                    
                    if (newPort.isNotEmpty()) {
                        wowPort = newPort
                        preferences.edit()
                            .putString(PREF_WOW_PORT, wowPort)
                            .apply()
                    }
                    
                    showToast("✅ Configuración guardada")
                    
                    // Recargar la página web si cambió la URL
                    if (newUrl.isNotEmpty() && newUrl != webView.url) {
                        loadUrl(currentUrl)
                    }
                    
                    dialog.dismiss()
                }
                .setNegativeButton("❌ CANCELAR") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("🔄 VALORES PREDETERMINADOS") { dialog, _ ->
                    // Restaurar valores predeterminados
                    currentUrl = DEFAULT_URL
                    wowServer = DEFAULT_WOW_SERVER
                    wowPort = DEFAULT_WOW_PORT
                    
                    preferences.edit().apply {
                        putString(PREF_SERVER_URL, currentUrl)
                        putString(PREF_WOW_SERVER, wowServer)
                        putString(PREF_WOW_PORT, wowPort)
                        apply()
                    }
                    
                    showToast("🔄 Valores predeterminados restaurados")
                    
                    // Recargar página con URL predeterminada
                    loadUrl(currentUrl)
                    
                    dialog.dismiss()
                }
                .create()
            
            dialog.show()
            
            try {
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            } catch (e: Exception) {
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0A1428")))
            }
            
            // Personalizar botones
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
            showToast("Error al configurar servidor")
        }
    }
    
    // Métodos existentes (sin cambios)...
    private fun loadUrl(url: String) {
        try {
            Log.d(TAG, "Cargando URL: $url")
            
            val headers = HashMap<String, String>()
            headers["User-Agent"] = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36"
            
            webView.loadUrl(url, headers)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error cargando URL: ${e.message}")
            try {
                webView.loadUrl(url)
            } catch (e2: Exception) {
                Log.e(TAG, "Error en segundo intento: ${e2.message}")
                showError("No se pudo cargar la página: ${e2.message}")
            }
        }
    }
    
    private fun toggleDarkMode() {
        try {
            val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            val newMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
            
            AppCompatDelegate.setDefaultNightMode(newMode)
            
            preferences.edit()
                .putBoolean(PREF_DARK_MODE, !isDarkMode)
                .apply()
            
            showToast(if (isDarkMode) "🌞 Modo claro activado" else "🌙 Modo oscuro activado")
            recreate()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error cambiando modo oscuro: ${e.message}")
            showToast("Error al cambiar modo")
        }
    }
    
    private fun clearCache() {
        try {
            webView.clearCache(true)
            webView.clearHistory()
            webView.clearFormData()
            webView.clearSslPreferences()
            
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            
            WebStorage.getInstance().deleteAllData()
            
            showToast("🧹 Caché y datos limpiados")
            
            if (!webView.url.isNullOrEmpty()) {
                Handler(Looper.getMainLooper()).postDelayed({
                    webView.reload()
                }, 500)
            } else {
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
                ⚡ ThunderNet APK ⚡
                
                Versión: 1.1.0
                
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
                .setPositiveButton("⬅️  VOLVER") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            
            dialog.show()
            
            try {
                dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
            } catch (e: Exception) {
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0A1428")))
            }
            
            dialog.findViewById<TextView>(android.R.id.title)?.apply {
                setTextColor(Color.parseColor("#00B4FF"))
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(0, 16.dpToPx(), 0, 8.dpToPx())
            }
            
            dialog.findViewById<TextView>(android.R.id.message)?.apply {
                setTextColor(Color.WHITE)
                textSize = 14f
                gravity = Gravity.CENTER
                setLineSpacing(1.2f, 1.2f)
                setPadding(16.dpToPx(), 8.dpToPx(), 16.dpToPx(), 8.dpToPx())
            }
            
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
    
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopSplashAnimations()
        Log.d(TAG, "onDestroy - recursos limpiados")
    }
    
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