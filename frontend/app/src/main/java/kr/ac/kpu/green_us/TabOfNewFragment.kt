package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kr.ac.kpu.green_us.adapter.GreenCardAdapter
import kr.ac.kpu.green_us.adapter.TabNewAdapter
import kr.ac.kpu.green_us.adapter.TabPopAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.databinding.FragmentTabOfNewBinding
import kr.ac.kpu.green_us.databinding.FragmentTabOfPopularBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TabOfNewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TabOfNewFragment : Fragment() {

    private var _binding: FragmentTabOfNewBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTabOfNewBinding.inflate(inflater,container,false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewInit()
    }
    fun viewInit(){
        val viewManager = GridLayoutManager(requireContext(),2)
        val viewAdapter = TabNewAdapter()
        binding.recyclerviewNewGreening.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewAdapter.itemClickListener = object : TabNewAdapter.OnItemClickListener{
            override fun onItemClick(gSeq:Int) {
                val user = Firebase.auth.currentUser
                if(user != null){
                    val email = user.email?:""
                    Log.d("TabOfNewFragment","$email")
                    val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                    apiService.findPSeqByGSeqAndUserEmail(email,gSeq).enqueue(object : Callback<Int> {
                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            if (response.isSuccessful) {
                                val pSeq = response.body()?:-1
                                Log.d("TabOfNewFragment", "pSeq : ${pSeq}")
                                if (pSeq >= 0) {
                                    Log.d("TabOfNewFragment", "in")
                                    val intent = Intent(requireActivity(),CertifyGreeningActivity::class.java)
                                    intent.putExtra("status","in")
                                    intent.putExtra("gSeq", gSeq)
                                    startActivity(intent)
                                }else{
                                    Log.d("TabOfNewFragment", "notIn")
                                    val intent = Intent(requireActivity(),GreeningDetailActivity::class.java)
                                    intent.putExtra("status","notIn")
                                    intent.putExtra("gSeq", gSeq)
                                    startActivity(intent)
                                }
                            } else {
                                Log.e("TabOfNewFragment", "Participate 데이터 로딩 실패: ${response.code()}")
                                Toast.makeText(context, "다시 시도하세요", Toast.LENGTH_SHORT).show()
                                //오류 처리
                            }
                        }
                        override fun onFailure(call: Call<Int>, t: Throwable) {
                            Log.e("TabOfNewFragment", "서버 통신 중 오류 발생", t)
                            Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                            val intent = Intent(getActivity(), MainActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                            startActivity(intent)
                        }
                    })

                }else{
                    Log.d("TabOfNewFragment", "user null")
                    Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    val intent = Intent(getActivity(), LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP )
                    startActivity(intent)
                }
            }

        }
        loadGreeningDate(viewAdapter)
    }

    private fun calculateStatus(gSeq: Int, callback: (String)-> Unit){
        val user = Firebase.auth.currentUser
        if(user != null){
            val email = user.email?:""
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.findPSeqByGSeqAndUserEmail(email,gSeq).enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    if (response.isSuccessful) {
                        val pSeq = response.body()?:-1
                        Log.d("TabOfNewAdapter", "pSeq : ${pSeq}")
                        callback(if (pSeq >= 0) "in" else "notIn")
                    } else {
                        Log.e("TabOfNewAdapter", "Participate 데이터 로딩 실패: ${response.code()}")
                        callback("notIn")
                    }
                }
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.e("TabOfNewAdapter", "서버 통신 중 오류 발생", t)
                    callback("notIn")
                }
            })

        }else{
            callback("notIn")
        }

    }

    private fun loadGreeningDate(adapter: TabNewAdapter){
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        val today = LocalDate.now()
        apiService.getNewGreening().enqueue(object : Callback<List<Greening>> {
            override fun onResponse(call: Call<List<Greening>>, response: Response<List<Greening>>) {
                if (response.isSuccessful) {
                    val greeningList = response.body() ?: emptyList()
                    adapter.updateData(greeningList)
                    // 데이터를 어댑터에 설정
                } else {
                    Log.e("TabOfNewFragment", "Greening 데이터 로딩 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Greening>>, t: Throwable) {
                Log.e("TabOfNewFragment", "서버 통신 중 오류 발생", t)
            }
        })
    }


}