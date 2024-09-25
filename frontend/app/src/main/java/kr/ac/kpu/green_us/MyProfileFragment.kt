package kr.ac.kpu.green_us

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.FragmentMyProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileFragment : Fragment() {
    lateinit var binding:FragmentMyProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var userEmail: String
    var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater,container,false)

        // auth 인스턴스 초기화
        auth = Firebase.auth
        uid = auth.currentUser?.uid.toString()
        userEmail = auth.currentUser?.email.toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 현재 사용자 uid에 맞는 프로필 이미지 셋팅
        binding.userImg.clipToOutline = true
        uploadImgToProfile(uid)

        getUserByEmail{ user ->
            val userName = user!!.userName.toString()
            val userPhone = user.userPhone.toString()
            var userPhone1:String = "000"
            var userPhone2:String = "0000"
            var userPhone3:String = "0000"
            if(userPhone.length == 10){
                userPhone1 = userPhone.substring(0,2)
                userPhone2 = userPhone.substring(2,6)
                userPhone3 = userPhone.substring(6 .. userPhone.lastIndex)
                binding.phone2.text = userPhone1 + "-"+userPhone2 + "-" + userPhone3
            }
            else if(userPhone.length == 11){
                userPhone1 = userPhone.substring(0,3)
                userPhone2 = userPhone.substring(3,7)
                userPhone3 = userPhone.substring(7 .. userPhone.lastIndex)
                binding.phone2.text = userPhone1 + "-"+userPhone2 + "-" + userPhone3
            }
            else{
                binding.phone2.text = userPhone
            }
            val userAddr = user.userAddr.toString()
            val userAddrDetail = user.userAddrDetail.toString()

            binding.name2.text = userName
            binding.email2.text = userEmail
            binding.address2.text = userAddr + " " + userAddrDetail
        }
    }

    private fun uploadImgToProfile(uid:String){
        val storage = Firebase.storage
        val storageRef = storage.getReference("profileImgs/$uid")
        storageRef.downloadUrl.addOnSuccessListener { it ->
            Glide.with(this).load(it).into(binding.userImg)
        }.addOnFailureListener {
            Log.d("profileImg","사진 불러오기 실패")
        }
    }

    private fun getUserByEmail(callback: (User?) -> Unit) {
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
                            Log.d("MyProfileFragment", "회원 찾음 : ${user!!.userSeq}")
                        } else {
                            Log.e("MyProfileFragment", "회원 못찾음")
                        }
                    } else {
                        Log.e("MyProfileFragment", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    }
                    callback(user)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyProfileFragment", "서버 통신 중 오류 발생", t)
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }

}