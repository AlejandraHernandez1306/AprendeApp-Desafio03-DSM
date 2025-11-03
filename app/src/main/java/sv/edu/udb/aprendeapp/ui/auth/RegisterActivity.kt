package sv.edu.udb.aprendeapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import sv.edu.udb.aprendeapp.R
import sv.edu.udb.aprendeapp.databinding.ActivityRegisterBinding
import sv.edu.udb.aprendeapp.model.Usuario
import sv.edu.udb.aprendeapp.repository.UsuarioRepository
import sv.edu.udb.aprendeapp.utils.ValidationUtils

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val usuarioRepository = UsuarioRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            handleRegistration()
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun handleRegistration() {
        val nombre = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (validateInputs(nombre, email, password, confirmPassword)) {
            val nuevoUsuario = Usuario(
                nombre = nombre,
                email = email,
                password = password
            )
            registerUser(nuevoUsuario)
        }
    }

    private fun validateInputs(nombre: String, email: String, pass: String, confirmPass: String): Boolean {

        if (nombre.isEmpty()) {
            binding.etName.error = getString(R.string.error_empty_field)
            return false
        }
        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.error_empty_field)
            return false
        }
        if (!ValidationUtils.isEmailValid(email)) {
            binding.etEmail.error = getString(R.string.error_invalid_email)
            return false
        }
        if (pass.isEmpty()) {
            binding.etPassword.error = getString(R.string.error_empty_field)
            return false
        }
        if (pass != confirmPass) {
            binding.etConfirmPassword.error = getString(R.string.error_passwords_not_match)
            return false
        }

        val passwordValidation = ValidationUtils.isPasswordValid(pass)
        var errorMessage = ""
        if (passwordValidation["length"] == false) errorMessage += "- ${getString(R.string.error_password_short)}\n"
        if (passwordValidation["uppercase"] == false) errorMessage += "- ${getString(R.string.error_password_uppercase)}\n"
        if (passwordValidation["lowercase"] == false) errorMessage += "- ${getString(R.string.error_password_lowercase)}\n"
        if (passwordValidation["number"] == false) errorMessage += "- ${getString(R.string.error_password_number)}\n"
        if (passwordValidation["special"] == false) errorMessage += "- ${getString(R.string.error_password_special)}\n"

        if (errorMessage.isNotEmpty()) {
            binding.etPassword.error = errorMessage.trim()
            return false
        }

        return true
    }

    private fun registerUser(usuario: Usuario) {
        showLoading(true)

        lifecycleScope.launch {
            val resultado = usuarioRepository.registrarUsuario(usuario)
            resultado.onSuccess { usuarioRegistrado ->
                Toast.makeText(this@RegisterActivity, "¡Registro exitoso! ID: ${usuarioRegistrado.id}", Toast.LENGTH_LONG).show()
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }.onFailure { error ->
                Toast.makeText(this@RegisterActivity, "Error en el registro: ${error.message}", Toast.LENGTH_LONG).show()
            }
            showLoading(false)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }
}