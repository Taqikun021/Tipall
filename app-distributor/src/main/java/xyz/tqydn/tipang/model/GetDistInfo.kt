package xyz.tqydn.tipang.model

data class GetDistInfo(
    val success: Long,
    val status: Long,
    val dist_data: DistData,
    val message: String
)