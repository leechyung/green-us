package kr.ac.kpu.green_us.common

import com.google.gson.GsonBuilder
import kr.ac.kpu.green_us.common.api.RetrofitAPI
import kr.ac.kpu.green_us.common.dto.Greening
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


class RetrofitManager {
    //baseUrl에 본인pc cmd열고 ipconfig를 통해서 주소 확인한 다음 자신의 컴퓨터 주소로 바꿔줘야함. + 휴대폰으로 할경우 휴대폰의 와이파이도 같은 주소로 연결이 되어야함.
    companion object{
        private val gson = GsonBuilder()
            .registerTypeAdapter(Greening::class.java, GreeningDeserializer())
            .create()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        // Retrofit 설정

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.25.8:8080/")
//            .baseUrl("http://192.168.219.105:8080/")// 유진
////            .baseUrl("http://192.168.1.2:8080/") //본인 Url로 변경
//        .baseUrl("http://192.168.219.107:8080/")// 세진
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
        val api: RetrofitAPI = retrofit.create(RetrofitAPI::class.java)
    }
}
class NullOnEmptyConverterFactory : Converter.Factory() {
    fun converterFactory() = this
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
        val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
        override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) {
            try{
                nextResponseBodyConverter.convert(value)
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        } else{
            null
        }
    }
}
