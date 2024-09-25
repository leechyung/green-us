package kr.ac.kpu.green_us

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.adapter.AdvAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Prize
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.FragmentMypageBinding
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.net.URI
import java.net.URL
import kotlin.math.log

// 마이페이지 - 포인트, 개설하기, 내리뷰, 프로필관리, 공지사항, FAQ, 고객센터 화면으로 이동 가능
class MypageFragment : Fragment(),ReportDialogInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var userEmail: String
    private var allPrizes: List<Prize> = emptyList()
    lateinit var binding: FragmentMypageBinding
    private lateinit var sharedPreferencesToMypage: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater,container,false)
        sharedPreferencesToMypage = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        auth = Firebase.auth
        uid = auth.currentUser?.uid.toString()
        userEmail = auth.currentUser?.email.toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAdvImgList()
        updatePointBalance()
        checkWarningCounts()

        // 프로필
        binding.userImg.clipToOutline = true //이미지 둥글게
        uploadImgToProfile(uid) // 현재 로그인한 회원에 맞는 이미지 불러오기
        settingUserName(userEmail) // 현재 로그인한 회원에 맞는 이름 불러오기

        // 개설하기
        binding.goToGreenOpen.setOnClickListener {
            val intent = Intent(getActivity(), SubActivity::class.java)
            intent.putExtra("2","open_green")
            startActivity(intent)
        }

        // 포인트
        binding.pointV.setOnClickListener {
            val intent = Intent(getActivity(), PointActivity::class.java)
            startActivity(intent)
        }

        // 내 리뷰
        binding.myReview.setOnClickListener {
            val intent = Intent(getActivity(), MyReviewActivity::class.java)
            // intent.putExtra("3","my_review")
            startActivity(intent)
        }

        // 프로필 관리
        binding.profileSetting.setOnClickListener {
            val intent = Intent(getActivity(), SubActivity::class.java)
            intent.putExtra("4","my_profile")
            startActivity(intent)
        }

        // 공지사항
        binding.notice.setOnClickListener {
            val intent = Intent(getActivity(), NoticeActivity::class.java)
            startActivity(intent)
        }

        // FAQ
        binding.faq.setOnClickListener {
            val intent = Intent(getActivity(), FaqActivity::class.java)
            startActivity(intent)
        }

        // 고객센터
        binding.csc.setOnClickListener {

        }

        // 로그아웃
        binding.logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(getActivity(), LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
            startActivity(intent)
        }
        // 회원탈퇴
        binding.quit.setOnClickListener {
            showDialog()
        }

    }
    private fun getAdvImgList(){
        var imgList = arrayListOf<Uri>()
        val storageRef = Firebase.storage.reference.child("advImgs")
        storageRef.listAll().addOnSuccessListener { list ->
            for (img in list.items){
                img.downloadUrl.addOnSuccessListener {uri->
                    imgList.add(uri)
                }.addOnSuccessListener {
                    val advAdapter = AdvAdapter(imgList)
                    advAdapter.notifyDataSetChanged()
                    binding.adv.adapter = advAdapter
                    binding.adv.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                }
            }
        }
    }
    // 탈퇴 다이얼로그 띄우기
    private fun showDialog(){
        val dialog = ReportDialog(this,"quit")
        dialog.isCancelable = false //다이얼로그 띄워진동안 클릭 막기
        this.let { dialog.show(it.parentFragmentManager,"ReportDialog") }
    }
    // 다이얼로그에서 탈퇴버튼 클릭시
    override fun ontYesButton() {
        val user = Firebase.auth.currentUser!!
        val Email = user.email?:""
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        apiService.deleteUserByEmail(Email).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("MypageFragment", "User account deleted.")
                            }
                        }
                    val user = response.body()
                    if(user != null) {
                        Toast.makeText(context, "${user.userName}님의 탈퇴를 완료했습니다", Toast.LENGTH_SHORT).show()
                        // LoginActivity로 이동하는 Intent 생성
                        val intent = Intent(context, LoginActivity::class.java)

                        // 기존 액티비티 스택을 지우고 새로운 스택으로 시작하는 플래그를 설정
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        // 새로운 액티비티 시작
                        startActivity(intent)

                        // 현재 액티비티 종료
                        activity?.finish()
                    }
                    Log.d("MypageFragment", "서버에서 데이터 삭제 성공")

                } else {
                    Log.e("MypageFragment", "서버에서 데이터 삭제 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    Toast.makeText(context, "다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MypageFragment", "서버 통신 중 오류 발생", t)
                Toast.makeText(context, "다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }
        })


    }
    private fun uploadImgToProfile(uid:String){
        val storage = Firebase.storage
        val storageRef = storage.getReference("profileImgs/$uid")
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(binding.userImg)
        }.addOnFailureListener {
            Log.d("profileImg","사진 불러오기 실패")
        }
    }
    private fun settingUserName(userEmail:String){
        val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        retrofitAPI.getUserbyEmail(userEmail).enqueue(object :Callback<User>{
            override fun onResponse(call: Call<User>, response: retrofit2.Response<User>) {
                if(response.isSuccessful){
                    val userName = response.body()?.userName.toString()
                    binding.name.text = userName
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("settingUserName-retrofitApi","불러오기 실패")
            }

        })
    }
    private fun updatePointBalance() {
        val pointBalance = sharedPreferencesToMypage.getInt("point_balance", -1)
        binding.point.text = pointBalance.toString()
    }

    private fun fetchPrizes(userSeq: Int) {
        val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)

        retrofitAPI.getPrizeByUserSeq(userSeq).enqueue(object : Callback<List<Prize>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<List<Prize>>, response: Response<List<Prize>>) {
                if (response.isSuccessful) {
                    allPrizes = response.body() ?: emptyList()

                    // 포인트 총액 계산
                    val totalPoints = allPrizes.sumOf { it.prizeMoney ?: 0 }
                    Log.d("PointActivity", "포인트 총액: $totalPoints")

                } else {
                    Log.d("PointActivity", "API 응답 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Prize>>, t: Throwable) {
                Log.d("PointActivity", "API 호출 실패: ${t.message}")
            }
        })
    }
    private fun checkWarningCounts(){
        val userEmail = auth.currentUser?.email.toString()
        val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        retrofitAPI.getUserWCountByUserEmail(userEmail).enqueue(object :Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful){
                    val waringCounts = response.body()
                    Log.d("MypageFragment","신고당한횟수: $waringCounts ")

                    if (waringCounts != null) {
                        if (waringCounts >= 5){
                            binding.warning.text = "신고 횟수가 5회이상이므로\n그리닝 참여가 제한됩니다."
                            binding.warning.isVisible = true
                        }else{
                            binding.warning.text = ""
                            binding.warning.isGone = true
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d("MypageFragment", "API 호출 실패: ${t.message}")
            }


        })
    }
}