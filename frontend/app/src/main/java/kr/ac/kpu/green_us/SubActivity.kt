package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import kr.ac.kpu.green_us.databinding.ActivitySubBinding


// 이전버튼, 타이틀바 중복 화면 프래그먼트를 위한 액티비티
class SubActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubBinding
    val manager = supportFragmentManager
    var gSeq = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전버튼
        binding.btnEsc.setOnClickListener {
            if(binding.subject.text == "프로필관리" && binding.edit.visibility == View.GONE){
//                binding.edit.visibility = View.VISIBLE
//                manager?.beginTransaction()?.remove(MyProfileEditFragment())?.commit()
                MyProfileFragment().changeFragment()
                hidingEdit("show")
            }
            else if (binding.subject.text =="프로필관리" && binding.edit.isVisible){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("key3","mypage")
                startActivity(intent)
                this.finishAffinity()
            }
            else{
                finish()
            }
        }
        // 휴대폰 이전 버튼
        onBackPressedDispatcher.addCallback(this, profileBackPressedCallback)

        // 0 진행중 그리닝 전체보기
        val value0 = intent.getStringExtra("0")
        if(value0 == "green_ing_more"){
            binding.subject.text = "진행중인 그리닝"
            MyGreenIngMoreFragment().changeFragment()
        }

        // 1 완료 그리닝 전체보기
        val value1 = intent.getStringExtra("1")
        if(value1 == "green_end_more"){
            binding.subject.setText("완료한 그리닝")
            MyGreenEndMoreFragment().changeFragment()
        }

        // 2 개설하기
        val value2 = intent.getStringExtra("2")
        if(value2 == "open_green"){
            binding.subject.setText("개설하기")
            GreenOpenFragment().changeFragment()
        }

        // 3 내리뷰 작성
        val value3 = intent.getStringExtra("3")
        if(value3 == "my_review_write"){
            val gSeqValue = intent.getIntExtra("gSeq", -1)
            gSeq = gSeqValue
            binding.subject.text = "리뷰 작성"
            MyReviewWriteFragment().changeFragment()
        }

        // 4 프로필관리
        val value4 = intent.getStringExtra("4")
        if(value4 == "my_profile"){
            binding.subject.text = "프로필관리"
            binding.edit.visibility = View.VISIBLE
            binding.edit.setOnClickListener {
//                binding.edit.visibility = View.GONE
                MyProfileEditFragment().changeFragment()
                hidingEdit("hide")
            }
            MyProfileFragment().changeFragment()
        }

        // 5 공지사항

        // 6 faq

        // 7 포인트

        // 8 만보기
        val value8 = intent.getStringExtra("8")
        if(value8 == "pedometer"){
            binding.subject.text = "만보기"
            PedometerFragment().changeFragment()
        }

        // 9 내주변


        // 11 구매형 그리닝 전체보기
        val value11 = intent.getStringExtra("11")
        if(value11 == "buy_green"){
            binding.subject.text = "구매형 그리닝"
            MyGreenBuyMoreFragment().changeFragment()
        }

        // 12 활동형 그리닝 전체보기
        val value12 = intent.getStringExtra("12")
        if(value12 == "do_green"){
            binding.subject.text = "활동형 그리닝"
            MyGreenDoMoreFragment().changeFragment()
        }

        // 13 히어로 섹션 리스트
        val value13 = intent.getStringExtra("13")
        if(value13 == "hero_list"){
            binding.subject.text = "정보"
            HeroSectionListFragment().changeFragment()
        }

    }
    private val profileBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 프로필 편집화면일 경우
            if(binding.subject.text == "프로필관리" && binding.edit.visibility == View.GONE){
                MyProfileFragment().changeFragment()
                hidingEdit("show")
            }
            // 프로필 화면일 경우
            else if (binding.subject.text =="프로필관리" && binding.edit.isVisible){
                val intent = Intent(this@SubActivity,MainActivity::class.java)
                intent.putExtra("key3","mypage")
                startActivity(intent)
                this@SubActivity.finishAffinity()
            }
            // 다른경우
            else{
               finish()
            }
        }
    }

    fun Fragment.changeFragment(){
        manager.beginTransaction().replace(R.id.sub_frame,this).commit()
    }

    fun changeFragment(){
//        manager.beginTransaction().remove(MyProfileEditFragment()).add(R.id.sub_frame,MyProfileFragment()).commit()
        manager.beginTransaction().replace(R.id.sub_frame,MyProfileFragment()).commit()
    }


    fun changeVisibility(){
        binding.edit.visibility = View.VISIBLE
    }

    fun gSeqCheck(): Int{
        return gSeq
    }
    fun makeToast(){
        Toast.makeText(this,"이미지 업로드 실패",Toast.LENGTH_SHORT).show()
    }
    fun hidingEdit(enable:String){
        when(enable){
            "show" -> binding.edit.isVisible = true
            "hide" -> binding.edit.isVisible = false
        }
    }

}