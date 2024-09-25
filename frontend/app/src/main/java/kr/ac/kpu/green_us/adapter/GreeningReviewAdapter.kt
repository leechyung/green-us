package kr.ac.kpu.green_us.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Review
import kr.ac.kpu.green_us.common.dto.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GreeningReviewAdapter(private var greeningReview: List<Review> = emptyList()) :
    RecyclerView.Adapter<GreeningReviewAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userId: TextView = itemView.findViewById(R.id.userId)
        var reviewRating: RatingBar = itemView.findViewById(R.id.review_rating)
        var review: TextView = itemView.findViewById(R.id.review)
        var date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GreeningReviewAdapter.MyViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_review, parent, false)

        return MyViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = greeningReview[position]
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        var user: User? = null
        apiService.getUserByReviewSeq(review!!.reviewSeq).enqueue(object :
            Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    user = response.body()
                    var name = user?.userName ?: "아무개"
                    Log.d("GreeningReviewAdapter", "${user?.userName}")
                    var star = name.substring(1,name.length-1)
                    star = "*".repeat(star.length)
                    name = name.substring(0,1) + star + name.substring(name.length-1)
                    holder.userId.text = name
                    holder.reviewRating.rating = review.reviewRate!!
                    holder.review.text = review.reviewContent
                    holder.date.text = review.reviewDate
                    Log.d("GreeningReviewAdapter", "사용자 정보 가져오기 성공")
                } else {
                    Log.e("GreeningReviewAdapter", "사용자 정보 가져오기 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("GreeningReviewAdapter", "서버 통신 중 오류 발생", t)
            }
        })
    }

    override fun getItemCount(): Int {
        return greeningReview.size
    }

}