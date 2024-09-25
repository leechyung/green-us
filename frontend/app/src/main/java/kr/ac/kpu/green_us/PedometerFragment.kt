package kr.ac.kpu.green_us

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kr.ac.kpu.green_us.common.RetrofitManager
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.User
import kr.ac.kpu.green_us.databinding.FragmentPedometerBinding
import kr.ac.kpu.green_us.pedometer.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class PedometerFragment : Fragment(), SensorEventListener {
    lateinit var binding: FragmentPedometerBinding
    lateinit var sensorManager: SensorManager
    lateinit var stepCountSensor: Sensor

    lateinit var auth: FirebaseAuth
    var user: User? = null

    //현재 걸음 수
    private var mSteps = 0
    //리스너가 등록되고 난 후의 step count
    private var mCounterSteps = 0

    var today = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPedometerBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        binding.today.text = today.toString()

        val today_value = MyApplication.prefs.getString("today", "0")

        getUserByEmail { user ->
            if(today_value == today.toString()){
                mSteps = user!!.userPedometer
                binding.stepCounter.text = mSteps.toString()
                binding.pedometerDegree.progress = mSteps
                binding.btn.visibility = View.GONE
            }
            else{
                mCounterSteps = 0
                user!!.userPedometer = 0

                val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                apiService.updateUser(user!!.userSeq, user!!).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Log.d("PedometerFragment", "서버로 데이터 전송 성공")
                            mSteps = user!!.userPedometer
                            binding.stepCounter.text = mSteps.toString()
                            binding.pedometerDegree.progress = mSteps
                        } else {
                            Log.e("PedometerFragment", "서버로 데이터 전송 실패: ${response.code()}, ${response.errorBody()?.string()}")
                        }
                    }
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("PedometerFragment", "서버 통신 중 오류 발생", t)
                    }
                })
            }
        }

        sensorManager =
            (requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager)!!
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!

        // 디바이스에 걸음 센서의 존재 여부 체크
        if (stepCountSensor == null) {
            Toast.makeText(getActivity(), "No Step Sensor", Toast.LENGTH_SHORT).show()
        }

        binding.btn.setOnClickListener {
            MyApplication.prefs.setString("today", "${today}")
            MyApplication.prefs.setString("mCounterSteps", "${mCounterSteps}")
            binding.btn.visibility = View.GONE
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (stepCountSensor != null) {
            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onStop() {
        super.onStop()
        if (sensorManager != null) {
            getUserByEmail { user ->
                user!!.userPedometer = mSteps

                val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
                apiService.updateUser(user!!.userSeq, user!!).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Log.d("PedometerFragment", "서버로 데이터 전송 성공")

                        } else {
                            Log.e("PedometerFragment", "서버로 데이터 전송 실패: ${response.code()}, ${response.errorBody()?.string()}")
                        }
                    }
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("PedometerFragment", "서버 통신 중 오류 발생", t)
                    }
                })
            }
            sensorManager.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        mCounterSteps = MyApplication.prefs.getString("mCounterSteps", "0").toInt()
        // 걸음 센서 이벤트 발생시
        if (mCounterSteps < 1) {
            // initial value
            mCounterSteps = event.values[0].toInt()
            MyApplication.prefs.setString("mCounterSteps", "${mCounterSteps}")
        }
        //리셋 안된 값 + 현재값 - 리셋 안된 값
        mSteps = event.values[0].toInt() - mCounterSteps
        binding.stepCounter.text = mSteps.toString()
        binding.pedometerDegree.progress = mSteps
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun getUserByEmail(callback: (User?) -> Unit) {
        val currentUser = auth.currentUser
        val currentEmail = currentUser?.email.toString()
        Log.d("currentEmail", currentEmail)

        if (currentEmail.isNotEmpty()) {
            val apiService = RetrofitManager.retrofit.create(RetrofitAPI::class.java)
            apiService.getUserbyEmail(currentEmail).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        user = response.body()
                        if (user != null) {
                            Log.d("MyGreenIngFragment", "회원 찾음 : ${user!!.userSeq}")
                        } else {
                            Log.e("MyGreenIngFragment", "회원 못찾음")
                        }
                    } else {
                        Log.e(
                            "MyGreenIngFragment",
                            "사용자 조회 실패: ${response.code()}, ${response.errorBody()?.string()}"
                        )
                    }
                    callback(user)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("MyGreenIngFragment", "서버 통신 중 오류 발생", t)
                    callback(null)
                }
            })
        } else {
            callback(null)
        }
    }
}