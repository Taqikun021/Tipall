package xyz.tqydn.tipang.model

data class DataBarang(
    val id_barang: String,
    val id_distributor: String,
    val nama_barang: String,
    val foto_barang: String,
    val jumlah_stok: String,
    val deskripsi_produk: String,
    val harga_awal: String,
    val harga_jual: String
)