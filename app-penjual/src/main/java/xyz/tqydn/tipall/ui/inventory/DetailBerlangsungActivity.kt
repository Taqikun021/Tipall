package xyz.tqydn.tipall.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_detail_berlangsung.*
import kotlinx.android.synthetic.main.alert_laporan.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipall.R
import xyz.tqydn.tipall.model.DefaultResponse
import xyz.tqydn.tipall.model.TransaksiItem
import xyz.tqydn.tipall.utils.Constants
import xyz.tqydn.tipall.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipall.utils.SharedPreference

class DetailBerlangsungActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var dari: String
    private lateinit var ke: String
    private lateinit var telepon: String
    private lateinit var status: String
    private lateinit var totalTagihan: String
    private lateinit var statusBayar: String
    private lateinit var sisa: String
    private lateinit var exp: String

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
        buatLaporan.setOnClickListener {
            buatLaporan()
        }
        tandaiSelesai.setOnClickListener {
            transaksiSelesai(preference.getValues("trans_click"))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun buatLaporan() {
        val photoDialog = MaterialAlertDialogBuilder(this@DetailBerlangsungActivity).create()
        val inflater = LayoutInflater.from(this@DetailBerlangsungActivity)
        val dialogView = inflater.inflate(R.layout.alert_laporan, null)
        photoDialog.setCancelable(true)
        photoDialog.setView(dialogView)
        val item = listOf("Stok Tersisa", "Barang Rusak/Expired")
        val adapter = ArrayAdapter(dialogView.context, R.layout.list_kelamin, item)
        (dialogView.LayoutLaporan.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        photoDialog.show()

        dialogView.kirimLaporan.setOnClickListener {
            when (dialogView.etJenisLaporan.text.toString()) {
                "Stok Tersisa" -> {
                    val jumlah = dialogView.etJumlah.text.toString().trim()
                    updateLaporan(jumlah, exp, preference.getValues("trans_click"))
                    photoDialog.dismiss()
                }
                "Barang Rusak/Expired" -> {
                    val jumlah = dialogView.etJumlah.text.toString().trim()
                    updateLaporan(sisa, jumlah, preference.getValues("trans_click"))
                    photoDialog.dismiss()
                }
            }
        }

    }

    private fun updateLaporan(sisa: String, exp: String, id: String?) {
        val call: Call<DefaultResponse> = apiInterface.updateLaporan(sisa, exp, id)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val item: DefaultResponse? = response.body()
                if (item?.status.toString() != "200") {
                    Toast.makeText(this@DetailBerlangsungActivity, item?.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@DetailBerlangsungActivity).create()
                val inflater = LayoutInflater.from(this@DetailBerlangsungActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }

    private fun transaksiSelesai(id: String?) {
        val call: Call<DefaultResponse> = apiInterface.transaksiSelesai(totalTagihan, statusBayar.toInt(), Constants.status3, id)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                val item: DefaultResponse? = response.body()
                if (item?.status.toString() != "200") {
                    Toast.makeText(this@DetailBerlangsungActivity, item?.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent().apply {
                        putExtra(Constants.TITLE, item?.message.toString())
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@DetailBerlangsungActivity).create()
                val inflater = LayoutInflater.from(this@DetailBerlangsungActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
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
                val itungTerjual = item?.jumlah_barang!!.toInt() - item.jumlah_sisa!!.toInt()
                dari = "${preference.getValues("lat")},${preference.getValues("long")}"
                ke = "${item.lat},${item.lng}"
                telepon = "62${item.no_hp}"
                status = item.status_transaksi.toString()
                totalTagihan = item.total_tagihan.toString()
                statusBayar = item.status_bayar.toString()
                sisa = item.jumlah_sisa
                exp = item.jumlah_exp.toString()
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
                kode_transaksi.text = item.kode_transaksi
                tanggal.text = item.waktu_mulai
                namaUsaha.text = item.nama_usaha
                alamat.text = item.alamat
                nomorHP.text = "+62 ${item.no_hp}"
                namaBarang.text = item.nama_barang
                descBarang.text = item.deskripsi_produk
                jumlahBarang.text = "${item.jumlah_barang} Item"
                totalHarga.text = Constants.formatRupiah(item.total_tagihan!!.toDouble())
                ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                terjual.text = "$itungTerjual item"
                rusak.text = "${item.jumlah_exp} item"
                if (item.status_bayar == "1"){
                    tandaiSelesai.visibility = View.GONE
                }
                if (item.jenis_kelamin == "Perempuan") {
                    namaPemilik.text = "Ibu ${item.username}"
                } else {
                    namaPemilik.text = "Bapak ${item.username}"
                }
            }

            override fun onFailure(call: Call<TransaksiItem>, t: Throwable) {
                val photoDialog = MaterialAlertDialogBuilder(this@DetailBerlangsungActivity).create()
                val inflater = LayoutInflater.from(this@DetailBerlangsungActivity)
                val dialogView = inflater.inflate(R.layout.alert_error, null)
                photoDialog.setCancelable(true)
                photoDialog.setView(dialogView)
                photoDialog.show()
            }
        })
    }
}