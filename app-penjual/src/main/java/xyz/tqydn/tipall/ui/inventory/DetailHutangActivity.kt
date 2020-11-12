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
import kotlinx.android.synthetic.main.activity_detail_hutang.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hutang)
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
                        .into(imagePenjual)
                Glide.with(this@DetailHutangActivity)
                        .load(imgUsaha)
                        .apply(RequestOptions.centerCropTransform())
                        .into(imageUsaha)
                Glide.with(this@DetailHutangActivity)
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
                totalHarga.text = formatRupiah(item?.total_tagihan!!.toDouble())
                ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                if (item.jenis_kelamin == "Perempuan") {
                    namaPemilik.text = "Ibu ${item.username}"
                } else {
                    namaPemilik.text = "Bapak ${item.username}"
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