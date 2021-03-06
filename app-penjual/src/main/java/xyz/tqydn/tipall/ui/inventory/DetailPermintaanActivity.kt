package xyz.tqydn.tipall.ui.inventory

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.databinding.ActivityDetailPermintaanBinding
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipall.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipall.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipall.utils.SharedPreference

class DetailPermintaanActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var binding: ActivityDetailPermintaanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPermintaanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = SharedPreference(this)
        getDetail()
    }

    private fun getDetail() {
        val call: Call<TransaksiItem> = apiInterface.getDetailTransaksi(preference.getValues("trans_click"))
        call.enqueue(object : Callback<TransaksiItem> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<TransaksiItem>, response: Response<TransaksiItem>) {
                val item: TransaksiItem? = response.body()
                val imgProfil = Uri.parse(item?.foto)
                val imgBarang = Uri.parse(item?.foto_barang)
                val distance = hitungJarak(
                    preference.getValues("lat")!!.toDouble(),
                    preference.getValues("long")!!.toDouble(),
                    item?.lat!!.toDouble(),
                    item.lng!!.toDouble())
                binding.kodeTransaksi.text = item.kode_transaksi
                binding.tanggal.text = item.waktu_mulai
                binding.namaUsaha.text = item.nama_usaha
                binding.ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                binding.transaksi.text = "Dari ${item.jumlah_transaksi} Transaksi"
                binding.jarak.text = "${"%.2f".format(distance)} km"
                binding.namaBarang.text = item.nama_barang
                binding.descBarang.text = item.deskripsi_produk
                binding.HargaBarang.text = formatRupiah(item.harga_awal!!.toDouble())
                binding.alamat.text = item.alamat
                binding.total.text = formatRupiah(item.total_tagihan!!.toDouble())
                binding.jumlah.setText(item.jumlah_barang)
                binding.jumlah.isClickable = false
                Glide.with(this@DetailPermintaanActivity)
                    .load(imgProfil)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imagePenjual)
                Glide.with(this@DetailPermintaanActivity)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.barang)
                if (item.jenis_kelamin == "Perempuan") {
                    binding.namaPemilik.text = "Ibu ${item.username}"
                } else {
                    binding.namaPemilik.text = "Bapak ${item.username}"
                }
            }

            override fun onFailure(call: Call<TransaksiItem>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@DetailPermintaanActivity).create()
                val inflater = LayoutInflater.from(this@DetailPermintaanActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }
}