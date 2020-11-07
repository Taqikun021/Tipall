package xyz.tqydn.tipang.model

data class TransaksiItem(
    val id_transaksi: String?,
    val id_penjual: String?,
    val id_distributor: String?,
    val id_barang: String?,
    val kode_transaksi: String?,
    val jumlah_barang: String?,
    val total_tagihan: String?,
    val waktu_mulai: String?,
    val waktu_selesai: String?,
    val status_bayar: String?,
    val status_transaksi: String?,
    val nama_barang: String?,
    val foto_barang: String?,
    val jumlah_stok: String?,
    val deskripsi_produk: String?,
    val harga_awal: String?,
    val harga_jual: String?,
    val nama_usaha: String?,
    val foto_usaha: String?,
    val lat: String?,
    val lng: String?,
    val alamat: String?,
    val username: String?,
    val jenis_kelamin: String?,
    val no_hp: String?,
    val foto: String?,
    val rating: String?,
    val jumlah_transaksi: String?
)