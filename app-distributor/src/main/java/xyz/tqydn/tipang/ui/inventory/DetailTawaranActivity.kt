package xyz.tqydn.tipang.ui.inventory

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_detail_tawaran.*
import okhttp3.internal.notifyAll
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipang.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipang.utils.SharedPreference

class DetailTawaranActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tawaran)
        preference = SharedPreference(this)

        getDetail()
    }

    private fun getDetail() {
        val call: Call<TransaksiItem> = apiInterface.getDetailTransaksi(preference.getValues("trans_click"))
        call.enqueue(object : Callback<TransaksiItem> {
            override fun onResponse(call: Call<TransaksiItem>, response: Response<TransaksiItem>) {
                val item: TransaksiItem? = response.body()
                val img_profil = Uri.parse(item?.foto)
                val img_barang = Uri.parse(item?.foto_barang)
                val distance = hitungJarak(
                        preference.getValues("lat")!!.toDouble(),
                        preference.getValues("long")!!.toDouble(),
                        item?.lat!!.toDouble(),
                        item.lng!!.toDouble()
                )

                kode_transaksi.text = item.kode_transaksi
                tanggal.text = item.waktu_mulai
                namaUsaha.text = item.nama_usaha
                ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                transaksi.text = "Dari ${item.jumlah_transaksi} Transaksi"
                jarak.text = "${"%.2f".format(distance)} km"
                namaBarang.text = item.nama_barang
                descBarang.text = item.deskripsi_produk
                HargaBarang.text = formatRupiah(item.harga_awal!!.toDouble())
                alamat.text = item.alamat
                total.text = formatRupiah(item.total_tagihan!!.toDouble())
                jumlah.setText(item.jumlah_barang)
                jumlah.isClickable = false

                Glide.with(this@DetailTawaranActivity)
                        .load(img_profil)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imagePenjual)

                Glide.with(this@DetailTawaranActivity)
                        .load(img_barang)
                        .apply(RequestOptions.centerCropTransform())
                        .into(barang)

                if (item.jenis_kelamin == "Perempuan") {
                    namaPemilik.text = "Ibu ${item.username}"
                } else {
                    namaPemilik.text = "Bapak ${item.username}"
                }
            }

            override fun onFailure(call: Call<TransaksiItem>, t: Throwable) {
                Toast.makeText(this@DetailTawaranActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}