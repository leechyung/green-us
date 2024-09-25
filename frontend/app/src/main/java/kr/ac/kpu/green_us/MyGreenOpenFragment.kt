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
import kr.ac.kpu.green_us.adapter.MyGreenIngAdapter
import kr.ac.kpu.green_us.adapter.MyGreenOpenAdapter
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.databinding.FragmentMyGreenOpenBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 개설 그리닝 - 개설한 그리닝 볼 수 있음
class MyGreenOpenFragment : Fragment() {

    lateinit var binding: FragmentMyGreenOpenBinding
    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager
    var greenExist = true // 데이터에 따라 달라지게

    private lateinit var auth: FirebaseAuth
    private var userSeq: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyGreenOpenBinding.inflate(inflater)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email

        // 개설 그리닝 존재 여부에 따라
        if(greenExist) { // 존재 할 경우
            if (currentEmail != null) {
                getUserSeqByEmail(currentEmail) { seq ->
                    userSeq = seq
                    if (userSeq != null) {
                        // 진행중인 그리닝
                        viewManager = GridLayoutManager(requireContext(), 2)
                        viewAdapter = MyGreenOpenAdapter()
                        recyclerView = binding.recyclerviewOpenGreening.apply {
                            setHasFixedSize(true)
                            suppressLayout(true)
                            layoutManager = viewManager
                            adapter = viewAdapter
                        }

                        loadGreeningData(viewAdapter as MyGreenOpenAdapter)

                        (viewAdapter as MyGreenOpenAdapter).itemClickListener = object : MyGreenOpenAdapter.OnItemClickListener {
                            //onItemClick(position: Int)
                            override fun onItemClick(gSeq:Int) {
                                val intent = Intent(requireActivity(), GreeningDetailSubActivity::class.java)
                                intent.putExtra("open","open_state")
                                intent.putExtra("gSeq",gSeq)
                                startActivity(intent)
                            }
                        }

                    } else {
                        Log.e("MyGreenOpenFragment", "userSeq를 가져올 수 없습니다.")
                    }
                }
            } else {
                Log.e("MyGreenOpenFragment", "로그인된 사용자의 이메일을 가져올 수 없습니다.")
            }
        }
        else { // 존재하지 않을 경우
            binding.notExistOpen.visibility = View.VISIBLE
            binding.recyclerviewOpenGreening.visibility = View.GONE
        }

        return binding.root
    }

    private fun getUserSeqByEmail(email: String, callback: (Int?) -> Unit) {
        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        apiService.getUserSeqByEmail(email).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.e("MyGreenOpenFragment", "UserSeq 조회 실패: ${response.code()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.e("MyGreenOpenFragment", "서버 통신 중 오류 발생", t)
                callback(null)
            }
        })
    }

    private fun loadGreeningData(adapter: MyGreenOpenAdapter) {
        if (userSeq == null) {
            Log.e("MyGreenOpenFragment", "userSeq가 설정되지 않았습니다.")
            return
        }

        val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
        apiService.getGreeningByUserSeq(userSeq!!).enqueue(object : Callback<List<Greening>> {
            override fun onResponse(call: Call<List<Greening>>, response: Response<List<Greening>>) {
                if (response.isSuccessful) {
                    val greeningList = response.body() ?: emptyList()

                    // 순서대로 20개의 그리닝 선택
                    val selectedGreeningList = greeningList.take(20)


                    // 데이터를 어댑터에 설정
                    adapter.updateData(selectedGreeningList)
                } else {
                    Log.e("MyGreenOpenFragment", "Greening 데이터 로딩 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Greening>>, t: Throwable) {
                Log.e("MyGreenOpenFragment", "서버 통신 중 오류 발생", t)
            }
        })
    }
}