package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Withdraw (
    @SerializedName("withdrawSeq") val withdrawSeq: Int = 0,
    @SerializedName("user") @Expose var user: User? = null,
    @SerializedName("withdrawContent") val withdrawContent: String? = null,
    @SerializedName("withdrawDate") val withdrawDate: String? = null,
    @SerializedName("withdrawAmount") val withdrawAmount: Int = 0
)