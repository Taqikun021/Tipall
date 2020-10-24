package xyz.tqydn.tipall.utils

import android.content.Context
import android.content.Intent
import android.location.Location
import xyz.tqydn.tipall.network.ApiClient
import xyz.tqydn.tipall.network.ApiInterface
import xyz.tqydn.tipall.ui.inventory.TambahUsahaActivity
import xyz.tqydn.tipall.ui.inventory.BuatTransaksiActivity
import xyz.tqydn.tipall.ui.inventory.EditProfilActivity
import xyz.tqydn.tipall.ui.inventory.EditUsahaActivity
import java.text.NumberFormat
import java.util.*

class Constants {
    companion object{
        const val BASE_URL = "http://tqydn.xyz/restapi/"
        const val REQUEST_IMAGE_CAPTURE = 1
        const val EDIT_PROFIL = 2
        const val TAMBAH_USAHA = 3
        const val EDIT_USAHA = 4
        const val BUAT_TRANSAKSI = 5
        private const val ID = "id"
        const val TITLE = "title"
        const val _1 = "PERMINTAAN_DIBUAT"
        const val _2 = "BERLANGSUNG"
        const val _3 = "SELESAI"
        const val _4 = "GAGAL"

        val apiInterface: ApiInterface by lazy {
            ApiClient.getClient().create(ApiInterface::class.java)
        }

        fun kodeTransaksi(): String {
            val header = "T-PER"
            val code = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.getDefault())
            return header + code.substring(10)
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

        fun buatTransaksi(context: Context, id: Int?): Intent {
            return Intent(context, BuatTransaksiActivity::class.java)
                .putExtra(ID, id)
        }

        fun editProfil(context: Context, id: Int?): Intent {
            return Intent(context, EditProfilActivity::class.java)
                .putExtra(ID, id)
        }

        fun tambahUsaha(context: Context, id: Int?): Intent {
            return Intent(context, TambahUsahaActivity::class.java)
                .putExtra(ID, id)
        }

        fun editUsaha(context: Context, id: Int?): Intent {
            return Intent(context, EditUsahaActivity::class.java)
                .putExtra(ID, id)
        }
    }
}