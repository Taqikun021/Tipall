package xyz.tqydn.tipang.network

import retrofit2.Call
import retrofit2.http.*
import xyz.tqydn.tipang.model.*

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

    @GET("getUserInfo.php")
    fun getUserInfo(
        @Header("Authorization") authHeader: String?
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
    @POST("createUsaha.php")
    fun tambahUsaha(
        @Field("id_user") id_user: String?,
        @Field("nama_usaha") nama_usaha: String?,
        @Field("foto_usaha") foto: String?,
        @Field("lat") lat: String,
        @Field("lng") lng: String,
        @Field("alamat") alamat: String?,
        @Field("desc") desc: String?
    ): Call<DefaultResponse>

    @GET("getDistInfo.php")
    fun getDistInfo(
        @Query("id_user") id_user: String?
    ): Call<GetDistInfo>

    @FormUrlEncoded
    @POST("updateUsaha.php")
    fun updateUsaha(
        @Field("id_distributor") id_distributor: String?,
        @Field("nama_usaha") nama_usaha: String?,
        @Field("foto_usaha") foto_usaha: String?,
        @Field("lat") lat: String?,
        @Field("lng") lng: String?,
        @Field("alamat") alamat: String?,
        @Field("desc") desc: String?
    ): Call<DefaultResponse>

    @GET("getBarangUser.php")
    fun getBarang(
        @Query("id_distributor") id_distributor: String?
    ): Call<Barang>

    @FormUrlEncoded
    @POST("createBarang.php")
    fun tambahBarang(
        @Field("id_distributor") id_distributor: String?,
        @Field("nama_barang") nama_barang: String?,
        @Field("foto_barang") foto_barang: String?,
        @Field("jumlah_stok") jumlah_stok: String?,
        @Field("deskripsi_produk") deskripsi_produk: String?,
        @Field("harga_awal") harga_awal: String?,
        @Field("harga_jual") harga_jual: String?
    ): Call<DefaultResponse>

    @GET("getBarangInfo.php")
    fun getInfoBarang(
        @Query("id_barang") id_barang: String?
    ): Call<DataBarang>

    @FormUrlEncoded
    @POST("updateBarang.php")
    fun editBarang(
        @Field("id_barang") id_barang: String?,
        @Field("nama_barang") nama_barang: String?,
        @Field("foto_barang") foto_barang: String?,
        @Field("jumlah_stok") jumlah_stok: String?,
        @Field("deskripsi_produk") deskripsi_produk: String?,
        @Field("harga_awal") harga_awal: String?,
        @Field("harga_jual") harga_jual: String?
    ): Call<DefaultResponse>

    @GET("hapusBarang.php")
    fun hapusBarang(
        @Query("id_barang") id_barang: String?
    ): Call<DefaultResponse>

    @GET("getPenjual.php")
    fun getPenjual(): Call<Penjual>
}