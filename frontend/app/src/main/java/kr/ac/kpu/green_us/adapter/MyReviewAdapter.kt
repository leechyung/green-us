package kr.ac.kpu.green_us.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kpu.green_us.DeleteCheckActivity
import kr.ac.kpu.green_us.MyReviewActivity
import kr.ac.kpu.green_us.R
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Review
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyReviewAdapter(private var reviewList: MutableList<Review>? = mutableListOf<Review>()) :

    RecyclerView.Adapter<MyReviewAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title)
        var review: TextView = itemView.findViewById(R.id.review)
        var date: TextView = itemView.findViewById(R.id.date)
        var delete: Button = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MyReviewAdapter.MyViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_my_review, parent, false)

        return MyViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = reviewList?.get(position)

        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        apiService.getGreeningByReviewSeq(review!!.reviewSeq).enqueue(object :
            Callback<Greening> {
            override fun onResponse(call: Call<Greening>, response: Response<Greening>) {
                if (response.isSuccessful) {
                    val greening = response.body()
                    holder.title.text = greening!!.gName
                } else {
                    Log.e("MyReviewAdapter", "Greening 데이터 로딩 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Greening>, t: Throwable) {
                Log.e("MyReviewAdapter", "서버 통신 중 오류 발생", t)
            }
        })

        holder.review.text = review?.reviewContent
        holder.date.text = review?.reviewDate
        holder.delete.setOnClickListener {
            val dlg = DeleteCheckActivity(holder.itemView.context as MyReviewActivity)
            dlg.setOnDeleteClickedListener { content ->
                if (content == 2) {
                    reviewList?.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, reviewList?.size ?: 0)
                    val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                    apiService.deleteReview(review!!.reviewSeq).enqueue(object :
                        Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("MyReviewAdapter", "Review 데이터 삭제 성공")
                            } else {
                                Log.e("MyReviewAdapter", "Review 데이터 삭제 실패: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("MyReviewAdapter", "서버 통신 중 오류 발생", t)
                        }
                    })
                }
            }
            dlg.show()
        }
    }

    override fun getItemCount(): Int {
        return reviewList?.size ?: 0
    }

    fun updateData(newList: MutableList<Review>) {
        reviewList = newList
        notifyDataSetChanged()
    }

}