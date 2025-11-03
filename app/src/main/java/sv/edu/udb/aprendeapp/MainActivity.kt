package sv.edu.udb.aprendeapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import sv.edu.udb.aprendeapp.ui.auth.LoginActivity
import sv.edu.udb.aprendeapp.ui.recursos.RecursosActivity
import sv.edu.udb.aprendeapp.utils.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, RecursosActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}
