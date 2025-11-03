package sv.edu.udb.aprendeapp.repository

import sv.edu.udb.aprendeapp.model.Usuario
import sv.edu.udb.aprendeapp.network.RetrofitClient

class UsuarioRepository {

    private val api = RetrofitClient.apiService

    suspend fun registrarUsuario(usuario: Usuario): Result<Usuario> {
        // Bloque try-catch para atrapar CUALQUIER error de red o de la API
        return try {
            val response = api.registrarUsuario(usuario)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al registrar: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de conexión: ${e.message}"))
        }
    }

    suspend fun login(email: String, password: String): Result<Usuario> {
        // Bloque try-catch para atrapar CUALQUIER error
        return try {
            val response = api.getUsuarios()
            if (response.isSuccessful) {
                val usuarios = response.body() ?: emptyList()
                val usuarioEncontrado = usuarios.find { it.email == email && it.password == password }
                if (usuarioEncontrado != null) {
                    Result.success(usuarioEncontrado)
                } else {
                    Result.failure(Exception("Credenciales inválidas"))
                }
            } else {
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de conexión: ${e.message}"))
        }
    }
}
