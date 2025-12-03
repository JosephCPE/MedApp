package ph.edu.auf.student.lacson.joseph.medapp.data.api

import com.google.gson.annotations.SerializedName


data class HealthTipResponse(
    @SerializedName("tip")
    val tip: String
)


data class NewsApiResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int,
    @SerializedName("articles")
    val articles: List<HealthArticle>
)


data class HealthArticle(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("url")
    val url: String,
    @SerializedName("urlToImage")
    val imageUrl: String?,
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("source")
    val source: ArticleSource
)


data class ArticleSource(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String
)
