package sv.edu.udb.aprendeapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.aprendeapp.databinding.ActivitySplashBinding
import sv.edu.udb.aprendeapp.ui.auth.LoginActivity
import sv.edu.udb.aprendeapp.ui.recursos.RecursoFormActivity
import sv.edu.udb.aprendeapp.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_DELAY = 2000L // 2 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, SPLASH_DELAY)
    }

    private fun checkSession() {
        val sessionManager = SessionManager(this)

        val intent = if (sessionManager.isLoggedIn()) {
            Intent(this, RecursoFormActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}