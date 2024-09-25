package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.SerializedName

//spring server에서 받는 변수명으로 serialzed해줘야함

data class Users(
    @SerializedName("userEmail") val userEmail : String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userPhone") val userPhone: String,
    @SerializedName("userAddr") val userAddr: String,
    @SerializedName("userAddrDetail") val userAddrDetail: String,
//    @SerializedName("userAccount") val userAccount: String
)