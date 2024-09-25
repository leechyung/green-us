package kr.ac.kpu.green_us

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Users
import kr.ac.kpu.green_us.databinding.FragmentJoinCompltBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinCompltFragment : Fragment() {
    private var email = ""
    private var pw = ""
    private var phoneNumber = ""
    private var address = ""
    private var address_detail = ""
    private lateinit var callback: OnBackPressedCallback
    private var lastBackPressedTime = 0L
    private var name = ""
    private var _binding: FragmentJoinCompltBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentJoinCompltBinding.inflate(inflater, container, false)
        // 이전 프래그먼트로부터 온 bundle 데이터 받기
//        email = arguments?.getString("email").toString()
//        pw = arguments?.getString("pw").toString()
//        phoneNumber = arguments?.getString("phone").toString()
        name = arguments?.getString("name").toString()
//        address = arguments?.getString("address").toString()
//        address_detail = arguments?.getString("address_detail").toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 회원 이름 보이기
        binding.tvUserId.text = name
        // 버튼 클릭시
        binding.btnGotoLogin.setOnClickListener {
            // 로그인 화면으로 이동
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (lastBackPressedTime > System.currentTimeMillis() - 1500 ){
                    (activity as JoinActivity).finish()
                }else{
                    Toast.makeText(activity,"앱을 종료하려면 뒤로 가기를 한 번 더 눌러주세요",Toast.LENGTH_SHORT).show()
                    lastBackPressedTime = System.currentTimeMillis()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}