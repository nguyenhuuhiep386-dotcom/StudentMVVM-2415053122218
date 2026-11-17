package com.example.studentmvvm.network

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

data class StudentDto(
    @SerializedName("id") val remoteId: String?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("student_code") val studentCode: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("gpa") val gpa: Double?
)

data class CreateStudentRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("student_code") val studentCode: String,
    @SerializedName("email") val email: String,
    @SerializedName("gpa") val gpa: Double
)

interface StudentApiService {
    @GET("api/v1/students")
    suspend fun getStudents(): List<StudentDto?>

    @POST("api/v1/students")
    suspend fun createStudent(@Body student: CreateStudentRequest): StudentDto
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8765/"

    val apiService: StudentApiService by lazy {
        val client = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StudentApiService::class.java)
    }
}
