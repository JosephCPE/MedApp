package ph.edu.auf.student.lacson.joseph.medapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val API_NINJAS_BASE_URL = "https://api.api-ninjas.com/"
    private const val NEWS_API_BASE_URL = "https://newsapi.org/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val apiNinjasRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(API_NINJAS_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val newsApiRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(NEWS_API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = apiNinjasRetrofit.create(ApiService::class.java)
    val newsApiService: NewsApiService = newsApiRetrofit.create(NewsApiService::class.java)
}
