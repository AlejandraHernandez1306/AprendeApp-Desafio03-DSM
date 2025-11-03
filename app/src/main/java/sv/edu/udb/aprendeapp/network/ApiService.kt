package sv.edu.udb.aprendeapp.network

import retrofit2.Response
import retrofit2.http.*
import sv.edu.udb.aprendeapp.model.Recurso
import sv.edu.udb.aprendeapp.model.Usuario

interface ApiService {
    @GET("recursos")
    suspend fun getRecursos(): Response<List<Recurso>>

    @GET("recursos/{id}")
    suspend fun getRecursoById(@Path("id") id: String): Response<Recurso>

    @POST("recursos")
    suspend fun createRecurso(@Body recurso: Recurso): Response<Recurso>

    @PUT("recursos/{id}")
    suspend fun updateRecurso(@Path("id") id: String, @Body recurso: Recurso): Response<Recurso>

    @DELETE("recursos/{id}")
    suspend fun deleteRecurso(@Path("id") id: String): Response<Void>

    @GET("usuarios")
    suspend fun getUsuarios(): Response<List<Usuario>>
    @POST("usuarios")
    suspend fun registrarUsuario(@Body usuario: Usuario): Response<Usuario>
}
