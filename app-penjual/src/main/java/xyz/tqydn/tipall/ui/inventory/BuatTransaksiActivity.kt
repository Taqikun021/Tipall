package xyz.tqydn.tipall.ui.inventory

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_buat_transaksi.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.model.DataBarang
import xyz.tqydn.tipall.model.RatingBarang
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.SharedPreference

class BuatTransaksiActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private var hargaBarang: Double = 0.0
    private var stok: Int = 0

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
        /*kirimTawaran.setOnClickListener {
            val jml = jumlah.text.toString().trim()

        }*/
    }

    private fun getRatingBarang() {
        val call: Call<RatingBarang> = Constants.apiInterface.getRatingBarang(preference.getValues("barang_click"))
        call.enqueue(object : Callback<RatingBarang> {
            override fun onResponse(call: Call<RatingBarang>, response: Response<RatingBarang>) {
                if (response.code() == 200) {
                    ratingPenjual.text = "%.2f".format(response.body()?.rating_barang?.toFloat())
                    transaksi.text = "${response.body()?.jumlah_transaksi} Transaksi"
                }
            }

            override fun onFailure(call: Call<RatingBarang>, t: Throwable) {
                Toast.makeText(this@BuatTransaksiActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getDataBarang() {
        val call: Call<DataBarang> = Constants.apiInterface.getBarangInfo(preference.getValues("barang_click"))
        call.enqueue(object : Callback<DataBarang> {
            override fun onResponse(call: Call<DataBarang>, response: Response<DataBarang>) {
                val data: DataBarang? = response.body()
                if (response.code() == 200) {
                    val distance = Constants.hitungJarak(
                        preference.getValues("lat")!!.toDouble(),
                        preference.getValues("long")!!.toDouble(),
                        data?.lat!!.toDouble(),
                        data.lng.toDouble()
                    )
                    hargaBarang = data.harga_awal.toDouble()
                    stok = data.jumlah_stok.toInt()

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
                }
            }

            override fun onFailure(call: Call<DataBarang>, t: Throwable) {
                Toast.makeText(this@BuatTransaksiActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}