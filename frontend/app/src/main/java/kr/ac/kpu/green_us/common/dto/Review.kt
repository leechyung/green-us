package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Review (
    @SerializedName("reviewSeq") @Expose val reviewSeq: Int = 0,
    @SerializedName("reviewContent") @Expose val reviewContent: String? = null,
    @SerializedName("reviewDate") @Expose val reviewDate: String? = null,
    @SerializedName("reviewRate") @Expose val reviewRate: Float? = null,
    @SerializedName("greening") @Expose val greening: Greening,
    @SerializedName("user") @Expose val user: User?
)