package kr.ac.kpu.green_us.common.dto


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
//import java.time.LocalDate -> 년/월/일
//import java.util.Date -> 시/분/초까지 포함

data class Greening(
//    @SerializedName("reviews") @Expose val reviews: List<Review> = emptyList(),
    @SerializedName("gseq") @Expose val gSeq: Int,
    @SerializedName("gname") @Expose val gName: String?,
    @SerializedName("gstartDate") @Expose val gStartDate: String?,
    @SerializedName("gendDate") @Expose val gEndDate: String?,
    @SerializedName("gcertiWay") @Expose val gCertiWay: String?,
    @SerializedName("ginfo") @Expose val gInfo: String? = null,
    @SerializedName("gmemberNum") @Expose val gMemberNum: Int? = 0,
    @SerializedName("gfreq") @Expose val gFreq: Int?,
    @SerializedName("gdeposit") @Expose val gDeposit: Int?,
    @SerializedName("gtotalCount") @Expose val gTotalCount: Int? = 0,
    @SerializedName("gnumber") @Expose val gNumber: Int?,
    @SerializedName("gkind") @Expose val gKind: Int?,
    @SerializedName("user") @Expose val user: User?
)