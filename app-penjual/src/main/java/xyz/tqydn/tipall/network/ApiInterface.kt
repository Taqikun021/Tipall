package xyz.tqydn.tipall.network

import retrofit2.Call
import retrofit2.http.*
import xyz.tqydn.tipall.model.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("login.php")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun createUser(
            @Field("id_user") idUser: String,
            @Field("email") email: String,
            @Field("username") username: String,
            @Field("password") password: String,
            @Field("tipe_user") tipeUser: String
    ): Call<DefaultResponse>

    @GET("getBarang.php")
    fun getBarang(): Call<Barang>

    @GET("getBarangRowInfo.php")
    fun getBarangInfo(
            @Query("id_barang") id_barang: String?
    ): Call<DataBarang>

    @GET("getRatingBarang.php")
    fun getRatingBarang(
            @Query("id_barang") id_barang: String?
    ): Call<RatingBarang>

    @GET("getInfoPenjual.php")
    fun getInfoPenjual(
            @Query("id_user") id_user: String?
    ): Call<GetInfoPenjual>

    @GET("getUserInfo.php")
    fun getUserInfo(
            @Header("Authorization") authHeader: String
    ): Call<GetUserInfo>

    @FormUrlEncoded
    @POST("updateProfil.php")
    fun updateProfil(
            @Field("id_user") idUser: String?,
            @Field("email") email: String,
            @Field("username") username: String,
            @Field("jenis_kelamin") jenisKelamin: String,
            @Field("no_hp") noHP: String,
            @Field("foto") foto: String
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("createPenjual.php")
    fun tambahUsaha(
            @Field("id_user") id_user: String?,
            @Field("nama_usaha") nama_usaha: String?,
            @Field("foto_usaha") foto: String?,
            @Field("lat") lat: String,
            @Field("lng") lng: String,
            @Field("alamat") alamat: String?
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("updatePenjual.php")
    fun updateUsaha(
            @Field("id_penjual") id_penjual: String?,
            @Field("nama_usaha") nama_usaha: String?,
            @Field("foto_usaha") foto_usaha: String?,
            @Field("lat") lat: String?,
            @Field("lng") lng: String?,
            @Field("alamat") alamat: String?
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("createTransaksi.php")
    fun buatTransaksi(
            @Field("id_penjual") id_penjual: String?,
            @Field("id_distributor") id_distributor: String?,
            @Field("id_barang") id_barang: String?,
            @Field("kode_transaksi") kode_transaksi: String?,
            @Field("jumlah_barang") jumlah_barang: String?,
            @Field("total_tagihan") total_tagihan: String?,
            @Field("status_bayar") status_bayar: String?,
            @Field("status_transaksi") status_transaksi: String?
    ): Call<DefaultResponse>
}