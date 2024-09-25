package kr.ac.kpu.green_us

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.FragmentMyProfileEditBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileEditBinding
    private lateinit var uri: Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var userEmail: String
    private var address = ""
    private lateinit var compltCode : String
    var user: User? = null
    lateinit var userAddr:String
    lateinit var userAddrDetail:String

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                compltCode = "changed" //프로필 이미지를 변경했으면
                uri = data?.data!!
                // 선택한 이미지 프로필에 배치 (스토리지 저장 안 된 상태)
                Glide.with(this).load(uri).into(binding.userImg)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Log.d("ImagePicker","이미지 선택을 취소했습니다.")
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyProfileEditBinding.inflate(inflater,container,false)
        compltCode = "unchanged"
        // auth 인스턴스 초기화
        auth = Firebase.auth
        uid = auth.currentUser?.uid.toString()
        userEmail = auth.currentUser?.email.toString()

        // 프로필 이미지 둥글게
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
            userAddr = user.userAddr.toString()
            userAddrDetail = user.userAddrDetail.toString()

            binding.name2.text = userName
            binding.email2.text = userEmail
            binding.address2.text = userAddr
            binding.addressDetail.setText(userAddrDetail)
        }

        // 주소찾기 버튼 클릭 시
        binding.btnSearchAddress.setOnClickListener {
            val dialogFragment = AddressDialogFragment()
            dialogFragment.show(parentFragmentManager, "AddressDialog")
        }
        setFragmentResultListener("addressData") { _, bundle ->
            address = bundle.getString("address", "")
            address?.let {
                binding.address2.setText(it)
                binding.addressDetail.setText("")
            }
        }


        // 카메라 버튼 클릭 시 카메라/갤러리 창 띄우기
        binding.camera.setOnClickListener{
            // 1. 이미지 선택
            // 2. 선택한 이미지 프로필에 배치(스토리지 저장x)
            both()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 완료 버튼 클릭 시
        // db에 데이터 저장 후
        // 이미지 스토리지에 업로드
        binding.complete.setOnClickListener {
            Log.d("MyProfileEditFragment","compltCode -> {$compltCode}")

            user!!.userAddr = binding.address2.text.toString()
            user!!.userAddrDetail = binding.addressDetail.text.toString()
            Log.d("MyProfileEditFragment", "${user!!.userAddr}, ${user!!.userAddrDetail}")
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.updateUser(user!!.userSeq, user!!).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Log.d("MyProfileEditFragment", "서버로 데이터 전송 성공")

                        // 이미지 바꾸면 업로드함
                        if (compltCode == "changed"){
                            uploadImgAndReplaceView(uri,uid)
                        }
                        //안 바꾸면 업로드 하지 않고 화면 이동 및 편집 et 보이기
                        else{
                            (activity as SubActivity).supportFragmentManager.beginTransaction()
                                .replace(kr.ac.kpu.green_us.R.id.sub_frame,MyProfileFragment()).commit()
                            (activity as SubActivity).hidingEdit("show")
                        }
                    } else {
                        Log.e("MyProfileEditFragment", "서버로 데이터 전송 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyProfileEditFragment", "서버 통신 중 오류 발생", t)
                }
            })
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun both(){
        // permission checker 변수 선언
        val galleryPermissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_MEDIA_IMAGES
        )
        val cameraPermissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        )
        // 권한이 없을 경우
        if (galleryPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 갤러리
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                1000
            )
        }
        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 카메라
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                1000
            )
        }
        //권한이 있는 경우
        if ((galleryPermissionCheck== PackageManager.PERMISSION_GRANTED)&&(cameraPermissionCheck== PackageManager.PERMISSION_GRANTED)) {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080,1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }

    private fun uploadImgAndReplaceView(uri: Uri,uid:String) {
        // storage 인스턴스 생성
        val storage = Firebase.storage

        // storage 참조(파일명을 포함하는 전체경로 참조)
        val storageRef = storage.reference.child("profileImgs/$uid")

        // storage에 uri 업로드
        val uploadTask = storageRef.putFile(uri)
        Log.d("uploadTask",uploadTask.toString())
        uploadTask.addOnSuccessListener { taskSnapshot  ->
            // 업로드 성공
            Log.d("MyProfileEditFragment","프로필 이미지 스토리지 업로드 성공")

            // 성공하면 MyProfileFragment로 화면 이동
            (activity as SubActivity).hidingEdit("show")
            (activity as SubActivity).changeFragment()
        }.addOnFailureListener {
            // 파일 업로드 실패
            Log.d("MyProfileEditFragment","프로필 이미지 스토리지 업로드 실패")
            (activity as SubActivity).makeToast()
        }
    }
    private fun uploadImgToProfile(uid:String){
        val storage = Firebase.storage
        val storageRef = storage.reference.child("profileImgs/$uid")
        storageRef.downloadUrl.addOnSuccessListener { it ->
            Log.d("profileImg",it.toString())
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
                            Log.d("MyProfileEditFragment", "회원 찾음 : ${user!!.userSeq}")
                        } else {
                            Log.e("MyProfileEditFragment", "회원 못찾음")
                        }
                    } else {
                        Log.e("MyProfileEditFragment", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    }
                    callback(user)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyProfileEditFragment", "서버 통신 중 오류 발생", t)
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }
}