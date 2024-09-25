package kr.ac.kpu.green_us

import android.app.DatePickerDialog
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
//import com.google.android.gms.cast.framework.media.ImagePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.data.CertifiedImgs
import kr.ac.kpu.green_us.data.GreeningImgs
import kr.ac.kpu.green_us.databinding.FragmentGreenOpenBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class GreenOpenFragment : Fragment() {
    lateinit var binding: FragmentGreenOpenBinding
    lateinit var auth: FirebaseAuth
    var start_date = ""
    var week = 0 //주 선택 값 저장할 변수
    var certiWay = ""
    var photo = ""
    var freq = 0
    var kind = 0
    private var uri: Uri? = null
    var gDeposit by Delegates.notNull<Int>()
    var gSeq by Delegates.notNull<Int>()
    var user :User? = null
    private lateinit var email :String

    //날짜 형식 정의
    //private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentGreenOpenBinding.inflate(inflater,container,false)

        auth = Firebase.auth


        // 날짜 입력 editText 달력으로만 받게 하기 위해 비활성화
        binding.startDateEt.setClickable(false);
        binding.startDateEt.setFocusable(false);

        // 사진명 입력 editText 정보 가져와서 받게 하기 위해 비활성화
        binding.uploadPictureEt.setClickable(false);
        binding.uploadPictureEt.setFocusable(false);

        // 개설하기 버튼
        binding.openGreenBtn.setOnClickListener {
            getUserByEmail { retrievedUser ->
                if (retrievedUser == null) {
                    Log.e("GreenOpenFragment", "user가 없거나 조회 실패")
                    Toast.makeText(context, "사용자 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    return@getUserByEmail
                }
                user = retrievedUser

                val gName = binding.nameEt.text.toString()
                val gInfo = binding.greenDetailEt.text.toString()
                val fee =  binding.depositEx.text.toString().trim()
                val img = binding.uploadPictureEt.text.toString()

                try {
                    if(fee.isEmpty()){
                        Toast.makeText(context, "예치금을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@getUserByEmail
                    }
                    if(fee.toInt()>100000){
                        Toast.makeText(context, "10만원 이하의 예치금을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return@getUserByEmail
                    }
                    gDeposit = fee.toInt()
                    print("gDeposit : $gDeposit")
                } catch (e: NumberFormatException) {
                    println("Not number: $fee")
                    return@getUserByEmail
                }
//            val gDeposit = binding.depositEx.text.toString().trim().toInt()
                val gNumber = week*freq
                if (start_date.isEmpty()){
                    Toast.makeText(context, "시작일을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@getUserByEmail
                }

                if(gName.isEmpty()){
                    Toast.makeText(context, "그리닝 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@getUserByEmail
                }

                val endDate = calculateEndDate(start_date, week)
                if (endDate == null){
                    //종료일 계산이 잘못되었을 때
                    Log.e("GreenOpenFragment", "종료일 계산 실패")
                    Toast.makeText(context, "시작일과 기간을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@getUserByEmail
                }
                if (certiWay.isEmpty()){
                    //인증방법이 입력되지 않았을 때
                    Log.e("GreenOpenFragment", "인증수단 입력 누락")
                    Toast.makeText(context, "인증수단을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@getUserByEmail
                }

                if (img.isEmpty()){
                    //이미지가 입력되지 않았을 때
                    Log.e("GreenOpenFragment", "이미지 입력 누락")
                    Toast.makeText(context, "그리닝이미지를 업로드해주세요.", Toast.LENGTH_SHORT).show()
                    return@getUserByEmail
                }

                if (gInfo.isEmpty()){
                    //이미지가 입력되지 않았을 때
                    Log.e("GreenOpenFragment", "그리닝 설명 누락")
                    Toast.makeText(context, "그리닝 설명을 작성해주세요.", Toast.LENGTH_SHORT).show()
                    return@getUserByEmail
                }

                //인증횟수
                if (gNumber <= 0){
                    //인증횟수 계산이 잘못되었을 때
                    Toast.makeText(context, "기간과 인증빈도를 선택해주세요.", Toast.LENGTH_SHORT).show()
                    Log.e("GreenOpenFragment", "인증횟수 계산 실패 : : $gNumber")
                    return@getUserByEmail
                }


                //그리닝 유형
                if (kind > 0){
                    //그리닝 유형을 입력했을 때
                    Log.d("GreenOpenFragment", "그리닝 유형 : ${kind}")
                }else{
                    //그리닝 유형을 미입력했을 때
                    Toast.makeText(context, "그리닝 유형를 선택해주세요.", Toast.LENGTH_SHORT).show()
                    Log.e("GreenOpenFragment", "그리닝 유형 미입력")
                    return@getUserByEmail
                }

                if (user != null){
                    Log.d("GreenOpenFragment", "userSeq: ${user!!.userSeq}")
                }else{
                    Log.e("GreenOpenFragment", "user가 없거나 조회 실패")
                    return@getUserByEmail
                }

                //데이터 전송
                val greening = Greening(
                    gSeq = 0,
                    gName = gName,
                    gStartDate = start_date,
                    gEndDate = endDate,
                    gCertiWay = certiWay,
                    gInfo = gInfo,
                    gMemberNum = 0,
                    gFreq = freq,
                    gDeposit = gDeposit,
                    gTotalCount = 0,
                    gNumber = gNumber,
                    gKind = kind,
                    user = user)
                val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                apiService.registerGreening(greening).enqueue(object : Callback<Greening> {
                    override fun onResponse(call: Call<Greening>, response: Response<Greening>) {
                        if (response.isSuccessful) {
                            val greening = response.body()
                            if(greening != null) {
                                gSeq = greening.gSeq

                                //gSeq를 이미지명으로 그리닝 이미지 저장
                                imageUpload(uri)

                            }
                            Log.d("GreenOpenFragment", "서버로 데이터 전송 성공")

                        } else {
                            Log.e("GreenOpenFragment", "서버로 데이터 전송 실패: ${response.code()}, ${response.errorBody()?.string()}")
                            Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(getActivity(),MainActivity::class.java))
                        }
                    }

                    override fun onFailure(call: Call<Greening>, t: Throwable) {
                        Log.e("GreenOpenFragment", "서버 통신 중 오류 발생", t)
                        Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(getActivity(),MainActivity::class.java))
                    }
                })

                val intent = Intent(getActivity(), MainActivity::class.java)
                intent.putExtra("key2_3", "open")
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

            }
        }

        // 달력 이미지 클릭
        binding.btnDate.setOnClickListener {
            showDatePicker()
        }

        //기간 선택
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            week = when (checkedId){
                R.id.week1 -> 1
                R.id.week2 -> 2
                R.id.week3 -> 3
                R.id.week4 -> 4
                R.id.week5 -> 5
                R.id.week6 -> 6
                R.id.week7 -> 7
                R.id.week8 -> 8
                else -> 0
            }
        }

        //기간 선택
        binding.radioGroup2.setOnCheckedChangeListener { _, checkedId ->
            certiWay = when (checkedId){
                R.id.only_camera -> "카메라"
                R.id.camera_gallery -> "카메라/갤러리"
                else -> ""
            }
        }

        //인증빈도 선택
        binding.radioGroup3.setOnCheckedChangeListener { _, checkedId ->
            freq = when (checkedId){
                R.id.frequency1 -> 1
                R.id.frequency2 -> 2
                R.id.frequency3 -> 3
                R.id.frequency4 -> 4
                R.id.frequency5 -> 5
                R.id.frequency6 -> 6
                R.id.frequency7 -> 7
                else -> 0
            }
        }

        //그리닝 유형
        binding.radioGroup4.setOnCheckedChangeListener { _, checkedId ->
            kind = when (checkedId){
                R.id.do_green -> 3
                R.id.buy_green -> 4
                else -> 0
            }
        }

        // 사진 업로드 이미지
        binding.btnUpload.setOnClickListener {
            // 변수 선언
            val galleryPermissionCheck = ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
            val cameraPermissionCheck = ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            )
            // 권한이 없는 경우
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
            if ((galleryPermissionCheck == PackageManager.PERMISSION_GRANTED)&&(cameraPermissionCheck == PackageManager.PERMISSION_GRANTED)) {
                com.github.dhaval2404.imagepicker.ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(150,75)
                    .createIntent { intent ->
                        intent.type = "image/*"
                        intent.action = ACTION_PICK
                        activityResult.launch(intent)
                    }
            }
        }

        return binding.root
    }
    // 이미지 저장
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val resultUri = it.data?.data
        if (resultUri != null) {
            uri = resultUri
            binding.uploadPictureEt.setText(uri?.getLastPathSegment())
        } else {
            Toast.makeText(requireContext(), "사진을 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun imageUpload(uri: Uri?) {
        if (uri != null) {
            val storage = Firebase.storage
            //현재 로그인 한 사용자 이메일 가져오기

            // storage에 저장할 파일명 선언 (그리닝시퀀스)
            val fileName = gSeq.toString()

            // storage 및 store에 업로드 작업 greeningImgs/에 gseq으로 이미지 저장
            /* store저장은 추후에 삭제할 것 같습니다*/
            // 스토리지에 저장후 url을 다운로드 받아 스토어에 저장
            storage.getReference("greeningImgs/").child(fileName).putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    Log.d("storageUploadSuccess", "인증사진 스토리지 업로드 성공")
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        val store = Firebase.firestore
                        val url = it.toString()
                        val data = GreeningImgs(url, gSeq.toString())
                        store.collection("greeningImgs").document()
                            .set(data).addOnSuccessListener {
                                Log.d("storeUploadSuccess", "그리닝사진 db 업로드 성공")
                            }.addOnFailureListener {
                                Log.d("storeUploadFail", "그리닝사진 db 업로드 실패")
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "사진 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    Log.d("storageUploadFail", "그리닝사진 스토리지 업로드 실패")
                }
        } else {
            Toast.makeText(requireContext(), "사진을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            Log.d("UriError", "uri null 에러")
        }
    }

    // 달력 다이얼로그
    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        var dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
            var year = y.toString()
            var month = (m + 1).toString().padStart(2,'0')
            var date = d.toString().padStart(2,'0')
            start_date = "$year-$month-$date"
            binding.startDateEt.setText(start_date)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        dpd.datePicker.minDate = (System.currentTimeMillis() - 1000)+24*60*60*1000
        dpd.show()
    }

    //종료일 계산
    private fun calculateEndDate(startDate: String, week: Int): String?{
        if(week==0) return null

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(startDate) ?: return null
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, 7*week)
        return sdf.format(cal.time)
    }

    private fun getUserByEmail(onUserRetrieved: (User?) -> Unit) {
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email.toString()

        //로그인중인 email에 해당하는 user 가져오는 코드
        if (currentEmail.isNotEmpty()) {
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getUserbyEmail(currentEmail).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        user = response.body()
                        onUserRetrieved(user) // Call the callback with the user
                        if (user != null) {
                            Log.d("GreeningOpenFragment", "회원 찾음 : ${user!!.userSeq}")
                        } else {
                            Log.e("GreeningOpenFragment", "회원 못찾음")
                        }
                    } else {
                        Log.e("GreeningOpenFragment", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                        onUserRetrieved(null) // Indicate failure
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("GreeningOpenFragment", "서버 통신 중 오류 발생", t)
                    onUserRetrieved(null) // Indicate failure
                }
            })
        } else {
            //로그인된 Email을 못가져온 경우
            //로그아웃 시키고 처음 화면으로 가도록
            onUserRetrieved(null)
            return
        }
    }

}