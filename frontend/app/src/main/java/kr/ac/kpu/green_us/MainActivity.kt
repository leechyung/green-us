package kr.ac.kpu.green_us

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.initialize
import kr.ac.kpu.green_us.adapter.GreenAdapter
import kr.ac.kpu.green_us.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    val manager = supportFragmentManager
    private var lastBackPressedTime = 0L
    private val BackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (lastBackPressedTime > System.currentTimeMillis() - 1500 ){
                this@MainActivity.finishAffinity()
            }else{
                HomeFragment().changeFragment()
                binding.bottomNavigationView.selectedItemId = R.id.icon_home
                Toast.makeText(this@MainActivity,"앱을 종료하려면 뒤로 가기를 한 번 더 눌러주세요",Toast.LENGTH_SHORT).show()
                lastBackPressedTime = System.currentTimeMillis()
            }
        }
    }
    private val multiplePermissionsCode = 100
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_MEDIA_IMAGES,
        android.Manifest.permission.ACTIVITY_RECOGNITION)
//    private val BackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
//        override fun handleOnBackPressed() {
//            return
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

        // Initialize Firebase Auth
        auth = Firebase.auth
        setContentView(binding.root)
        // 휴대폰 이전 버튼
        onBackPressedDispatcher.addCallback(this, BackPressedCallback)
        checkPermissions()

        showInit() //최초 프래그먼트
        initBottomNavi() //아이템 선택시
    }
    //활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            /* 현재로그인한 사용자 이메일 가져오는 코드
            val user = Firebase.auth.currentUser
            user?.let { val email = it.email}
            Log.d("email", user?.email.toString())
ㄱ             */
            reload()
        }
    }
    private fun reload() {
    }

    //퍼미션 체크 및 권한 요청 함수
    private fun checkPermissions() {
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }

    private fun initBottomNavi(){
        binding.bottomNavigationView.itemIconTintList = null

        // 이전 버튼을 눌러서 main으로 나왔을 경우
        val value1 = intent.getStringExtra("key2")
        if(value1 == "mygreen"){
            MyGreenFragment().changeFragment()
            binding.bottomNavigationView.selectedItemId = R.id.icon_mygreen
        }

        val value2 = intent.getStringExtra("key3")
        if(value2 == "mypage"){
            MypageFragment().changeFragment()
            binding.bottomNavigationView.selectedItemId = R.id.icon_mypage
        }

        val value3 = intent.getStringExtra("key2_3")
        if(value3 == "open"){
            changeFragmentByOpening()
            binding.bottomNavigationView.selectedItemId = R.id.icon_mygreen
        }

        val value4 = intent.getStringExtra("key1")
        if(value4 == "home"){
            HomeFragment().changeFragment()
            binding.bottomNavigationView.selectedItemId = R.id.icon_home
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.icon_home -> {
                    HomeFragment().changeFragment()
                }
                R.id.icon_mygreen -> {
                    MyGreenFragment().changeFragment()
                }
                R.id.icon_mypage -> {
                    MypageFragment().changeFragment()
                }
            }
            return@setOnItemSelectedListener true
        }
        binding.bottomNavigationView.setOnItemReselectedListener {  } // 재클릭 재생성 방지
    }
    private fun Fragment.changeFragment(){
        manager.beginTransaction().replace(R.id.main_frame,this).commit()
    }
    private fun changeFragmentByOpening(){
        val bundle = Bundle()
        bundle.putString("from", "open")
        val fragment = MyGreenFragment()
        fragment.arguments = bundle
        manager.beginTransaction().replace(R.id.main_frame,fragment).commit()
    }
    private fun showInit(){
            val transaction = manager.beginTransaction().add(R.id.main_frame,HomeFragment())
            transaction.commit()
    }
}
