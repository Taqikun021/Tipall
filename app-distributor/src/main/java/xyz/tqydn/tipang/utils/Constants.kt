package xyz.tqydn.tipang.utils

import android.content.Context
import android.content.Intent
import android.location.Location
import xyz.tqydn.tipang.network.ApiClient
import xyz.tqydn.tipang.network.ApiInterface
import xyz.tqydn.tipang.ui.inventory.*
import java.text.NumberFormat
import java.util.*

class Constants {
    companion object{
        const val BASE_URL = "http://tqydn.xyz/restapi/"
        const val REQUEST_IMAGE_CAPTURE = 1
        const val EDIT_PROFIL = 2
        const val TAMBAH_USAHA = 3
        const val EDIT_USAHA = 4
        const val TAMBAH_BARANG = 5
        const val EDIT_BARANG = 6
        const val BUAT_TRANSAKSI = 7
        const val DETAIL_TAWARAN = 8
        const val DETAIL_PERMINTAAN = 9
        const val DETAIL_BERLANGSUNG = 10
        const val DETAIL_HUTANG = 11
        const val RIWAYAT_TRANSAKSI = 12
        private const val ID = "id"
        const val TITLE = "title"
        const val status1 = "TAWARAN_DIBUAT"
        const val status2 = "BERLANGSUNG"
        const val status3 = "SELESAI"
        const val status4 = "GAGAL"
        const val status5 = "PERMINTAAN_DIBUAT"

        val apiInterface: ApiInterface by lazy {
            ApiClient.getClient().create(ApiInterface::class.java)
        }

        fun isNumber(s: String?): Boolean {
            return if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) }
        }

        fun kodeTransaksi(): String {
            val header = "T-TAW"
            val code = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.getDefault())
            return header + code
        }

        fun formatRupiah(number: Double): String? {
            val localeID = Locale("in", "ID")
            val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
            return formatRupiah.format(number)
        }

        fun hitungJarak(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
            val jarak = FloatArray(1)
            Location.distanceBetween(lat1, lng1, lat2, lng2, jarak)
            return (jarak[0]/1000)
        }

        fun detailHutang(context: Context, id: Int?): Intent {
            return Intent(context, DetailHutangActivity::class.java)
                    .putExtra(ID, id)
        }

        fun riwayatTransaksi(context: Context, id: Int?): Intent {
            return Intent(context, RiwayatTransaksiActivity::class.java)
                    .putExtra(ID, id)
        }

        fun detailBerlangsung(context: Context, id: Int?): Intent {
            return Intent(context, DetailBerlangsungActivity::class.java)
                    .putExtra(ID, id)
        }

        fun detailPermintaan(context: Context, id: Int?): Intent {
            return Intent(context, DetailPermintaanActivity::class.java)
                    .putExtra(ID, id)
        }

        fun detailTawaran(context: Context, id: Int?): Intent {
            return Intent(context, DetailTawaranActivity::class.java)
                    .putExtra(ID, id)
        }

        fun buatTransaksi(context: Context, id: Int?): Intent {
            return Intent(context, BuatTransaksiActivity::class.java)
                    .putExtra(ID, id)
        }

        fun editBarang(context: Context, id: Int?): Intent {
            return Intent(context, EditBarangActivity::class.java)
                .putExtra(ID, id)
        }

        fun editProfil(context: Context, id: Int?): Intent {
            return Intent(context, EditProfilActivity::class.java)
                .putExtra(ID, id)
        }

        fun editUsaha(context: Context, id: Int?): Intent {
            return Intent(context, EditUsahaActivity::class.java)
                .putExtra(ID, id)
        }

        fun tambahBarang(context: Context, id: Int?): Intent {
            return Intent(context, TambahBarangActivity::class.java)
                .putExtra(ID, id)
        }

        fun tambahUsaha(context: Context, id: Int?): Intent {
            return Intent(context, TambahUsahaActivity::class.java)
                .putExtra(ID, id)
        }
    }
}