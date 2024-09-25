package kr.ac.kpu.green_us.common.api

import kr.ac.kpu.green_us.common.dto.Certify
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Notice
import kr.ac.kpu.green_us.common.dto.Participate
import kr.ac.kpu.green_us.common.dto.Payment
import kr.ac.kpu.green_us.common.dto.Prize
import kr.ac.kpu.green_us.common.dto.Report
import kr.ac.kpu.green_us.common.dto.Review
import kr.ac.kpu.green_us.common.dto.Users
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.common.dto.Withdraw
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitAPI {
    @POST("/users/new")
    fun registerUser(@Body user: Users): Call<Users>

    @GET("/users/byEmail/{email}")
    fun getUserbyEmail(@Path("email") email: String): Call<User>

    @GET("/users/byPhone/{phone}")
    fun getUserByPhone(@Path("phone") phone: String): Call<User>

    @GET("/users/seqByEmail/{email}")
    fun getUserSeqByEmail(@Path("email") email: String): Call<Int>

    @PUT("/users/update/{userSeq}")
    fun updateUser(@Path("userSeq") userSeq: Int, @Body user: User): Call<User>

    @POST("/greening/new")
    fun registerGreening(@Body greening: Greening): Call<Greening>

    @GET("/greening/byId/{id}")
    fun getGreeningById(@Path("id") id: Int): Call<Greening>

    @GET("/greening/list/do")
    fun getDoGreening(): Call<List<Greening>>

    @GET("/greening/list/buy")
    fun getBuyGreening(): Call<List<Greening>>

    @GET("/greening/list/new")
    fun getNewGreening(): Call<List<Greening>>

    @GET("/greening/list/pop")
    fun getPopGreening(): Call<List<Greening>>

    @GET("/greening/byUserSeq/{userSeq}")
    fun getGreeningByUserSeq(@Path("userSeq") userSeq: Int): Call<List<Greening>>

    @POST("/participate/new")
    fun registerParticipate(@Body participate: Participate): Call<Participate>

    @GET("/participate/GreeningByUserSeq/{userSeq}")
    fun findGreeningByUserSeq(@Path("userSeq") userSeq: Int): Call<List<Greening>>

    @GET("/participate/YGreeningByUserSeq/{userSeq}")
    fun findYGreeningByUserSeq(@Path("userSeq") userSeq: Int): Call<List<Greening>>

    @GET("/participate/ByUserEmailAndGSeq/{gSeq}/{userEmail}")
    fun findPSeqByGSeqAndUserEmail(
        @Path("userEmail") userEmail: String,
        @Path("gSeq") gSeq: Int,
    ): Call<Int>

    @GET("/participate/greening/byId/{id}")
    fun getParticipateByPId(@Path("id") id: Int): Call<Greening>

    @GET("/participate/NbyUserSeq/{userSeq}")
    fun getNParticipateByUserSeq(@Path("userSeq") userSeq: Int): Call<List<Participate>>

    @GET("/participate/byUserSeq/{userSeq}")
    fun getGreeningParticipateByUserSeq(@Path("userSeq") userSeq: Int): Call<List<Participate>>

    @GET("/participate/gSeqByUserAndGreening/{gSeq}/{userSeq}")
    fun findpSeqByUserSeqAndgSeq(
        @Path("userSeq") userSeq: Int,
        @Path("gSeq") gSeq: Int,
    ): Call<Int>

    @FormUrlEncoded
    @POST("/certify/new")
    fun registerCertify(
        @Field("userEmail") userEmail: String,
        @Field("gSeq") gSeq: Int,
        @Field("certifyDate") certifyDate: String // ISO 8601 형식으로 날짜를 전달
    ): Call<Certify>

    @GET("/certify/byGreeningUser/{userSeq}/{gSeq}")
    fun getCertifyByUserSeqAndGSeq(
        @Path("userSeq") userSeq: Int,
        @Path("gSeq") gSeq: Int,
    ): Call<List<Certify>>

    @GET("/certify/byUSerEmailAndGSeq/{userEmail}/{gSeq}")
    fun getCertifyByUserEmailAndGSeq(@Path("userEmail") userEmail: String, @Path("gSeq") gSeq: Int): Call<List<Certify>>

    @FormUrlEncoded
    @POST("/report/new")
    fun registerReport(
        @Field("userEmail") userEmail:String,
        @Field("certifySeq") certifySeq: Int
    ):Call<Report>

    @GET("/notice/list")
    fun getNotices(): Call<List<Notice>>

    @POST("/payment/new")
    fun createPayment(@Body payment: Payment): Call<Payment>

    @GET("/review/byUserSeq/{userSeq}")
    fun getReviewByUserSeq(@Path("userSeq") userSeq: Int): Call<List<Review>>

    @POST("/review/new")
    fun createReview(@Body review: Review): Call<Review>

    @GET("/review/greeningbyUserSeq/{userSeq}")
    fun getMyReviewGreeningByUserSeq(@Path("userSeq") userSeq: Int): Call<List<Greening>>

    @DELETE("/review/delete/{reviewSeq}")
    fun deleteReview(@Path("reviewSeq") reviewSeq: Int) : Call<Void>

    @GET("/review/byUserSeqAndGreening/{gSeq}/{userSeq}")
    fun getReviewByUserSeqAndgSeq(@Path("userSeq") userSeq: Int, @Path("gSeq") gSeq: Int): Call<Review>

    @GET("/review/bygSeq/{gSeq}")
    fun getReviewByGreeningSeq(@Path("gSeq") gSeq: Int): Call<List<Review>>

    @GET("/review/byReviewSeq/{reviewSeq}")
    fun getUserByReviewSeq(@Path("reviewSeq") reviewSeq: Int): Call<User>

    @GET("/review/greeningByReviewSeq/{reviewSeq}")
    fun getGreeningByReviewSeq(@Path("reviewSeq") reviewSeq: Int): Call<Greening>

    @GET("/prize/list")
    fun getPrizes(): Call<List<Prize>>

    @GET("/prize/byUserSeq/{userSeq}")
    fun getPrizeByUserSeq(@Path("userSeq") userSeq: Int): Call<List<Prize>>

    @FormUrlEncoded
    @POST("/prize/new")
    fun registerPrize( @Field("userEmail") userEmail:String,
                       @Field("gSeq") gSeq:Int,
                       @Field("prizeMoney") prizeMoney:Int): Call<Prize>

    @POST("/withdraw/new")
    fun createWithdraw(@Body withdraw: Withdraw) : Call<Withdraw>

    @GET("/withdraw/list")
    fun getWithdraws(): Call<List<Withdraw>>

    @GET("/withdraw/byUserSeq/{userSeq}")
    fun getWithdrawByUserSeq(@Path("userSeq") userSeq: Int): Call<List<Withdraw>>

    @POST("/users/deleteByEmail/{userEmail}")
    fun deleteUserByEmail(@Path("userEmail") userEmail: String): Call<User>

    @GET("/users/wCountByEmail/{userEmail}")
    fun getUserWCountByUserEmail(@Path("userEmail") userEmail: String): Call<Int>
}