package kr.ac.kpu.green_us.common.dto

import com.google.gson.annotations.SerializedName


data class Notice(
    @SerializedName("noticeSeq") val noticeSeq: Int,
    @SerializedName("noticeTitle") val noticeTitle: String,
    @SerializedName("noticeContent") val noticeContent: String,
    @SerializedName("noticeDate") val noticeDate: String?
)