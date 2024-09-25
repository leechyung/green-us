package kr.ac.kpu.green_us
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.*
import kr.ac.kpu.green_us.adapter.MarketAdapter
import kr.ac.kpu.green_us.data.Market
import kr.ac.kpu.green_us.data.MarketTime
import kr.ac.kpu.green_us.databinding.ActivityMapBinding
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import kotlin.concurrent.thread


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var naverMap: NaverMap
    lateinit var keyword :String
    val searchId = BuildConfig.SEARCH_ID
    val searchSecret = BuildConfig.SEARCH_SECRET
    val mapId = BuildConfig.MAP_ID
    val mapSecret = BuildConfig.MAP_SECRET




    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NaverMapSdk 인스턴스 설정
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(mapId)

        // 위치찾기를 위한 fusedLocationProviderClient 선언
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        initializeMap() // 맵뷰 초기화


        // 바텀시트
        binding.bottomLayout.recyclerviewMarketList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val behavior = BottomSheetBehavior.from(binding.bottomLayout.root)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                when (state) {
                    // 바텀시트가 확장된 상태라면 화살표 버튼이 아래로 향하게 이미지 변경
                    BottomSheetBehavior.STATE_COLLAPSED -> binding.bottomLayout.btnArrow.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
                    //접혔을 시 화살표 버튼이 위로 향하게 이미지 변경
                    BottomSheetBehavior.STATE_EXPANDED -> binding.bottomLayout.btnArrow.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        // 바텀 시트 화살표 버튼 클릭시
        binding.bottomLayout.btnArrow.setOnClickListener {
            // 접힌 상태에서 클릭하면 확장시킴
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else { // 확장상태에서 클릭하면 접힘
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        // 이전 버튼
        binding.btnEsc.setOnClickListener {
            this.finish()
        }
    }
    private fun getAddress(lati:Double,longi:Double){
        if (lati != null  && longi != null) {
            Log.d("getAddress", "Lat: ${lati}, lon: ${longi}")

            // 위도, 경도를 주소값으로 변환
            val geocoder = Geocoder(applicationContext, Locale.KOREAN)
            // 안드로이드 API 레벨이 33 이상인 경우
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    lati, longi, 1
                ) { address ->
                    if (address.size != 0) {
                        // (반환 값에서) 전체 주소 중 첫번째 값만 사용하여 한국어 주소로 변환하러 간다
                        filterAddress(address[0].getAddressLine(0))
                    }
                }
            } else { // API 레벨이 33 미만인 경우
                val addresses = geocoder.getFromLocation(lati, longi, 1)
                if (addresses != null) {
                    filterAddress(addresses[0].getAddressLine(0))
                }
            }
        }
    }
    private fun filterAddress(words:String){
        val seletedWords: String
        var result:String

        val splitWords = words.split(" ")
        println("splitWords -> {$splitWords}")
        // **로까지 포함하면 제로웨이스트샵이 많이 없어서 검색되지 않음
        // 그래서 **시 **구 로 검색합니다
        seletedWords = splitWords[1]+" "+splitWords[2]
        println("splitWords selected ->{$seletedWords} ")

        val filterAddress =
            try {
                URLEncoder.encode(seletedWords, "UTF-8")
            }catch (e: UnsupportedEncodingException){
                throw RuntimeException("검색어 인코딩 실패", e)
            }
        keyword =
            try {
                URLEncoder.encode("제로웨이스트", "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException("검색어 인코딩 실패", e)
            }
        result = filterAddress+" "+keyword // 예시) 해운대구 제로웨이스트
        conHttp(result) // url에 붙일 query로 네트워킹 작업하러 감
    }
    //네이버 검색,geocoding api 통신 및 웹크롤링 함수
    private fun conHttp(address:String){
        thread {
            try {
                // 검색api
                var marketList:ArrayList<Market>
                val apiURL = "https://openapi.naver.com/v1/search/local?query=$address&display=10"
                val requestHeaders = mapOf(
                    "X-Naver-Client-Id" to searchId,
                    "X-Naver-Client-Secret" to searchSecret
                )
                val httpReslt = get(apiURL, requestHeaders) // 검색 api 요청 및 응답
                marketList = jsonData(httpReslt) // 마켓 데이터 추출
                // geocoding api -> 마커 찍기를 위해 마켓 주소로 위도, 경도 불러와서 marketList에 추가함
                for (i in 0 until marketList.size){
                    val query =
                        try {
                            URLEncoder.encode(marketList[i].location, "UTF-8")
                        }catch (e: UnsupportedEncodingException) {
                            throw RuntimeException("geocoding api address 인코딩 실패", e)
                        }
                    println("query -> {$query}")
                    val apiUrl = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=${query}"
                    val requestHeaders = mapOf(
                        "X-NCP-APIGW-API-KEY-ID" to mapId ,
                        "X-NCP-APIGW-API-KEY" to mapSecret
                    )
                    val httpResult = get(apiUrl,requestHeaders)
                    val root = JSONObject(httpResult)
                    val dataArray = root.getJSONArray("addresses")
                    for (index in 0 until dataArray.length()){
                        val data = dataArray.getJSONObject(index)
                        val lati = data.getDouble("y")
                        val longi = data.getDouble("x")
                        marketList[i].lati = lati
                        marketList[i].longi = longi
                    }
                }
                println("marketList -> {${marketList}}")


                // 웹크롤링 -> api로는 영업에 대한 정보가 없기에 웹크롤링으로 찾음 검색 api가 가져오는 정보리스트와 동일한 url
                val timeList: ArrayList<MarketTime> = arrayListOf()
                val crawlingURL = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=$address"
                val doc = Jsoup.connect(crawlingURL).get()
                val elements: Elements = doc.select("li.VLTHu.JJ4Cd")

                elements.forEach { element ->
                   val closed = element.select("div.Gvf9B span:nth-child(2)").text()
                    println("closed -> {$closed}")
                    if (closed.isNullOrEmpty()) { // 웹크롤링으로도 정보가 없는 것은 "정보없음"으로 저장함
                        val data = MarketTime("정보없음")
                        timeList.add(data)
                    } else { 
                        val data = MarketTime(closed)
                        timeList.add(data)
                    }
                }
                runOnUiThread { // ui 업데이트
                    // 바텀시트 업데이트
                    val bottomAdapter = MarketAdapter(marketList, timeList)
                    binding.bottomLayout.recyclerviewMarketList.adapter = bottomAdapter
                    bottomAdapter.notifyDataSetChanged()
                    bottomAdapter.itemClickListener = object : MarketAdapter.OnItemClickListener {
                        override fun onItemClick(url: String) {
                            if(url.isNotEmpty()){
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(intent)
                            }else{
                                Toast.makeText(applicationContext,"링크 정보가 없습니다.",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    // 마커 업데이트
                    marking(marketList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun marking(positions : ArrayList<Market>){
        val markers = arrayOfNulls<Marker>(positions.size)
        for (i in 0 until positions.size){
            markers[i] =  Marker()
            val lati = positions[i].lati
            println("markin lati -> {$lati}")
            val longi = positions[i].longi
            println("markin longi -> {$longi}")
            markers[i]?.position = LatLng(lati, longi)
            markers[i]?.captionText = positions[i].name
            markers[i]?.map = naverMap
        }
    }
    private fun get(apiUrl: String, requestHeaders: Map<String, String>): String {
        val url = URL(apiUrl)
        val con = url.openConnection() as HttpURLConnection
        return try {
            con.requestMethod = "GET"
            for ((key, value) in requestHeaders) {
                con.setRequestProperty(key, value)
            }

            val responseCode = con.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                readBody(con.inputStream)
            } else { // 오류 발생
                readBody(con.errorStream)
            }
        } catch (e: IOException) {
            throw RuntimeException("API 요청과 응답 실패", e)
        } finally {
            con.disconnect()
        }
    }
    private fun readBody(body: InputStream): String {
        val streamReader = InputStreamReader(body)

        return BufferedReader(streamReader).use { lineReader ->
            val responseBody = StringBuilder()

            var line: String?
            while (lineReader.readLine().also { line = it } != null) {
                responseBody.append(line)
            }
            responseBody.toString()
        }
    }
    private fun jsonData(body:String):ArrayList<Market>{
        val root = JSONObject(body)
        val dataArray = root.getJSONArray("items")
        val marketInfos : ArrayList<Market> = arrayListOf()
        for (index in 0 until dataArray.length()){
            val data = dataArray.getJSONObject(index)
            val name = data.getString("title").replace("<b>","").replace("</b>","")
            val location = data.getString("address")
            val link = data.getString("link")
            val infos = Market(name,location,link,0.0,0.0)
            marketInfos.add(infos)
        }
        return marketInfos
    }

    // 위치 권한 요청
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this@MapActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        val uiSettings: UiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true // 현위치버튼 활성화
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            Log.d("MapActivity","권한 없음")
        }else{
            Log.d("MapActivity","권한 승인")
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // 위치 오버레이의 가시성은 기본적으로 false로 지정되어 있습니다. 가시성을 true로 변경하면 지도에 위치 오버레이가 나타납니다.
                        // 파랑색 점으로 현재 위치 표시
                        naverMap.locationOverlay.run {
                            isVisible = true
                            position = LatLng(it.latitude, it.longitude)
                        }

                        // 네트워킹 시작
                        getAddress(it.latitude,it.longitude)

                        // 카메라 현재위치로 이동
                        val cameraUpdate = CameraUpdate.scrollTo(
                            LatLng(it.latitude, it.longitude)
                        )
                        naverMap.moveCamera(cameraUpdate)
                    }
                }
            naverMap.addOnLocationChangeListener { location ->
                val latitude = location.latitude
                val longitude = location.longitude
                getAddress(latitude,longitude)
                val cameraUpdate = CameraUpdate.scrollTo(
                    LatLng(latitude, longitude)
                )
                naverMap.moveCamera(cameraUpdate)
            }
        }
    }
    private fun initializeMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

    }
}