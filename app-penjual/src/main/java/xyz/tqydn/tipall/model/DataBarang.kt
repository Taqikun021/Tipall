package xyz.tqydn.tipall.model

data class DataBarang(
    val id_user: String,
    val username: String,
    val email: String,
    val no_hp: String,
    val id_distributor: String,
    val nama_usaha: String,
    val foto_usaha: String,
    val lat: String,
    val lng: String,
    val alamat: String,
    val desc: String,
    val id_barang: String,
    val nama_barang: String,
    val foto_barang: String,
    val jumlah_stok: String,
    val deskripsi_produk: String,
    val harga_awal: String,
    val harga_jual: String
)