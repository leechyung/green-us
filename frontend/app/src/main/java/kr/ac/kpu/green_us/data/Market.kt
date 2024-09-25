package kr.ac.kpu.green_us.data

data class Market(
    val name:String,
    val location: String,
    val link : String,
    var lati : Double,
    var longi : Double
) {
}