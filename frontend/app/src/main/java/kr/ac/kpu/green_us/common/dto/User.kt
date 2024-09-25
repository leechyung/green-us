package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//spring server에서 받는 변수명으로 serialzed해줘야함

data class User(
    @SerializedName("userSeq") @Expose val userSeq: Int = 0,
    @SerializedName("userName") @Expose val userName: String? = null,
    @SerializedName("userEmail") @Expose val userEmail: String? = null,
    @SerializedName("userAddr") @Expose var userAddr: String? = null,
    @SerializedName("userAddrDetail")@Expose var userAddrDetail : String?=null,
    @SerializedName("userPhone") @Expose val userPhone: String? = null,
    @SerializedName("userPhoto") @Expose val userPhoto: String? = null,
    @SerializedName("userPedometer")@Expose var userPedometer: Int = 0,
    @SerializedName("userWCount") @Expose val userWCount: Int? = null
)