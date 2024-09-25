package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

class Certify (
    @SerializedName("certifySeq") @Expose val certifySeq:Int=0,
    @SerializedName("certifyImg") @Expose val certifyImg:String?= null,
    @SerializedName("certifyDate") @Expose val certifyDate: String? = null,
    @SerializedName("userSeq") @Expose val userSeq:Int? = null,
    @SerializedName("gseq") @Expose val gSeq: Int? = null,
    @SerializedName("pseq") @Expose val pSeq:Int? = null
)