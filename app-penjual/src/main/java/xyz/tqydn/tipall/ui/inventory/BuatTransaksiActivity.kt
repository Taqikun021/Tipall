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
import kotlinx.android.synthetic.main.activity_buat_transaksi.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_transaksi)
        preference = SharedPreference(this)
        var jumlahBarang = 0
        getDataBarang()
        getRatingBarang()
        kurang.setOnClickListener {
            if (jumlahBarang == 0){
                jumlah.error = "Jumlah harus positif"
            } else {
                jumlahBarang -= 1
            }
            jumlah.setText(jumlahBarang.toString())
            jumlah.requestFocus()
            total.text = Constants.formatRupiah(jumlahBarang*hargaBarang)
        }
        tambah.setOnClickListener {
            if (jumlahBarang == stok){
                jumlah.error = "Stok tidak mencukupi"
            } else {
                jumlahBarang += 1
            }
            jumlah.setText(jumlahBarang.toString())
            jumlah.requestFocus()
            total.text = Constants.formatRupiah(jumlahBarang*hargaBarang)
        }
        kirimTawaran.setOnClickListener {
            val jml = jumlah.text.toString().trim()
            val hasil = (hargaBarang*jumlahBarang).toString()
            if(!isNumber(jml)) {
                jumlah.error = "Jumlah harus angka"
                jumlah.requestFocus()
            } else if(jml.toInt() < 1) {
                jumlah.error = "Jumlah harus terisi minimal 1"
                jumlah.requestFocus()
            } else if(jml.toInt() > stok) {
                jumlah.error = "Stok tidak mencukupi"
                jumlah.requestFocus()
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
                    ratingPenjual.text = "%.2f".format(response.body()?.rating_barang?.toFloat())
                    transaksi.text = "${response.body()?.jumlah_transaksi} Transaksi"
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
                    namaUsaha.text = data.nama_usaha
                    namaPemilik.text = "Pemilik ${data.username}"
                    jarak.text = "${"%.2f".format(distance)} km"
                    alamat.text = data.alamat
                    namaBarang.text = data.nama_barang
                    descBarang.text = data.deskripsi_produk
                    HargaBarang.text = Constants.formatRupiah(data.harga_awal.toDouble())
                    HargaJualBarang.text = Constants.formatRupiah(data.harga_jual.toDouble())
                    tersedia.text = "Tersedia ${data.jumlah_stok} item"
                    Glide.with(this@BuatTransaksiActivity)
                        .load(Uri.parse(data.foto_usaha))
                        .apply(RequestOptions.circleCropTransform())
                        .into(imagePenjual)
                    Glide.with(this@BuatTransaksiActivity)
                        .load(Uri.parse(data.foto_barang))
                        .apply(RequestOptions.centerCropTransform())
                        .into(barang)
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