package xyz.tqydn.tipang.ui.inventory

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.databinding.ActivityDetailBerlangsungBinding
import xyz.tqydn.tipang.model.DefaultResponse
import xyz.tqydn.tipang.model.TransaksiItem
import xyz.tqydn.tipang.utils.Constants
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.SharedPreference

class DetailBerlangsungActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference
    private lateinit var dari: String
    private lateinit var ke: String
    private lateinit var telepon: String
    private lateinit var status: String
    private lateinit var binding: ActivityDetailBerlangsungBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBerlangsungBinding.inflate(layoutInflater)
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
        binding.tandaiLunas.setOnClickListener {
            val alertDialog = MaterialAlertDialogBuilder(this).create()
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.alert_lunas, null)
            alertDialog.setCancelable(true)
            alertDialog.setView(dialogView)
            val yakin = dialogView.findViewById(R.id.yakin) as Button
            val batal = dialogView.findViewById(R.id.batal) as Button
            yakin.setOnClickListener {
                tandaiLunas(preference.getValues("trans_click"))
                alertDialog.dismiss()
                showTransaksi()
            }
            batal.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
    }

    private fun tandaiLunas(id: String?) {
        val call: Call<DefaultResponse> = apiInterface.updateStatusTransaksi(1, status,id)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                Toast.makeText(this@DetailBerlangsungActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                val intent = Intent().apply {
                    putExtra(Constants.TITLE, response.body()?.message.toString())
                }
                setResult(RESULT_OK, intent)
                finish()
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
                dari = "${preference.getValues("lat")},${preference.getValues("long")}"
                ke = "${item?.lat},${item?.lng}"
                telepon = "62${item?.no_hp}"
                status = item?.status_transaksi.toString()

                Glide.with(this@DetailBerlangsungActivity)
                        .load(imgProfil)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imagePenjual)
                Glide.with(this@DetailBerlangsungActivity)
                        .load(imgUsaha)
                        .apply(RequestOptions.centerCropTransform())
                        .into(binding.imageUsaha)
                Glide.with(this@DetailBerlangsungActivity)
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
                binding.totalHarga.text = Constants.formatRupiah(item?.total_tagihan!!.toDouble())
                binding.ratingPenjual.text = "%.2f".format(item.rating?.toDouble())
                val itungTerjual = item.jumlah_barang!!.toInt() - item.jumlah_sisa!!.toInt()
                binding.terjual.text = "$itungTerjual item"
                binding.rusak.text = "${item.jumlah_exp} item"
                if (item.status_bayar == "1") {
                    binding.tandaiLunas.visibility = View.GONE
                }
                if (item.jenis_kelamin == "Perempuan") {
                    binding.namaPemilik.text = "Ibu ${item.username}"
                } else {
                    binding.namaPemilik.text = "Bapak ${item.username}"
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