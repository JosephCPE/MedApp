package ph.edu.auf.student.lacson.joseph.medapp.data.api

import com.google.gson.annotations.SerializedName

data class HealthTipResponse(
    @SerializedName("tip")
    val tip: String
)