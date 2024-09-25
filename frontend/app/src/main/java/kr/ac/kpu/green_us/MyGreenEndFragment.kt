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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kr.ac.kpu.green_us.adapter.MyGreenEndAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.Prize
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.FragmentMyGreenEndBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 완료한 그리닝 - 완료한 그리닝, 완료한 그리닝 수, 총 획득 포인트 확인 가능
class MyGreenEndFragment : Fragment() {
    lateinit var binding: FragmentMyGreenEndBinding
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager
    var greenExist = true // 데이터에 따라 달라지게
    private var allPrizes: List<Prize> = emptyList()

    lateinit var auth: FirebaseAuth
    var user : User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyGreenEndBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        // 리사이클러뷰 중복 스크롤 막기
        binding.recyclerviewEndGreening.isNestedScrollingEnabled = false
        showNoDataView()

        val today = LocalDate.now()

        // 데이터 가져오기
        getUserByEmail { user ->
            if (user != null) {
                // API 호출
                fetchPrizes()

                val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                apiService.findYGreeningByUserSeq(user.userSeq).enqueue(object : Callback<List<Greening>> {
                    override fun onResponse(call: Call<List<Greening>>, response: Response<List<Greening>>) {
                        if (response.isSuccessful) {
                            val greeningList = response.body() ?: emptyList()
                            val selectedGreeningList = greeningList.filter { greening ->
                                try {
                                    val endDate = LocalDate.parse(greening.gEndDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    (today.isEqual(endDate) ||endDate.isBefore(today))
                                } catch (e: Exception) {
                                    false
                                }
                            }.shuffled().take(4)
                            if(selectedGreeningList.isNotEmpty()){
                                showDataView()
                            }
                            binding.endGreenNum.text = selectedGreeningList.size.toString()
                            Log.d("MyGreenEndFragment", "Greening Size : ${greeningList.size} -> ${selectedGreeningList.size}")
                            setupRecyclerView(selectedGreeningList)

                            selectedGreeningList.forEachIndexed { index, participate ->
                                Log.d("MyGreenEndFragment", "Participate $index: ${participate.toString()}")
                            }
                        } else {
                            Log.e("MyGreenEndFragment", "Greening 데이터 로딩 실패: ${response.code()}")
                            showNoDataView()
                        }
                    }

                    override fun onFailure(call: Call<List<Greening>>, t: Throwable) {
                        Log.e("MyGreenEndFragment", "서버 통신 중 오류 발생", t)
                        showNoDataView()
                    }
                })
            } else {
                showNoDataView()
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
                            Log.d("MyGreenEndFragment", "회원 찾음 : ${user!!.userSeq}")
                        } else {
                            Log.e("MyGreenEndFragment", "회원 못찾음")
                        }
                    } else {
                        Log.e("MyGreenEndFragment", "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}")
                    }
                    callback(user)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyGreenEndFragment", "서버 통신 중 오류 발생", t)
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }
    private fun setupRecyclerView(greeningList: List<Greening>) {
        viewManager = GridLayoutManager(requireContext(), 2)
        viewAdapter = MyGreenEndAdapter()
        recyclerView = binding.recyclerviewEndGreening.apply {
            setHasFixedSize(true)
            suppressLayout(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        (viewAdapter as MyGreenEndAdapter).itemClickListener = object : MyGreenEndAdapter.OnItemClickListener {
            override fun onItemClick(gSeq:Int) {
                val intent = Intent(requireActivity(), GreeningDetailSubActivity::class.java)
                intent.putExtra("end", "end_state")
                intent.putExtra("gSeq", gSeq)
                startActivity(intent)
            }
        }

        binding.btnMore.setOnClickListener {
            val intent = Intent(getActivity(), SubActivity::class.java)
            intent.putExtra("1", "green_end_more")
            startActivity(intent)
        }

        (viewAdapter as MyGreenEndAdapter).updateData(greeningList)
    }

    private fun showNoDataView() {
        binding.notExistEnd.visibility = View.VISIBLE
        binding.recyclerviewEndGreening.visibility = View.GONE
        binding.moreBtn.visibility = View.GONE
        binding.endGreeningStatistics.visibility = View.GONE
        binding.endGreenCnt.visibility = View.GONE
        binding.totalPoint.visibility = View.GONE
    }

    private fun showDataView() {
        binding.notExistEnd.visibility = View.GONE
        binding.recyclerviewEndGreening.visibility = View.VISIBLE
        binding.moreBtn.visibility = View.VISIBLE
        binding.endGreeningStatistics.visibility = View.VISIBLE
        binding.endGreenCnt.visibility = View.VISIBLE
        binding.totalPoint.visibility = View.VISIBLE
    }

    private fun fetchPrizes() {
        val retrofitAPI = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        retrofitAPI.getPrizeByUserSeq(user!!.userSeq).enqueue(object : Callback<List<Prize>> {
            override fun onResponse(call: Call<List<Prize>>, response: Response<List<Prize>>) {
                if (response.isSuccessful) {
                    allPrizes = response.body() ?: emptyList()
                    binding.totalPointNum.text = allPrizes.sumOf { it.prizeMoney ?: 0 }.toString()
                    Log.d(
                        "PointActivity",
                        "총 포인트: ${allPrizes.sumOf { it.prizeMoney ?: 0 }}"
                    )
                } else {
                    Log.d("PointActivity", "API 응답 실패: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Prize>>, t: Throwable) {
                Log.d("PointActivity", "API 호출 실패: ${t.message}")
            }
        })
    }
}