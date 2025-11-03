package sv.edu.udb.aprendeapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import sv.edu.udb.aprendeapp.R
import sv.edu.udb.aprendeapp.databinding.ActivityLoginBinding
import sv.edu.udb.aprendeapp.repository.UsuarioRepository
import sv.edu.udb.aprendeapp.ui.recursos.RecursosActivity
import sv.edu.udb.aprendeapp.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    // Retrasamos la inicialización hasta que sea seguro usarlos
    private val usuarioRepository: UsuarioRepository by lazy { UsuarioRepository() }
    private val sessionManager: SessionManager by lazy { SessionManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ahora que es seguro, configuramos los listeners
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (validateInputs(email, password)) {
                loginUser(email, password)
            }
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, pass: String): Boolean {
        binding.etEmail.error = null
        binding.etPassword.error = null

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.error_empty_field)
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = getString(R.string.error_invalid_email)
            return false
        }
        if (pass.isEmpty()) {
            binding.etPassword.error = getString(R.string.error_empty_field)
            return false
        }
        return true
    }

    private fun loginUser(email: String, password: String) {
        showLoading(true)
        lifecycleScope.launch {
            val resultado = usuarioRepository.login(email, password)
            resultado.onSuccess { usuarioLogueado ->
                sessionManager.saveAuthToken(usuarioLogueado.id ?: "")
                sessionManager.saveUserName(usuarioLogueado.nombre)
                Toast.makeText(this@LoginActivity, "¡Bienvenido, ${usuarioLogueado.nombre}!", Toast.LENGTH_SHORT).show()
                goToRecursos()
            }.onFailure { error ->
                Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                runOnUiThread { showLoading(false) }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.tvGoToRegister.isEnabled = !isLoading
    }

    private fun goToRecursos() {
        val intent = Intent(this, RecursosActivity::class.java)
        startActivity(intent)
        finish()
    }
}
