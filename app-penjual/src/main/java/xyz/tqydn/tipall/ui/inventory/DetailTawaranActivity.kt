package xyz.tqydn.tipall.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.databinding.ActivityDetailTawaranBinding
import xyz.tqydn.tipall.model.DefaultResponse
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipall.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipall.utils.Constants.Companion.status2
import xyz.tqydn.tipall.utils.Constants.Companion.status4
import xyz.tqydn.tipall.utils.SharedPreference

class DetailTawaranActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var dari: String
    private lateinit var ke: String
    private lateinit var telepon: String
    private lateinit var idBarang: String
    private lateinit var stok: String
    private lateinit var binding: ActivityDetailTawaranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTawaranBinding.inflate(layoutInflater)
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
        binding.terima.setOnClickListener {
            transDiterima(preference.getValues("trans_click"))
        }
        binding.tolak.setOnClickListener {
            val alertDialog = MaterialAlertDialogBuilder(this).create()
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.alert_tolak, null)
            alertDialog.setCancelable(true)
            alertDialog.setView(dialogView)
            val yakin = dialogView.findViewById(R.id.yakin) as Button
            val batal = dialogView.findViewById(R.id.batal) as Button
            yakin.setOnClickListener {
                transDitolak(preference.getValues("trans_click"))
                alertDialog.dismiss()
            }
            batal.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
    }

    private fun showTransaksi() {
        val call: Call<TransaksiItem> = apiInterface.getDetailTransaksi(preference.getValues("trans_click"))
        call.enqueue(object : Callback<TransaksiItem> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<TransaksiItem>, response: Response<TransaksiItem>) {
                val item: TransaksiItem? = response.body()
                val imgProfil = Uri.parse(item?.foto)
                val imgUsaha = Uri.parse(item?.foto_usaha)
                val imgBarang = Uri.parse(item?.foto_barang)
                dari = "${preference.getValues("lat")},${preference.getValues("long")}"
                ke = "${item?.lat},${item?.lng}"
                telepon = "62${item?.no_hp}"
                idBarang = item?.id_barang.toString()
                val jumlah = item?.jumlah_stok!!.toInt() - item.jumlah_barang!!.toInt()
                stok = jumlah.toString()
                Glide.with(this@DetailTawaranActivity)
                    .load(imgProfil)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imagePenjual)
                Glide.with(this@DetailTawaranActivity)
                    .load(imgUsaha)
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.imageUsaha)
                Glide.with(this@DetailTawaranActivity)
                    .load(imgBarang)
                    .apply(RequestOptions.centerCropTransform())
                    .into(binding.barang)
                binding.kodeTransaksi.text = item.kode_transaksi
                binding.tanggal.text = item.waktu_mulai
                binding.namaUsaha.text = item.nama_usaha
                binding.alamat.text = item.alamat
                binding.nomorHP.text = "+62 ${item.no_hp}"
                binding.namaBarang.text = item.nama_barang
                binding.descBarang.text = item.deskripsi_produk
                binding.jumlahBarang.text = "${item.jumlah_barang} Item"
                binding.totalHarga.text = formatRupiah(item.total_tagihan!!.toDouble())
                binding.ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                if (item.jenis_kelamin == "Perempuan") {
                    binding.namaPemilik.text = "Ibu ${item.username}"
                } else {
                    binding.namaPemilik.text = "Bapak ${item.username}"
                }
            }

            override fun onFailure(call: Call<TransaksiItem>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@DetailTawaranActivity).create()
                val inflater = LayoutInflater.from(this@DetailTawaranActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun transDiterima(id: String?) {
        val call: Call<DefaultResponse> = apiInterface.updateStatusTransaksi(0, status2, id)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val item: DefaultResponse? = response.body()
                if (item?.status.toString() != "200") {
                    Toast.makeText(this@DetailTawaranActivity, item?.message, Toast.LENGTH_SHORT).show()
                    updateStok(stok, idBarang)
                    val intent = Intent().apply {
                        putExtra(Constants.TITLE, item?.message.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@DetailTawaranActivity).create()
                val inflater = LayoutInflater.from(this@DetailTawaranActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun updateStok(stok: String, idBarang: String) {
        val call: Call<DefaultResponse> = apiInterface.updateStok(stok, idBarang)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.body()?.status.toString() == "200") {
                    Toast.makeText(this@DetailTawaranActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@DetailTawaranActivity).create()
                val inflater = LayoutInflater.from(this@DetailTawaranActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun transDitolak(id: String?) {
        val call: Call<DefaultResponse> = apiInterface.transaksiSelesai("0", 1, status4, id)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val item: DefaultResponse? = response.body()
                if (item?.status.toString() != "200") {
                    Toast.makeText(this@DetailTawaranActivity, item?.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent().apply {
                        putExtra(Constants.TITLE, item?.message.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@DetailTawaranActivity).create()
                val inflater = LayoutInflater.from(this@DetailTawaranActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }
}