package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Review
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.FragmentMyReviewWriteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class MyReviewWriteFragment : Fragment() {
    lateinit var binding: FragmentMyReviewWriteBinding
    var review_content = ""

    var gSeq by Delegates.notNull<Int>()

    lateinit var auth: FirebaseAuth
    var user: User? = null
    var greening: Greening? = null

    val today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyReviewWriteBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        gSeq = (activity as SubActivity).gSeqCheck()

        getUserByEmail()

        if(gSeq <= -1){
            //gSeq조회 실패한 경우 예외처리 -> 로그아웃하고 초기화면으로
            Log.e("MyReviewWriteFragment","gSeq 실패")
        }else{
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getGreeningById(gSeq).enqueue(object : Callback<Greening> {
                override fun onResponse(call: Call<Greening>, response: Response<Greening>) {
                    if (response.isSuccessful) {
                        greening = response.body()
                        if(greening != null){
                            binding.greenName.text = greening!!.gName
                        }
                    } else {
                        Log.e("MyReviewWriteFragment", "Greening 데이터 로딩 실패: ${response.code()}")
                    }
                }

                override fun onFailure(p0: Call<Greening>, p1: Throwable) {
                    Log.e("MyReviewWriteFragment", "서버 통신 중 오류 발생", p1)
                }
            })
        }

        // 리뷰 작성 버튼
        binding.writeReviewBtn.setOnClickListener {
            if(binding.editText.text.toString().length > 200){
                Toast.makeText(requireContext(), "200자 이하로 작성해 주세요", Toast.LENGTH_SHORT).show()
            }
            else if(binding.editText.text.toString().length < 0 || binding.editText.text.toString().length == 0 ) {
                Toast.makeText(requireContext(), "리뷰를 작성해 주세요", Toast.LENGTH_SHORT).show()
            }
            else {

                var rate = binding.ratingBar.rating
                var review = Review(
                reviewSeq = 0,
                reviewContent = review_content,
                reviewDate = today.toString(),
                reviewRate = rate,
                greening = greening!!,
                user = user
            )
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.createReview(review).enqueue(object : Callback<Review> {
                override fun onResponse(call: Call<Review>, response: Response<Review>) {
                    if (response.isSuccessful) {
                        val review = response.body() ?: null
                        if(review != null){

                        }
                    } else {
                        Log.e("MyReviewWriteFragment", "Review 데이터 로딩 실패: ${response.code()}")
                    }
                }

                override fun onFailure(p0: Call<Review>, p1: Throwable) {
                    Log.e("MyReviewWriteFragment", "서버 통신 중 오류 발생", p1)
                }
            })
                val intent = Intent(getActivity(), MyReviewActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("gSeq", gSeq)
                startActivity(intent)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textNum.text = "0/200"
                binding.textNum.setTextColor(ContextCompat.getColor(context!!, R.color.grey))
            }

            override fun afterTextChanged(p0: Editable?) {
                review_content = binding.editText.text.toString()
                if(review_content.length > 200){
                    binding.textNum.setTextColor(ContextCompat.getColor(context!!, R.color.red))
                }
                else{
                    binding.textNum.setTextColor(ContextCompat.getColor(context!!, R.color.grey))
                }
                binding.textNum.text = review_content.length.toString() + "/200"
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                review_content = binding.editText.text.toString()
                if(review_content.length > 200){
                    binding.textNum.setTextColor(ContextCompat.getColor(context!!, R.color.red))
                }
                else{
                    binding.textNum.setTextColor(ContextCompat.getColor(context!!, R.color.grey))
                }
                binding.textNum.text = review_content.length.toString() + "/200"
            }
        })
    }

    private fun getUserByEmail() {
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email.toString()
        Log.d("currentEmail", currentEmail)

        if (currentEmail.isNotEmpty()) {
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getUserbyEmail(currentEmail).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        user = response.body()
                        if (user != null) {
                            Log.d("MyGreenIngFragment", "회원 찾음 : ${user!!.userSeq}")
                        } else {
                            Log.e("MyGreenIngFragment", "회원 못찾음")
                        }
                    } else {
                        Log.e("MyGreenIngFragment", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyGreenIngFragment", "서버 통신 중 오류 발생", t)
                }
            })
        } else {
        }
    }

}