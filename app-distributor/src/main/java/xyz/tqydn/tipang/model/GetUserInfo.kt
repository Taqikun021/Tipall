package xyz.tqydn.tipang.model

data class GetUserInfo(
    val success: Long,
    val status: Long,
    val user: User,
    val message: String
)