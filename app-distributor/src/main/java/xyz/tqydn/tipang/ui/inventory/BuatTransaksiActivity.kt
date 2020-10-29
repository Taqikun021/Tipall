package xyz.tqydn.tipang.ui.inventory

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_buat_transaksi.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.tqydn.tipang.R
import xyz.tqydn.tipang.adapter.BarangPagerAdapter
import xyz.tqydn.tipang.model.Barang
import xyz.tqydn.tipang.model.DataBarang
import xyz.tqydn.tipang.model.DataPenjual
import xyz.tqydn.tipang.model.Rating
import xyz.tqydn.tipang.utils.Constants.Companion.apiInterface
import xyz.tqydn.tipang.utils.Constants.Companion.hitungJarak
import xyz.tqydn.tipang.utils.SharedPreference

class BuatTransaksiActivity : AppCompatActivity() {

    private lateinit var preference: SharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_transaksi)
        preference = SharedPreference(this)

        getPenjualInfo()
        getRatingPenjual()
        getBarang()
    }

    private fun getBarang() {
        val call: Call<Barang> = apiInterface.getBarang(preference.getValues("id_distributor"))
        call.enqueue(object : Callback<Barang> {
            override fun onResponse(call: Call<Barang>, response: Response<Barang>) {
                val item: Barang? = response.body()
                if (item!!.jumlahData > 0) {
                    viewPager.adapter = BarangPagerAdapter(item.dataBarang)
                }
            }

            override fun onFailure(call: Call<Barang>, t: Throwable) {
                Toast.makeText(this@BuatTransaksiActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getRatingPenjual() {
        val call: Call<Rating> = apiInterface.getRatingPenjual(preference.getValues("penjual_click"))
        call.enqueue(object : Callback<Rating> {
            override fun onResponse(call: Call<Rating>, response: Response<Rating>) {
                val item: Rating? = response.body()
                if (response.code() == 200) {
                    ratingPenjual.text = "%.2f".format(item?.rating_penjual!!.toDouble())
                    transaksi.text = "Dari ${item.jumlah_transaksi} Transaksi"
                }
            }

            override fun onFailure(call: Call<Rating>, t: Throwable) {
                Toast.makeText(this@BuatTransaksiActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPenjualInfo() {
        val call: Call<DataPenjual> = apiInterface.getPenjualInfo(preference.getValues("penjual_click"))
        call.enqueue(object : Callback<DataPenjual> {
            override fun onResponse(call: Call<DataPenjual>, response: Response<DataPenjual>) {
                val item: DataPenjual? = response.body()
                if (response.code() == 200) {
                    val img = Uri.parse(item?.foto)
                    val distance = hitungJarak(
                        preference.getValues("lat")!!.toDouble(),
                        preference.getValues("long")!!.toDouble(),
                        item?.lat!!.toDouble(),
                        item.lng.toDouble()
                    )

                    namaUsaha.text = item.nama_usaha
                    alamat.text = item.alamat
                    jarak.text = "${"%.2f".format(distance)} km"
                    if (item.jenis_kelamin == "Perempuan") {
                        namaPemilik.text = "Ibu ${item.username}"
                    } else {
                        namaPemilik.text = "Bapak ${item.username}"
                    }

                    Glide.with(this@BuatTransaksiActivity)
                        .load(img)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imagePenjual)
                }
            }

            override fun onFailure(call: Call<DataPenjual>, t: Throwable) {
                Toast.makeText(this@BuatTransaksiActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}