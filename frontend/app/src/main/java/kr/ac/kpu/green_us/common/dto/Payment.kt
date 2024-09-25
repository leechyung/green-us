package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("user") @Expose var user: User? = null,
    @SerializedName("paymentSeq") @Expose var paymentSeq: Int = 0,
    @SerializedName("paymentContent") @Expose var paymentContent: String? = null,
    @SerializedName("paymentMethod") @Expose var paymentMethod: String? = null,
    @SerializedName("paymentDate") @Expose var paymentDate: String? = null,
    @SerializedName("paymentMoney") @Expose var paymentMoney: Int? = null
)