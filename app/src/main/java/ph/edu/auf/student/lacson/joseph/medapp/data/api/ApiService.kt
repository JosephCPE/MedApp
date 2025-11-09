package ph.edu.auf.student.lacson.joseph.medapp.data.api

import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @Headers("X-Api-Key: /kip+a1iOiu417UeEWdl1Q==LyvmBOmg4ikj5KUV")
    @GET("v1/healthtip")
    suspend fun getHealthTip(): HealthTipResponse
}