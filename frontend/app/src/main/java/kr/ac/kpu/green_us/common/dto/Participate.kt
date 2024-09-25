package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Participate (
    @SerializedName("pseq") @Expose val pSeq: Int = 0,
    @SerializedName("user") @Expose val user: User? = null,
    @SerializedName("greening") @Expose val greening: Greening? = null,
    @SerializedName("pcomplete") @Expose val pComplete: String? = "N",
    @SerializedName("pcount") @Expose val pCount: Int? = 0
)