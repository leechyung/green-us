package kr.ac.kpu.green_us

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.common.internal.FallbackServiceBroker
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Report
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.ActivityCertificationImgDetailBinding
import kr.ac.kpu.green_us.databinding.ActivityCertifyGreeningBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

class CertificationImgDetailActivity : AppCompatActivity(),ReportDialogInterface {
    private lateinit var binding: ActivityCertificationImgDetailBinding
    private var url : String = ""
    private var gSeq : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertificationImgDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 액티비티에서 보낸 url 받기
        url = intent.getStringExtra("imgUrl").toString()
//        Log.d("url check",url)
        gSeq = intent.getIntExtra("gSeq", -1)

        //초기세팅
        viewInit(url)

        // 신고 버튼 클릭
        binding.btnReport.setOnClickListener {
            showDialog()
        }

        // 이전 버튼 클릭
        binding.btnEsc.setOnClickListener { finish() }

        // 닫기 버튼 클릭
        binding.btnClose.setOnClickListener { finish() }
    }

    private fun viewInit(url:String){
        // 클릭한 이미지 url 받아와서 이미지뷰에 붙임
        Glide.with(this).load(url).into(binding.selectedImg)

        // 현재 로그인한 사용자 이메일 값 받아옴
        val currentUser = Firebase.auth.currentUser
        val currentEmail = currentUser?.email.toString()
        Log.d("currentEmail",currentEmail)

        // 클릭한 사진이 현재 로그인한 사용자가 올린 사진인지 확인함
        val store = Firebase.firestore
        val datas = store.collection("certificationImgs")
        datas.get().addOnSuccessListener {
                dataList -> for (data in dataList){
            if (data.data.get("url").toString() == url){
                val userEmail = data.data.get("userEmail").toString()
                if (userEmail == currentEmail){
                    // 내가올린 사진이면 신고버튼 안보임
                    Log.d("email","matched")
                    binding.btnReport.isGone = true
                }else{
                    // 아니면 신고버튼 보임
                    binding.btnReport.isVisible = true
                }
            }
        }
        }
    }
    // 다이얼로그에서 신고 버튼 클릭시 해당 url의 이메일 값 받아옴
    private fun searchUrlUid(url: String) {
        val store = Firebase.firestore
        val storageUrlList = arrayListOf<String>() //firestore에 저장된 이미지 url 담을 리스트
        store.collection("certificationImgs").get().addOnSuccessListener {
            dataList -> for (data in dataList){
                val aimData = data.data.get("url").toString()
                storageUrlList.add(aimData)
                if (aimData == url){ // 선택된 이미지와 같은 url을 찾아 그것의 uid를 가져옴
                    val result = data.data.get("userEmail").toString() // result = 찾은 email 값
                    val certifySeq = try{
                        data.data.get("certifySeq").toString().toInt()
                    }catch (e: NumberFormatException){
                        Log.e("CertificationImgError", "certifySeq 변환 오류: ${data.data.get("certifySeq").toString()}", e)
                        -1 // 변환 실패 시 -1 반환
                    }
                    if (result.isNotBlank() && certifySeq > -1){ // email이 null이 아니라면 reportedEmail 이라는 태그로 로그에 찍음
                        Log.d("reportedEmail",result)
                        ReportBycertifySeq(result, certifySeq)
                        finish()
                    }else{ // url은 있는데 email이 없는 경우
                        Log.d("reportedEmail","null Error")
                        Toast.makeText(this, "삭제된 회원입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (url !in storageUrlList){ // firebasestore에 없는 url인 경우
                Toast.makeText(this,"삭제되거나 이미 신고된 사진입니다.",Toast.LENGTH_SHORT).show()
                Log.d("CertificationImgError","firebasestore(DB)에 없는 이미지 url")
            }
        }
    }
    // 신고 다이얼로그 띄우기
    private fun showDialog(){
        val dialog = ReportDialog(this,"report")
        dialog.isCancelable = false //다이얼로그 띄워진동안 클릭 막기
        this.let { dialog.show(it.supportFragmentManager,"ReportDialog") }
    }
    // 다이얼로그에서 신고버튼 클릭시
    override fun ontYesButton() {
        searchUrlUid(url)
    }

    private fun ReportBycertifySeq(userEmail: String, certifySeq:Int){
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        apiService.registerReport(userEmail, certifySeq).enqueue(object : Callback<Report> {
            override fun onResponse(call: Call<Report>, response: Response<Report>) {
                if (response.isSuccessful) {
                    val report = response.body()
                    Log.d("CertificationImgDetailActivity", "신고 저장 완료: ${report!!.reportSeq}}")
                    Toast.makeText(this@CertificationImgDetailActivity, "신고가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }else{
                    val errorBody = response.errorBody()?.string()
                    when(response.code()){
                        HttpURLConnection.HTTP_CONFLICT, HttpURLConnection.HTTP_INTERNAL_ERROR ->{
                            Toast.makeText(this@CertificationImgDetailActivity, "이미 신고된 사진입니다!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.e("CertificationImgDetailActivity", "신고 저장 실패: ${response.code()}, ${response.errorBody()?.string()}")
                            //실패 처리 로직
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Report>, t: Throwable) {
                Log.e("CertificationImgDetailActivity", "서버 통신 중 오류 발생", t)
            }
        })
    }
}
