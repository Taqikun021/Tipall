package xyz.tqydn.tipall.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
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
import xyz.tqydn.tipall.databinding.ActivityDetailHutangBinding
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipall.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipall.utils.SharedPreference

class DetailHutangActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var dari: String
    private lateinit var ke: String
    private lateinit var telepon: String
    private lateinit var status: String
    private lateinit var binding: ActivityDetailHutangBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHutangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = SharedPreference(this)
        showTransaksi()
        binding.keMaps.setOnClickListener {
            startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://google.co.id/maps/dir/${dari}/${ke}"))
            )
        }
        binding.keWA.setOnClickListener {
            startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${telepon}"))
            )
        }
    }

    private fun showTransaksi() {
        val call: Call<TransaksiItem> = apiInterface.getDetailTransaksi(preference.getValues("trans_click"))
        call.enqueue(object : Callback<TransaksiItem> {
            @SuppressLint("SetTextI18n", "ResourceAsColor")
            override fun onResponse(call: Call<TransaksiItem>, response: Response<TransaksiItem>) {
                val item: TransaksiItem? = response.body()
                val imgProfil = Uri.parse(item?.foto)
                val imgUsaha = Uri.parse(item?.foto_usaha)
                val imgBarang = Uri.parse(item?.foto_barang)
                dari = "${preference.getValues("lat")},${preference.getValues("long")}"
                ke = "${item?.lat},${item?.lng}"
                telepon = "62${item?.no_hp}"
                status = item?.status_transaksi.toString()
                Glide.with(this@DetailHutangActivity)
                        .load(imgProfil)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imagePenjual)
                Glide.with(this@DetailHutangActivity)
                        .load(imgUsaha)
                        .apply(RequestOptions.centerCropTransform())
                        .into(binding.imageUsaha)
                Glide.with(this@DetailHutangActivity)
                        .load(imgBarang)
                        .apply(RequestOptions.centerCropTransform())
                        .into(binding.barang)
                binding.kodeTransaksi.text = item?.kode_transaksi
                binding.tanggal.text = item?.waktu_mulai
                binding.namaUsaha.text = item?.nama_usaha
                binding.alamat.text = item?.alamat
                binding.nomorHP.text = "+62 ${item?.no_hp}"
                binding.namaBarang.text = item?.nama_barang
                binding.descBarang.text = item?.deskripsi_produk
                binding.jumlahBarang.text = "${item?.jumlah_barang} Item"
                binding.totalHarga.text = formatRupiah(item?.total_tagihan!!.toDouble())
                binding.ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                if (item.jenis_kelamin == "Perempuan") {
                    binding.namaPemilik.text = "Ibu ${item.username}"
                } else {
                    binding.namaPemilik.text = "Bapak ${item.username}"
                }
            }

            override fun onFailure(call: Call<TransaksiItem>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@DetailHutangActivity).create()
                val inflater = LayoutInflater.from(this@DetailHutangActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }
}