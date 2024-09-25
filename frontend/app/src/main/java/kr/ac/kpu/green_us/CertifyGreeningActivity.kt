package kr.ac.kpu.green_us

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kr.ac.kpu.green_us.adapter.CertifiedRepresentAdapter
import kr.ac.kpu.green_us.adapter.StampAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Certify
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Prize
import kr.ac.kpu.green_us.data.CertifiedImgs
import kr.ac.kpu.green_us.databinding.ActivityCertifyGreeningBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CertifyGreeningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCertifyGreeningBinding
    lateinit var uri: Uri
    private lateinit var auth: FirebaseAuth
    val today = LocalDate.now()
    private val representImgList = mutableListOf<String>()
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1000
    private var gSeq: Int = -1
    var currentCertifyNum = 0
    private lateinit var receivedStatus:String
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                imageUpload(fileUri)

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Log.d("ImagePicker","이미지 선택을 취소했습니다.")
            }
        }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCertifyGreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 참여상태로 넘어왔다면 버튼 비활성화 및 관련 뷰 가리기
        receivedStatus = intent.getStringExtra("status").toString()
        if (receivedStatus == "in"){
            binding.button.isEnabled = false
            binding.button.setAlpha(0.5f)
            binding.stampsLayout.isGone = true
            binding.textView9.isVisible = false
            binding.currentCertifiNum.isVisible = false
            binding.totalCertifiNum.isVisible = false
            binding.dash.isVisible = false
        }else{
            binding.button.isEnabled = true
            binding.button.setAlpha(1f)
            binding.stampsLayout.isGone = false
            binding.textView9.isVisible = true
            binding.currentCertifiNum.isVisible = true
            binding.totalCertifiNum.isVisible = true
            binding.dash.isVisible = true
        }

        // auth 인스턴스 초기화
        auth = Firebase.auth
        val userEmail = auth.currentUser?.email.toString()
        loadCertifyData(userEmail){

        gSeq = intent.getIntExtra("gSeq", -1)
        if(gSeq <= -1){
            //gSeq조회 실패한 경우 예외처리 -> 로그아웃하고 초기화면으로
            Log.e("CertifyGreeningActivity","gSeq 실패")
        }else{
            viewInit()
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getGreeningById(gSeq).enqueue(object : Callback<Greening> {
                override fun onResponse(call: Call<Greening>, response: Response<Greening>) {
                    if (response.isSuccessful) {
                        val greening = response.body() ?: null
                        if(greening != null){
                            var greenWeek = 0
                            if(greening.gFreq != 0 && greening.gNumber != 0 && greening.gFreq != null && greening.gNumber != null) {
                                greenWeek = (greening.gNumber)/(greening.gFreq)
                            }

                            // greening.gStartDate는 "yyyy-MM-dd" 형식의 문자열
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val startDate = LocalDate.parse(greening.gStartDate, formatter)

                            // 년/월/일 추출하여 변수에 저장
                            val year = startDate.year
                            val month = startDate.monthValue
                            val day = startDate.dayOfMonth

                            // 이미지 로드
                            val gseq = gSeq.toString()
                            val imgName = gseq
                            val storage = Firebase.storage
                            if ((greening.gKind == 1).or(greening.gKind == 2)){
                                val ref = storage.getReference("officialGreeningImgs/").child(imgName)
                                ref.downloadUrl.addOnSuccessListener {
                                        uri -> Glide.with(this@CertifyGreeningActivity).load(uri).into(binding.imgGreening)
                                }
                            }else{
                                val ref = storage.getReference("greeningImgs/").child(imgName)
                                ref.downloadUrl.addOnSuccessListener {
                                        uri -> Glide.with(this@CertifyGreeningActivity).load(uri).into(binding.imgGreening)
                                }

                            }
                            if(today.isEqual(startDate) || today.isAfter(startDate)){
                                binding.button.isEnabled = true
                            }

                            binding.subject.text = greening.gName ?: ""
                            binding.greeningTitle.text = greening.gName ?: ""
                            binding.tagTerm.text = "${greenWeek}주"
                            binding.tagFreq.text = "주${greening.gFreq}회"
                            val totalNum = (greenWeek * greening.gFreq!!)
                            binding.totalCertifiNum.text = totalNum.toString()
                            binding.currentCertifiNum.text = currentCertifyNum.toString()
                            binding.tagCertifi.text = greening.gCertiWay
                            binding.button.setOnClickListener {
                                checkMethod(greening.gCertiWay.toString())
                            }
                            binding.tvStartDate.text = "${month}월 ${day}일부터 시작"
                            binding.tvParticipateFee.text = "${greening.gDeposit}"
                            binding.textView10.text = when(greening.gKind){
                                1,3 -> "활동형" //1->공식 3->회원
                                2,4 -> "구매형" //2->공식 4->회원
                                else -> ""
                            }
                        }

                    } else {
                        Log.e("CertifyGreeningActivity", "Greening 데이터 로딩 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Greening>, t: Throwable) {
                    Log.e("CertifyGreeningActivity", "서버 통신 중 오류 발생", t)
                }
            })
        }}

        // 이전 버튼 클릭
        binding.btnEsc.setOnClickListener {
            this.finish()
        }
        // 전체보기 클릭
        binding.btnMoreLayout.setOnClickListener {
            val intent = Intent(this,ViewAllCertifiedImgActivity::class.java)
            intent.putExtra("gSeq", gSeq)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkMethod(method:String){
        Log.d("greeningMethod",method)
        val camera : String = "카메라"
        val both : String = "카메라/갤러리"
        when(method){
            camera -> onlyCamera()
            both -> both()
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun both(){
        // permission checker 변수 선언
        val galleryPermissionCheck = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_MEDIA_IMAGES
        )
        val cameraPermissionCheck = ContextCompat.checkSelfPermission(
            this@CertifyGreeningActivity,
            android.Manifest.permission.CAMERA
        )
        // 권한이 없을 경우
        if (galleryPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 갤러리
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                1000
            )
        }
        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 카메라
            ActivityCompat.requestPermissions(
                this,
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
    private fun onlyCamera(){
        // 변수 선언
        val cameraPermissionCheck = ContextCompat.checkSelfPermission(
            this@CertifyGreeningActivity,
            android.Manifest.permission.CAMERA
        )
        val cameraPermissionCheckForLower = ContextCompat.checkSelfPermission(
            this@CertifyGreeningActivity,android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        // 권한이 없는 경우
        if (cameraPermissionCheckForLower!=PackageManager.PERMISSION_GRANTED){
            requestWriteExternalStoragePermission()
        }
        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                1000
            )
        }
        // 권한이 있는 경우
        if (cameraPermissionCheck == PackageManager.PERMISSION_GRANTED) {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(150,75)
                .cameraOnly()
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }

    private fun viewInit(){

        val layoutAdapter = CertifiedRepresentAdapter(representImgList)
        layoutAdapter.notifyDataSetChanged()

        val storage = FirebaseStorage.getInstance()
        //certificationImgs경로의 사진들 참조함
        val storageRef = storage.reference.child("certificationImgs/${gSeq}/")

        //3개의 사진을 가져와서 각각의 url representImgList에 저장함
        storageRef.list(3).addOnSuccessListener { listResult ->
            if (listResult.items.size == 0) {
                binding.representImgArea.isGone = true
                binding.btnMoreLayout.isGone = true
            }else{
                for (img in listResult.items){
                    img.downloadUrl.addOnSuccessListener { uri ->
                        representImgList.add(uri.toString())
//                        Log.d("representImgList",representImgList.toString())
                    }.addOnSuccessListener {// url 가져오기 성공하면 화면에 뷰 어댑팅
                        // 이미지 3개 불러와서 인증사진 대표 3개에 어댑팅
                        binding.representImgArea.apply {
                            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = layoutAdapter
                            setHasFixedSize(true) //데이터가 나가고 들어올 때 아이템들의 자리만 다시 잡음 -> 비용작업 줄이기
                        }
                    }
                }
            }
        }
        // 대표 인증사진 3개 클릭 리스너
        // 받은 url값을 담아 디테일 액티비티로 보냄
        layoutAdapter.itemClickListener = object :CertifiedRepresentAdapter.OnItemClickListener{
            override fun onItemClick(url:String) {
                val intent = Intent(applicationContext,CertificationImgDetailActivity::class.java)
                intent.putExtra("imgUrl",url)
                intent.putExtra("gSeq", gSeq)
                startActivity(intent)
            }
        }
    }
    private fun imageUpload(uri: Uri) {
        Log.d("이미지업로드 uri",uri.toString())
        if (uri != null) {

            // 현재 로그인 한 사용자 이메일 가져오기
            val user = Firebase.auth.currentUser
            val userEmail = user?.email.toString()
            Log.d("currentEmail", userEmail)

            val storage = Firebase.storage

            // 날짜를 파일 이름으로 사용하기 위해 포맷팅
            val date = getFormattedDate()

            // registerCertify 함수가 비동기적으로 실행되므로 콜백을 사용하여 처리
            registerCertify(userEmail, date) { certifySeq ->
                if (certifySeq != null) {
                    // 파일 이름을 인증 번호로 설정
                    val fileName = "${certifySeq}"

                    // storage 및 store에 업로드 작업
                    storage.getReference("certificationImgs/${gSeq}").child(fileName).putFile(uri)
                        .addOnSuccessListener { taskSnapshot ->
                            Log.d("storageUploadSuccess", "인증사진 스토리지 업로드 성공")
                            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                                val store = Firebase.firestore
                                val url = downloadUri.toString()
                                val data = CertifiedImgs(url, userEmail, certifySeq)

                                store.collection("certificationImgs").document()
                                    .set(data)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "사진이 업로드 되었습니다.", Toast.LENGTH_SHORT).show()
                                        Log.d("storeUploadSuccess", "인증사진 db 업로드 성공")
                                    }
                                    .addOnFailureListener {
                                        Log.d("storeUploadFail", "인증사진 db 업로드 실패")
                                    }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "사진 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("storageUploadFail", "인증사진 스토리지 업로드 실패")
                        }
                } else {
                    //Toast.makeText(this, "인증 번호를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("CertifyError", "certifySeq null 에러")
                }
            }
        } else {
            Toast.makeText(this, "사진을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            Log.d("UriError", "uri null 에러")
        }
    }
    private fun getFormattedDate() : String {
        // 현재 시간
        val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        // ISO 8601 형식으로 변환
        val isoFormatter = DateTimeFormatter.ISO_DATE_TIME

        //현재시간에 적용
        return now.format(isoFormatter)
    }
    private fun requestWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
    }

    private fun registerCertify(userEmail: String, date: String, callback: (Int?) -> Unit) {
        val gSeq: Int = intent.getIntExtra("gSeq", -1)
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        var greening : Greening? = null
        apiService.getGreeningById(gSeq).enqueue(object : Callback<Greening> {
            override fun onResponse(call: Call<Greening>, response: Response<Greening>) {
                if (response.isSuccessful) {
                    greening = response.body()
                    if(greening != null && greening!!.gStartDate != null) {
                        val gStartDate = greening!!.gStartDate

                        // 현재 날짜와 주차 계산을 위한 날짜 포맷터
                        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        try {
                            val today = Date()
                            val startDate = dateFormatter.parse(gStartDate)

                            if (startDate != null) {
                                // 주차 계산
                                val diffInMillis = today.time - startDate.time
                                val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
                                val currentWeek = (diffInDays / 7) + 1

                                // 오늘이 포함된 주차의 시작과 끝 날짜 계산
                                val currentWeekStart = Calendar.getInstance().apply {
                                    time = startDate
                                    add(Calendar.DAY_OF_YEAR, (currentWeek - 1) * 7)
                                }.time

                                val currentWeekEnd = Calendar.getInstance().apply {
                                    time = currentWeekStart
                                    add(Calendar.DAY_OF_YEAR, 6)
                                }.time

                                apiService.getCertifyByUserEmailAndGSeq(userEmail, gSeq)
                                    .enqueue(object : Callback<List<Certify>> {
                                        override fun onResponse(call: Call<List<Certify>>, response: Response<List<Certify>>) {
                                            if (response.isSuccessful) {
                                                val participateList = response.body()

                                                // 현재 주차에 해당하는 certifyDate 필터링
                                                val currentWeekCertifies = participateList?.filter { certify ->
                                                        val certifyDate = dateFormatter.parse(certify.certifyDate)
                                                        certifyDate != null && certifyDate >= currentWeekStart && certifyDate <= currentWeekEnd
                                                }

                                                // 오늘 날짜의 인증 필터링
                                                val todayCertifies = currentWeekCertifies?.filter { certify ->
                                                        val certifyDate = dateFormatter.parse(certify.certifyDate)
                                                        certifyDate != null && certifyDate == dateFormatter.parse(date)
                                                }

                                                // 이번 주차에 해당하는 인증이 이미 3개 이상인 경우
                                                if (currentWeekCertifies?.size ?: 0 >= greening!!.gFreq ?:0) {
                                                    if(participateList!!.size >= greening!!.gNumber!!){
                                                        Toast.makeText(this@CertifyGreeningActivity, "이미 전체 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                                        Log.d("CertifyGreeningActivity", "parciaipteList Size : ${participateList.size}, greening Number : ${greening!!.gNumber!!}")
                                                        registerPrizeData(userEmail, greening)
                                                    }else{
                                                        Toast.makeText(this@CertifyGreeningActivity, "이번 주 인증을 이미 완료했습니다.", Toast.LENGTH_SHORT).show()
                                                    }
                                                    callback(null)
                                                    return  // 작업을 멈춤
                                                }

                                                // 오늘 이미 인증이 있는 경우
                                                if (todayCertifies != null && todayCertifies.isNotEmpty()) {
                                                    if(participateList!!.size >= greening!!.gNumber!!){
                                                        Toast.makeText(this@CertifyGreeningActivity, "이미 전체 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                                        Log.d("CertifyGreeningActivity", "parciaipteList Size : ${participateList.size}, greening Number : ${greening!!.gNumber!!}")
                                                        registerPrizeData(userEmail, greening)
                                                    }else{
                                                        Toast.makeText(this@CertifyGreeningActivity,
                                                            "오늘 이미 인증을 완료했습니다.", Toast.LENGTH_SHORT).show()
                                                    }
                                                    callback(null)
                                                    return  // 전체 작업을 종료합니다.
                                                }

                                                if(participateList!!.size >= greening!!.gNumber!!){
                                                    Toast.makeText(this@CertifyGreeningActivity, "이미 전체 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                                    Log.d("CertifyGreeningActivity", "parciaipteList Size : ${participateList.size}, greening Number : ${greening!!.gNumber!!}")
                                                    registerPrizeData(userEmail, greening)
                                                }

                                                apiService.registerCertify(userEmail, gSeq, date).enqueue(object : Callback<Certify> {
                                                        override fun onResponse(call: Call<Certify>, response: Response<Certify>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                val certify = response.body()
                                                                val certifySeq = certify?.certifySeq
                                                                Log.d("CertifyGreeningActivity", "데이터 저장 성공 : $certifySeq")
                                                                callback(certifySeq)
                                                            } else {
                                                                when (response.code()) {
                                                                    409 -> {
                                                                        Toast.makeText(this@CertifyGreeningActivity, "이미 전체 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                                                        registerPrizeData(userEmail, greening)}
                                                                }
                                                                Log.e("CertifyGreeningActivity", "데이터 저장 실패: ${response.code()}")
                                                                callback(null)
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: Call<Certify>,
                                                            t: Throwable
                                                        ) {
                                                            Log.e("CertifyGreeningActivity", "서버 통신 중 오류 발생", t)
                                                            callback(null)
                                                        }
                                                    })

                                            } else {
                                                Log.e("CertifyGreeningActivity", "데이터 저장 실패: ${response.code()}")
                                                callback(null)
                                            }
                                        }

                                        override fun onFailure(call: Call<List<Certify>>, t: Throwable) {
                                            Log.e("CertifyGreeningActivity", "서버 통신 중 오류 발생", t)
                                            callback(null)
                                        }
                                    })
                            }
                        }catch (e: ParseException){
                            Log.e("CertifyGreeningActivity","날짜 파싱 오류",e)
                            callback(null)
                        }
                    }
                } else {
                    when (response.code()) {
                        409 -> {
                            Toast.makeText(this@CertifyGreeningActivity, "잘못된 요청입니다.", Toast.LENGTH_SHORT).show()
                            registerPrizeData(userEmail, greening)
                        }
                        else -> Toast.makeText(this@CertifyGreeningActivity, "데이터 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("CertifyGreeningActivity", "데이터 저장 실패: ${response.code()}")
                    callback(null)
                }
            }
            override fun onFailure(call: Call<Greening>, t: Throwable) {
                Log.e("CertifyGreeningActivity", "서버 통신 중 오류 발생", t)
                callback(null)
            }
        })
    }

    private fun loadCertifyData(userEmail:String, callback: () -> Unit){
        val gSeq: Int = intent.getIntExtra("gSeq", -1)
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        apiService.getCertifyByUserEmailAndGSeq(userEmail, gSeq).enqueue(object : Callback<List<Certify>> {
            override fun onResponse(call: Call<List<Certify>>, response: Response<List<Certify>>) {
                if (response.isSuccessful) {
                    val certifyList = response.body()
                    if(certifyList != null) {
                        //여기서 스탬프로 데이터 넘기면 됩니다! List 문제없이 넘어오게 수정했습니다!
                        Log.d("certifyList",certifyList.toString())
                        Log.d("CertifyGreeningActivity", "인증 정보 불러오기 성공 ")
                        currentCertifyNum = certifyList.size
                        // 스탬프영역
                        binding.stampsLayout.apply {
                            adapter = StampAdapter(certifyList)
                            layoutManager = GridLayoutManager(context,3)
                            setHasFixedSize(true)
                        }
                    }
                    callback()
                } else {
                    Log.e("CertifyGreeningActivity", "인증 정보 불러오기 실패: ${response.code()}")
                    callback()
                }
            }

            override fun onFailure(call: Call<List<Certify>>, t: Throwable) {
                Log.e("CertifyGreeningActivity", "서버 통신 중 오류 발생", t)
                callback()
            }
        })
    }

    private fun registerPrizeData(userEmail:String, greening: Greening?){
        val gSeq: Int = intent.getIntExtra("gSeq", -1)
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        val money = greening!!.gDeposit ?: 0
        apiService.registerPrize(userEmail, gSeq, money).enqueue(object : Callback<Prize> {
            override fun onResponse(call: Call<Prize>, response: Response<Prize>) {
                if (response.isSuccessful) {
                    val prize = response.body()
                    Log.d("CertifyGreeningActivity", "상금 지급 완료 : ${prize!!.prizeSeq}")
                } else {
                    Log.e("CertifyGreeningActivity", "상금 저장 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Prize>, t: Throwable) {
                Log.e("CertifyGreeningActivity", "서버 통신 중 오류 발생", t)
            }
        })
    }

    //DB에서 넘어온 CertifyDate를 LocalDateTime으로 변환
    fun convertStringToLocalDateTime(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
    }

    //DB에서 넘어온 CertifyDate를 yyyy-MM-dd형식 문자열로 변환
    fun formatCertifyDate(dateString: String): String {
        // 날짜 문자열을 LocalDateTime으로 파싱
        val localDateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)

        // yyyy-MM-dd 형식으로 포맷팅
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return localDateTime.format(formatter)
    }
}


