package kr.ac.kpu.green_us

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kr.ac.kpu.green_us.adapter.MyGreenEndMoreAdapter
import kr.ac.kpu.green_us.adapter.MyGreenIngAdapter
import kr.ac.kpu.green_us.adapter.MyGreenIngMoreAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.FragmentMyGreenIngMoreBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyGreenIngMoreFragment : Fragment() {
    lateinit var binding: FragmentMyGreenIngMoreBinding
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    lateinit var auth: FirebaseAuth
    var user : User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyGreenIngMoreBinding.inflate(inflater, container, false)

        val today = LocalDate.now()
        auth = FirebaseAuth.getInstance()

        // 데이터 가져오기
        getUserByEmail { user ->
            if (user != null) {
                val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                apiService.findGreeningByUserSeq(user.userSeq).enqueue(object :
                    Callback<List<Greening>> {
                    override fun onResponse(call: Call<List<Greening>>, response: Response<List<Greening>>) {
                        if (response.isSuccessful) {
                            val greeningList = response.body() ?: emptyList()
                            val selectedGreeningList = greeningList.filter { greening ->
                                try {
                                    val startDate = LocalDate.parse(greening.gStartDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    val endDate = LocalDate.parse(greening.gEndDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    (today.isEqual(startDate) || (today.isAfter(startDate) && today.isBefore(endDate)))
                                } catch (e: Exception) {
                                    false
                                }
                            }
                            Log.d("MyGreenIngMoreFragment", "Greening Size : ${greeningList.size} -> ${selectedGreeningList.size}")
                            setupRecyclerView(selectedGreeningList)
                        } else {
                            Log.e("MyGreenIngMoreFragment", "Greening 데이터 로딩 실패: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<List<Greening>>, t: Throwable) {
                        Log.e("MyGreenIngMoreFragment", "서버 통신 중 오류 발생", t)
                    }
                })
            } else {
                //예외처리
            }
        }

        return binding.root
    }

    private fun getUserByEmail(callback: (User?) -> Unit) {
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email.toString()
        Log.d("currentEmail", currentEmail)

        if (currentEmail != null) {
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getUserbyEmail(currentEmail).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        user = response.body()
                        if (user != null) {
                            Log.d("MyGreenIngMoreFragment", "회원 찾음 : ${user!!.userSeq}")
                        } else {
                            Log.e("MyGreenIngMoreFragment", "회원 못찾음")
                        }
                    } else {
                        Log.e("MyGreenIngMoreFragment", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    }
                    callback(user)
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyGreenIngMoreFragment", "서버 통신 중 오류 발생", t)
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }

    private fun setupRecyclerView(greeningList: List<Greening>) {
        // 진행중인 그리닝
        viewManager = GridLayoutManager(requireContext() , 2)
        viewAdapter = MyGreenIngMoreAdapter()
        recyclerView = binding.recyclerviewIngMoreGreening.apply {
            setHasFixedSize(true)
            suppressLayout(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        (viewAdapter as MyGreenIngMoreAdapter).itemClickListener = object : MyGreenIngMoreAdapter.OnItemClickListener {
            //onItemClick(position: Int)
            override fun onItemClick(gSeq:Int) {
                val intent = Intent(requireActivity(), CertifyGreeningActivity::class.java)
                intent.putExtra("status","ing")
                intent.putExtra("gSeq", gSeq)
                startActivity(intent)
            }
        }

        (viewAdapter as MyGreenIngMoreAdapter).updateData(greeningList)
    }

}