package xyz.tqydn.tipang.ui.inventory

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
import kotlinx.android.synthetic.main.activity_detail_permintaan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.model.DefaultResponse
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.Constants.Companion.formatRupiah
import xyz.tqydn.tipang.utils.Constants.Companion.status2
import xyz.tqydn.tipang.utils.Constants.Companion.status3
import xyz.tqydn.tipang.utils.Constants.Companion.status4
import xyz.tqydn.tipang.utils.SharedPreference
import javax.security.auth.DestroyFailedException

class DetailPermintaanActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var dari: String
    private lateinit var ke: String
    private lateinit var telepon: String
    private lateinit var idBarang: String
    private lateinit var stok: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_permintaan)
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
        terima.setOnClickListener {
            transDiterima(preference.getValues("trans_click"))
        }
        tolak.setOnClickListener {
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

    private fun transDitolak(id: String?) {
        val call: Call<DefaultResponse> = apiInterface.transaksiSelesai("0", 1, status4, id)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val item: DefaultResponse? = response.body()
                if (item?.status.toString() != "200") {
                    Toast.makeText(this@DetailPermintaanActivity, item?.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent().apply {
                        putExtra(Constants.TITLE, item?.message.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@DetailPermintaanActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun transDiterima(id: String?) {
        val call: Call<DefaultResponse> = apiInterface.updateStatusTransaksi(0, status2, id)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val item: DefaultResponse? = response.body()
                if (item?.status.toString() != "200") {
                    Toast.makeText(this@DetailPermintaanActivity, item?.message, Toast.LENGTH_SHORT).show()
                    updateStok(stok, idBarang)
                    val intent = Intent().apply {
                        putExtra(Constants.TITLE, item?.message.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@DetailPermintaanActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateStok(stok: String, idBarang: String) {
        val call: Call<DefaultResponse> = apiInterface.updateStok(stok, idBarang)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.body()?.status.toString() == "200") {
                    Toast.makeText(this@DetailPermintaanActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@DetailPermintaanActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
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

                Glide.with(this@DetailPermintaanActivity)
                        .load(imgProfil)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imagePenjual)
                Glide.with(this@DetailPermintaanActivity)
                        .load(imgUsaha)
                        .apply(RequestOptions.centerCropTransform())
                        .into(imageUsaha)
                Glide.with(this@DetailPermintaanActivity)
                        .load(imgBarang)
                        .apply(RequestOptions.centerCropTransform())
                        .into(barang)

                kode_transaksi.text = item.kode_transaksi
                tanggal.text = item.waktu_mulai
                namaUsaha.text = item.nama_usaha
                alamat.text = item.alamat
                nomorHP.text = "+62 ${item.no_hp}"
                namaBarang.text = item.nama_barang
                descBarang.text = item.deskripsi_produk
                jumlahBarang.text = "${item.jumlah_barang} Item"
                totalHarga.text = formatRupiah(item.total_tagihan!!.toDouble())
                ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                if (item.jenis_kelamin == "Perempuan") {
                    namaPemilik.text = "Ibu ${item.username}"
                } else {
                    namaPemilik.text = "Bapak ${item.username}"
                }
            }

            override fun onFailure(call: Call<TransaksiItem>, t: Throwable) {
                Toast.makeText(this@DetailPermintaanActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}