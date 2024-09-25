package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Report (
    @SerializedName("reportSeq") @Expose val reportSeq:Int=0,
    @SerializedName("certify") @Expose val certify:Certify?= null,
    @SerializedName("reportDate") @Expose val reportDate: String? = null,
    @SerializedName("reportResult") @Expose val reportResult:String? = null
)