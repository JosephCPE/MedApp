package ph.edu.auf.student.lacson.joseph.medapp.data.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ApiService {
    @Headers("X-Api-Key: kIpga1i1Iu147UcEWQ1IQ==LyvmBOmg4jkj5RuV")
    @GET("v1/healthtip")
    suspend fun getHealthTip(): HealthTipResponse
}


interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getHealthNews(
        @Query("category") category: String = "health",
        @Query("country") country: String = "us",
        @Query("pageSize") pageSize: Int = 10,
        @Query("page") page: Int = 1, // ✅ allows refreshing with different pages
        @Query("apiKey") apiKey: String
    ): NewsApiResponse
}
