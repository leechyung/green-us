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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import kr.ac.kpu.green_us.adapter.GreenCardAdapter
import kr.ac.kpu.green_us.adapter.TabPopAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.databinding.FragmentTabOfPopularBinding
import retrofit2.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TabOfPopularFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TabOfPopularFragment : Fragment() {

    private var _binding: FragmentTabOfPopularBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTabOfPopularBinding.inflate(inflater,container,false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // binding 객체 해제
    }

    fun viewInit(){
        val viewManager = GridLayoutManager(requireContext(),2)
        val viewAdapter = TabPopAdapter()
        binding.recyclerviewPopularGreening.apply {
            layoutManager = viewManager
            adapter = viewAdapter
            setHasFixedSize(true)
        }

        viewAdapter.itemClickListener = object : TabPopAdapter.OnItemClickListener{
            override fun onItemClick(gSeq: Int) {
                val user = auth.currentUser
                if(user != null){
                    val email = user.email?:""
                    Log.d("TabOfNewFragment","$email")
                    val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                    apiService.findPSeqByGSeqAndUserEmail(email,gSeq).enqueue(object : Callback<Int> {
                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            if(!isAdded) return
                            if (response.isSuccessful) {
                                val pSeq = response.body()?:-1
                                Log.d("TabOfNewFragment", "pSeq : ${pSeq}")
                                val intent = if (pSeq >= 0) {
                                    Intent(requireActivity(), CertifyGreeningActivity::class.java).apply {
                                        putExtra("status", "in")
                                    }
                                } else {
                                    Intent(requireActivity(), GreeningDetailActivity::class.java).apply {
                                        putExtra("status", "notIn")
                                    }
                                }
                                intent.putExtra("gSeq", gSeq)
                                startActivity(intent)
                            } else {
                                Log.e("TabOfPopularFragment", "Participate 데이터 로딩 실패: ${response.code()}")
                                Toast.makeText(context, "다시 시도하세요", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<Int>, t: Throwable) {
                            if (!isAdded) return
                            Log.e("TabOfPopularFragment", "서버 통신 중 오류 발생", t)
                            Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, MainActivity::class.java)
                            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                    })
                }else{
                    Log.d("TabOfPopularFragment", "user null")
                    Toast.makeText(context, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    val intent = Intent(activity, LoginActivity::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
            }

        }

        loadGreeningDate(viewAdapter)
    }

    private fun loadGreeningDate(adapter: TabPopAdapter){
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        apiService.getPopGreening().enqueue(object : Callback<List<Greening>> {
            override fun onResponse(call: Call<List<Greening>>, response: Response<List<Greening>>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    val greeningList = response.body() ?: emptyList()
                    adapter.updateData(greeningList)
                } else {
                    Log.e("TabOfPopularFragment", "Greening 데이터 로딩 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Greening>>, t: Throwable) {
                if (!isAdded) return
                Log.e("TabOfPopularFragment", "서버 통신 중 오류 발생", t)
            }
        })
    }

}