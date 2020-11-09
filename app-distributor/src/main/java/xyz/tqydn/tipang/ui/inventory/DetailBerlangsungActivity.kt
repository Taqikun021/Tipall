package xyz.tqydn.tipang.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_detail_berlangsung.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.SharedPreference

class DetailBerlangsungActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var dari: String
    private lateinit var ke: String
    private lateinit var telepon: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_berlangsung)
        preference = SharedPreference(this)

        showTransaksi()
        keMaps.setOnClickListener {
            startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://google.co.id/maps/dir/${dari}/${ke}"))
            )
        }
        keWA.setOnClickListener {
            startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${telepon}"))
            )
        }
    }

    private fun showTransaksi() {
        val call: Call<TransaksiItem> = Constants.apiInterface.getDetailTransaksi(preference.getValues("trans_click"))
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

                Glide.with(this@DetailBerlangsungActivity)
                        .load(imgProfil)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imagePenjual)
                Glide.with(this@DetailBerlangsungActivity)
                        .load(imgUsaha)
                        .apply(RequestOptions.centerCropTransform())
                        .into(imageUsaha)
                Glide.with(this@DetailBerlangsungActivity)
                        .load(imgBarang)
                        .apply(RequestOptions.centerCropTransform())
                        .into(barang)

                kode_transaksi.text = item?.kode_transaksi
                tanggal.text = item?.waktu_mulai
                namaUsaha.text = item?.nama_usaha
                alamat.text = item?.alamat
                nomorHP.text = "+62 ${item?.no_hp}"
                namaBarang.text = item?.nama_barang
                descBarang.text = item?.deskripsi_produk
                jumlahBarang.text = "${item?.jumlah_barang} Item"
                totalHarga.text = Constants.formatRupiah(item?.total_tagihan!!.toDouble())
                ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                if (item.status_bayar == "1"){
                    tandaiLunas.isClickable = false
                    tandaiLunas.setBackgroundColor(R.color.textGrey)
                }
                if (item.jenis_kelamin == "Perempuan") {
                    namaPemilik.text = "Ibu ${item.username}"
                } else {
                    namaPemilik.text = "Bapak ${item.username}"
                }
            }

            override fun onFailure(call: Call<TransaksiItem>, t: Throwable) {
                Toast.makeText(this@DetailBerlangsungActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}