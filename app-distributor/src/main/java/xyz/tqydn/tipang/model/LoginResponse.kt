package xyz.tqydn.tipang.model

data class LoginResponse(
    val success: Long,
    val status: Long?,
    val message: String,
    val token: String
)