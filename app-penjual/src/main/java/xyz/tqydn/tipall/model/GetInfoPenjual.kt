package xyz.tqydn.tipall.model

data class GetInfoPenjual(
    val success: Long,
    val status: Long,
    val dist_data: DistData,
    val message: String
)