package sv.edu.udb.aprendeapp.repository

import sv.edu.udb.aprendeapp.model.Recurso
import sv.edu.udb.aprendeapp.network.RetrofitClient

class RecursoRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun obtenerRecursos(): Result<List<Recurso>> {
        return try {
            val response = apiService.getRecursos()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al obtener recursos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de conexión: ${e.message}"))
        }
    }

    suspend fun crearRecurso(recurso: Recurso): Result<Recurso> {
        return try {
            val response = apiService.createRecurso(recurso)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear el recurso: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de conexión: ${e.message}"))
        }
    }

    suspend fun actualizarRecurso(id: String, recurso: Recurso): Result<Recurso> {
        return try {
            val response = apiService.updateRecurso(id, recurso)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar el recurso: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de conexión: ${e.message}"))
        }
    }

    suspend fun eliminarRecurso(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteRecurso(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar el recurso: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Fallo de conexión: ${e.message}"))
        }
    }
}
