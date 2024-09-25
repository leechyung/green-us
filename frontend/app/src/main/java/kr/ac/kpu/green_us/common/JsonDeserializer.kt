package kr.ac.kpu.green_us.common

import com.google.gson.*
import kr.ac.kpu.green_us.common.dto.Greening
import kr.ac.kpu.green_us.common.dto.User
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

//class GreeningDeserializer : JsonDeserializer<Greening> {
//    override fun deserialize(
//        json: JsonElement,
//        typeOfT: Type,
//        context: JsonDeserializationContext
//    ): Greening {
//        val jsonObject = json.asJsonObject
//
//        // JSON 필드를 직접 읽고 변환 -> JSON에서 LocalDate를 배열로 보내줌 -> String으로 변환
//        val gStartDate = jsonObject.getAsJsonArray("gstartDate")?.toFormattedString()
//        val gEndDate = jsonObject.getAsJsonArray("gendDate")?.toFormattedString()
//
//
//
//        // User 객체를 추출
//        val userJson = jsonObject.getAsJsonObject("user")
//        val user = if (userJson != null && !userJson.isJsonNull) {
//            context.deserialize<User>(userJson, User::class.java)
//        } else {
//            null
//        }
//
//        return Greening(
//            gSeq = jsonObject.getAsIntOrNull("gseq") ?: 0,
//            gName = jsonObject.getAsStringOrNull("gname") ?: "",
//            gStartDate = gStartDate ?: "",
//            gEndDate = gEndDate ?: "",
//            gCertiWay = jsonObject.getAsStringOrNull("gcertiWay") ?: "",
//            gInfo = jsonObject.getAsStringOrNull("ginfo") ?: "",
//            gMemberNum = jsonObject.getAsIntOrNull("gmemberNum") ?: 0,
//            gFreq = jsonObject.getAsIntOrNull("gfreq") ?: 0,
//            gDeposit = jsonObject.getAsIntOrNull("gdeposit") ?: 0,
//            gTotalCount = jsonObject.getAsIntOrNull("gtotalCount") ?: 0,
//            gNumber = jsonObject.getAsIntOrNull("gnumber") ?: 0,
//            gKind = jsonObject.getAsIntOrNull("gkind") ?: 0,
//            user = user
//        )
//    }
//
//    private fun JsonArray?.toFormattedString(): String {
//        return if (this != null && this.size() == 3) {
//            String.format("%04d-%02d-%02d", this[0].asInt, this[1].asInt, this[2].asInt)
//        } else {
//            "" // 잘못된 데이터 처리
//        }
//    }
//
//    private fun JsonObject.getAsStringOrNull(memberName: String): String? {
//        return if (this.has(memberName) && !this.get(memberName).isJsonNull) {
//            this.get(memberName).asString
//        } else {
//            null
//        }
//    }
//
//    private fun JsonObject.getAsIntOrNull(memberName: String): Int? {
//        return if (this.has(memberName) && !this.get(memberName).isJsonNull) {
//            this.get(memberName).asInt
//        } else {
//            null
//        }
//    }
//}




//창형
class GreeningDeserializer : JsonDeserializer<Greening> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Greening {
        val jsonObject = json.asJsonObject

        // JSON 필드를 직접 읽고 변환
        val gStartDate = jsonObject.get("gstartDate")?.toFormattedString()
        val gEndDate = jsonObject.get("gendDate")?.toFormattedString()

        // User 객체를 추출
        val userJson = jsonObject.getAsJsonObject("user")
        val user = if (userJson != null && !userJson.isJsonNull) {
            context.deserialize<User>(userJson, User::class.java)
        } else {
            null
        }

        return Greening(
            gSeq = jsonObject.getAsIntOrNull("gseq") ?: 0,
            gName = jsonObject.getAsStringOrNull("gname") ?: "",
            gStartDate = gStartDate ?: "",
            gEndDate = gEndDate ?: "",
            gCertiWay = jsonObject.getAsStringOrNull("gcertiWay") ?: "",
            gInfo = jsonObject.getAsStringOrNull("ginfo") ?: "",
            gMemberNum = jsonObject.getAsIntOrNull("gmemberNum") ?: 0,
            gFreq = jsonObject.getAsIntOrNull("gfreq") ?: 0,
            gDeposit = jsonObject.getAsIntOrNull("gdeposit") ?: 0,
            gTotalCount = jsonObject.getAsIntOrNull("gtotalCount") ?: 0,
            gNumber = jsonObject.getAsIntOrNull("gnumber") ?: 0,
            gKind = jsonObject.getAsIntOrNull("gkind") ?: 0,
            user = user
        )
    }

    // 수정된 부분: JsonElement를 검사하여 배열 또는 단일 값 처리
    private fun JsonElement?.toFormattedString(): String {
        return when {
            this is JsonArray && this.size() == 3 -> {
                String.format("%04d-%02d-%02d", this[0].asInt, this[1].asInt, this[2].asInt)
            }
            this is JsonPrimitive && this.isString -> {
                this.asString
            }
            else -> ""
        }
    }

    private fun JsonObject.getAsStringOrNull(memberName: String): String? {
        return if (this.has(memberName) && !this.get(memberName).isJsonNull) {
            this.get(memberName).asString
        } else {
            null
        }
    }

    private fun JsonObject.getAsIntOrNull(memberName: String): Int? {
        return if (this.has(memberName) && !this.get(memberName).isJsonNull) {
            this.get(memberName).asInt
        } else {
            null
        }
    }
}