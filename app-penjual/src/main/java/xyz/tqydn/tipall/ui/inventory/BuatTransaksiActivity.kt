package xyz.tqydn.tipall.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.databinding.ActivityBuatTransaksiBinding
import xyz.tqydn.tipall.model.DataBarang
import xyz.tqydn.tipall.model.DefaultResponse
import xyz.tqydn.tipall.model.RatingBarang
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.Constants.Companion.status1
import xyz.tqydn.tipall.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipall.utils.Constants.Companion.isNumber
import xyz.tqydn.tipall.utils.Constants.Companion.kodeTransaksi
import xyz.tqydn.tipall.utils.SharedPreference

@SuppressLint("SetTextI18n")
class BuatTransaksiActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private var hargaBarang: Double = 0.0
    private var stok: Int = 0
    private lateinit var idPenjual: String
    private lateinit var idDistributor: String
    private lateinit var idBarang: String
    private lateinit var binding: ActivityBuatTransaksiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuatTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preference = SharedPreference(this)
        var jumlahBarang = 0
        getDataBarang()
        getRatingBarang()
        binding.kurang.setOnClickListener {
            if (jumlahBarang == 0){
                binding.jumlah.error = "Jumlah harus positif"
            } else {
                jumlahBarang -= 1
            }
            binding.jumlah.setText(jumlahBarang.toString())
            binding.jumlah.requestFocus()
            binding.total.text = Constants.formatRupiah(jumlahBarang*hargaBarang)
        }
        binding.tambah.setOnClickListener {
            if (jumlahBarang == stok){
                binding.jumlah.error = "Stok tidak mencukupi"
            } else {
                jumlahBarang += 1
            }
            binding.jumlah.setText(jumlahBarang.toString())
            binding.jumlah.requestFocus()
            binding.total.text = Constants.formatRupiah(jumlahBarang*hargaBarang)
        }
        binding.kirimTawaran.setOnClickListener {
            val jml = binding.jumlah.text.toString().trim()
            val hasil = (hargaBarang*jumlahBarang).toString()
            if(!isNumber(jml)) {
                binding.jumlah.error = "Jumlah harus angka"
                binding.jumlah.requestFocus()
            } else if(jml.toInt() < 1) {
                binding.jumlah.error = "Jumlah harus terisi minimal 1"
                binding.jumlah.requestFocus()
            } else if(jml.toInt() > stok) {
                binding.jumlah.error = "Stok tidak mencukupi"
                binding.jumlah.requestFocus()
            } else {
                buatTransaksi(jml, hasil)
            }
        }
    }

    private fun buatTransaksi(jml: String, hasil: String) {
        val call: Call<DefaultResponse> = apiInterface.buatTransaksi(idPenjual, idDistributor, idBarang, kodeTransaksi(), jml, hasil, 0, status1)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.body()?.status.toString() == "201") {
                    Toast.makeText(this@BuatTransaksiActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                } else {
                    val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                    val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                }
                val intent = Intent().apply {
                    putExtra(Constants.TITLE, response.body()?.message.toString())
                }
                setResult(RESULT_OK, intent)
                finish()
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun getRatingBarang() {
        val call: Call<RatingBarang> = apiInterface.getRatingBarang(preference.getValues("barang_click"))
        call.enqueue(object : Callback<RatingBarang> {
            override fun onResponse(call: Call<RatingBarang>, response: Response<RatingBarang>) {
                if (response.code() == 200) {
                    binding.ratingPenjual.text = "%.2f".format(response.body()?.rating_barang?.toFloat())
                    binding.transaksi.text = "${response.body()?.jumlah_transaksi} Transaksi"
                } else {
                    val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                    val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                }
            }

            override fun onFailure(call: Call<RatingBarang>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun getDataBarang() {
        val call: Call<DataBarang> = apiInterface.getBarangInfo(preference.getValues("barang_click"))
        call.enqueue(object : Callback<DataBarang> {
            override fun onResponse(call: Call<DataBarang>, response: Response<DataBarang>) {
                val data: DataBarang? = response.body()
                if (response.code() == 200) {
                    val distance = Constants.hitungJarak(
                        preference.getValues("lat")!!.toDouble(),
                        preference.getValues("long")!!.toDouble(),
                        data?.lat!!.toDouble(),
                        data.lng.toDouble())
                    hargaBarang = data.harga_awal.toDouble()
                    stok = data.jumlah_stok.toInt()
                    idPenjual = preference.getValues("id_penjual")!!
                    idBarang = data.id_barang
                    idDistributor = data.id_distributor
                    binding.namaUsaha.text = data.nama_usaha
                    binding.namaPemilik.text = "Pemilik ${data.username}"
                    binding.jarak.text = "${"%.2f".format(distance)} km"
                    binding.alamat.text = data.alamat
                    binding.namaBarang.text = data.nama_barang
                    binding.descBarang.text = data.deskripsi_produk
                    binding.HargaBarang.text = Constants.formatRupiah(data.harga_awal.toDouble())
                    binding.HargaJualBarang.text = Constants.formatRupiah(data.harga_jual.toDouble())
                    binding.tersedia.text = "Tersedia ${data.jumlah_stok} item"
                    Glide.with(this@BuatTransaksiActivity)
                        .load(Uri.parse(data.foto_usaha))
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imagePenjual)
                    Glide.with(this@BuatTransaksiActivity)
                        .load(Uri.parse(data.foto_barang))
                        .apply(RequestOptions.centerCropTransform())
                        .into(binding.barang)
                } else {
                    val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                    val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                    val dialogView = inflater.inflate(R.layout.alert_error, null)
                    photoDialog.setCancelable(true)
                    val tv = dialogView.findViewById(R.id.tv) as TextView
                    tv.text = "Ups! Ada yang salah nih. Coba cek koneksi kamu dan swipe down untuk memuat ulang"
                    photoDialog.setView(dialogView)
                    photoDialog.show()
                }
            }

            override fun onFailure(call: Call<DataBarang>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@BuatTransaksiActivity).create()
                val inflater = LayoutInflater.from(this@BuatTransaksiActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }
}