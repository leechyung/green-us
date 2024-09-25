package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.SerializedName

data class Prize (
    @SerializedName ("prizeSeq") val prizeSeq: Int = 0,
    @SerializedName("userSeq") val userSeq: Int? = null,
    @SerializedName("gSeq") val greeningSeq: Int? = null,
    @SerializedName("pSeq") val participateSeq: Int? = null,
    @SerializedName("prizeName") val prizeName: String? = null,
    @SerializedName("prizeMoney") val prizeMoney: Int? = null,
    @SerializedName("prizeDate") val prizeDate: String? = null
)